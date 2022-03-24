package com.app.surveasy.list.firstsurvey

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.app.surveasy.MainActivity
import com.app.surveasy.databinding.ActivitySurveyListFirstSurveyLastBinding

class SurveyListFirstSurveyLast : AppCompatActivity() {
    lateinit var binding : ActivitySurveyListFirstSurveyLastBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySurveyListFirstSurveyLastBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.ToolbarFirstSurveyLast)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.ToolbarFirstSurveyLast.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.SurveyListFirstSurveyLastBtn.setOnClickListener {
            finish()
            val intent : Intent = Intent(this,MainActivity::class.java)
            intent.putExtra("defaultFragment_list", true)
            startActivity(intent)

        }

        val spannableString = SpannableString(binding.colorRedText.text)
        spannableString.setSpan(ForegroundColorSpan(Color.RED),64,79, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.colorRedText.text = spannableString

        val spannableString2 = SpannableString(binding.colorGreenText.text)
        spannableString2.setSpan(ForegroundColorSpan(Color.parseColor("#4da830")),18,20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.colorGreenText.text = spannableString2
    }
}