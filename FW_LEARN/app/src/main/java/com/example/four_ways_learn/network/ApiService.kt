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

data class LoginFirebaseRequest(val id_token: String)
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
    fun login(@Body peticion: LoginRequest): Call<ApiResponse<LoginData>>
    @GET("me.php")
    fun me(): Call<ApiResponse<Usuario>>
    @POST("logout.php")
    fun cerrarSesion(): Call<ApiResponse<JsonElement>>
    @GET("cursos.php")
    fun cursos(): Call<ApiResponse<List<Curso>>>
    @GET("topicos.php")
    fun topicos(@Query("curso_id") id: Int): Call<ApiResponse<List<Topico>>>
    @GET("lecciones.php")
    fun lecciones(@Query("topico_id") id: Int): Call<ApiResponse<List<Leccion>>>
    @GET("leccion.php")
    fun leccion(@Query("id") id: Int): Call<ApiResponse<Leccion>>
    @POST("evaluaciones.php")
    fun iniciar(@Body peticion: IniciarEvaluacion): Call<ApiResponse<IntentoEvaluacion>>
    @POST("enviar_evaluacion.php")
    fun enviar(@Body peticion: EnviarEvaluacion): Call<ApiResponse<ResultadoEvaluacion>>
    @GET("resultados.php")
    fun resultados(): Call<ApiResponse<List<ResultadoHistorial>>>
    @GET("admin.php?recurso=cursos")
    fun cursosAdmin(): Call<ApiResponse<List<CursoAdmin>>>

    @POST("admin.php?recurso=cursos")
    fun cursoCrear(@Body curso: CursoAdmin): Call<ApiResponse<JsonElement>>

    @PUT("admin.php?recurso=cursos")
    fun cursoActualizar(@Body curso: CursoAdmin): Call<ApiResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "admin.php?recurso=cursos", hasBody = true)
    fun cursoDesactivar(@Body id: IdCurso): Call<ApiResponse<JsonElement>>

    @GET("admin.php?recurso=topicos")
    fun topicosAdmin(): Call<ApiResponse<List<TopicoAdmin>>>

    @POST("admin.php?recurso=topicos")
    fun topicoCrear(@Body topico: TopicoAdmin): Call<ApiResponse<JsonElement>>

    @PUT("admin.php?recurso=topicos")
    fun topicoActualizar(@Body topico: TopicoAdmin): Call<ApiResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "admin.php?recurso=topicos", hasBody = true)
    fun topicoDesactivar(@Body id: IdTopico): Call<ApiResponse<JsonElement>>

    @GET("admin.php?recurso=lecciones")
    fun leccionesAdmin(): Call<ApiResponse<List<LeccionAdmin>>>

    @POST("admin.php?recurso=lecciones")
    fun leccionCrear(@Body leccion: LeccionAdmin): Call<ApiResponse<JsonElement>>

    @PUT("admin.php?recurso=lecciones")
    fun leccionActualizar(@Body leccion: LeccionAdmin): Call<ApiResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "admin.php?recurso=lecciones", hasBody = true)
    fun leccionDesactivar(@Body id: IdLeccion): Call<ApiResponse<JsonElement>>

    @GET("admin.php?recurso=preguntas")
    fun preguntasAdmin(): Call<ApiResponse<List<PreguntaAdmin>>>

    @POST("admin.php?recurso=preguntas")
    fun preguntaCrear(@Body pregunta: PreguntaAdmin): Call<ApiResponse<JsonElement>>

    @PUT("admin.php?recurso=preguntas")
    fun preguntaActualizar(@Body pregunta: PreguntaAdmin): Call<ApiResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "admin.php?recurso=preguntas", hasBody = true)
    fun preguntaDesactivar(@Body id: IdPregunta): Call<ApiResponse<JsonElement>>

    @GET("admin.php?recurso=usuarios")
    fun usuariosAdmin(): Call<ApiResponse<List<UsuarioAdmin>>>

    @POST("admin.php?recurso=usuarios")
    fun usuarioCrear(@Body usuario: UsuarioAdmin): Call<ApiResponse<JsonElement>>

    @PUT("admin.php?recurso=usuarios")
    fun usuarioActualizar(@Body usuario: UsuarioAdmin): Call<ApiResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "admin.php?recurso=usuarios", hasBody = true)
    fun usuarioDesactivar(@Body id: IdUsuario): Call<ApiResponse<JsonElement>>

    @GET("admin.php?recurso=resultados")
    fun resultadosAdmin(): Call<ApiResponse<List<ResultadoAdmin>>>

    @GET("admin.php?recurso=estadisticas")
    fun estadisticasAdmin(): Call<ApiResponse<EstadisticasAdmin>>

    @POST("login_firebase.php")
    fun loginFirebase(@Body peticion: LoginFirebaseRequest): Call<ApiResponse<LoginData>>

    @POST("mensaje_enviar.php")
    fun enviarMensaje(@Body request: EnviarMensajeRequest): Call<ApiResponse<MensajeCreado>>

    @GET("mensajes_alumno.php")
    fun misMensajes(): Call<ApiResponse<List<MensajeAlumno>>>

    @POST("mensajes_alumno.php")
    fun marcarRespuestaVista(@Body request: MensajeVistoRequest): Call<ApiResponse<JsonElement>>
}
