package com.minikode.summit.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.minikode.summit.databinding.ViewholderSearchBinding
import com.minikode.summit.vo.ListViewHolderVo
import timber.log.Timber

class SearchRecyclerAdapter(
    val clickEventLambda: (ListViewHolderVo) -> Unit,
) : ListAdapter<ListViewHolderVo, SearchRecyclerViewHolder>(object :
    DiffUtil.ItemCallback<ListViewHolderVo>() {
    override fun areItemsTheSame(
        oldItem: ListViewHolderVo, newItem: ListViewHolderVo
    ): Boolean {
        return oldItem.mountainName == newItem.mountainName
    }

    override fun areContentsTheSame(
        oldItem: ListViewHolderVo, newItem: ListViewHolderVo
    ): Boolean {

//        return oldItem.summitName == newItem.summitName

        val condition0 = oldItem.distance == newItem.distance
        val condition1 = oldItem.degree == newItem.degree
        return condition0 && condition1
    }
}) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SearchRecyclerViewHolder {
        val viewholderSummitBinding = ViewholderSearchBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return SearchRecyclerViewHolder(
            binding = viewholderSummitBinding,
            clickEventLambda = clickEventLambda,
        )
    }

    override fun onBindViewHolder(viewHolder: SearchRecyclerViewHolder, position: Int) {
        viewHolder.bind(getItem(position), position)
        viewHolder.binding.executePendingBindings()
    }

}
