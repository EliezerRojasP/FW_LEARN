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
    val indice: Int = 0,
    val cargando: Boolean = false,
    val error: ApiError? = null,
    val resultado: ResultadoEvaluacion? = null
)

class EvaluacionViewModel(private val guardado: SavedStateHandle) : ViewModel() {
    private val gson = Gson()
    private var intento: IntentoEvaluacion? = guardado.get<String>("intento_json")
        ?.let { runCatching { gson.fromJson(it, IntentoEvaluacion::class.java) }.getOrNull() }
    private var indice = guardado.get<Int>("indice") ?: 0
    private val respuestas = mutableMapOf<Int, Int>()
    private var resultado: ResultadoEvaluacion? = guardado.get<String>("resultado_json")
        ?.let { runCatching { gson.fromJson(it, ResultadoEvaluacion::class.java) }.getOrNull() }
    private var enCurso: Call<*>? = null
    val interfaz = MutableLiveData(EvaluationUi(intento, indice, resultado = resultado))

    init {
        guardado.get<ArrayList<Int>>("preguntas")
            ?.zip(guardado.get<ArrayList<Int>>("alternativas").orEmpty())
            ?.forEach { (p, a) -> respuestas[p] = a }
    }

    fun cargar(leccion: Int) {
        if (intento != null || resultado != null || enCurso != null) return
        val call = RetrofitClient.api.iniciar(IniciarEvaluacion(leccion)); enCurso = call; publicar(
            cargando = true
        )
        ApiRepository.execute(call, { data ->
            enCurso = null
            if (data == null || data.preguntas.isEmpty()) publicar(error = ApiError(mensajeRes = R.string.error_evaluacion_vacia))
            else {
                intento = data; indice = 0; persistir(); publicar()
            }
        }, { error -> enCurso = null; publicar(error = error) })
    }

    fun seleccionada(pregunta: Int) = respuestas[pregunta]
    fun cantidadRespondidas() = respuestas.size
    fun elegir(pregunta: Int, alternativa: Int) {
        respuestas[pregunta] = alternativa; persistir()
    }

    fun anterior() {
        if (indice > 0 && enCurso == null) {
            indice--; persistir(); publicar()
        }
    }

    fun siguiente() {
        val a = intento ?: return
        if (enCurso != null) return
        val pregunta = a.preguntas[indice]
        if (respuestas[pregunta.id] == null) {
            publicar(error = ApiError(mensajeRes = R.string.error_elegir_opcion)); return
        }
        if (indice < a.preguntas.lastIndex) {
            indice++; persistir(); publicar()
        } else enviar()
    }

    fun reintentar(leccion: Int) {
        if (intento == null) cargar(leccion) else if (indice == intento!!.preguntas.lastIndex && respuestas.size == intento!!.preguntas.size) enviar() else publicar()
    }

    private fun enviar() {
        val a = intento ?: return
        if (enCurso != null || resultado != null) return
        val seleccionada =
            a.preguntas.mapNotNull { p -> respuestas[p.id]?.let { RespuestaAlumno(p.id, it) } }
        if (seleccionada.size != a.preguntas.size) {
            publicar(error = ApiError(mensajeRes = R.string.error_responder_todo)); return
        }
        val call = RetrofitClient.api.enviar(EnviarEvaluacion(a.id_intento, seleccionada)); enCurso =
            call; publicar(cargando = true)
        ApiRepository.execute(
            call,
            { data ->
                enCurso = null; resultado =
                data; persistir(); publicar(error = if (data == null) ApiError(mensajeRes = R.string.error_resultado_faltante) else null)
            },
            { error -> enCurso = null; publicar(error = error) })
    }

    private fun persistir() {
        guardado["intento_json"] = intento?.let { gson.toJson(it) }; guardado["resultado_json"] =
            resultado?.let { gson.toJson(it) }; guardado["indice"] = indice
        guardado["preguntas"] = ArrayList(respuestas.keys); guardado["alternativas"] =
            ArrayList(respuestas.values)
    }

    private fun publicar(cargando: Boolean = false, error: ApiError? = null) {
        interfaz.value = EvaluationUi(intento, indice, cargando, error, resultado)
    }

    override fun onCleared() {
        enCurso?.cancel(); super.onCleared()
    }
}
