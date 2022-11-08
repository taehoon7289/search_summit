package com.minikode.summit.ui.search

import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.activityViewModels
import com.minikode.summit.BaseFragment
import com.minikode.summit.R
import com.minikode.summit.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val layoutRes: Int = R.layout.fragment_search

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun initView() {

        val searchRecyclerAdapter = SearchRecyclerAdapter(
            clickEventLambda = {
                Timber.d("initView: it ${it.mountainName}")
            }
        )
        binding.recyclerView.adapter = searchRecyclerAdapter
        searchViewModel.listViewHolderItems.observe(this@SearchFragment) {
            Timber.d("initView: it ${it.size}")
            Timber.d("initView: it@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ${it.size}")
            searchRecyclerAdapter.submitList(it)
        }

        binding.model = searchViewModel

        searchViewModel.azimuth.observe(this@SearchFragment) {
            changeNorthDirection(it)
        }

        searchViewModel.pitch.observe(this@SearchFragment) {
//            Timber.d("initView: pitch $it")
        }

        searchViewModel.roll.observe(this@SearchFragment) {
//            Timber.d("initView: roll $it")
        }

        searchViewModel.location.observe(this@SearchFragment) {
            it?.let {
                Timber.d("initView: computeListViewHolderVoList $it")
                searchViewModel.computeListViewHolderVoList(it.latitude, it.longitude)
            }
        }

    }

    var northDegree: Float = 0f

    private fun changeNorthDirection(azimuth: Double) {
        val rotateAnnotation = RotateAnimation(
            -azimuth.toFloat(),
            northDegree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
        )

        rotateAnnotation.duration = 1000
        rotateAnnotation.fillAfter = true
        binding.imageViewNorthDirection.startAnimation(rotateAnnotation)
        northDegree = (-azimuth).toFloat()
    }

//    override fun onPause() {
//        super.onPause()
//        searchViewModel.stopLocation()
//    }
}