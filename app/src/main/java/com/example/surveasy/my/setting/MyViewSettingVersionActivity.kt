package com.example.surveasy.my.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.surveasy.R
import com.example.surveasy.databinding.ActivityMyviewnoticelistBinding
import com.example.surveasy.databinding.ActivityMyviewsettingversionBinding

class MyViewSettingVersionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyviewsettingversionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyviewsettingversionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tool bar
        setSupportActionBar(binding.ToolbarMyViewSettingVersion)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        binding.ToolbarMyViewSettingVersion.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}