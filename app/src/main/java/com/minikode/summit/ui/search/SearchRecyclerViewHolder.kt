package com.minikode.summit.ui.search

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.recyclerview.widget.RecyclerView
import com.minikode.summit.databinding.ViewholderSearchBinding
import com.minikode.summit.vo.ListViewHolderVo
import timber.log.Timber
import java.util.*

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
//            Handler(Looper.getMainLooper()).postDelayed({
//                Timber.d("invalidate@@@")
//
//            }, 1000)


//            val handler = object : Handler(Looper.getMainLooper()) {
//                override fun handleMessage(msg: Message) {
//                    textViewDegree.requestLayout()
//                    textViewDistance.requestLayout()
//                }
//            }
//
//            val timer = Timer()
//
//            val timerTask = object : TimerTask() {
//                override fun run() {
//                    Timber.d("run: timerTask!!")
//                    cancel()
//                    handler.sendMessage(handler.obtainMessage())
//                }
//            }
//            timer.schedule(timerTask, 1000)


        }

    }

}