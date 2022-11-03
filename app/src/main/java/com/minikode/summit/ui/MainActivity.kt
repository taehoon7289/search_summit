package com.minikode.summit.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.minikode.summit.App
import com.minikode.summit.BaseActivity
import com.minikode.summit.R
import com.minikode.summit.databinding.ActivityMainBinding
import com.minikode.summit.repository.AzimuthRepository
import com.minikode.summit.ui.list.ListViewModel
import com.minikode.summit.util.Util
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutRes: Int = R.layout.activity_main
    override val requestCode: Int = 100

    private val listViewModel: ListViewModel by viewModels()

    var northDegree = 0f // 현재 북쪽 방향각

    override fun initView() {

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

//        bindMagneticSensorAndAccelerometerSensor()
    }

    lateinit var magneticSensor: Sensor
    lateinit var accelerometerSensor: Sensor

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

    var mLastAccelerometer: FloatArray = FloatArray(3)
    var mLastMagnetometer: FloatArray = FloatArray(3)
    var mR: FloatArray = FloatArray(9)
    var mI: FloatArray = FloatArray(9)
    var mOrientation: FloatArray = FloatArray(3)
    var mLastAccelerometerSet = false
    var mLastMagnetometerSet = false

    val onSensorChangeEventLambda: (SensorEvent?) -> Unit = {

        it?.let {
            if (it.sensor == accelerometerSensor) {
                mLastAccelerometer = it.values.clone()
                mLastAccelerometerSet = true
            } else if (it.sensor == magneticSensor) {
                mLastMagnetometer = it.values.clone()
                mLastMagnetometerSet = true
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {

                var azimuth = 0.0
                var pitch = 0.0
                var roll = 0.0

                SensorManager.getRotationMatrix(mR, mI, mLastAccelerometer, mLastMagnetometer)
                SensorManager.getInclination(mI)
                SensorManager.getOrientation(mR, mOrientation)

                azimuth = Math.toDegrees(mOrientation[0].toDouble()) // 방위값(-180 ~ +180)
                pitch = Math.toDegrees(mOrientation[1].toDouble())
                roll = Math.toDegrees(mOrientation[2].toDouble())

                val rotate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    display?.rotation
                } else {
                    windowManager.defaultDisplay.rotation
                }

                when (rotate) {
                    Surface.ROTATION_0 -> {
                    }
                    Surface.ROTATION_90 -> { // 반시계방향 가로
                        azimuth += 90
                    }
                    Surface.ROTATION_270 -> { // 시계방향 가로
                        azimuth -= 90
                    }
                    else -> {}
                }

                if (azimuth < 0) {
                    azimuth += 360
                }
                northDegree = (-azimuth).toFloat()

            }
        }
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val successLocationLambda: (Location?) -> Unit = {
        it?.let {
            Log.d(TAG, "it latitude: ${it.latitude}")
            Log.d(TAG, "it longitude: ${it.longitude}")
            listViewModel.reload(it.latitude, it.longitude)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations) {
                successLocationLambda(location)
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val permissionCamera =
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

            val permissionFindLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            val permissionCoarseLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
//                cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//                cameraProviderFuture.addListener(Runnable {
//                    val cameraProvider = cameraProviderFuture.get()
//                    bindPreview(cameraProvider)
//                }, ContextCompat.getMainExecutor(this))
            } else {
                Toast.makeText(this, "카메라 권한 없습니다.", Toast.LENGTH_SHORT).show()
//                val flag = ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.CAMERA
//                )
//                if (!flag) {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(
//                            Manifest.permission.CAMERA,
//                        ),
//                        requestCode,
//                    )
//                } else {
//
//                }
            }

            if (permissionFindLocation == PackageManager.PERMISSION_GRANTED && permissionCoarseLocation == PackageManager.PERMISSION_GRANTED) {
//                bindLocation()
//                bindLocation2()
                fusedLocationProviderClient = Util.getLocation(
                    locationCallback = locationCallback,
                    successLocationLambda = successLocationLambda
                )
            } else {
                Toast.makeText(this, "위치정보 권한 없습니다.", Toast.LENGTH_SHORT).show()
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                    ),
//                    requestCode,
//                )
            }
        }

//    fun drawArrow() {
//        for (listViewHolderVo in listViewModel.listViewHolderItems) {
//            val distance = calDist(latitude2, longitude2, summitInfoVo.lati!!, summitInfoVo.longi!!)
//            Log.d(Companion.TAG, "parseJson: name: ${summitInfoVo.name}")
//            Log.d(Companion.TAG, "parseJson: distance ${distance}")
//
//
//            var degree = calBearing(
//                latitude2,
//                longitude2,
//                summitInfoVo.lati!!,
//                summitInfoVo.longi!!,
//            )
//            Log.d(
//                Companion.TAG,
//                "parseJson: 현재위치에서 방향 각도 ${
//                    degree
//                }"
//            )
//
////            if (degree < 0) {
////                degree += 360
////            }
//
////            Log.d(TAG, "parseJson: currentDegree $currentDegree")
//            Log.d(Companion.TAG, "parseJson: -------------------------------------")
//
//            degree += currentDegree // 현재 북쪽으로 각도를 추가
//
////            if (degree < 0) {
////                degree += 360
////            }
//
//            val rotateAnnotation = RotateAnimation(
//                summitInfoVo.computedDegree,
//                degree.toFloat(),
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//            )
//
//            rotateAnnotation.duration = 250
//            rotateAnnotation.fillAfter = true
//            summitInfoVo.computedDegree = degree.toFloat()
//
//            val id = resources.getIdentifier(summitInfoVo.id, "id", packageName)
//            val imageView = findViewById(id) as ImageView
//
//            imageView.startAnimation(rotateAnnotation)
//
//
//        }
//}

    override fun onStart() {
        super.onStart()
        fusedLocationProviderClient?.let {
            fusedLocationProviderClient = Util.getLocation(
                locationCallback = locationCallback,
                successLocationLambda = successLocationLambda
            )
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
