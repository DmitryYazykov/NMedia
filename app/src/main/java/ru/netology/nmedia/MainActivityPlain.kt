package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivityPlain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<ImageButton>(R.id.likesIcon).setOnClickListener {
//            if (it is ImageButton) {
//                it.setImageResource(R.drawable.ic_liked)
//            }
//        }
    }
}