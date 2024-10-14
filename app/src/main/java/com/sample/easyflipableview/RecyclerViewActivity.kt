package com.sample.easyflipableview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.sample.easyflipableview.databinding.ActivityRecyclerViewBinding

class RecyclerViewActivity : AppCompatActivity() {

    private var binding: ActivityRecyclerViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        val list: List<TestModel> = ArrayList()
        val list = mutableListOf<TestModel>()
        binding?.apply {
            recyclerView.layoutManager = GridLayoutManager(this@RecyclerViewActivity, 2)

            for (i in 0..19) {
                val model = TestModel()
                model.isFlipped = false
                list.add(model)
            }

            recyclerView.adapter = SampleAdapter(list)

        }
    }
}