package com.hypersoft.flipableview

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hypersoft.flipableview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.apply {
            mbSimpleView.setOnClickListener { startActivity(Intent(this@MainActivity, SimpleViewActivity::class.java))}
            mbRecyclerView.setOnClickListener { startActivity(Intent(this@MainActivity, RecyclerViewActivity::class.java)) }
            mbFlipOnceExample.setOnClickListener { startActivity(Intent(this@MainActivity, FlipOnceActivity::class.java)) }
        }

    }
}