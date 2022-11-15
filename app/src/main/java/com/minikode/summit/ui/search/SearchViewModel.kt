package com.minikode.summit.ui.search

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.minikode.summit.repository.LocationRepository
import com.minikode.summit.repository.SensorRepository
import com.minikode.summit.repository.SummitRepository
import com.minikode.summit.vo.ListViewHolderVo
import com.minikode.summit.vo.SummitInfoVo
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val summitRepository: SummitRepository,
    private val sensorRepository: SensorRepository,
    private val locationRepository: LocationRepository,
) :
    ViewModel() {

    private var mLastAccelerometer: FloatArray = FloatArray(3)
    private var mLastMagnetometer: FloatArray = FloatArray(3)
    private var mR: FloatArray = FloatArray(9)
    private var mI: FloatArray = FloatArray(9)

    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false

    private val onSensorChangeEventLambda: (SensorEvent?) -> Unit = {

        it?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                mLastAccelerometer = it.values.clone()
                mLastAccelerometerSet = true
            } else if (it.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mLastMagnetometer = it.values.clone()
                mLastMagnetometerSet = true
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                if (SensorManager.getRotationMatrix(
                        mR,
                        mI,
                        mLastAccelerometer,
                        mLastMagnetometer
                    )
                ) {
                    val mOrientation = FloatArray(3)
                    SensorManager.getOrientation(mR, mOrientation)
                    _azimuth.value = Math.toDegrees(mOrientation[0].toDouble()) // 방위값(-180 ~ +180)
                    _pitch.value = Math.toDegrees(mOrientation[1].toDouble())
                    _roll.value = Math.toDegrees(mOrientation[2].toDouble())
                }
            }
        }
    }

    private val successLocationLambda: (Location?) -> Unit = {
        it?.let {
            Timber.d("it latitude: ${it.latitude}")
            Timber.d("it longitude: ${it.longitude}")
            _location.value = it
        }
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient

    init {
        sensorRepository.bindMagneticSensorAndAccelerometerSensor(onSensorChangeEventLambda)
        fusedLocationProviderClient =
            locationRepository.getFusedLocationProviderClient(successLocationLambda)
    }

    private val _azimuth: MutableLiveData<Double> =
        MutableLiveData(0.0)
    val azimuth: LiveData<Double>
        get() = _azimuth

    private val _pitch: MutableLiveData<Double> =
        MutableLiveData(0.0)
    val pitch: LiveData<Double>
        get() = _pitch

    private val _roll: MutableLiveData<Double> =
        MutableLiveData(0.0)
    val roll: LiveData<Double>
        get() = _roll


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

//    fun computeListViewHolderVoList(latitude: Double, longitude: Double) {
//        _listViewHolderItems.value = summitRepository.getListViewHolderVoList(latitude, longitude)
//    }

    fun stopLocation() {
        locationRepository.removeLocationUpdate(fusedLocationProviderClient)
    }

    val location: LiveData<Location>
        get() = _location

    private val _location: MutableLiveData<Location> = MutableLiveData(null)

    var isLoading = false


    val maxDistance: LiveData<Double>
        get() = _maxDistance

    private val _maxDistance: MutableLiveData<Double> = MutableLiveData(20.0)

    fun computeListViewHolderVoList() {
        location.value?.let {
            if (!isLoading) {
                isLoading = true
                Handler(Looper.getMainLooper()).postDelayed({
                    _listViewHolderItems.value =
                        summitRepository.getListViewHolderVoList(
                            it.latitude,
                            it.longitude,
                            azimuth.value!!
                        ).filter {
                            it.distance <= maxDistance.value!!
                        }.sortedBy { it.distance }.toMutableList()
                    isLoading = false
                }, 1000)
            }

        }
    }

}