package com.sopt.appjam_sggsag.Fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.sopt.appjam_sggsag.CardStackAdapter
import com.sopt.appjam_sggsag.Data.DetailPosterData
import com.sopt.appjam_sggsag.MyApplication
import com.sopt.appjam_sggsag.Network.NetworkService
import com.sopt.appjam_sggsag.R
import com.sopt.appjam_sggsag.SpotDiffCallback
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.find

class HomeFragment : Fragment(), CardStackListener {
    private val drawerLayout by lazy { drawer_layout }
    private var cardStackView: CardStackView? = null
    //본래 선언
    //private val manager by lazy { CardStackLayoutManager(context, this) }
    //private val adapter by lazy { CardStackAdapter(createPosters()) }
    lateinit var manager: CardStackLayoutManager
    lateinit var adapter: CardStackAdapter
    private var homeFragmentView: View? = null

    val networkService: NetworkService by lazy {
        MyApplication.instance.networkService
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeFragmentView = inflater!!.inflate(R.layout.fragment_home, container, false)
//        setupNavigation()
        return homeFragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //내가 차라리 메인에서 불러온다...
        //getPosterListResponse()
        //by lazy에서 lateinit으로 변경함에 따라 adapter 초기화하기 위함
        manager = CardStackLayoutManager(context)
        adapter = CardStackAdapter(createPosters())
        setupCardStackView()//CardStackAdapter가 처음 쓰이는 부분
        setupButton()
    }
/*
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
*/

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

/*
    private fun setupNavigation() {
        // Toolbar
        val toolbar = findViewById<Toolbar>(R.posterIdx.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        val navigationView = findViewById<NavigationView>(R.posterIdx.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.posterIdx.reload -> reload()
                R.posterIdx.add_spot_to_first -> addFirst(1)
                R.posterIdx.add_spot_to_last -> addLast(1)
                R.posterIdx.remove_spot_from_first -> removeFirst(1)
                R.posterIdx.remove_spot_from_last -> removeLast(1)
                R.posterIdx.replace_first_spot -> replace()
                R.posterIdx.swap_first_for_last -> swap()
            }
            drawerLayout.closeDrawers()
            true
        }
    }
*/

    private fun setupCardStackView() {
        cardStackView = homeFragmentView!!.find(R.id.card_stack_view)
//        LeftButtonView=homeFragmentView!!.find(R.posterIdx.cs_view_for_progress)
//        manager=CardStackLayoutManager(context, this)
//        adapter=CardStackAdapter(createPosters())
        initialize()
    }

    private fun setupButton() {
        val skip: FloatingActionButton = homeFragmentView!!.find(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView?.swipe()
        }

        val like: FloatingActionButton = homeFragmentView!!.find(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView?.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)

        cardStackView!!.layoutManager = manager
        cardStackView!!.adapter = adapter
//        LeftButtonView!!.adapter = lbadapter
        cardStackView!!.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
//        setupProgressView()
//        setupCardTab()
    }

/*
    private fun setupProgressView(){
        var pvReverse: View = homeFragmentView!!.find(R.posterIdx.pv_reverse)
        cardStackView!!.bringToFront()
        pvReverse.setOnClickListener {
            pvReverse.bringToFront()
            Toast.makeText(activity,"눌리는거 맞냐?",Toast.LENGTH_SHORT).show()
        }

        var pvSkip: View =homeFragmentView!!.find(R.posterIdx.pv_skip)
        pvSkip.setOnClickListener {
            Toast.makeText(activity,"아아아아아아앙",Toast.LENGTH_LONG).show()
        }
    }
*/

/*
    private fun setupCardTab(){
        var viewForCardSize : View = homeFragmentView!!.find(R.posterIdx.button_container)
        var widthOfCard : Int= viewForCardSize.width
//        var widthOfCard : Int= viewForCardSize.layoutParams.width
        homeFragmentView!!.setOnTouchListener{v,event->
            when (event?.action){
                MotionEvent.ACTION_BUTTON_PRESS->{
                    var x :  Float =event.getX()
                    if (x<=widthOfCard/2){
                        Toast.makeText(v.context,"이것도 왼쪽일까",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(v.context,"제발 오른쪽",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            v?.onTouchEvent(event)?:true
        }
    }
*/

    /*
    private fun paginate() {
        val old = adapter.getSpots()
        val new = old.plus(createPosters())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
    */

    private fun paginate() {
        val old = adapter.getSpots()
//        val new = old.plus(createPosters())
        val new = old.plus(createPosters())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new as ArrayList<DetailPosterData>)
        result.dispatchUpdatesTo(adapter)
    }

/*
    private fun reload() {
        val old = adapter.getSpots()
        val new = createPosters()
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun addFirst(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createSpot())
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun addLast(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            addAll(List(size) { createSpot() })
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun removeFirst(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun removeLast(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun replace() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createSpot())
        }
        adapter.setSpots(new)
        adapter.notifyItemChanged(manager.topPosition)
    }
*/
/*
    private fun swap() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
*/
/*
    private fun createSpot(): Spot {
        return Spot(
            name = "Yasaka Shrine",
            city = "Kyoto",
            photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800"
        )
    }
*/

    /*
    private fun getPosterListResponse() {
        Log.e("111111함수 정상 실행되구요", "함수 정상 실행되구요")
        val postPosterListResponse: Call<PostPosterListResponse> =
            networkService.postPosterResponse("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJEb0lUU09QVCIsInVzZXJfaWR4IjoxfQ.5lCvAqnzYP4-2pFx1KTgLVOxYzBQ6ygZvkx5jKCFM08")
        //networkService.postPosterResponse(SharedPreferenceController.getAuthorization(this.context!!))
        Log.e("111111여기까지는 들어옵니다.","여기까지는 들어옵니다.")
        postPosterListResponse.enqueue(object : Callback<PostPosterListResponse> {
            override fun onFailure(call: Call<PostPosterListResponse>, t: Throwable) {
                Log.e("111111그런데 onFailure도 안들어오고","그런데 onFailure도 안 들어오고")
            }
            override fun onResponse(
                call: Call<PostPosterListResponse>,
                response: Response<PostPosterListResponse>
            ) {
                Log.e("111111onResponse도 안 들어옵니다." ,"onResponse도 안들어옵니다.")
                if (response.isSuccessful) {
                    //?.의 오른쪽이 함수이면 null safe operator
                    //?.의 오른쪽이 변수/상수이면 null이 될 수 있는 타입 표시
                    if (response.body()?.data!=null) {
                        Log.e("pleaseeeeeeeeeeeee", "can you come to here")
                        inputPosterData = response.body()!!.data
                    }
                }
            }
        })
    }
    */
    /*
    private fun getPosterListResponse() {
        ApplicationController.instance.networkService.postPosterResponse("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJEb0lUU09QVCIsInVzZXJfaWR4IjoxfQ.5lCvAqnzYP4-2pFx1KTgLVOxYzBQ6ygZvkx5jKCFM08")
            .enqueue(object: Callback<PostPosterListResponse>{
                override fun onFailure(call : Call<PostPosterListResponse>?,t : Throwable?){
                    Log.e("onFailure 들어옴","들어옴")
                }
                override fun onResponse(call: Call<PostPosterListResponse>?, response: Response<PostPosterListResponse>) {
                    Log.e("들어옴1",response.toString())
                    Log.e("들어옴2",response!!.body().toString())
                    if (response.body()?.data!=null) {
                        Log.e("pleaseeeeeeeeeeeee", "can you come to here")
                        inputPosterData = response.body()!!.data
                        Log.e("들어옴3",inputPosterData.toString())
                        hehehehe =1
                        Log.e("hehehehe in response",hehehehe.toString())
                    }
                }
            })
    }
    */
/*
    private fun createPosters(): ArrayList<DetailPosterData> {
        val posters = ArrayList<DetailPosterData>()
//        Log.e(inputPosterData!!.posters[0].photoUrl, inputPosterData!!.posters[0].photoUrl.toString())
//        Log.e("posterIdx",inputPosterData!!.posters[0].posterIdx.toString())
        Log.e("입력 전전전", "eeeeeeeeeeeee")
//        Log.e("inputPosterData",inputPosterData.toString())
        Log.e("inputPosterData!!.posters[0]", inputPosterData!!.posters[0].toString())
        Log.e("poster 확인", posters.toString())
//        Log.e("inputPosterData!!.posters[0].posterIdx",inputPosterData!!.posters[0].posterIdx.toString())
        Log.e("-------------------", "----------------------")

        //0번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[0].posterIdx,
                categoryIdx = inputPosterData!!.posters[0].categoryIdx,
                photoUrl = inputPosterData!!.posters[0].photoUrl,
                posterName = inputPosterData!!.posters[0].posterName,
                posterRegDate = inputPosterData!!.posters[0].posterRegDate,
                posterStartDate = inputPosterData!!.posters[0].posterStartDate,
                posterEndDate = inputPosterData!!.posters[0].posterEndDate,
                posterWebsite = inputPosterData!!.posters[0].posterWebsite,
                isSeek = inputPosterData!!.posters[0].isSeek,
                outline = inputPosterData!!.posters[0].outline,
                target = inputPosterData!!.posters[0].target,
                period = inputPosterData!!.posters[0].period,
                benefit = inputPosterData!!.posters[0].benefit,
                announceDate1 = inputPosterData!!.posters[0].announceDate1,
                announceDate2 = inputPosterData!!.posters[0].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[0].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[0].interviewDate,
                documentDate = inputPosterData!!.posters[0].documentDate
            )
        )

        Log.e("posters[0] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[1]",inputPosterData!!.posters[1].toString())
        Log.e("inputPosterData!!.posters[1].posterIdx", inputPosterData!!.posters[1].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        //1번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[1].posterIdx,
                categoryIdx = inputPosterData!!.posters[1].categoryIdx,
                photoUrl = inputPosterData!!.posters[1].photoUrl,
                posterName = inputPosterData!!.posters[1].posterName,
                posterRegDate = inputPosterData!!.posters[1].posterRegDate,
                posterStartDate = inputPosterData!!.posters[1].posterStartDate,
                posterEndDate = inputPosterData!!.posters[1].posterEndDate,
                posterWebsite = inputPosterData!!.posters[1].posterWebsite,
                isSeek = inputPosterData!!.posters[1].isSeek,
                outline = inputPosterData!!.posters[1].outline,
                target = inputPosterData!!.posters[1].target,
                period = inputPosterData!!.posters[1].period,
                benefit = inputPosterData!!.posters[1].benefit,
                announceDate1 = inputPosterData!!.posters[1].announceDate1,
                announceDate2 = inputPosterData!!.posters[1].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[1].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[1].interviewDate,
                documentDate = inputPosterData!!.posters[1].documentDate
            )
        )

        Log.e("posters[1] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[2]",inputPosterData!!.posters[2].toString())
        Log.e("inputPosterData!!.posters[2].posterIdx", inputPosterData!!.posters[2].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")

        //2번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[2].posterIdx,
                categoryIdx = inputPosterData!!.posters[2].categoryIdx,
                photoUrl = inputPosterData!!.posters[2].photoUrl,
                posterName = inputPosterData!!.posters[2].posterName,
                posterRegDate = inputPosterData!!.posters[2].posterRegDate,
                posterStartDate = inputPosterData!!.posters[2].posterStartDate,
                posterEndDate = inputPosterData!!.posters[2].posterEndDate,
                posterWebsite = inputPosterData!!.posters[2].posterWebsite,
                isSeek = inputPosterData!!.posters[2].isSeek,
                outline = inputPosterData!!.posters[2].outline,
                target = inputPosterData!!.posters[2].target,
                period = inputPosterData!!.posters[2].period,
                benefit = inputPosterData!!.posters[2].benefit,
                announceDate1 = inputPosterData!!.posters[2].announceDate1,
                announceDate2 = inputPosterData!!.posters[2].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[2].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[2].interviewDate,
                documentDate = inputPosterData!!.posters[2].documentDate
            )
        )

        Log.e("posters[2] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[3]",inputPosterData!!.posters[3].toString())
        Log.e("inputPosterData!!.posters[3].posterIdx", inputPosterData!!.posters[3].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")

        //3번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[3].posterIdx,
                categoryIdx = inputPosterData!!.posters[3].categoryIdx,
                photoUrl = inputPosterData!!.posters[3].photoUrl,
                posterName = inputPosterData!!.posters[3].posterName,
                posterRegDate = inputPosterData!!.posters[3].posterRegDate,
                posterStartDate = inputPosterData!!.posters[3].posterStartDate,
                posterEndDate = inputPosterData!!.posters[3].posterEndDate,
                posterWebsite = inputPosterData!!.posters[3].posterWebsite,
                isSeek = inputPosterData!!.posters[3].isSeek,
                outline = inputPosterData!!.posters[3].outline,
                target = inputPosterData!!.posters[3].target,
                period = inputPosterData!!.posters[3].period,
                benefit = inputPosterData!!.posters[3].benefit,
                announceDate1 = inputPosterData!!.posters[3].announceDate1,
                announceDate2 = inputPosterData!!.posters[3].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[3].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[3].interviewDate,
                documentDate = inputPosterData!!.posters[3].documentDate
            )
        )

        Log.e("posters[3] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[4]",inputPosterData!!.posters[4].toString())
        Log.e("inputPosterData!!.posters[4].posterIdx", inputPosterData!!.posters[4].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")

        //4번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[4].posterIdx,
                categoryIdx = inputPosterData!!.posters[4].categoryIdx,
                photoUrl = inputPosterData!!.posters[4].photoUrl,
                posterName = inputPosterData!!.posters[4].posterName,
                posterRegDate = inputPosterData!!.posters[4].posterRegDate,
                posterStartDate = inputPosterData!!.posters[4].posterStartDate,
                posterEndDate = inputPosterData!!.posters[4].posterEndDate,
                posterWebsite = inputPosterData!!.posters[4].posterWebsite,
                isSeek = inputPosterData!!.posters[4].isSeek,
                outline = inputPosterData!!.posters[4].outline,
                target = inputPosterData!!.posters[4].target,
                period = inputPosterData!!.posters[4].period,
                benefit = inputPosterData!!.posters[4].benefit,
                announceDate1 = inputPosterData!!.posters[4].announceDate1,
                announceDate2 = inputPosterData!!.posters[4].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[4].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[4].interviewDate,
                documentDate = inputPosterData!!.posters[4].documentDate
            )
        )

        Log.e("posters[4] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[5]",inputPosterData!!.posters[5].toString())
        Log.e("inputPosterData!!.posters[5].posterIdx", inputPosterData!!.posters[5].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        //5번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[5].posterIdx,
                categoryIdx = inputPosterData!!.posters[5].categoryIdx,
                photoUrl = inputPosterData!!.posters[5].photoUrl,
                posterName = inputPosterData!!.posters[5].posterName,
                posterRegDate = inputPosterData!!.posters[5].posterRegDate,
                posterStartDate = inputPosterData!!.posters[5].posterStartDate,
                posterEndDate = inputPosterData!!.posters[5].posterEndDate,
                posterWebsite = inputPosterData!!.posters[5].posterWebsite,
                isSeek = inputPosterData!!.posters[5].isSeek,
                outline = inputPosterData!!.posters[5].outline,
                target = inputPosterData!!.posters[5].target,
                period = inputPosterData!!.posters[5].period,
                benefit = inputPosterData!!.posters[5].benefit,
                announceDate1 = inputPosterData!!.posters[5].announceDate1,
                announceDate2 = inputPosterData!!.posters[5].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[5].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[5].interviewDate,
                documentDate = inputPosterData!!.posters[5].documentDate
            )
        )

        Log.e("posters[5] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[6]",inputPosterData!!.posters[6].toString())
        Log.e("inputPosterData!!.posters[6].posterIdx", inputPosterData!!.posters[6].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        //6번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[6].posterIdx,
                categoryIdx = inputPosterData!!.posters[6].categoryIdx,
                photoUrl = inputPosterData!!.posters[6].photoUrl,
                posterName = inputPosterData!!.posters[6].posterName,
                posterRegDate = inputPosterData!!.posters[6].posterRegDate,
                posterStartDate = inputPosterData!!.posters[6].posterStartDate,
                posterEndDate = inputPosterData!!.posters[6].posterEndDate,
                posterWebsite = inputPosterData!!.posters[6].posterWebsite,
                isSeek = inputPosterData!!.posters[6].isSeek,
                outline = inputPosterData!!.posters[6].outline,
                target = inputPosterData!!.posters[6].target,
                period = inputPosterData!!.posters[6].period,
                benefit = inputPosterData!!.posters[6].benefit,
                announceDate1 = inputPosterData!!.posters[6].announceDate1,
                announceDate2 = inputPosterData!!.posters[6].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[6].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[6].interviewDate,
                documentDate = inputPosterData!!.posters[6].documentDate
            )
        )
        Log.e("posters[6] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[7]",inputPosterData!!.posters[7].toString())
        Log.e("inputPosterData!!.posters[7].posterIdx", inputPosterData!!.posters[7].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        //7번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[7].posterIdx,
                categoryIdx = inputPosterData!!.posters[7].categoryIdx,
                photoUrl = inputPosterData!!.posters[7].photoUrl,
                posterName = inputPosterData!!.posters[7].posterName,
                posterRegDate = inputPosterData!!.posters[7].posterRegDate,
                posterStartDate = inputPosterData!!.posters[7].posterStartDate,
                posterEndDate = inputPosterData!!.posters[7].posterEndDate,
                posterWebsite = inputPosterData!!.posters[7].posterWebsite,
                isSeek = inputPosterData!!.posters[7].isSeek,
                outline = inputPosterData!!.posters[7].outline,
                target = inputPosterData!!.posters[7].target,
                period = inputPosterData!!.posters[7].period,
                benefit = inputPosterData!!.posters[7].benefit,
                announceDate1 = inputPosterData!!.posters[7].announceDate1,
                announceDate2 = inputPosterData!!.posters[7].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[7].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[7].interviewDate,
                documentDate = inputPosterData!!.posters[7].documentDate
            )
        )
        Log.e("posters[7] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[8]",inputPosterData!!.posters[8].toString())
        Log.e("inputPosterData!!.posters[8].posterIdx", inputPosterData!!.posters[8].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        //8번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[8].posterIdx,
                categoryIdx = inputPosterData!!.posters[8].categoryIdx,
                photoUrl = inputPosterData!!.posters[8].photoUrl,
                posterName = inputPosterData!!.posters[8].posterName,
                posterRegDate = inputPosterData!!.posters[8].posterRegDate,
                posterStartDate = inputPosterData!!.posters[8].posterStartDate,
                posterEndDate = inputPosterData!!.posters[8].posterEndDate,
                posterWebsite = inputPosterData!!.posters[8].posterWebsite,
                isSeek = inputPosterData!!.posters[8].isSeek,
                outline = inputPosterData!!.posters[8].outline,
                target = inputPosterData!!.posters[8].target,
                period = inputPosterData!!.posters[8].period,
                benefit = inputPosterData!!.posters[8].benefit,
                announceDate1 = inputPosterData!!.posters[8].announceDate1,
                announceDate2 = inputPosterData!!.posters[8].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[8].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[8].interviewDate,
                documentDate = inputPosterData!!.posters[8].documentDate
            )
        )
        Log.e("posters[8] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[9]",inputPosterData!!.posters[9].toString())
        Log.e("inputPosterData!!.posters[9].posterIdx", inputPosterData!!.posters[9].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        //9번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = inputPosterData!!.posters[9].posterIdx,
                categoryIdx = inputPosterData!!.posters[9].categoryIdx,
                photoUrl = inputPosterData!!.posters[9].photoUrl,
                posterName = inputPosterData!!.posters[9].posterName,
                posterRegDate = inputPosterData!!.posters[9].posterRegDate,
                posterStartDate = inputPosterData!!.posters[9].posterStartDate,
                posterEndDate = inputPosterData!!.posters[9].posterEndDate,
                posterWebsite = inputPosterData!!.posters[9].posterWebsite,
                isSeek = inputPosterData!!.posters[9].isSeek,
                outline = inputPosterData!!.posters[9].outline,
                target = inputPosterData!!.posters[9].target,
                period = inputPosterData!!.posters[9].period,
                benefit = inputPosterData!!.posters[9].benefit,
                announceDate1 = inputPosterData!!.posters[9].announceDate1,
                announceDate2 = inputPosterData!!.posters[9].announceDate2,
                finalAnnounceDate = inputPosterData!!.posters[9].finalAnnounceDate,
                interviewDate = inputPosterData!!.posters[9].interviewDate,
                documentDate = inputPosterData!!.posters[9].documentDate
            )
        )
        Log.e("posters[9] 입력 완료", " ")
        Log.e("inputPosterData!!.posters[9]", inputPosterData!!.posters[9].toString())
        Log.e("inputPosterData!!.posters[9].posterIdx", inputPosterData!!.posters[9].posterIdx.toString())
        Log.e("poster 확인", posters.toString())
        Log.e("-------------------", "----------------------")
        return posters
    }
*/

    //직접 넘겨주기를 가정하고 전역변수로 userCnt넘겨주기


    private fun createPosters(): ArrayList<DetailPosterData> {
        val posters = ArrayList<DetailPosterData>()

        Log.e("입력 전전전", "eeeeeeeeeeeee")
//        Log.e("inputPosterData!!.posters[0]",inputPosterData!!.posters[0].toString())
//        Log.e("poster 확인",posters.toString())

        //1번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 1,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                posterName = "pepepe",
                posterRegDate = "pepepe",
                posterStartDate = "pepepe",
                posterEndDate = "pepepe",
                posterWebsite = "pepepe",
                isSeek = 1,
                outline = "pepepe",
                target = "pepepe",
                period = "pepepe",
                benefit = "pepepe",
                documentDate = "pepepe",
                announceDate1 = "pepepe",
                announceDate2 = "pepepe",
                finalAnnounceDate = "pepepe",
                interviewDate = "pepepe"
            )
        )

        Log.e("posters[0] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[1]",inputPosterData!!.posters[1].toString())
//        Log.e("inputPosterData!!.posters[1].posterIdx",inputPosterData!!.posters[1].posterIdx.toString())
//        Log.e("poster 확인",posters.toString())

        //2번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 2,
                categoryIdx = 2,
                photoUrl = "https://source.unsplash.com/THozNzxEP3g/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        Log.e("posters[1] 입력 완료", " ")
//        Log.e("inputPosterData!!.posters[1]",inputPosterData!!.posters[1].toString())
//        Log.e("inputPosterData!!.posters[2].posterIdx",inputPosterData!!.posters[2].posterIdx.toString())
//        Log.e("poster 확인",posters.toString())

        //3번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 3,
                categoryIdx = 2,
                photoUrl = "https://source.unsplash.com/USrZRcRS2Lw/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = null,
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //4번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 4,
                categoryIdx = 2,
                photoUrl = "https://source.unsplash.com/NYyCqdBOKwc/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = null,
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = null
            )
        )

        //5번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 5,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/buF62ewDLcQ/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //6번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 6,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/PeFk7fzxTdk/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //7번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 7,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/LrMWHKqilUw/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //8번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 8,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/LrMWHKqilUw/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //9번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 9,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/CdVAUADdqEc/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )

        //10번 CARD
        posters.add(
            DetailPosterData(
                posterIdx = 10,
                categoryIdx = 1,
                photoUrl = "https://source.unsplash.com/AWh9C-QjhE4/600x800",
                posterName = "hahaha",
                posterRegDate = "hahah",
                posterStartDate = "hahah",
                posterEndDate = "hahah",
                posterWebsite = "hahah",
                isSeek = 0,
                outline = "hahah",
                target = "hahah",
                period = "hahah",
                benefit = "hahah",
                documentDate = "hahah",
                announceDate1 = "hahah",
                announceDate2 = "hahah",
                finalAnnounceDate = "hahah",
                interviewDate = "hahah"
            )
        )
        return posters
    }

}
