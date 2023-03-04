package com.surveasy.surveasy

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.surveasy.surveasy.login.CurrentUser
import com.surveasy.surveasy.model.ContributionModel
import com.surveasy.surveasy.model.OpinionAModel
import com.surveasy.surveasy.model.OpinionQModel
import com.surveasy.surveasy.model.SurveyModel
import kotlinx.coroutines.flow.Flow

interface MainRepositoryInterface {

    //fetchCurrentUser
    suspend fun fetchCurrentUser(uid : String, model : MutableLiveData<CurrentUser>)
    suspend fun fetchSurvey(model : MutableLiveData<ArrayList<SurveyModel>>, userAge: Int, userGender: String)
    suspend fun fetchSurveyForHome(model : MutableLiveData<ArrayList<SurveyModel>>, userAge: Int, userGender: String, uid : String)
    fun setAmplitude(model : CurrentUser)
    suspend fun fetchBannerImg(model : MutableLiveData<ArrayList<String>>)
    suspend fun fetchContribution(model : MutableLiveData<ArrayList<ContributionModel>>)
    suspend fun fetchOpinion(model1 : MutableLiveData<OpinionQModel>, model2 : MutableLiveData<List<OpinionAModel>>)

}