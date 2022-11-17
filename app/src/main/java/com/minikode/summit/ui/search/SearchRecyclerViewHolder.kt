package com.minikode.summit.ui.search

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.minikode.summit.App
import com.minikode.summit.R
import com.minikode.summit.databinding.ViewholderSearchBinding
import com.minikode.summit.vo.ListViewHolderVo

class SearchRecyclerViewHolder(
    val binding: ViewholderSearchBinding,
    val clickEventLambda: (ListViewHolderVo) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ListViewHolderVo, position: Int) {
        with(binding) {
            model = item
            constraintLayout.setOnClickListener {
                clickEventLambda(item)
            }
            imageViewMountainDirection.rotation = item.degree.toFloat()
            imageViewMountainDirection.setColorFilter(
                ContextCompat.getColor(
                    App.instance,
                    R.color.green_600
                )
            )

        }

    }

}