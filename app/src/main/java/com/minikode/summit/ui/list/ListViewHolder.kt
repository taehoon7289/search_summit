package com.minikode.summit.ui.list

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.minikode.summit.databinding.ViewholderListBinding
import com.minikode.summit.vo.ListViewHolderVo

class ListViewHolder(
    val binding: ViewholderListBinding,
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

    companion object {
        private const val TAG = "ListViewHolder"
    }


}