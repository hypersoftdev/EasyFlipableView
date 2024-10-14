package com.sample.easyflipableview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hypersoft.easyviewflip.EasyViewFlip
import com.sample.easyflipableview.databinding.ActivitySimpleViewBinding

class SimpleViewActivity : AppCompatActivity() {

    private var binding: ActivitySimpleViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySimpleViewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.apply {
            easyFlipView.setFlipDuration(1000)
            easyFlipView.setFlipEnabled(true)
            imgFrontCard.setOnClickListener { easyFlipView.flipTheView() }
            imgBackCard.setOnClickListener { easyFlipView.flipTheView() }

            easyFlipView2.setFlipDuration(5000)
            easyFlipView2.setToHorizontalType()
            easyFlipView2.setFlipTypeFromLeft()

            easyFlipView.setOnFlipListener(object: EasyViewFlip.OnFlipAnimationListener {
                override fun onViewFlipCompleted(
                    easyFlipView: EasyViewFlip?,
                    newCurrentSide: EasyViewFlip.FlipState?
                ) {
                    Toast.makeText(this@SimpleViewActivity,
                        "Flip Completed! New Side is: " + newCurrentSide, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}