package com.sample.easyflipableview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.easyviewflip.EasyViewFlip
import com.sample.easyflipableview.databinding.ItemRecyclerviewBinding

class SampleAdapter(): RecyclerView.Adapter<SampleAdapter.CustomViewHolder>() {

    private lateinit var list: List<TestModel>

    constructor(list: List<TestModel>): this() {
        this.list = list
    }

    inner class CustomViewHolder(val binding: ItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecyclerviewBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.binding.apply {
            if (flipView.getCurrentFlipState() == EasyViewFlip.FlipState.FRONT_SIDE && list[position].isFlipped) {
                flipView.setFlipDuration(0)
                flipView.flipTheView()
            } else if (flipView.getCurrentFlipState() == EasyViewFlip.FlipState.BACK_SIDE && !list[position].isFlipped) {
                flipView.setFlipDuration(0)
                flipView.flipTheView()
            }

            flipView.setOnClickListener {
                list[position].isFlipped = !list[position].isFlipped
                flipView.setFlipDuration(700)
                flipView.flipTheView()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}