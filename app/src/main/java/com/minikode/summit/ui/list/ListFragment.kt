package com.minikode.summit.ui.list

import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.activityViewModels
import com.minikode.summit.BaseFragment
import com.minikode.summit.R
import com.minikode.summit.databinding.FragmentListBinding
import com.minikode.summit.ui.MainActivity
import com.minikode.summit.util.Util
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : BaseFragment<FragmentListBinding>() {

    override val layoutRes: Int = R.layout.fragment_list

    private val listViewModel: ListViewModel by activityViewModels()

    override fun initView() {

        val listViewAdapter = ListViewAdapter(
            clickEventLambda = {
                Log.d(TAG, "initView: it ${it.mountainName}")
            }
        )
        binding.recyclerView.adapter = listViewAdapter
        listViewModel.listViewHolderItems.observe(this@ListFragment) {
            Log.d(TAG, "initView: it ${it.size}")
            listViewAdapter.submitList(it)
        }

    }

    fun changeNorthDirection(azimuth: Double) {
        val rotateAnnotation = RotateAnimation(
            -azimuth.toFloat(),
            (activity as MainActivity).northDegree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
        )

        rotateAnnotation.duration = 250
        rotateAnnotation.fillAfter = true
        binding.imageViewNorthDirection.startAnimation(rotateAnnotation)
        (activity as MainActivity).northDegree = (-azimuth).toFloat()
    }

    companion object {
        private const val TAG = "ListFragment"
    }
}