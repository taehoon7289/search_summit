package com.minikode.summit.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.minikode.summit.databinding.ViewholderListBinding
import com.minikode.summit.vo.ListViewHolderVo

class ListViewAdapter(
    val clickEventLambda: (ListViewHolderVo) -> Unit,
) :
    ListAdapter<ListViewHolderVo, ListViewHolder>(object :
        DiffUtil.ItemCallback<ListViewHolderVo>() {
        override fun areItemsTheSame(
            oldItem: ListViewHolderVo,
            newItem: ListViewHolderVo
        ): Boolean {
            return oldItem.mountainName == newItem.mountainName
        }

        override fun areContentsTheSame(
            oldItem: ListViewHolderVo,
            newItem: ListViewHolderVo
        ): Boolean {
            return oldItem.distance == newItem.distance
        }
    }) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val viewholderSummitBinding =
            ViewholderListBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        return ListViewHolder(
            binding = viewholderSummitBinding,
            clickEventLambda = clickEventLambda,
        )

    }

    override fun onBindViewHolder(viewHolder: ListViewHolder, position: Int) {
        viewHolder.bind(getItem(position), position)
        viewHolder.binding.executePendingBindings()

    }

    companion object {
        private const val TAG = "ListViewAdapter"
    }

}
