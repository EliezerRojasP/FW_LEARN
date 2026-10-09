package com.example.four_ways_learn.ui

import android.os.Bundle
import com.example.four_ways_learn.R
import com.example.four_ways_learn.network.SessionStore

class AccountActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); screen(getString(R.string.account_title))
        val user = SessionStore.usuario ?: return
        panel.addView(text(getString(R.string.account_title), 28, true))
        panel.addView(muted(getString(R.string.account_description)))
        box { inside ->
            inside.addView(icon(R.drawable.ic_fw_user, 64))
            inside.addView(text(getString(R.string.admin_user_identity, user.nombre, user.apellido), 22, true))
            inside.addView(muted(getString(R.string.account_username, user.usuario)))
            inside.addView(badge(getString(R.string.account_role, getString(if (isAdmin) R.string.role_admin else R.string.role_student))))
        }
        panel.addView(muted(getString(if (isAdmin) R.string.account_help else R.string.account_student_help), 14))
        panel.addView(button(getString(R.string.action_logout)) { logout() }.apply { setIconResource(R.drawable.ic_fw_exit) })
    }
}
