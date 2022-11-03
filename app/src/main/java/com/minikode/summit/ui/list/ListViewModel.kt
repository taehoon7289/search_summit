package com.minikode.summit.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minikode.summit.repository.SummitRepository
import com.minikode.summit.vo.ListViewHolderVo
import com.minikode.summit.vo.SummitInfoVo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val summitRepository: SummitRepository) :
    ViewModel() {

    private val _azimuth: MutableLiveData<Double> = MutableLiveData(0.0)

    private val azimuth: LiveData<Double>
        get() = _azimuth

    private val _listViewHolderItems: MutableLiveData<MutableList<ListViewHolderVo>> =
        MutableLiveData(
            mutableListOf()
        )
    val listViewHolderItems: LiveData<MutableList<ListViewHolderVo>>
        get() = _listViewHolderItems

    private val _summitInfoItems: MutableLiveData<MutableList<SummitInfoVo>> = MutableLiveData(
        summitRepository.getSummitInfoVoList()
    )

    val summitInfoItems: LiveData<MutableList<SummitInfoVo>>
        get() = _summitInfoItems

    fun reload(latitude: Double, longitude: Double) {
        _listViewHolderItems.value = summitRepository.getListViewHolderVoList(latitude, longitude)
    }

}