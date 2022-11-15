package com.minikode.summit.vo

data class ListViewHolderVo(
    var url: String? = null,
    var mountainName: String? = null,
    var summitName: String? = null,
    var distance: Double = 0.0,
    var degree: Double = 0.0,
    var oldDegree: Double = 0.0,
)