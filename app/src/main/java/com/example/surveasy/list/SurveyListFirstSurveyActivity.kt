package com.example.surveasy.list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import com.example.surveasy.R
import com.example.surveasy.databinding.ActivitySurveylistfirstsurveyBinding
import com.example.surveasy.login.CurrentUser
import com.example.surveasy.login.CurrentUserViewModel

class SurveyListFirstSurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveylistfirstsurveyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userModel by viewModels<CurrentUserViewModel>()

        // Current User from MainActivity
        val intent_main: Intent = intent
        val currentUser = intent_main.getParcelableExtra<CurrentUser>("currentUser")
        userModel.currentUser = currentUser!!


        binding = ActivitySurveylistfirstsurveyBinding.inflate(layoutInflater)

        setContentView(binding.root)


        setSupportActionBar(binding.ToolbarFirstSurvey)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.ToolbarFirstSurvey.setNavigationOnClickListener {
            onBackPressed()
        }

        val transaction = supportFragmentManager.beginTransaction()
        setContentView(binding.root)
        transaction.add(R.id.SurveyListFirstSurvey_view,SurveyListFirstSurvey1Fragment()).commit()
    }

    fun next() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.SurveyListFirstSurvey_view, SurveyListFirstSurvey2Fragment())
            .commit()
    }

}