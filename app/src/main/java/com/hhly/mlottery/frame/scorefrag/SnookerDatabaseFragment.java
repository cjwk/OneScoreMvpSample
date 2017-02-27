package com.hhly.mlottery.frame.scorefrag;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hhly.mlottery.R;
import com.hhly.mlottery.adapter.snooker.ChoseHeadInformationAdapter;
import com.hhly.mlottery.adapter.snooker.InformationDataAdapter;
import com.hhly.mlottery.adapter.snooker.PinnedHeaderExpandableAdapter;
import com.hhly.mlottery.bean.SnookerNoDataBean;
import com.hhly.mlottery.bean.snookerbean.SnookerRaceListitemBean;
import com.hhly.mlottery.bean.snookerbean.SnookerRefrshBean;
import com.hhly.mlottery.bean.snookerbean.SnookerSuccessBean;
import com.hhly.mlottery.bean.videobean.NewMatchVideoinfo;
import com.hhly.mlottery.config.BaseURLs;
import com.hhly.mlottery.config.StaticValues;
import com.hhly.mlottery.util.DisplayUtil;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.net.VolleyContentFast;
import com.hhly.mlottery.view.SnookerPinnedHeaderExpandableListView;
import com.hhly.mlottery.widget.GrapeGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by yuely198 on 2017/2/13.
 */

public class SnookerDatabaseFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    /*标题，暂无数据*/
    private TextView live_no_data_txt;
    /*直播列表*/
    private List<String> groupDataList = new ArrayList<>();
    /*直播子列表*/
    private List<List<String>> childDataList = new ArrayList<>();//直播子列表
    private static final String QUALIFICATIONHEAT = "102";
    private static final String RACEHEAD = "103";
    boolean isAddHeadDatas = false;

    //动画效果
    private RotateAnimation ra;

    private static final int QUALIFICATIONS = 0;  //资格赛
    private static final int RACE = 1;         //正赛
    private static final int SUCCESSIVE = 2;    //历届冠军
    private static final int PROFILE = 3;          //赛事简介

    /*视频直播适配器*/
    private PinnedHeaderExpandableAdapter pheadapter;
    private List<NewMatchVideoinfo.MatchVideoBean> groupMatchVideoList;
    /*加载失败显示的layout*/
    private LinearLayout live_error_ll;
    /*重新加载网络的按钮*/
    private TextView live_error_btn;
    /*下拉刷新*/
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout1;
    private ChoseHeadInformationAdapter choseHeadInformationAdapter;

    List headDatas = new ArrayList<>();

    private Context mContext;
    private Activity mActivity;

    private static final String PARAM_ID = "leagueId";
    private static final String PARAM_TYPE = "type";

    private static final String TYPE_PARM = "TYPE_PARM";
    private int mType;
    private String mLeagueId;
    private View view;
    private ListView mRecyclerView;
    // private GalleryAdapter galleryAdapter;
    private SnookerPinnedHeaderExpandableListView explistview_live;
    private GrapeGridView head_gridview;
    private TextView snooker_profile;
    private List<SnookerRaceListitemBean.DataBean.StageMapBean.StageInfoBean> stageInfo;

    private SnookerRaceListitemBean.DataBean.StageMapBean stageMap;
    private InformationDataAdapter informationDataAdapter;
    private SegmentedGroup segmented5;
    private HorizontalScrollView snooker_race_male_gridview;
    private LinearLayout snooker_race_time_head;
    private String currentStage = "";
    private String mSeason = "";
    private TextView live_pr_no_data_txt;
    private TextView lay_agendafg;

    public static SnookerDatabaseFragment newInstance(int type, String leagueId) {
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_PARM, type);
        bundle.putString(PARAM_ID, leagueId);
        SnookerDatabaseFragment footballDatabaseFragment = new SnookerDatabaseFragment();
        footballDatabaseFragment.setArguments(bundle);
        return footballDatabaseFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE_PARM);
            mLeagueId = getArguments().getString(PARAM_ID);
        }
        //注册EventBus
        EventBus.getDefault().register(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.snooker_race, container, false);
        mContext = mActivity;
        return view;
    }

    //赛事简介数据传输
    public void onEventMainThread(SnookerNoDataBean snookerNoDataBean) {
        if (snookerNoDataBean.isNoData()) {
            live_pr_no_data_txt.setVisibility(View.VISIBLE);
            snooker_profile.setVisibility(View.GONE);
        } else {
            live_pr_no_data_txt.setVisibility(View.GONE);
            snooker_profile.setText("\t\t\t\t" + snookerNoDataBean.getNoData());
        }
    }

    //赛事简介数据传输
    public void onEventMainThread(SnookerRefrshBean snookerRefrshBean) {
        L.d("cvb", " pre segmented5.getChildCount()=" + segmented5.getChildCount());

        segmented5.removeAllViews();

        L.d("cvb", " finish segmented5.getChildCount()=" + segmented5.getChildCount());

        isAddHeadDatas = false;
        upLeagueRace("", snookerRefrshBean.getSeason(), "onEventMainThread");


    }

    private void upLeagueRace(String secondTitle, String season, String s) {


        Log.i("aaaa", "sadasdas=====" + s);

        final Map<String, String> map = new HashMap();
        map.put("leagueId", mLeagueId);
        map.put("season", season);//默认不填是当前数据
        map.put("firstTitle", "102");//102.资格赛 103.正赛
        map.put("secondTitle", secondTitle);//联赛列表

        VolleyContentFast.requestJsonByPost(BaseURLs.SNOOKER_FINDLEAGUEMATCHLIST, map, new VolleyContentFast.ResponseSuccessListener<SnookerRaceListitemBean>() {

            @Override
            public void onResponse(SnookerRaceListitemBean json) {
                if (json == null) {
                    return;
                }
                if (json.getResult() == 200) {
                    // stageInfo.clear();
                    //获取整体的头部数据
                    stageMap = json.getData().getStageMap();
                    //获取头部数据
                    stageInfo = json.getData().getStageMap().getStageInfo();
                    //获取默认头部信息
                    currentStage = json.getData().getStageMap().getCurrentStageNum();

                    //获取子列表数据
                    if (json.getData().getMatchList().size() == 0) {
                        explistview_live.setVisibility(View.GONE);
                        live_no_data_txt.setVisibility(View.VISIBLE);
                        live_error_ll.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        snooker_race_male_gridview.setVisibility(View.GONE);
                        snooker_race_time_head.setVisibility(View.GONE);
                        live_pr_no_data_txt.setVisibility(View.GONE);
                        lay_agendafg.setVisibility(View.GONE);
                        return;
                    } else {
                        live_pr_no_data_txt.setVisibility(View.GONE);
                        live_no_data_txt.setVisibility(View.GONE);
                        live_error_ll.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        explistview_live.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        snooker_race_male_gridview.setVisibility(View.VISIBLE);
                        snooker_race_time_head.setVisibility(View.VISIBLE);
                        lay_agendafg.setVisibility(View.VISIBLE);
                    }
                    if (stageInfo.size() == 0) {

                        snooker_race_male_gridview.setVisibility(View.GONE);
                    } else {
                        if (!isAddHeadDatas) {
                            Log.i("dasd", "stageInfo.size()" + stageInfo.size());
                            for (int i = 0; i < stageInfo.size(); i++) {

                                addButton(segmented5, stageInfo.get(i).getStage(), stageInfo.get(i).getNum());

                            }

                            isAddHeadDatas = true;
                        }
                    }
                    //获取子列表数据
                    for (int i = 0; i < json.getData().getMatchList().size(); i++) {
                        //获取默认赛季时间
                        mSeason = json.getData().getMatchList().get(i).getSeason();
                        //添加子view数据
                        childDataList.add(json.getData().getMatchList().get(i).getDetailedScoreList());
                    }
                    pheadapter = new PinnedHeaderExpandableAdapter(json.getData().getMatchList(), childDataList, mContext, explistview_live);
                    explistview_live.setAdapter(pheadapter);
                    pheadapter.notifyDataSetChanged();
                }
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                //  head_gridview.setVisibility(View.GONE);
                live_pr_no_data_txt.setVisibility(View.GONE);
                snooker_race_time_head.setVisibility(View.GONE);
                lay_agendafg.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                explistview_live.setVisibility(View.GONE);
                live_error_ll.setVisibility(View.VISIBLE);
                live_no_data_txt.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.exp_net_status_txt, Toast.LENGTH_SHORT).show();

            }
        }, SnookerRaceListitemBean.class);
    }

    private void upSuccessive() {

        final Map<String, String> map = new HashMap();
        map.put("leagueId", mLeagueId);

        VolleyContentFast.requestJsonByPost(BaseURLs.SNOOKER_FINDPREVIOUSWINNERS, map, new VolleyContentFast.ResponseSuccessListener<SnookerSuccessBean>() {

            @Override
            public void onResponse(SnookerSuccessBean json) {
                if (json == null) {
                    return;
                }
                if (json.getResult() == 200) {
                    if (json.getData().size() == 0) {
                        mRecyclerView.setVisibility(View.GONE);
                        live_no_data_txt.setVisibility(View.VISIBLE);
                        live_error_ll.setVisibility(View.GONE);
                        mSwipeRefreshLayout1.setVisibility(View.GONE);
                        mSwipeRefreshLayout1.setRefreshing(false);
                        snooker_race_time_head.setVisibility(View.GONE);
                        lay_agendafg.setVisibility(View.GONE);
                        snooker_race_male_gridview.setVisibility(View.GONE);
                    } else {
                        live_no_data_txt.setVisibility(View.GONE);
                        live_error_ll.setVisibility(View.GONE);
                        mSwipeRefreshLayout1.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout1.setRefreshing(false);
                        snooker_race_time_head.setVisibility(View.VISIBLE);
                        lay_agendafg.setVisibility(View.VISIBLE);
                        snooker_race_male_gridview.setVisibility(View.GONE);
                    }
                    informationDataAdapter = new InformationDataAdapter(mContext, json.getData(), R.layout.snooker_race_group);
                    mRecyclerView.setAdapter(informationDataAdapter);
                }
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                lay_agendafg.setVisibility(View.GONE);
                snooker_race_time_head.setVisibility(View.GONE);
                mSwipeRefreshLayout1.setRefreshing(false);
                mSwipeRefreshLayout1.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                live_error_ll.setVisibility(View.VISIBLE);
                live_no_data_txt.setVisibility(View.GONE);
                snooker_race_male_gridview.setVisibility(View.GONE);
                Toast.makeText(mContext, R.string.exp_net_status_txt, Toast.LENGTH_SHORT).show();

            }
        }, SnookerSuccessBean.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        mRecyclerView = (ListView) view.findViewById(R.id.header_item_view);

        segmented5 = (SegmentedGroup) view.findViewById(R.id.segmented5);


        segmented5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            /*    L.d("cvb", "index==" + group.indexOfChild(group.findViewById(checkedId)));

                L.d("cvb", "checkedId=" + checkedId);
                L.d("cvb", "stageInfo=" + stageInfo.size());
                L.d("cvb", "  segmented5.getChildCount()=" + segmented5.getChildCount());*/


                upLeagueRace(stageInfo.get(group.indexOfChild(group.findViewById(checkedId))).getNum() + "", mSeason, "segmented5");


            }
        });
        snooker_profile = (TextView) view.findViewById(R.id.snooker_profile);

        //级联列表listview
        explistview_live = (SnookerPinnedHeaderExpandableListView) view.findViewById(R.id.explistview_live);
        //设置悬浮头部VIEW

        //列表头部条目
        snooker_race_male_gridview = (HorizontalScrollView) view.findViewById(R.id.snooker_race_male_gridview);

        //列表头部条目
        snooker_race_time_head = (LinearLayout) view.findViewById(R.id.snooker_race_time_head);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.live_swiperefreshlayout);// 数据板块，listview
        mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_header);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DisplayUtil.dip2px(getActivity(), StaticValues.REFRASH_OFFSET_END));

        mSwipeRefreshLayout1 = (SwipeRefreshLayout) view.findViewById(R.id.live_swiperefreshlayout1);// 数据板块，listview
        mSwipeRefreshLayout1.setColorSchemeResources(R.color.bg_header);
        mSwipeRefreshLayout1.setOnRefreshListener(this);

        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DisplayUtil.dip2px(getActivity(), StaticValues.REFRASH_OFFSET_END));

        if (mType == QUALIFICATIONS) {   //资格赛
            upLeagueRace("", "", "onview");
        } else if (mType == SUCCESSIVE) {
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout1.setVisibility(View.VISIBLE);
            upSuccessive();
        } else {
            view.findViewById(R.id.snooker_race_fragemnt).setVisibility(View.GONE);
            snooker_profile.setVisibility(View.VISIBLE);
        }
        live_error_ll = (LinearLayout) view.findViewById(R.id.live_error_ll);
        live_error_btn = (TextView) view.findViewById(R.id.live_error_btn);
        live_error_btn.setOnClickListener(this);
        //暂无数据
        live_no_data_txt = (TextView) view.findViewById(R.id.live_no_data_txt);

        live_error_ll = (LinearLayout) view.findViewById(R.id.live_error_ll);
        live_error_btn = (TextView) view.findViewById(R.id.live_error_btn);
        live_error_btn.setOnClickListener(this);
        //赛事简介的暂无数据
        live_pr_no_data_txt = (TextView) view.findViewById(R.id.live_pr_no_data_txt);

        //赛事头部线条
        lay_agendafg = (TextView) view.findViewById(R.id.lay_agendafg);

    }


    /*下拉刷新*/
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /* upLeagueRace("0");*/
                reFH();
            }
        }, 500);
    }

    public void reFH() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mType == QUALIFICATIONS) {
                    upLeagueRace(currentStage, mSeason, "reFH");
                } else if (mType == SUCCESSIVE) {
                    upSuccessive();
                }
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_error_btn:

                reFH();

                break;
            default:
                break;


        }

    }

    /*添加Radiobutton*/
    private void addButton(SegmentedGroup group, String stage, String num) {


        RadioButton radioButton = (RadioButton) mActivity.getLayoutInflater().inflate(R.layout.radio_button_item, null);
        radioButton.setText(stage);
        group.addView(radioButton);
        group.updateBackground();
        //默认选择
     /*   if (num.equals(currentStage)) {
            radioButton.setChecked(true);
        }*/

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
