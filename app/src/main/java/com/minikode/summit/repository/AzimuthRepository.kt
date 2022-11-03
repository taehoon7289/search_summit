package com.minikode.summit.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.Log
import com.minikode.summit.App

class AzimuthRepository {

    lateinit var magneticSensor: Sensor
    lateinit var accelerometerSensor: Sensor

//    var mLastAccelerometer: FloatArray = FloatArray(3)
//    var mLastMagnetometer: FloatArray = FloatArray(3)
//    var mR: FloatArray = FloatArray(9)
//    var mI: FloatArray = FloatArray(9)
//    var mOrientation: FloatArray = FloatArray(3)
//    var mLastAccelerometerSet = false
//    var mLastMagnetometerSet = false

    fun bindMagneticSensorAndAccelerometerSensor(onSensorChangeEventLambda: (SensorEvent?) -> Unit) {
        val sensorManager = App.instance.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                onSensorChangeEventLambda(event)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "onAccuracyChanged: ")
            }

            override fun onFlushCompleted(sensor: Sensor?) {
                Log.d(TAG, "onFlushCompleted: ")
            }

        }, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                onSensorChangeEventLambda(event)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "onAccuracyChanged: ")
            }

            override fun onFlushCompleted(sensor: Sensor?) {
                Log.d(TAG, "onFlushCompleted: ")
            }

        }, magneticSensor, SensorManager.SENSOR_DELAY_UI)
    }

//    val onSensorChangeEventLambda: (SensorEvent?) -> Unit = {
//
//        it?.let {
//            if (it.sensor == accelerometerSensor) {
//                mLastAccelerometer = it.values.clone()
//                mLastAccelerometerSet = true
//            } else if (it.sensor == magneticSensor) {
//                mLastMagnetometer = it.values.clone()
//                mLastMagnetometerSet = true
//            }
//            if (mLastAccelerometerSet && mLastMagnetometerSet) {
//
//                var azimuth = 0.0
//                var pitch = 0.0
//                var roll = 0.0
//
//                SensorManager.getRotationMatrix(mR, mI, mLastAccelerometer, mLastMagnetometer)
//                SensorManager.getInclination(mI)
//                SensorManager.getOrientation(mR, mOrientation)
//
//                azimuth = Math.toDegrees(mOrientation[0].toDouble()) // 방위값(-180 ~ +180)
//                pitch = Math.toDegrees(mOrientation[1].toDouble())
//                roll = Math.toDegrees(mOrientation[2].toDouble())
//
//                val rotate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    display?.rotation
//                } else {
//                    windowManager.defaultDisplay.rotation
//                }
//
//                when (rotate) {
//                    Surface.ROTATION_0 -> {
//                    }
//                    Surface.ROTATION_90 -> { // 반시계방향 가로
//                        azimuth += 90
//                    }
//                    Surface.ROTATION_270 -> { // 시계방향 가로
//                        azimuth -= 90
//                    }
//                    else -> {}
//                }
//
//                if (azimuth < 0) {
//                    azimuth += 360
//                }
//                northDegree = (-azimuth).toFloat()
//
//            }
//        }
//    }

    companion object {
        private const val TAG = "AzimuthRepository"
    }
}