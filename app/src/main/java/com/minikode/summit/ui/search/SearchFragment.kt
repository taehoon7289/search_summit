package com.minikode.summit.ui.search

import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.activityViewModels
import com.minikode.summit.BaseFragment
import com.minikode.summit.R
import com.minikode.summit.databinding.FragmentListBinding
import com.minikode.summit.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentListBinding>() {

    override val layoutRes: Int = R.layout.fragment_list

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun initView() {

        val searchRecyclerAdapter = SearchRecyclerAdapter(
            clickEventLambda = {
                Log.d(TAG, "initView: it ${it.mountainName}")
            }
        )
        binding.recyclerView.adapter = searchRecyclerAdapter
        searchViewModel.listViewHolderItems.observe(this@SearchFragment) {
            Log.d(TAG, "initView: it ${it.size}")
            searchRecyclerAdapter.submitList(it)
        }

        searchViewModel.azimuth.observe(this@SearchFragment) {
            Log.d(TAG, "initView: azimuth $it")
        }

        searchViewModel.pitch.observe(this@SearchFragment) {
//            Log.d(TAG, "initView: pitch $it")
        }

        searchViewModel.roll.observe(this@SearchFragment) {
//            Log.d(TAG, "initView: roll $it")
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