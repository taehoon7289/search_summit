package com.minikode.summit.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.minikode.summit.App
import com.minikode.summit.BaseActivity
import com.minikode.summit.R
import com.minikode.summit.databinding.ActivityMainBinding
import com.minikode.summit.ui.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutRes: Int = R.layout.activity_main
    override val requestCode: Int = 100
    override val permissionArray = arrayOf(
//        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )


    private val searchViewModel: SearchViewModel by viewModels()

    var permissionCamera = PackageManager.PERMISSION_DENIED
    var permissionFindLocation = PackageManager.PERMISSION_DENIED
    var permissionCoarseLocation = PackageManager.PERMISSION_DENIED

    override fun initPermissions() {
        permissionLauncher.launch(permissionArray)
    }

    override fun initView() {}

    /**
     * 앱실행시 필요한 권한 체크
     */
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res ->

//            permissionCamera =
//                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

            permissionFindLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            permissionCoarseLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )

//            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
//                // 카메라 on
////                cameraProviderFuture = ProcessCameraProvider.getInstance(this)
////
////                cameraProviderFuture.addListener(Runnable {
////                    val cameraProvider = cameraProviderFuture.get()
////                    bindPreview(cameraProvider)
////                }, ContextCompat.getMainExecutor(this))
//                val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
//                } else false
//                if (flag) {
//                    // 1번 거절
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(
//                            Manifest.permission.CAMERA,
//                        ),
//                        requestCode,
//                    )
//                    Toast.makeText(this, "카메라 권한 없습니다.", Toast.LENGTH_SHORT).show()
//                } else {
//                    // 2번 이상 거절
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    intent.data = Uri.parse("package:$packageName")
//                    startActivityLauncher(intent)
//                    Toast.makeText(this, "카메라 권한 허용해주세요.", Toast.LENGTH_SHORT).show()
//                }
//            }
            if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                } else false

                if (flag) {
                    // 1번 거절
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, // 정밀
                        ),
                        requestCode,
                    )
                    Toast.makeText(this, "위치정보 권한 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 2번 이상 거절
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityLauncher(intent)
                    Toast.makeText(this, "위치정보 권한 허용해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                searchViewModel.initFusedLocationProviderClient()
            }
        }

}
