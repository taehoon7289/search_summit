package com.minikode.summit.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.Log
import com.minikode.summit.App
import timber.log.Timber

class SensorRepository {


    private lateinit var magneticSensor: Sensor
    private lateinit var accelerometerSensor: Sensor

    fun bindMagneticSensorAndAccelerometerSensor(onSensorChangeEventLambda: (SensorEvent?) -> Unit) {
        val sensorManager = App.instance.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                onSensorChangeEventLambda(event)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Timber.d("onAccuracyChanged: ")
            }

            override fun onFlushCompleted(sensor: Sensor?) {
                Timber.d("onFlushCompleted: ")
            }

        }, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                onSensorChangeEventLambda(event)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Timber.d("onAccuracyChanged: ")
            }

            override fun onFlushCompleted(sensor: Sensor?) {
                Timber.d("onFlushCompleted: ")
            }

        }, magneticSensor, SensorManager.SENSOR_DELAY_UI)
    }

}