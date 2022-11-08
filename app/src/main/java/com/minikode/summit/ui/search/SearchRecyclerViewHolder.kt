package com.minikode.summit.ui.search

import androidx.recyclerview.widget.RecyclerView
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
            linearLayout.setOnClickListener {
                clickEventLambda(item)
            }

        }

    }

}