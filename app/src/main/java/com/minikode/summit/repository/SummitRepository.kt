package com.minikode.summit.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minikode.summit.App
import com.minikode.summit.util.Util
import com.minikode.summit.vo.ListViewHolderVo
import com.minikode.summit.vo.SummitInfoVo
import timber.log.Timber

class SummitRepository {

    private lateinit var summitInfoVoList: MutableList<SummitInfoVo>

    init {
        parseDataJson()
    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    fun getListViewHolderVoList(
        latitude: Double,
        longitude: Double,
        azimuth: Double,
    ): MutableList<ListViewHolderVo> {
        val listViewHolderVoList = summitInfoVoList.map {
            val listViewHolderVo = ListViewHolderVo(
                url = it.url,
                mountainName = it.mName,
                summitName = it.sName,
                distance = Util.calDist(latitude, longitude, it.lati, it.longi).div(1000), // km 단위
                degree = Util.calBearing(latitude, longitude, it.lati, it.longi).plus(azimuth),
                oldDegree = Util.calBearing(
                    this@SummitRepository.latitude,
                    this@SummitRepository.longitude,
                    it.lati,
                    it.longi
                ).plus(azimuth),
            )

            Timber.d("degree ${listViewHolderVo.degree} oldDegree ${listViewHolderVo.oldDegree}")
            listViewHolderVo
        }.toMutableList()
        this.latitude = latitude
        this.longitude = longitude

        Timber.d("111 latitude $latitude")
        Timber.d("111 longitude $longitude")

        return listViewHolderVoList
    }

    fun getSummitInfoVoList() = summitInfoVoList

    private fun parseDataJson() {
        val jsonString = App.instance.assets.open("data.json").reader().readText()
        val gson = Gson()
        summitInfoVoList = gson.fromJson(
            jsonString, object : TypeToken<MutableList<SummitInfoVo>>() {}.type
        )
    }
}