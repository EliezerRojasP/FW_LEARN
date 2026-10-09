package com.example.four_ways_learn.alumno

import android.content.Intent
import android.os.Bundle
import com.example.four_ways_learn.ui.BaseActivity

class TemasActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); startActivity(
            Intent(
                this,
                CursosActivity::class.java
            )
        ); finish()
    }
}
