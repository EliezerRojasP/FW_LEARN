package com.example.four_ways_learn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.four_ways_learn.admin.AdminActivity
import com.example.four_ways_learn.alumno.BienvenidaActivity
import com.example.four_ways_learn.databinding.ActivityMainBinding
import com.example.four_ways_learn.network.*
import com.example.four_ways_learn.ui.BaseActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth



class MainActivity : BaseActivity() {
    override val requiereSesion = false

    private lateinit var binding: ActivityMainBinding

    private lateinit var autenticacion: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); pantalla(getString(R.string.titulo_app), false)

        binding = ActivityMainBinding.inflate(
            layoutInflater,
            panel,
            true
        )

        autenticacion = FirebaseAuth.getInstance()

        configurarFormulario()
        verificarSesionGuardada()
    }

    private fun configurarFormulario(){
        binding.btnIngresar.setOnClickListener {
            iniciarSesion()
        }

        binding.etContrasena.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_DONE){
                iniciarSesion()
                true
            } else {
                false
            }
        }
    }

    private fun iniciarSesion(){
        if (progreso.visibility  == View.VISIBLE){
            return
        }

        val correo = binding.etUsuario.text.toString().trim()
        val contrasena = binding.etContrasena.text.toString()

        if (!camposCompletos(correo, contrasena)){
            mensaje(getString(R.string.error_credenciales_requeridas))
            return
        }

        progreso.visibility = View.VISIBLE
        binding.btnIngresar.isEnabled = false

        autenticacion.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this){ resultado ->
                    progreso.visibility = View.GONE
                    binding.btnIngresar.isEnabled = true

                    if (resultado.isSuccessful){
                        obtenerSesionServidor()
                    } else {
                        Log.e(
                            "FirebaseFW",
                            "Error al iniciar sesión",
                            resultado.exception
                        )
                        mensaje(getString(R.string.firebase_error_ingreso))
                    }
            }
    }

    private fun obtenerSesionServidor(){
        val cuenta = autenticacion.currentUser

        if(cuenta == null){
            mensaje(getString(R.string.firebase_error_ingreso))
            return
        }

        progreso.visibility = View.VISIBLE
        binding.btnIngresar.isEnabled = false

        cuenta.getIdToken(true)
            .addOnCompleteListener(this) { resultado ->
                progreso.visibility = View.GONE
                binding.btnIngresar.isEnabled = true

                if (!resultado.isSuccessful){
                    Log.e(
                        "FirebaseFW",
                        "Error al obtener el token",
                        resultado.exception
                    )
                    mensaje(getString(R.string.firebase_error_ingreso))
                    return@addOnCompleteListener
                }

                val tokenFirebase = resultado.result?.token

                if (tokenFirebase.isNullOrBlank()){
                    mensaje(getString(R.string.firebase_error_ingreso))
                    return@addOnCompleteListener
                }

                val datos = LoginFirebaseRequest(tokenFirebase)

                peticion(RetrofitClient.api.loginFirebase(datos)){ respuesta ->
                    if(respuesta != null){
                        SessionStore.guardar(respuesta)
                        abrirInicio(respuesta.usuario)
                    }
                }
            }
    }

    private fun camposCompletos(
        usuario: String,
        contrasena: String
    ): Boolean {
        return usuario.isNotBlank() && contrasena.isNotEmpty()
    }

    private fun verificarSesionGuardada(){
        if(SessionStore.token == null){
            return
        }

        peticion(RetrofitClient.api.me()) {usuario ->
            if(usuario != null){
                SessionStore.actualizar(usuario)
                abrirInicio(usuario)
            }
        }
    }

    private fun abrirInicio(usuario: Usuario){
        val destino: Intent

        if (usuario.rol == "ADMIN") {
            destino = Intent(this, AdminActivity::class.java)
        } else {
            destino = Intent(this, BienvenidaActivity::class.java)
        }

        destino.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )

        startActivity(destino)
        finish()
    }
}
