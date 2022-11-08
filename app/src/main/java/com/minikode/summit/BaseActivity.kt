package com.minikode.summit

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<View : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var binding: View
    protected abstract val layoutRes: Int
    protected abstract val requestCode: Int
    protected abstract val permissionArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding(layoutRes)
        initPermissions()
        initView()
    }

    private fun createBinding(@LayoutRes layoutRes: Int): View {
        binding = DataBindingUtil.setContentView(this, layoutRes)
        with(binding) {
            lifecycleOwner = this@BaseActivity
        }
        return binding
    }

    abstract fun initPermissions()
    abstract fun initView()
    protected fun startActivityLauncher(intent: Intent, callback: (ActivityResult) -> Unit = {}) {
        val startActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                callback(res)
            }
        startActivityLauncher.launch(intent)
    }

}