package com.example.four_ways_learn.network

import com.example.four_ways_learn.model.*
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

data class ApiResponse<T>(val success: Boolean, val data: T?, val mensaje: String?)
data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val apellido: String,
    val usuario: String,
    val rol: String
)

data class LoginRequest(val usuario: String, val password: String)
data class LoginData(val token: String, val usuario: Usuario)
data class IniciarEvaluacion(val id_leccion: Int)
data class OpcionEvaluacion(val id: Int, val texto: String)
data class PreguntaEvaluacion(
    val id: Int,
    val enunciado: String,
    val alternativas: List<OpcionEvaluacion>
)

data class IntentoEvaluacion(
    val id_intento: String,
    val id_leccion: Int,
    val preguntas: List<PreguntaEvaluacion>
)

data class RespuestaAlumno(val id_pregunta: Int, val id_alternativa: Int)
data class EnviarEvaluacion(val id_intento: String, val respuestas: List<RespuestaAlumno>)
data class Revision(val enunciado: String, val correcta: Boolean, val respuesta_correcta: String)
data class ResultadoEvaluacion(
    val id_resultado: Int,
    val puntaje: Double,
    val total_preguntas: Int,
    val porcentaje: Double,
    val revision: List<Revision>,
    val leccion_completada: Boolean,
    val porcentaje_finalizacion: Double,
    val aprobada: Boolean,
    val tema: Topico?
)

data class Progreso(
    val id_leccion: Int,
    val completada: Int,
    val mejor_puntaje: Double,
    val titulo: String
)

data class ResultadoHistorial(
    val puntaje: Double,
    val total_preguntas: Int,
    val porcentaje: Double,
    val fecha_resultado: String,
    val titulo: String,
    val id_leccion: Int = 0
)

data class AvanceGeneral(
    val total_lecciones: Int,
    val lecciones_completadas: Int,
    val porcentaje_avance: Double,
    val cursos: List<Curso>,
    val temas: List<Topico>
)

interface ApiService {
    @GET("avance.php")
    fun avance(): Call<ApiResponse<AvanceGeneral>>
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<ApiResponse<LoginData>>
    @GET("me.php")
    fun me(): Call<ApiResponse<Usuario>>
    @POST("logout.php")
    fun logout(): Call<ApiResponse<JsonElement>>
    @GET("cursos.php")
    fun cursos(): Call<ApiResponse<List<Curso>>>
    @GET("topicos.php")
    fun topicos(@Query("curso_id") id: Int): Call<ApiResponse<List<Topico>>>
    @GET("lecciones.php")
    fun lecciones(@Query("topico_id") id: Int): Call<ApiResponse<List<Leccion>>>
    @GET("leccion.php")
    fun leccion(@Query("id") id: Int): Call<ApiResponse<Leccion>>
    @POST("evaluaciones.php")
    fun iniciar(@Body request: IniciarEvaluacion): Call<ApiResponse<IntentoEvaluacion>>
    @POST("enviar_evaluacion.php")
    fun enviar(@Body request: EnviarEvaluacion): Call<ApiResponse<ResultadoEvaluacion>>
    @GET("progreso.php")
    fun progreso(): Call<ApiResponse<List<Progreso>>>
    @GET("resultados.php")
    fun resultados(): Call<ApiResponse<List<ResultadoHistorial>>>
    @GET("admin.php")
    fun adminList(@Query("recurso") recurso: String): Call<ApiResponse<JsonElement>>
    @POST("admin.php")
    fun adminCreate(
        @Query("recurso") recurso: String,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Call<ApiResponse<JsonElement>>

    @PUT("admin.php")
    fun adminUpdate(
        @Query("recurso") recurso: String,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Call<ApiResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "admin.php", hasBody = true)
    fun adminDisable(
        @Query("recurso") recurso: String,
        @Body data: Map<String, Int>
    ): Call<ApiResponse<JsonElement>>
}
