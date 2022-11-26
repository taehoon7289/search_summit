package com.minikode.summit.util

import kotlin.math.*


class Util {

    companion object {

        fun calBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

            val pi1 = lat1.times(Math.PI).div(180)
            val pi2 = lat2.times(Math.PI).div(180)
            val lambda1 = lon1.times(Math.PI).div(180)
            val lambda2 = lon2.times(Math.PI).div(180)

            val y = sin(lambda2.minus(lambda1)).times(cos(pi2))
            val x = cos(pi1).times(sin(pi2))
                .minus((sin(pi1).times(cos(pi2).times(cos(lambda2.minus(lambda1))))))

            val theta = atan2(y, x)
            val bearing = (theta.times(180).div(Math.PI).plus(360)).mod(360.0)
//            Timber.d("calBearing: theta $theta")
//            Timber.d("calBearing: bearing $bearing")
            return bearing


        }

        fun calDist(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val EARTH_R = 6371000.0
            val rad = Math.PI / 180
            val radLat1 = rad * lat1
            val radLat2 = rad * lat2
            val radDist = rad * (lon1 - lon2)

            var distance = Math.sin(radLat1) * Math.sin(radLat2)
            distance += Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist)
            val ret = EARTH_R * Math.acos(distance)

            return ret.roundToLong().toDouble() // 미터 단위
        }

        fun getColorWithAlpha(alpha: Float, baseColor: Int): Int {
            val a = min(255, max(0, (alpha * 255).toInt())) shl 24
            val rgb = 0x00ffffff and baseColor
            return a.plus(rgb)
        }

    }
}