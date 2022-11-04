package com.minikode.summit.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.Log
import com.minikode.summit.App

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

    companion object {
        private const val TAG = "SensorRepository"
    }
}