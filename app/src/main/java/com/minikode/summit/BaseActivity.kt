package com.minikode.summit

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<View : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var binding: View
    protected abstract val layoutRes: Int
    protected abstract val requestCode: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding(layoutRes)
        initView()
    }

    private fun createBinding(@LayoutRes layoutRes: Int): View {
        binding = DataBindingUtil.setContentView(this, layoutRes)
        with(binding) {
            lifecycleOwner = this@BaseActivity
        }
        return binding
    }

    abstract fun initView()

}