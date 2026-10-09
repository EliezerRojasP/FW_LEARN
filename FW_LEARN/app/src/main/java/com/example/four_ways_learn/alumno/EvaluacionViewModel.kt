package com.example.four_ways_learn.alumno

import com.example.four_ways_learn.R
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.four_ways_learn.network.*
import com.google.gson.Gson
import retrofit2.Call

data class EvaluationUi(
    val intento: IntentoEvaluacion? = null,
    val index: Int = 0,
    val loading: Boolean = false,
    val error: ApiError? = null,
    val resultado: ResultadoEvaluacion? = null
)

class EvaluacionViewModel(private val saved: SavedStateHandle) : ViewModel() {
    private val gson = Gson()
    private var attempt: IntentoEvaluacion? = saved.get<String>("intento_json")
        ?.let { runCatching { gson.fromJson(it, IntentoEvaluacion::class.java) }.getOrNull() }
    private var index = saved.get<Int>("index") ?: 0
    private val answers = mutableMapOf<Int, Int>()
    private var result: ResultadoEvaluacion? = saved.get<String>("resultado_json")
        ?.let { runCatching { gson.fromJson(it, ResultadoEvaluacion::class.java) }.getOrNull() }
    private var running: Call<*>? = null
    val ui = MutableLiveData(EvaluationUi(attempt, index, resultado = result))

    init {
        saved.get<ArrayList<Int>>("preguntas")
            ?.zip(saved.get<ArrayList<Int>>("alternativas").orEmpty())
            ?.forEach { (p, a) -> answers[p] = a }
    }

    fun load(lesson: Int) {
        if (attempt != null || result != null || running != null) return
        val call = RetrofitClient.api.iniciar(IniciarEvaluacion(lesson)); running = call; publish(
            loading = true
        )
        ApiRepository.execute(call, { data ->
            running = null
            if (data == null || data.preguntas.isEmpty()) publish(error = ApiError(mensajeRes = R.string.error_empty_evaluation))
            else {
                attempt = data; index = 0; persist(); publish()
            }
        }, { error -> running = null; publish(error = error) })
    }

    fun selected(question: Int) = answers[question]
    fun answeredCount() = answers.size
    fun choose(question: Int, option: Int) {
        answers[question] = option; persist()
    }

    fun previous() {
        if (index > 0 && running == null) {
            index--; persist(); publish()
        }
    }

    fun next() {
        val a = attempt ?: return
        if (running != null) return
        val question = a.preguntas[index]
        if (answers[question.id] == null) {
            publish(error = ApiError(mensajeRes = R.string.error_choose_option)); return
        }
        if (index < a.preguntas.lastIndex) {
            index++; persist(); publish()
        } else submit()
    }

    fun retry(lesson: Int) {
        if (attempt == null) load(lesson) else if (index == attempt!!.preguntas.lastIndex && answers.size == attempt!!.preguntas.size) submit() else publish()
    }

    private fun submit() {
        val a = attempt ?: return
        if (running != null || result != null) return
        val selected =
            a.preguntas.mapNotNull { p -> answers[p.id]?.let { RespuestaAlumno(p.id, it) } }
        if (selected.size != a.preguntas.size) {
            publish(error = ApiError(mensajeRes = R.string.error_answer_all)); return
        }
        val call = RetrofitClient.api.enviar(EnviarEvaluacion(a.id_intento, selected)); running =
            call; publish(loading = true)
        ApiRepository.execute(
            call,
            { data ->
                running = null; result =
                data; persist(); publish(error = if (data == null) ApiError(mensajeRes = R.string.error_result_missing) else null)
            },
            { error -> running = null; publish(error = error) })
    }

    private fun persist() {
        saved["intento_json"] = attempt?.let { gson.toJson(it) }; saved["resultado_json"] =
            result?.let { gson.toJson(it) }; saved["index"] = index
        saved["preguntas"] = ArrayList(answers.keys); saved["alternativas"] =
            ArrayList(answers.values)
    }

    private fun publish(loading: Boolean = false, error: ApiError? = null) {
        ui.value = EvaluationUi(attempt, index, loading, error, result)
    }

    override fun onCleared() {
        running?.cancel(); super.onCleared()
    }
}
