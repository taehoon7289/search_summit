package com.minikode.summit.vo

data class SummitInfoVo(
    var id: String? = null,
    var name: String? = null,
    var lati: Double? = null,
    var longi: Double? = null,
    var computedDegree: Float = 0f
)