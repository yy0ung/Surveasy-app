package com.example.surveasy.list

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.surveasy.databinding.ActivitySurveyprooflastdialogBinding
import com.example.surveasy.login.CurrentUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SurveyProofLastDialogActivity : AppCompatActivity() {
    val db = Firebase.firestore


    private lateinit var binding: ActivitySurveyprooflastdialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySurveyprooflastdialogBinding.inflate(layoutInflater)

        setContentView(binding.root)



        setSupportActionBar(binding.ToolbarSurveyListDetailResponseProof)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.ToolbarSurveyListDetailResponseProof.setNavigationOnClickListener {
            onBackPressed()
        }

        val thisSurveyInfo = intent.getParcelableArrayListExtra<UserSurveyItem>("list")!!

        val list = hashMapOf(
            "reward" to thisSurveyInfo.get(0).reward,
            "id" to thisSurveyInfo.get(0).id,
            "responseDate" to thisSurveyInfo.get(0).responseDate,
            "isSent" to thisSurveyInfo.get(0).isSent

        )

        binding.SurveyListDetailResponseProofBtn.setOnClickListener {

            if (Firebase.auth.currentUser!!.uid != null) {
                db.collection("AndroidUser").document(Firebase.auth.currentUser!!.uid)
                    .collection("UserSurveyList").document("00")
                    .set(list).addOnSuccessListener {
                        Toast.makeText(this,"#####info save success", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        Toast.makeText(this,"#####failed", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this,"#####user null", Toast.LENGTH_LONG).show()
            }
        }



    }


}

