package com.minikode.summit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.*
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minikode.summit.BaseActivity
import com.minikode.summit.R
import com.minikode.summit.databinding.ActivityMainBinding
import com.minikode.summit.vo.SummitInfoVo
import kotlin.math.*


class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutRes: Int = R.layout.activity_main
    override val requestCode: Int = 100

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun initView() {

        parseJson()

        binding.arrow.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF0000"))

        // 화면 켜진 상태 유지
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        with(binding) {
            val imageView = ImageView(this@MainActivity)
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_launcher_foreground
                )
            )
            val relativeParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            relativeParams.addRule(RelativeLayout.ABOVE, R.id.android)
            relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            imageView.layoutParams = relativeParams
            binding.relativeLayout.addView(imageView)
        }

//        bindGravitySensor()
//        parseJson()
        bindMagnetSensorAndAccelerSensor()
        bindRotationVectorSensor()

    }

    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        var preview: Preview = Preview.Builder().build()

        var cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)


        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        binding.previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

        calculateFov()

    }

    lateinit var magenetSensor: Sensor
    lateinit var accelerSensor: Sensor
    lateinit var rotationSensor: Sensor

    var mLastAccelerometer: FloatArray = FloatArray(3)
    var mLastMagnetometer: FloatArray = FloatArray(3)
    var mLastRotationVector: FloatArray = FloatArray(4)
    var mR: FloatArray = FloatArray(9)
    var mI: FloatArray = FloatArray(9)
    var mOrientation: FloatArray = FloatArray(3)
    var mLastAccelerometerSet = false
    var mLastMagnetometerSet = false
    var mLastRotationSet = false

    fun bindRotationVectorSensor() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
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
        }, rotationSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun bindMagnetSensorAndAccelerSensor() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magenetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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

        }, accelerSensor, SensorManager.SENSOR_DELAY_UI)
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

        }, magenetSensor, SensorManager.SENSOR_DELAY_UI)
    }

    var currentDegree = 0f // 현재 북쪽

    val onSensorChangeEventLambda: (SensorEvent?) -> Unit = {

        it?.let {
            if (it.sensor == accelerSensor) {
                mLastAccelerometer = it.values.clone()
                mLastAccelerometerSet = true
            } else if (it.sensor == magenetSensor) {
                mLastMagnetometer = it.values.clone()
                mLastMagnetometerSet = true
            } else if (it.sensor == rotationSensor) {
                mLastRotationVector = it.values.clone()
                mLastRotationSet = true
            }

            if (mLastAccelerometerSet && mLastMagnetometerSet) {

                var azimuth = 0.0
                var pitch = 0.0
                var roll = 0.0

                SensorManager.getRotationMatrix(mR, mI, mLastAccelerometer, mLastMagnetometer)

                val inclination = SensorManager.getInclination(mI)

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
//                        Log.d(TAG, "ROTATION_0: ")
                    }
                    Surface.ROTATION_90 -> { // 반시계방향 가로
//                        Log.d(TAG, "ROTATION_90: ")
                        azimuth += 90
                    }
                    Surface.ROTATION_270 -> { // 시계방향 가로
//                        Log.d(TAG, "ROTATION_270: ")
                        azimuth -= 90
                    }
                    else -> {
//                        Log.d(TAG, "ROTATION_else: ")
                    }
                }

//                Log.d(TAG, "azimuth: $azimuth")

                if (azimuth < 0) {
                    azimuth += 360
                }


                val rotateAnnotation = RotateAnimation(
                    -azimuth.toFloat(),
                    currentDegree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                )

                rotateAnnotation.duration = 250
                rotateAnnotation.fillAfter = true
                binding.arrow.startAnimation(rotateAnnotation)
                currentDegree = (-azimuth).toFloat()
//                binding.arrow.rotation = (-azimuth).toFloat()
                binding.textViewDegree.text = azimuth.toString()

            }
            if (mLastRotationSet) {
                val rotationMatrix = FloatArray(9)

                SensorManager.getRotationMatrixFromVector(rotationMatrix, mLastRotationVector)

                val axisX = SensorManager.AXIS_X

                val axisZ = SensorManager.AXIS_Z

                val adjustedRotationMatrix = FloatArray(9)

                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    axisX,
                    axisZ,
                    adjustedRotationMatrix
                )

                val orientation = FloatArray(3)

                SensorManager.getOrientation(adjustedRotationMatrix, orientation)

                var azimuch: Float = orientation[0] * 57

                val pitch: Float = orientation[1] * 57

                val roll: Float = orientation[2] * 57

                Log.d(TAG, "mLastRotationSet: azimuch $azimuch")
                Log.d(TAG, "mLastRotationSet: pitch $pitch")
                Log.d(TAG, "mLastRotationSet: roll $roll")
//                if (azimuch < 0) {
//                    azimuch = abs(azimuch)
//                } else {
//                    azimuch = 360 - azimuch
//                }
                if (azimuch < 0) {
                    azimuch += 360
                }
                if (Math.abs(pitch) < 55) {    //눕혀있는 경우에는 화면 상태가 어렵다고 판단되어 특정 각도 내에서만 작동하도록 함.
                    binding.textViewRotationAzimuch.text = azimuch.toString()
                }
                binding.textViewRotationPitch.text = pitch.toString()


            }
        }

    }

//    fun calculateOrientation(roll: Int, pitch: Int) {
//        if((-120 < roll && roll < -60) || (60 < roll && roll <120)){
//
//            return "LANDSCAPE " + roll + "/" + pitch;
//
//        }else if(( -30 < roll && roll < 30) || (150 < roll && roll < 180) || (-180 < roll && roll < -150)){
//
//            return "PORTRAIT " + roll + "/" + pitch;
//
//        }else{
//
//            return "UNKNOWN " + roll + "/" + pitch;
//
//        }
//    }

    fun bindGravitySensor() {
        // 중력센서
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        // 선형 가속도계
//        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)


        val xAxisTextView = TextView(this)
        xAxisTextView.id = View.generateViewId()
        val yAxisTextView = TextView(this)
        yAxisTextView.id = View.generateViewId()
        val zAxisTextView = TextView(this)
        zAxisTextView.id = View.generateViewId()
        val totalTextView = TextView(this)
        totalTextView.id = View.generateViewId()

        val xRelativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        xRelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        xRelativeParams.addRule(RelativeLayout.ABOVE, yAxisTextView.id)

        val yRelativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        yRelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        yRelativeParams.addRule(RelativeLayout.ABOVE, zAxisTextView.id)

        val zRelativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        zRelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        zRelativeParams.addRule(RelativeLayout.ABOVE, totalTextView.id)

        val totalRelativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        totalRelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        totalRelativeParams.addRule(RelativeLayout.ABOVE, R.id.android)

        xAxisTextView.layoutParams = xRelativeParams
        xAxisTextView.setTextColor(resources.getColor(R.color.white))
        yAxisTextView.layoutParams = yRelativeParams
        yAxisTextView.setTextColor(resources.getColor(R.color.white))
        zAxisTextView.layoutParams = zRelativeParams
        zAxisTextView.setTextColor(resources.getColor(R.color.white))
        totalTextView.layoutParams = totalRelativeParams
        totalTextView.setTextColor(resources.getColor(R.color.white))

        binding.relativeLayout.addView(xAxisTextView)
        binding.relativeLayout.addView(yAxisTextView)
        binding.relativeLayout.addView(zAxisTextView)
        binding.relativeLayout.addView(totalTextView)

        sensorManager.registerListener(object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val xValue = it.values[0]
                    val yValue = it.values[1]
                    val zValue = it.values[2]
                    val total = sqrt(
                        (xValue.toDouble().pow(2.0)).plus(
                            (yValue.toDouble().pow(2.0))
                        ).plus((zValue.toDouble().pow(2.0)))
                    )
                    xAxisTextView.text = xValue.toString()
                    yAxisTextView.text = yValue.toString()
                    zAxisTextView.text = zValue.toString()
                    totalTextView.text = total.toString()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "onAccuracyChanged: ")
            }

            override fun onFlushCompleted(sensor: Sensor?) {
                Log.d(TAG, "onFlushCompleted: ")
            }

        }, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var altitude: Double = 0.0
    private var bearing: Float = 0f

    private var latitude2: Double = 0.0
    private var longitude2: Double = 0.0
    private var altitude2: Double = 0.0
    private var bearing2: Float = 0f

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val permissionCamera =
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val permissionWriteExternalStorage =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permissionReadExternalStorage =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)


            val permissionFindLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            val permissionCoarseLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
                cameraProviderFuture = ProcessCameraProvider.getInstance(this)

                cameraProviderFuture.addListener(Runnable {
                    val cameraProvider = cameraProviderFuture.get()
                    bindPreview(cameraProvider)
                }, ContextCompat.getMainExecutor(this))
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

            if (permissionWriteExternalStorage == PackageManager.PERMISSION_GRANTED && permissionReadExternalStorage == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "저장소 읽기/쓰기 권한 없습니다.", Toast.LENGTH_SHORT).show()
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                    ),
//                    requestCode,
//                )
            }

            if (permissionFindLocation == PackageManager.PERMISSION_GRANTED && permissionCoarseLocation == PackageManager.PERMISSION_GRANTED) {
                bindLocation()
                bindLocation2()
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

    val updateLocationLambda: (Location?) -> Unit = { it ->
        it?.let {
            Log.d(TAG, "updateLocationLambda: update latitude ${it.latitude}")
            Log.d(TAG, "updateLocationLambda: update longitude ${it.longitude}")
            Log.d(TAG, "updateLocationLambda: update altitude ${it.altitude}")
            Log.d(TAG, "updateLocationLambda: update bearing ${it.bearing}")
            latitude = it.latitude
            longitude = it.longitude
            altitude = it.altitude
            bearing = it.bearing
            binding.textViewLatitude.text = latitude.toString()
            binding.textViewLongitude.text = longitude.toString()
            binding.textViewAltitude.text = altitude.toString()
            binding.textViewBearing.text = bearing.toString()
        }

    }

    val updateLocationLambda2: (Location?) -> Unit = { it ->
        it?.let {
            Log.d(TAG, "updateLocationLambda2: update latitude ${it.latitude}")
            Log.d(TAG, "updateLocationLambda2: update longitude ${it.longitude}")
            Log.d(TAG, "updateLocationLambda2: update altitude ${it.altitude}")
            Log.d(TAG, "updateLocationLambda2: update bearing ${it.bearing}")
            latitude2 = it.latitude
            longitude2 = it.longitude
            altitude2 = it.altitude
            bearing2 = it.bearing
            binding.textViewLatitude2.text = latitude2.toString()
            binding.textViewLongitude2.text = longitude2.toString()
            binding.textViewAltitude2.text = altitude2.toString()
            binding.textViewBearing2.text = bearing2.toString()
            drawArrow()
        }

    }

    val cancelLocationLambda: () -> Unit = {
        Log.d(TAG, "cancelLocationLambda: 실패@@@!!!")
        Toast.makeText(this, "위치 정보 조회 실패!!!!!", Toast.LENGTH_SHORT).show()
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun bindLocation2() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Log.d(TAG, "location client setting success")
        }

        task.addOnFailureListener {
            Log.d(TAG, "location client setting failure")
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(updateLocationLambda2)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations) {
                        updateLocationLambda2(location)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun bindLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria().apply {
            Criteria.ACCURACY_FINE
            accuracy = Criteria.ACCURACY_FINE
            isAltitudeRequired = true
            isBearingRequired = true
            isSpeedRequired = true
            isCostAllowed = true
            powerRequirement = Criteria.POWER_HIGH
        }
        val bestProvider = locationManager.getBestProvider(criteria, true)

        // 위치 권한 얻음
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let {
            updateLocationLambda(it)
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            1f,
            updateLocationLambda,
        )

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            1000,
            1f,
            updateLocationLambda,
        )

    }

    @SuppressLint("MissingPermission")
    fun callCurrentLocation(
        locationManager: LocationManager, updateLocationLambda: (Location) -> Unit, provider: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val cancellationSignal = CancellationSignal()
                cancellationSignal.setOnCancelListener(cancelLocationLambda)
                locationManager.getCurrentLocation(
                    provider, cancellationSignal, mainExecutor, updateLocationLambda
                )
                Toast.makeText(this, "위치 조회 함!!!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun drawArrow() {
        for (summitInfoVo in summitInfoVoList) {
            val distance = calDist(latitude2, longitude2, summitInfoVo.lati!!, summitInfoVo.longi!!)
            Log.d(TAG, "parseJson: name: ${summitInfoVo.name}")
            Log.d(TAG, "parseJson: distance ${distance}")


            var degree = calBearing(
                latitude2,
                longitude2,
                summitInfoVo.lati!!,
                summitInfoVo.longi!!,
            )
            Log.d(
                TAG,
                "parseJson: 현재위치에서 방향 각도 ${
                    degree
                }"
            )

//            if (degree < 0) {
//                degree += 360
//            }

//            Log.d(TAG, "parseJson: currentDegree $currentDegree")
            Log.d(TAG, "parseJson: -------------------------------------")

            degree += currentDegree // 현재 북쪽으로 각도를 추가

//            if (degree < 0) {
//                degree += 360
//            }

            val rotateAnnotation = RotateAnimation(
                summitInfoVo.computedDegree,
                degree.toFloat(),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
            )

            rotateAnnotation.duration = 250
            rotateAnnotation.fillAfter = true
            summitInfoVo.computedDegree = degree.toFloat()

            val id = resources.getIdentifier(summitInfoVo.id, "id", packageName)
            val imageView = findViewById(id) as ImageView

            imageView.startAnimation(rotateAnnotation)


        }
    }

    private var summitInfoVoList: MutableList<SummitInfoVo> = mutableListOf()

    fun parseJson() {
//        val inputStream = assets.open("data.json")
//        val inputStreamReader = InputStreamReader(inputStream)
//        val bufferedReader = BufferedReader(inputStreamReader)
//        val buffer = StringBuffer()
//        var line = bufferedReader.readLine()
//        while(line != null) {
//            buffer.append(line)
//            line = bufferedReader.readLine()
//        }
//        val jsonData = buffer.toString()
//        Log.d(TAG, "parseJson: jsonData $jsonData")
        val jsonString = assets.open("data.json").reader().readText()
        Log.d(TAG, "parseJson: jsonString $jsonString")
//        val jsonArray = JSONArray(jsonString)
//        Log.d(TAG, "parseJson: jsonArray $jsonArray")

        val gson = Gson()
        summitInfoVoList = gson.fromJson<MutableList<SummitInfoVo>>(
            jsonString,
            object : TypeToken<MutableList<SummitInfoVo>>() {}.type
        )
        Log.d(TAG, "parseJson: list $summitInfoVoList")
    }

    private fun calBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
//        val y = lon2 - lon1
//        val x = lat2 - lat1
//        val radian = Math.atan2(y, x)
//        val degree = radian.times(180).div(Math.PI)
//        return degree

        val pi1 = lat1.times(Math.PI).div(180)
        val pi2 = lat2.times(Math.PI).div(180)
        val lambda1 = lon1.times(Math.PI).div(180)
        val lambda2 = lon2.times(Math.PI).div(180)

        val y = sin(lambda2.minus(lambda1)).times(Math.cos(pi2))
        val x = cos(pi1).times(sin(pi2))
            .minus((sin(pi1).times(cos(pi2).times(cos(lambda2.minus(lambda1))))))

        val theta = atan2(y, x)
        val bearing = (theta.times(180).div(Math.PI).plus(360)).mod(360.0)
        Log.d(TAG, "calBearing: theta $theta")
        return bearing


    }

    private fun calDist(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Long {
        val EARTH_R = 6371000.0
        val rad = Math.PI / 180
        val radLat1 = rad * lat1
        val radLat2 = rad * lat2
        val radDist = rad * (lon1 - lon2)

        var distance = Math.sin(radLat1) * Math.sin(radLat2)
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist)
        val ret = EARTH_R * Math.acos(distance)

        return Math.round(ret) // 미터 단위
    }

    private fun calBear(lat1: Double, lon1: Double, lat2: Double, lon2: Double) {
        // https://tttsss77.tistory.com/177 참고하기
    }

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onPause() {
        super.onPause()
    }


    private var horizonalAngle: Float = 0f
    private var verticalAngle: Float = 0f

    private fun calculateFov() {
        val cManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        for (cameraId in cManager.cameraIdList) {
            val characteristics = cManager.getCameraCharacteristics(cameraId)
            val cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                val maxFocus =
                    characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                val size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
                val w = size?.width
                val h = size?.height
                maxFocus?.let {
                    if (w != null) {
                        horizonalAngle = (2 * atan(w.div(maxFocus[0].times(2))))
                    }
                    if (h != null) {
                        verticalAngle = (2 * atan(h.div(maxFocus[0].times(2))))
                    }

                    Log.d(TAG, "calculateFov: horizonalAngle $horizonalAngle")
                    Log.d(TAG, "calculateFov: verticalAngle $verticalAngle")
                }

            }
        }

    }

}
