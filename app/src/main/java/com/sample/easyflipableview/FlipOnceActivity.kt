package com.sample.easyflipableview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hypersoft.easyviewflip.EasyViewFlip
import com.sample.easyflipableview.databinding.ActivityFlipOnceBinding

class FlipOnceActivity : AppCompatActivity() {

    private var binding: ActivityFlipOnceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFlipOnceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.apply {
            cardFlipView.setFlipDuration(1000)
            cardFlipView.setFlipEnabled(true)
            kingBackSide.setOnClickListener { cardFlipView.flipTheView() }
            kingFrontSide.setOnClickListener { cardFlipView.flipTheView() }

            cardFlipView.setOnFlipListener(object : EasyViewFlip.OnFlipAnimationListener{
                override fun onViewFlipCompleted(
                    easyFlipView: EasyViewFlip?,
                    newCurrentSide: EasyViewFlip.FlipState?
                ) {
                    Toast.makeText(
                        this@FlipOnceActivity,
                        "Flipped once ! Ace revealed $newCurrentSide", Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

    }
}