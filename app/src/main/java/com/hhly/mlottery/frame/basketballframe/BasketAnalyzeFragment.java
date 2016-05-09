/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hhly.mlottery.frame.basketballframe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hhly.mlottery.MyApp;
import com.hhly.mlottery.R;
import com.hhly.mlottery.activity.BasketAnalyzeMoreRecordActivity;
import com.hhly.mlottery.activity.BasketDetailsActivity;
import com.hhly.mlottery.bean.basket.BasketDetails.BasketAnalyzeBean;
import com.hhly.mlottery.bean.basket.BasketDetails.BasketAnalyzeContentBean;
import com.hhly.mlottery.bean.basket.BasketDetails.BasketAnalyzeFutureMatchBean;
import com.hhly.mlottery.config.BaseURLs;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.net.VolleyContentFast;
import com.hhly.mlottery.view.ObservableScrollView;
import com.hhly.mlottery.view.ScrollUtils;
import com.hhly.mlottery.view.Scrollable;
import com.hhly.mlottery.widget.ExactSwipeRefrashLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class BasketAnalyzeFragment extends BasketDetailsBaseFragment<ObservableScrollView> implements SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_MORERECORD = 0x80;
    private View mView;
    private ProgressBar mProgressBar;//历史交锋进度条
    private TextView mBasketProgressbarGuest;
    private TextView mBasketProgressbarHome;
    private ImageView mRecentGuestImg1;
    private ImageView mRecentGuestImg2;
    private ImageView mRecentGuestImg3;
    private ImageView mRecentGuestImg4;
    private ImageView mRecentGuestImg5;
    private ImageView mRecentGuestImg6;
    private ImageView mRecentHomeImg1;
    private ImageView mRecentHomeImg2;
    private ImageView mRecentHomeImg3;
    private ImageView mRecentHomeImg4;
    private ImageView mRecentHomeImg5;
    private ImageView mRecentHomeImg6;
    private TextView mFutureGuestDate1;
    private TextView mFutureGuestDate2;
    private TextView mFutureGuestDate3;
    private TextView mFutureGuestName1;
    private TextView mFutureGuestName2;
    private TextView mFutureGuestName3;
    private ImageView mFutureGuestImg1;
    private ImageView mFutureGuestImg2;
    private ImageView mFutureGuestImg3;
    private TextView mFutureHomeDate1;
    private TextView mFutureHomeDate2;
    private TextView mFutureHomeDate3;
    private TextView mFutureHomeName1;
    private TextView mFutureHomeName2;
    private TextView mFutureHomeName3;
    private ImageView mFutureHomeImg1;
    private ImageView mFutureHomeImg2;
    private ImageView mFutureHomeImg3;
    private TextView mRankingGuestName;
    private TextView mRankingHomeName;
    private TextView mRankingGuestOverGame;
    private TextView mRankingHomeOverGame;
    private TextView mRankingGuestResult;
    private TextView mRankingHomeResult;
    private TextView mRankingGuestWinRate;
    private TextView mRankingHomeWinRate;
    private TextView mGuestScoreWinSix;
    private TextView mGuestScoreLoseSix;
    private TextView mHomeScoreWinSix;
    private TextView mHomeScoreLoseSix;

    private TextView mBasketAnalyzeMoreRecord;
    private ObservableScrollView scrollView;

    Handler mHandler = new Handler();
    private DisplayImageOptions options; //
    private com.nostra13.universalimageloader.core.ImageLoader universalImageLoader;
    private String mThirdId;
    private TextView mScoreWin;
    private TextView mScoreLose;
    private ExactSwipeRefrashLayout mRefresh;//下拉刷新


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mThirdId = ((BasketDetailsActivity) getActivity()).getmThirdId();
        L.d("mThirdId ==AAAAA===", mThirdId + "");

        mView = inflater.inflate(R.layout.basket_analysis, container, false);
        scrollView = (ObservableScrollView) mView.findViewById(R.id.scroll);

        // TouchInterceptionViewGroup should be a parent view other than ViewPager.
        // This is a workaround for the issue #117:
        // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
        scrollView.setTouchInterceptionViewGroup((ViewGroup) mView.findViewById(R.id.fragment_root));

        // Scroll to the specified offset after layout
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SCROLL_Y)) {
            final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
            ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, scrollY);
                }
            });
            updateFlexibleSpace(scrollY, mView);
        } else {
            updateFlexibleSpace(0, mView);
        }
        scrollView.setScrollViewCallbacks(this);

        initView();
        mHandler.postDelayed(mRun , 500); // 加载数据

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，多图片使用565
//                .showImageOnLoading(R.mipmap.basket_default)
                .showImageForEmptyUri(R.mipmap.basket_default)    //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.mipmap.basket_default)// 加载失败显示的图片
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        universalImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance(); //初始化
        universalImageLoader.init(config);

        return mView;
    }

    /**
     * 子线程 处理数据加载
     */
    private Runnable mRun = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };

    private void initView(){

        mProgressBar = (ProgressBar)mView.findViewById(R.id.basket_progressbar);
        mBasketProgressbarGuest = (TextView)mView.findViewById(R.id.basket_progressbar_guest);
        mBasketProgressbarHome = (TextView)mView.findViewById(R.id.basket_progressbar_home);
        mRecentGuestImg1 = (ImageView)mView.findViewById(R.id.basket_img_recent_guest1);
        mRecentGuestImg2 = (ImageView)mView.findViewById(R.id.basket_img_recent_guest2);
        mRecentGuestImg3 = (ImageView)mView.findViewById(R.id.basket_img_recent_guest3);
        mRecentGuestImg4 = (ImageView)mView.findViewById(R.id.basket_img_recent_guest4);
        mRecentGuestImg5 = (ImageView)mView.findViewById(R.id.basket_img_recent_guest5);
        mRecentGuestImg6 = (ImageView)mView.findViewById(R.id.basket_img_recent_guest6);

        mRecentHomeImg1 = (ImageView)mView.findViewById(R.id.basket_img_recent_home1);
        mRecentHomeImg2 = (ImageView)mView.findViewById(R.id.basket_img_recent_home2);
        mRecentHomeImg3 = (ImageView)mView.findViewById(R.id.basket_img_recent_home3);
        mRecentHomeImg4 = (ImageView)mView.findViewById(R.id.basket_img_recent_home4);
        mRecentHomeImg5 = (ImageView)mView.findViewById(R.id.basket_img_recent_home5);
        mRecentHomeImg6 = (ImageView)mView.findViewById(R.id.basket_img_recent_home6);

        mFutureGuestDate1 = (TextView)mView.findViewById(R.id.basket_future_guest_date1);
        mFutureGuestDate2 = (TextView)mView.findViewById(R.id.basket_future_guest_date2);
        mFutureGuestDate3 = (TextView)mView.findViewById(R.id.basket_future_guest_date3);

        mFutureGuestName1 = (TextView)mView.findViewById(R.id.basket_future_guest_name1);
        mFutureGuestName2 = (TextView)mView.findViewById(R.id.basket_future_guest_name2);
        mFutureGuestName3 = (TextView)mView.findViewById(R.id.basket_future_guest_name3);

        mFutureGuestImg1 = (ImageView)mView.findViewById(R.id.basket_future_guest_image1);
        mFutureGuestImg2 = (ImageView)mView.findViewById(R.id.basket_future_guest_image2);
        mFutureGuestImg3 = (ImageView)mView.findViewById(R.id.basket_future_guest_image3);

        mFutureHomeDate1 = (TextView)mView.findViewById(R.id.basket_future_home_date1);
        mFutureHomeDate2 = (TextView)mView.findViewById(R.id.basket_future_home_date2);
        mFutureHomeDate3 = (TextView)mView.findViewById(R.id.basket_future_home_date3);

        mFutureHomeName1 = (TextView)mView.findViewById(R.id.basket_future_home_name1);
        mFutureHomeName2 = (TextView)mView.findViewById(R.id.basket_future_home_name2);
        mFutureHomeName3 = (TextView)mView.findViewById(R.id.basket_future_home_name3);

        mFutureHomeImg1 = (ImageView)mView.findViewById(R.id.basket_future_home_image1);
        mFutureHomeImg2 = (ImageView)mView.findViewById(R.id.basket_future_home_image2);
        mFutureHomeImg3 = (ImageView)mView.findViewById(R.id.basket_future_home_image3);

        mRankingGuestName = (TextView)mView.findViewById(R.id.basket_ranking_guest_name);
        mRankingHomeName = (TextView)mView.findViewById(R.id.basket_ranking_home_name);

        mRankingGuestOverGame = (TextView)mView.findViewById(R.id.basket_ranking_guest_overgame);
        mRankingHomeOverGame = (TextView)mView.findViewById(R.id.basket_ranking_home_overgame);

        mRankingGuestResult = (TextView)mView.findViewById(R.id.basket_ranking_guest_result);
        mRankingHomeResult = (TextView)mView.findViewById(R.id.basket_ranking_home_result);

        mRankingGuestWinRate = (TextView)mView.findViewById(R.id.basket_ranking_guest_winrate);
        mRankingHomeWinRate = (TextView)mView.findViewById(R.id.basket_ranking_home_winrate);

        mGuestScoreWinSix = (TextView)mView.findViewById(R.id.basket_guest_scorewinix);
        mGuestScoreLoseSix = (TextView)mView.findViewById(R.id.basket_guest_scorelosesix);

        mHomeScoreWinSix = (TextView)mView.findViewById(R.id.basket_home_scorewinsix);
        mHomeScoreLoseSix = (TextView)mView.findViewById(R.id.basket_home_scorelosesix);

        mScoreWin = (TextView)mView.findViewById(R.id.basket_score_win);
        mScoreLose = (TextView)mView.findViewById(R.id.basket_score_lose);

        mBasketAnalyzeMoreRecord = (TextView) mView.findViewById(R.id.basket_analyze_more_record);
        mBasketAnalyzeMoreRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(MyApp.getContext(),"BasketAnalyzeMoreRecordActivity");
                Intent intent = new Intent(getActivity(), BasketAnalyzeMoreRecordActivity.class);
                intent.putExtra(BasketAnalyzeMoreRecordActivity.BASKET_ANALYZE_THIRD_ID, mThirdId);//跳转到详情
//                intent.putExtra(BasketAnalyzeMoreRecordActivity.BASKET_ANALYZE_GUEST_TEAM,"老鹰");//跳转到详情
//                intent.putExtra(BasketAnalyzeMoreRecordActivity.BASKET_ANALYZE_HOME_TEAM,"凯尔特人");//跳转到详情
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
            }
        });

//        basket_analyze_refresh
        mRefresh = (ExactSwipeRefrashLayout)mView.findViewById(R.id.basket_analyze_refresh);
        mRefresh.setColorSchemeResources(R.color.tabhost);
        mRefresh.setOnRefreshListener(this);

    }

    @Override
    public void initData(){

//        String url = "http://192.168.10.242:8181/mlottery/core/basketballDetail.findAnalysis.do";
        String url = BaseURLs.URL_BASKET_ANALYZE;
        Map<String , String> map = new Hashtable<>();
        map.put("thirdId", mThirdId);
//        map.put("thirdId", "228110");
        VolleyContentFast.requestJsonByGet(url, map, new VolleyContentFast.ResponseSuccessListener<BasketAnalyzeBean>() {
            @Override
            public void onResponse(BasketAnalyzeBean json) {

                if (getActivity() == null || json == null) {
                    return;
                }
                List<BasketAnalyzeContentBean> mAnalyzeDatas = new ArrayList<>();
                mAnalyzeDatas.add(json.getGuestData());
                mAnalyzeDatas.add(json.getHomeData());
                if (json.getGuestData() != null && json.getHomeData() != null) {
                    setDatas(mAnalyzeDatas);
                }
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                setErrorDatas();
            }
        },BasketAnalyzeBean.class);
    }

    /**
     * 请求数据失败设置
     */
    private void setErrorDatas(){

        mProgressBar.setProgress(50);
        mBasketProgressbarGuest.setText("--");
        mBasketProgressbarHome.setText("--");

        setRecent(mRecentGuestImg1, -1);
        setRecent(mRecentGuestImg2, -1);
        setRecent(mRecentGuestImg3, -1);
        setRecent(mRecentGuestImg4, -1);
        setRecent(mRecentGuestImg5, -1);
        setRecent(mRecentGuestImg6, -1);

        setRecent(mRecentHomeImg1 , -1);
        setRecent(mRecentHomeImg2 , -1);
        setRecent(mRecentHomeImg3 , -1);
        setRecent(mRecentHomeImg4 , -1);
        setRecent(mRecentHomeImg5 , -1);
        setRecent(mRecentHomeImg6 , -1);

        setFutureMatch(mFutureGuestDate1, mFutureGuestName1, mFutureGuestImg1, null, false);
        setFutureMatch(mFutureGuestDate2, mFutureGuestName2, mFutureGuestImg2, null, false);
        setFutureMatch(mFutureGuestDate3, mFutureGuestName3, mFutureGuestImg3, null, false);
        setFutureMatch(mFutureHomeDate1, mFutureHomeName1, mFutureHomeImg1, null, false);
        setFutureMatch(mFutureHomeDate2 , mFutureHomeName2 , mFutureHomeImg2 , null , false);
        setFutureMatch(mFutureHomeDate3 , mFutureHomeName3 , mFutureHomeImg3 , null , false);

        mRankingGuestName.setText("--");
        mRankingHomeName.setText("--");
        //已赛
        mRankingGuestOverGame.setText("--");
        mRankingHomeOverGame.setText("--");
        //胜负
        mRankingGuestResult.setText("--");
        mRankingHomeResult.setText("--");
        //胜率
        mRankingGuestWinRate.setText("--");
        mRankingHomeWinRate.setText("--");

        mGuestScoreWinSix.setText("--");
        mGuestScoreLoseSix.setText("--");
        mHomeScoreWinSix.setText("--");
        mHomeScoreLoseSix.setText("--");

        mScoreWin.setText("");
        mScoreLose.setText("");
    }

    private void setDatas(List<BasketAnalyzeContentBean> mAnalyzeDatas){

        if (mAnalyzeDatas == null) {
            return;
        }

        /**
         * 历史交锋
         */
        int guestWins = mAnalyzeDatas.get(0).getHistoryWin();
        int homeWins = mAnalyzeDatas.get(1).getHistoryWin();
        int progressNum;

        int guestNumWin;
        int homeNumWin;

        if (guestWins == 0 && homeWins == 0){
            progressNum = 50;
        }else{
            progressNum = guestWins * 100 / (homeWins + guestWins);
        }

        if (guestWins == 0) {
            guestNumWin = 1;
        }else{
            guestNumWin = guestWins;
        }
        if (homeWins == 0) {
            homeNumWin = 1;
        }else{
            homeNumWin = homeWins;
        }
        //设置textview位置（权重）
        mBasketProgressbarGuest.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, guestNumWin));
        mBasketProgressbarHome.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, homeNumWin));

        mProgressBar.setProgress(progressNum);
        mBasketProgressbarGuest.setText(mAnalyzeDatas.get(0).getHistoryWin() + "" + getResources().getText(R.string.basket_analyze_win));
        mBasketProgressbarHome.setText(mAnalyzeDatas.get(1).getHistoryWin() + "" + getResources().getText(R.string.basket_analyze_win));



        /**
         * 近期战绩
         */
        //客队 近期战绩
        List<Integer> mRecentGuest = mAnalyzeDatas.get(0).getRecentLW();
        if (mRecentGuest.size() != 0) {
            setRecent(mRecentGuestImg1 , mRecentGuest.get(0));
            setRecent(mRecentGuestImg2 , mRecentGuest.get(1));
            setRecent(mRecentGuestImg3 , mRecentGuest.get(2));
            setRecent(mRecentGuestImg4 , mRecentGuest.get(3));
            setRecent(mRecentGuestImg5 , mRecentGuest.get(4));
            setRecent(mRecentGuestImg6 , mRecentGuest.get(5));
        }
        List<Integer> mRecentHome = mAnalyzeDatas.get(1).getRecentLW();
        if (mRecentHome.size() != 0) {
            setRecent(mRecentHomeImg1 , mRecentHome.get(0));
            setRecent(mRecentHomeImg2 , mRecentHome.get(1));
            setRecent(mRecentHomeImg3 , mRecentHome.get(2));
            setRecent(mRecentHomeImg4 , mRecentHome.get(3));
            setRecent(mRecentHomeImg5 , mRecentHome.get(4));
            setRecent(mRecentHomeImg6 , mRecentHome.get(5));
        }
        /**
         * 未来三场
         */
        List<BasketAnalyzeFutureMatchBean> mFutureMatchGuest = mAnalyzeDatas.get(0).getFutureMatch();
        if (mFutureMatchGuest.size() != 0) {
            if (mFutureMatchGuest.size() == 1) {
                setFutureMatch(mFutureGuestDate1 , mFutureGuestName1 , mFutureGuestImg1 , mFutureMatchGuest.get(0) , true);
                setFutureMatch(mFutureGuestDate2 , mFutureGuestName2 , mFutureGuestImg2 , null , false);
                setFutureMatch(mFutureGuestDate3 , mFutureGuestName3 , mFutureGuestImg3 , null , false);
            }else if(mFutureMatchGuest.size() == 2){
                setFutureMatch(mFutureGuestDate1 , mFutureGuestName1 , mFutureGuestImg1 , mFutureMatchGuest.get(0) , true);
                setFutureMatch(mFutureGuestDate2 , mFutureGuestName2 , mFutureGuestImg2 , mFutureMatchGuest.get(1) , true);
                setFutureMatch(mFutureGuestDate3 , mFutureGuestName3 , mFutureGuestImg3 , null , false);
            }else if(mFutureMatchGuest.size() == 3){
                setFutureMatch(mFutureGuestDate1 , mFutureGuestName1 , mFutureGuestImg1 , mFutureMatchGuest.get(0) , true);
                setFutureMatch(mFutureGuestDate2 , mFutureGuestName2 , mFutureGuestImg2 , mFutureMatchGuest.get(1) , true);
                setFutureMatch(mFutureGuestDate3 , mFutureGuestName3 , mFutureGuestImg3 , mFutureMatchGuest.get(2) , true);
            }
        }

        List<BasketAnalyzeFutureMatchBean> mFutureMatchHome = mAnalyzeDatas.get(1).getFutureMatch();
        if (mFutureMatchHome.size() != 0) {
            if (mFutureMatchHome.size() == 1) {
                setFutureMatch(mFutureHomeDate1 , mFutureHomeName1 , mFutureHomeImg1 , mFutureMatchHome.get(0) , true);
                setFutureMatch(mFutureHomeDate2 , mFutureHomeName2 , mFutureHomeImg2 , null , false);
                setFutureMatch(mFutureHomeDate3 , mFutureHomeName3 , mFutureHomeImg3 , null , false);
            }else if(mFutureMatchHome.size() == 2){
                setFutureMatch(mFutureHomeDate1 , mFutureHomeName1 , mFutureHomeImg1 , mFutureMatchHome.get(0) , true);
                setFutureMatch(mFutureHomeDate2 , mFutureHomeName2 , mFutureHomeImg2 , mFutureMatchHome.get(1) , true);
                setFutureMatch(mFutureHomeDate3 , mFutureHomeName3 , mFutureHomeImg3 , null , false);
            }else if(mFutureMatchHome.size() == 3){
                setFutureMatch(mFutureHomeDate1 , mFutureHomeName1 , mFutureHomeImg1 , mFutureMatchHome.get(0) , true);
                setFutureMatch(mFutureHomeDate2 , mFutureHomeName2 , mFutureHomeImg2 , mFutureMatchHome.get(1) , true);
                setFutureMatch(mFutureHomeDate3 , mFutureHomeName3 , mFutureHomeImg3 , mFutureMatchHome.get(2) , true);
            }
        }
        /**
         * 积分排行
         */
        String mGuestRanking ;//排名
        String mGuestTeam;//球队
        String mGuestMatchAll ;//已赛
        String mGuestWins ;//胜
        String mGuestLose;//负
        String mGuestWinRate ;//胜率

        String mHomeRanking ;//排名
        String mHomeTeam;//球队
        String mHomeMatchAll ;//已赛
        String mHomeWins ;//胜
        String mHomeLose;//负
        String mHomeWinRate ;//胜率


        if (mAnalyzeDatas.get(0).getRanking() == null || mAnalyzeDatas.get(0).getRanking().equals("")) {
            mGuestRanking = "--";
        }else{
            mGuestRanking = mAnalyzeDatas.get(0).getRanking();
        }
        if (mAnalyzeDatas.get(0).getTeam() == null || mAnalyzeDatas.get(0).getTeam().equals("")) {
            mGuestTeam = "--";
        }else{
            mGuestTeam = mAnalyzeDatas.get(0).getTeam();
        }
        if (mAnalyzeDatas.get(0).getMatchAll() == null || mAnalyzeDatas.get(0).getMatchAll().equals("")) {
            mGuestMatchAll = "--";
        }else{
            mGuestMatchAll = mAnalyzeDatas.get(0).getMatchAll();
        }
        if (mAnalyzeDatas.get(0).getMatchWin() == null || mAnalyzeDatas.get(0).getMatchWin().equals("")) {
            mGuestWins = "--";
        }else{
            mGuestWins = mAnalyzeDatas.get(0).getMatchWin();
        }
        if (mAnalyzeDatas.get(0).getMatchLose() == null || mAnalyzeDatas.get(0).getMatchLose().equals("")) {
            mGuestLose = "--";
        }else{
            mGuestLose = mAnalyzeDatas.get(0).getMatchLose();
        }
        if (mAnalyzeDatas.get(0).getMatchWinRate() == null || mAnalyzeDatas.get(0).getMatchWinRate().equals("")) {
            mGuestWinRate = "--";
        }else{
            mGuestWinRate = mAnalyzeDatas.get(0).getMatchWinRate();
        }

        if (mAnalyzeDatas.get(1).getRanking() == null || mAnalyzeDatas.get(1).getRanking().equals("")) {
            mHomeRanking = "--";
        }else{
            mHomeRanking = mAnalyzeDatas.get(1).getRanking();
        }
        if (mAnalyzeDatas.get(1).getTeam() == null || mAnalyzeDatas.get(1).getTeam().equals("")) {
            mHomeTeam = "--";
        }else{
            mHomeTeam = mAnalyzeDatas.get(1).getTeam();
        }
        if (mAnalyzeDatas.get(1).getMatchAll() == null || mAnalyzeDatas.get(1).getMatchAll().equals("")) {
            mHomeMatchAll = "--";
        }else{
            mHomeMatchAll = mAnalyzeDatas.get(1).getMatchAll();
        }
        if (mAnalyzeDatas.get(1).getMatchWin() == null || mAnalyzeDatas.get(1).getMatchWin().equals("")) {
            mHomeWins = "--";
        }else{
            mHomeWins = mAnalyzeDatas.get(1).getMatchWin();
        }
        if (mAnalyzeDatas.get(1).getMatchLose() == null || mAnalyzeDatas.get(1).getMatchLose().equals("")) {
            mHomeLose = "--";
        }else{
            mHomeLose = mAnalyzeDatas.get(1).getMatchLose();
        }
        if (mAnalyzeDatas.get(1).getMatchWinRate() == null || mAnalyzeDatas.get(1).getMatchWinRate().equals("")) {
            mHomeWinRate = "--";
        }else{
            mHomeWinRate = mAnalyzeDatas.get(1).getMatchWinRate();
        }
        //排名/球队
        mRankingGuestName.setText(mGuestRanking + "  " +mGuestTeam);
        mRankingHomeName.setText(mHomeRanking + "  " + mHomeTeam);
        if (mGuestRanking.equals("--") || mGuestTeam.equals("--")) {
            mRankingGuestName.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else{
            mRankingGuestName.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
        }
        if (mHomeRanking.equals("--") || mHomeTeam.equals("--")) {
            mRankingHomeName.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else{
            mRankingHomeName.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
        }
        //已赛
//        mRankingGuestOverGame.setText(mAnalyzeDatas.get(0).getMatchAll());
        mRankingGuestOverGame.setText(mGuestMatchAll);
        mRankingHomeOverGame.setText(mHomeMatchAll);
        //胜负
        mRankingGuestResult.setText(mGuestWins + "/" + mGuestLose);
        mRankingHomeResult.setText(mHomeWins + "/" + mHomeLose);
        //胜率
        mRankingGuestWinRate.setText(mGuestWinRate);
        mRankingHomeWinRate.setText(mHomeWinRate);
        if (mGuestWinRate.equals("--")) {
            mRankingGuestWinRate.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else{
            mRankingGuestWinRate.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
        }
        if (mHomeWinRate.equals("--")) {
            mRankingHomeWinRate.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else{
            mRankingHomeWinRate.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
        }
        /**
         * 最近表现
         */

        String mGuestScoreWin ;
        String mGuestScoreLose;
        String mHomeScoreWin;
        String mHomeScoreLose;

        Double guestWin;
        Double homeWin;
        Double guestLose;
        Double homeLose;

        if (mAnalyzeDatas.get(0).getScoreWinSix() == null || mAnalyzeDatas.get(0).getScoreWinSix().equals("")) {
            mGuestScoreWin = "--";
        }else{
            mGuestScoreWin = mAnalyzeDatas.get(0).getScoreWinSix();
        }
        if (mAnalyzeDatas.get(0).getScoreLoseSix() == null || mAnalyzeDatas.get(0).getScoreLoseSix().equals("")) {
            mGuestScoreLose = "--";
        }else{
            mGuestScoreLose = mAnalyzeDatas.get(0).getScoreLoseSix();
        }
        if (mAnalyzeDatas.get(1).getScoreWinSix() == null || mAnalyzeDatas.get(1).getScoreWinSix().equals("")) {
            mHomeScoreWin = "--";
        }else{
            mHomeScoreWin = mAnalyzeDatas.get(1).getScoreWinSix();
        }
        if (mAnalyzeDatas.get(1).getScoreLoseSix() == null || mAnalyzeDatas.get(1).getScoreLoseSix().equals("")) {
            mHomeScoreLose = "--";
        }else{
            mHomeScoreLose = mAnalyzeDatas.get(1).getScoreLoseSix();
        }
        mGuestScoreWinSix.setText(mGuestScoreWin);
        mHomeScoreWinSix.setText(mHomeScoreWin);
        mGuestScoreLoseSix.setText(mGuestScoreLose);
        mHomeScoreLoseSix.setText(mHomeScoreLose);

        /**
         * 设置最近表现平均分颜色
         */
        if (!mGuestScoreWin.equals("--")) {
            guestWin = Double.parseDouble(mGuestScoreWin);
        }else{
            guestWin = 0d;
        }
        if (!mHomeScoreWin.equals("--")) {
            homeWin = Double.parseDouble(mHomeScoreWin);
        }else{
            homeWin = 0d;
        }
        if (!mGuestScoreLose.equals("--")) {
            guestLose = Double.parseDouble(mGuestScoreLose);
        }else{
            guestLose = 0d;
        }
        if (!mHomeScoreLose.equals("--")) {
            homeLose = Double.parseDouble(mHomeScoreLose);
        }else{
            homeLose = 0d;
        }

        if(guestWin > homeWin){
            mGuestScoreWinSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
            mHomeScoreWinSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else if (guestWin < homeWin) {
            mHomeScoreWinSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
            mGuestScoreWinSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else{
            mHomeScoreWinSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
            mGuestScoreWinSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }

        if(guestLose > homeLose){
            mGuestScoreLoseSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
            mHomeScoreLoseSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else if (guestWin < homeWin) {
            mHomeScoreLoseSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor2));
            mGuestScoreLoseSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }else{
            mHomeScoreLoseSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
            mGuestScoreLoseSix.setTextColor(getResources().getColor(R.color.black_details_ball_textcolor));
        }

        mScoreWin.setText(getResources().getText(R.string.basket_six_average_win_score));
        mScoreLose.setText(getResources().getText(R.string.basket_six_average_lost_score));
    }

    private void setRecent(ImageView mImage , int recent){
        if (recent == 0) {
            mImage.setBackground(getResources().getDrawable(R.mipmap.basket_lose));
        }else if(recent == 1){
            mImage.setBackground(getResources().getDrawable(R.mipmap.basket_win));
        }else{
            mImage.setBackground(getResources().getDrawable(R.mipmap.basket_none));
        }
    }

    private void setFutureMatch(TextView mTextData , TextView mTextName , ImageView mLogo , BasketAnalyzeFutureMatchBean mFutureMatch , boolean isValue){

        if(isValue){
            mTextData.setText(mFutureMatch.getDiffdays() + getResources().getText(R.string.basket_analyze_day));
            mTextName.setText(mFutureMatch.getTeam());
            universalImageLoader.displayImage(mFutureMatch.getLogourl(), mLogo,options);
        }else{
            mTextData.setText("--");
            mTextName.setText("--");
            mLogo.setBackground(getResources().getDrawable(R.mipmap.basket_default));
        }
    }

    @Override
    public void updateFlexibleSpace(int scrollY) {
        // Sometimes scrollable.getCurrentScrollY() and the real scrollY has different values.
        // As a workaround, we should call scrollVerticallyTo() to make sure that they match.
        Scrollable s = getScrollable();
        s.scrollVerticallyTo(scrollY);

        // If scrollable.getCurrentScrollY() and the real scrollY has the same values,
        // calling scrollVerticallyTo() won't invoke scroll (or onScrollChanged()), so we call it here.
        // Calling this twice is not a problem as long as updateFlexibleSpace(int, View) has idempotence.
        updateFlexibleSpace(scrollY, getView());
    }

    @Override
    protected void updateFlexibleSpace(int scrollY, View view) {
        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);

        // Also pass this event to parent Activity
        BasketDetailsActivity parentActivity = (BasketDetailsActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.onScrollChanged(scrollY, scrollView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(false);
                initData();
                BasketDetailsActivity parentActivity = (BasketDetailsActivity ) getActivity();
                parentActivity.refreshData();
            }
        },500);

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BasketAnalyzeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BasketAnalyzeFragment");
    }
}
