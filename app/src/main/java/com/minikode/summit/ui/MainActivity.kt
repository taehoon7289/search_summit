package com.minikode.summit.ui

import com.minikode.summit.BaseActivity
import com.minikode.summit.R
import com.minikode.summit.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutRes: Int = R.layout.activity_main

    override fun initView() {
        with(binding) {
            textView.text = "ddd gogogo"
        }
    }

}