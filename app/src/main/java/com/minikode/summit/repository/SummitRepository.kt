package com.minikode.summit.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minikode.summit.App
import com.minikode.summit.util.Util
import com.minikode.summit.vo.ListViewHolderVo
import com.minikode.summit.vo.SummitInfoVo

class SummitRepository {

    private lateinit var summitInfoVoList: MutableList<SummitInfoVo>

    init {
        parseDataJson()
    }

    fun getListViewHolderVoList(
        latitude: Double,
        longitude: Double
    ): MutableList<ListViewHolderVo> {
        return summitInfoVoList.map {
            val listViewHolderVo = ListViewHolderVo(
                mountainName = it.mName,
                summitName = it.sName,
                distance = Util.calDist(latitude, longitude, it.lati, it.longi).div(1000), // km 단위
                degree = Util.calBearing(latitude, longitude, it.lati, it.longi)
            )
            listViewHolderVo
        }.toMutableList()
    }

    fun getSummitInfoVoList(): MutableList<SummitInfoVo> = summitInfoVoList

    private fun parseDataJson() {
        val jsonString = App.instance.assets.open("data.json").reader().readText()
        val gson = Gson()
        summitInfoVoList = gson.fromJson(
            jsonString,
            object : TypeToken<MutableList<SummitInfoVo>>() {}.type
        )
    }

    companion object {
        private const val TAG = "SummitRepository"
    }
}