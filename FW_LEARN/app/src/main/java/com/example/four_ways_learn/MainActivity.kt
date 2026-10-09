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

class MainActivity : BaseActivity() {
    override val requiresSession = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.app_title), false)
        val form = ActivityMainBinding.inflate(layoutInflater, panel, true)
        fun signIn() {
            if (progress.visibility == View.VISIBLE) return
            val name = form.etUsuario.text.toString().trim()
            val pass = form.etContrasena.text.toString()
            if (name.isBlank() || pass.isEmpty()) {
                message(getString(R.string.error_credentials_required)); return
            }
            request(RetrofitClient.api.login(LoginRequest(name, pass))) { data ->
                if (data != null) {
                    SessionStore.save(data); openHome(data.usuario)
                }
            }
        }
        form.btnIngresar.setOnClickListener { signIn() }
        form.etContrasena.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                signIn(); true
            } else false
        }
        if (SessionStore.token != null) request(RetrofitClient.api.me()) { user ->
            if (user != null) {
                SessionStore.update(user); openHome(user)
            }
        }
    }

    private fun openHome(user: Usuario) {
        val target =
            if (user.rol == "ADMIN") AdminActivity::class.java else BienvenidaActivity::class.java
        startActivity(
            Intent(
                this,
                target
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ); finish()
    }
}
