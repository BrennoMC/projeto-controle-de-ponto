package com.example.controledeponto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeponto.databinding.ListItemPointBinding

class PointAdapter : ListAdapter<Point, PointAdapter.PointViewHolder>(PointDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPointBinding.inflate(inflater, parent, false)
        return PointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = getItem(position)
        holder.bind(point)
    }

    inner class PointViewHolder(private val binding: ListItemPointBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(point: Point) {
            binding.textView.text =
                point.date.toString() // Aqui você pode exibir os dados do ponto conforme necessário
        }
    }
}

class PointDiffCallback : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem == newItem
    }
}
