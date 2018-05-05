package com.tomorrow.learn

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_control.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                container.startCycle()
            } else {
                container.stopCycle()
            }
        }
    }
}
