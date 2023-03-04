package com.surveasy.surveasy.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

import com.surveasy.surveasy.list.*
import com.surveasy.surveasy.login.*
import com.surveasy.surveasy.home.Opinion.HomeOpinionDetailActivity
import com.surveasy.surveasy.home.Opinion.HomeOpinionViewModel
import com.surveasy.surveasy.home.banner.BannerViewModel
import com.surveasy.surveasy.home.banner.BannerViewPagerAdapter
import com.surveasy.surveasy.home.contribution.ContributionItemsAdapter
import com.surveasy.surveasy.home.contribution.HomeContributionViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.surveasy.surveasy.*
import com.surveasy.surveasy.databinding.FragmentHomeBinding
import com.surveasy.surveasy.home.Opinion.HomeOpinionAnswerActivity
import com.surveasy.surveasy.home.Opinion.HomeOpinionAnswerViewModel
import com.surveasy.surveasy.model.SurveyModel
import com.surveasy.surveasy.my.history.MyViewHistoryActivity
import kotlinx.coroutines.*
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    val db = Firebase.firestore
    val storage = Firebase.storage
    val userList = arrayListOf<UserSurveyItem>()
    private lateinit var bannerPager : ViewPager2
    private lateinit var mContext: Context
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userModel by activityViewModels<CurrentUserViewModel>()
    private val bannerModel by activityViewModels<BannerViewModel>()
    private val contributionModel by activityViewModels<HomeContributionViewModel>()
    private val opinionModel by activityViewModels<HomeOpinionViewModel>()
    private val answerModel by activityViewModels<HomeOpinionAnswerViewModel>()
    private val model by activityViewModels<SurveyInfoViewModel>()
    private val mainDataViewModel by activityViewModels<MainDataViewModel>()
    private var homeSurveyList = kotlin.collections.ArrayList<SurveyModel>()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainViewModelFactory : MainViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var left = 0
        var right = 1
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root

        val container : RecyclerView? = view.findViewById(R.id.homeList_recyclerView)



        mainViewModelFactory = MainViewModelFactory(MainRepository())
        mainViewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
        CoroutineScope(Dispatchers.Main).launch {

            //user info fetch
            fetchUserData()
            val currentUser = mainDataViewModel.currentUserModel[0]
            binding.HomeGreetingText.text = "안녕하세요, ${currentUser.name}님!"
            if(currentUser.UserSurveyList == null){
                binding.HomeSurveyNum.text = "0개"
            }else{
                binding.HomeSurveyNum.text = "${currentUser.UserSurveyList!!.size}개"
            }
            binding.HomeRewardAmount.text = "${currentUser.rewardTotal}원"
            mainViewModel.fetchSurvey(20, "여")
            mainViewModel.repositories6.observe(viewLifecycleOwner){
                //home list
                if (currentUser.didFirstSurvey == false) {
                    binding.HomeListItemContainerFirst.visibility= View.VISIBLE
                    binding.homeListText.visibility = View.GONE
                    binding.homeListRecyclerView.visibility = View.GONE
                    binding.HomeListItemTitleFirst.text = "${currentUser.name}님에 대해 알려주세요!"

                }

                else if(currentUser.didFirstSurvey == true) {
                    if (setHomeList(chooseHomeList()).size == 0) {
                        Log.d(TAG, "onCreateView: 불러오기 전")
                        binding.HomeListItemContainerFirst.visibility= View.GONE
                        binding.homeListRecyclerView.visibility = View.GONE

                        binding.homeListText.text = "현재 참여가능한 설문이 없습니다"
                        binding.homeListText.visibility = View.VISIBLE
                    }

                    else {
                        binding.HomeListItemContainerFirst.visibility= View.GONE
                        binding.homeListText.visibility = View.GONE
                        binding.homeListRecyclerView.visibility = View.VISIBLE
                        val adapter = HomeListItemsAdapter(setHomeList(chooseHomeList()))
                        container?.layoutManager = LinearLayoutManager(
                            context,
                            LinearLayoutManager.VERTICAL, false
                        )
                        container?.adapter = HomeListItemsAdapter(setHomeList(chooseHomeList()))
                    }

                }
            }



            //banner fetch
            mainViewModel.fetchBannerImg()
            mainViewModel.repositories2.observe(viewLifecycleOwner){
//                Log.d(TAG, "onCreateView: $it")
                binding.HomeBannerDefault.visibility = View.INVISIBLE
                binding.textViewTotalBanner.text = it.size.toString()
                bannerPager.offscreenPageLimit = it.size
                bannerPager.adapter = BannerViewPagerAdapter(mContext, it)
                bannerPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
        }



        // Banner init
        bannerPager = view.findViewById(R.id.Home_BannerViewPager)
        val bannerDefault : ImageView = view.findViewById(R.id.Home_BannerDefault)
//
//        Glide.with(this@HomeFragment).load(R.raw.app_loading).into(binding.HomeBannerDefault)
//        CoroutineScope(Dispatchers.Main).launch {
//            val banner = CoroutineScope(Dispatchers.IO).async {
//                while (bannerModel.uriList.size == 0) {
//                    //bannerDefault.visibility = View.VISIBLE
//                }
//                binding.HomeBannerDefault.visibility = View.INVISIBLE
//                bannerModel.uriList
//
//            }.await()
//
//            binding.HomeBannerDefault.visibility = View.INVISIBLE
//            binding.textViewTotalBanner.text = bannerModel.num.toString()
//            bannerPager.offscreenPageLimit = bannerModel.num
//            bannerPager.adapter = BannerViewPagerAdapter(mContext, bannerModel.uriList)
//            bannerPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//
//        }


        // Banner 넘기면 [현재 페이지/전체 페이지] 변화
        bannerPager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewCurrentBanner.text = "${position+1}"
                }
            })
        }

        binding.HomeParSurveyBox.setOnClickListener {
            val intent = Intent(context, MyViewHistoryActivity::class.java)
            startActivity(intent)
        }



        binding.homeListBtn.setOnClickListener {
            if (userModel.currentUser.didFirstSurvey == false) {
                (activity as MainActivity).navColor_in_Home()
                (activity as MainActivity).moreBtn()

//                val intent_surveylistfirstsurvey: Intent =
//                    Intent(context, FirstSurveyListActivity::class.java)
//                intent_surveylistfirstsurvey.putExtra("currentUser_main", userModel.currentUser)
//                startActivity(intent_surveylistfirstsurvey)

            } else {
                (activity as MainActivity).clickList()
                (activity as MainActivity).navColor_in_Home()
            }
        }




        //user name, reward 불러오기
//        if (userModel.currentUser.uid != null) {
//            binding.HomeGreetingText.text = "안녕하세요, ${userModel.currentUser.name}님!"
//            if(userModel.currentUser.UserSurveyList == null){
//                binding.HomeSurveyNum.text = "0개"
//            }else{
//                binding.HomeSurveyNum.text = "${userModel.currentUser.UserSurveyList!!.size}개"
//            }
//
//            binding.HomeRewardAmount.text = "${userModel.currentUser.rewardTotal}원"
//        } else {
//            if (Firebase.auth.currentUser?.uid != null) {
//                //query 보다 원래 방법이 더 빠름
//                db.collection("panelData")
//                    .document(Firebase.auth.currentUser!!.uid)
//                    .get().addOnSuccessListener { document ->
//                        binding.HomeGreetingText.text = "안녕하세요, ${document["name"].toString()}님"
//                        binding.HomeRewardAmount.text =
//                            "${(Integer.parseInt(document["reward_total"].toString()))}원"
//                    }
//
//                db.collection("panelData").document(Firebase.auth.currentUser!!.uid)
//                    .collection("UserSurveyList").get()
//                    .addOnSuccessListener { document ->
//                        var num = 0
//                        for(item in document) {
//                            num++
//                        }
//                        binding.HomeSurveyNum.text = num.toString() + "개"
//                    }
//
//            } else {
//                binding.HomeGreetingText.text = ""
//                binding.HomeRewardAmount.text = "$-----"
//            }
//        }

        //list 불러오기
        CoroutineScope(Dispatchers.Main).launch {
            //Log.d(TAG, "onCreateView: 시작 전")
            //val model by activityViewModels<SurveyInfoViewModel>()
            //getHomeList(model)
//            mainViewModel.fetchSurvey(20, "여")
//            mainViewModel.repositories6.observe(viewLifecycleOwner){
//                Log.d(TAG, "onCreate: 66666${it.get(0)}")
//
//            }
            //Log.d(TAG, "onCreateView: 끝")




        }


        // Contribution
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.fetchContribution()
            mainViewModel.repositories3.observe(viewLifecycleOwner){
                binding.HomeContributionRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.HomeContributionRecyclerView.adapter = ContributionItemsAdapter(it)
            }

        }


        // Opinion
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.fetchOpinion()
            mainViewModel.repositories4.observe(viewLifecycleOwner){ data->
                binding.HomeOpinionTextView.text = data.question
                binding.HomeOpinionQContainer.setOnClickListener {
                    val intent = Intent(context, HomeOpinionDetailActivity::class.java)
                    intent.putExtra("id", data.id)
                    intent.putExtra("question", data.question)
                    intent.putExtra("content1", data.content1)
                    intent.putExtra("content2", data.content2)
                    startActivity(intent)
                }

            }
            mainViewModel.repositories5.observe(viewLifecycleOwner){ data->
                activity?.runOnUiThread(Runnable {
                    if(data.get(left).id==2){

                    }
                    binding.HomeOpinionAnswerTitleL.text = data.get(left).question.toString()
                    binding.HomeOpinionAnswerTitleR.text = data.get(right).question.toString()

                    binding.HomeOpinionR.setOnClickListener{
                        if(right<data.size-1){
                            left++
                            right++
                            binding.HomeOpinionAnswerTitleL.text = data.get(left).question.toString()
                            binding.HomeOpinionAnswerTitleR.text = data.get(right).question.toString()
                        }

                    }
                    binding.HomeOpinionL.setOnClickListener{
                        if(left>0){
                            left--
                            right--
                            binding.HomeOpinionAnswerTitleL.text = data.get(left).question.toString()
                            binding.HomeOpinionAnswerTitleR.text = data.get(right).question.toString()
                        }

                    }
                })
                binding.HomePollAnswerContainerL.setOnClickListener {
                    if(data.get(left).id!=2){
                        val intent = Intent(context, HomeOpinionAnswerActivity::class.java)
                        intent.putExtra("id", data.get(left).id)
                        intent.putExtra("content1",data.get(left).content1)
                        intent.putExtra("content2",data.get(left).content2)
                        intent.putExtra("content3",data.get(left).content3)

                        putAnswerItemNum(intent, data.get(left).id)

                    }

                }
                binding.HomePollAnswerContainerR.setOnClickListener {
                    if(data.get(right).id!=2){
                        val intent = Intent(context, HomeOpinionAnswerActivity::class.java)
                        intent.putExtra("id", data.get(right).id)
                        intent.putExtra("content1",data.get(right).content1)
                        intent.putExtra("content2",data.get(right).content2)
                        intent.putExtra("content3",data.get(right).content3)

                        putAnswerItemNum(intent, data.get(right).id)
                    }
                }

            }
        }

        binding.HomeListItemContainerFirst.setOnClickListener{
            (activity as MainActivity).navColor_in_Home()
            (activity as MainActivity).moreBtn()
        }

            return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun fetchUserData(){
        CoroutineScope(Dispatchers.Main).async {
            val t = withContext(Dispatchers.IO){
                while(mainDataViewModel.currentUserModel.size==0){}
                1
            }
        }.await()
    }

    /*
    private fun chooseHomeList() : ArrayList<Boolean>{
//        val userModel by activityViewModels<CurrentUserViewModel>()
//        val model by activityViewModels<SurveyInfoViewModel>()
        val doneSurvey = userModel.currentUser.UserSurveyList
        var boolList = ArrayList<Boolean>(model.sortSurvey().size)
        var num: Int = 0

        //survey list item 크기와 같은 boolean type list 만들기. 모두 false 로
        while (num < model.surveyInfo.size) {
            boolList.add(false)
            num++
        }

        var index: Int = -1

        // userSurveyList 와 겹치는 요소가 있으면 boolean 배열의 해당 인덱스 값을 true로 바꿈
        if (doneSurvey?.size != 0) {
            if (doneSurvey != null) {
                for (done in doneSurvey) {
                    index = -1
                    for (survey in model.surveyInfo) {
                        index++
                        //homelist 마감 체크
                        val dueDate = survey.dueDate + " " + survey.dueTimeTime + ":00"
                        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = sf.parse(dueDate)
                        val now = Calendar.getInstance()
                        val calDate = (date.time - now.time.time) / (60 * 60 * 1000)

                        if(calDate<0){
                            boolList[index] = true
                        }
                        if (survey.id.equals(done.id)) {
                            boolList[index] = true
                        }else if(survey.progress >=3){
                            boolList[index] = true
                        }
                    }
                }
            }
        }else{
            index = -1
            for(survey in model.surveyInfo){
                index++
                boolList[index] = survey.progress>=3
            }
        }
        return boolList
    }
     */


    //설문 참여, 마감 유무 boolean list
    private fun chooseHomeList() : ArrayList<Boolean>{
//        val userModel by activityViewModels<CurrentUserViewModel>()
//        val model by activityViewModels<SurveyInfoViewModel>()

        val doneSurvey = mainDataViewModel.currentUserModel[0].UserSurveyList
        var boolList = ArrayList<Boolean>(model.sortSurvey().size)
        var num: Int = 0

        //survey list item 크기와 같은 boolean type list 만들기. 모두 false 로
        while (num < model.surveyInfo.size) {
            boolList.add(false)
            num++
        }

        var index: Int = -1
        // 여기서 model 을 새로 fetch 할 것인지를 체크하고 해야됨.
        // userSurveyList 와 겹치는 요소가 있으면 boolean 배열의 해당 인덱스 값을 true로 바꿈
        if (doneSurvey?.size != 0) {
            if (doneSurvey != null) {
                for (done in doneSurvey) {
                    index = -1
                    for (survey in model.surveyInfo) {
                        index++
                        //homelist 마감 체크
                        val dueDate = survey.dueDate + " " + survey.dueTimeTime + ":00"
                        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = sf.parse(dueDate)
                        val now = Calendar.getInstance()
                        val calDate = (date.time - now.time.time) / (60 * 60 * 1000)

                        if(calDate<0){
                            boolList[index] = true
                        }
                        if (survey.id.equals(done.id)) {
                            boolList[index] = true
                        }else if(survey.progress >=3){
                            boolList[index] = true
                        }
                    }
                }
            }
        }else{
            index = -1
            for(survey in model.surveyInfo){
                index++
                boolList[index] = survey.progress>=3
            }
        }
        return boolList
    }

    //home list에 들어갈 list return 하기
    private fun setHomeList(boolList : ArrayList<Boolean>) : ArrayList<SurveyModel>{
        val finList = arrayListOf<SurveyModel>()
        //val model by activityViewModels<SurveyInfoViewModel>()
        var index = 0
        while(index < model.surveyInfo.size){
            if(!boolList[index]){
                finList.add(sortSurvey(model.surveyInfo).get(index))
                index+=1
            }else{
                index+=1
            }

        }
        return finList
    }
    private fun sortSurvey(surveyList : kotlin.collections.ArrayList<SurveyModel>) : ArrayList<SurveyModel>{
        val sortList = arrayListOf<SurveyModel>()
        surveyList.sortWith(compareBy<SurveyModel> { it.dueDate }.thenBy { it.dueTimeTime })
        sortList.addAll(surveyList)

        return sortList
    }
    //Coroutine test -ing
//    private suspend fun getBannerImg(bannerModel : BannerViewModel){
//        withContext(Dispatchers.IO){
//            Log.d(TAG, "########coroutine ${print("where")}")
//            while (bannerModel.uriList.size == 0) {
//                //bannerDefault.visibility = View.VISIBLE
//            }
//        }
//    }
    private suspend fun getHomeList(listModel : SurveyInfoViewModel){
        withContext(Dispatchers.IO){
            while (listModel.surveyInfo.size == 0) {
                //Log.d(TAG, "########loading")
            }
        }
    }
//
//    fun <T>print(msg : T){
//        kotlin.io.println("$msg [${Thread.currentThread().name}")
//    }


    // 해당 id의 Opinion Answer의 Storage 이미지 개수 불러와서 HomeOpinionAnswerActivity에 전달
    private fun putAnswerItemNum(intent: Intent, id : Int) {
        val urlList = ArrayList<String>()
        CoroutineScope(Dispatchers.Main).launch {
            val url = CoroutineScope(Dispatchers.IO).async {
                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.reference.child("AppOpinionAnswerImage").child(id.toString())
                val listAllTask: Task<ListResult> = storageRef.listAll()

                var itemNum : Int = 0    // 해당 id의 Answer이 가진 이미지 개수
                listAllTask.addOnSuccessListener { result ->
                    result.items.forEach { it ->
                        itemNum++
                    }

                    intent.putExtra("itemNum", itemNum)
                    startActivity(intent)
                }
                itemNum
            }.await()
        }
    }




}