package com.hhly.mlottery.frame.basketballframe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hhly.mlottery.R;
import com.hhly.mlottery.activity.BasketFiltrateActivity;
import com.hhly.mlottery.activity.BasketballSettingActivity;
import com.hhly.mlottery.activity.FootballActivity;
import com.hhly.mlottery.adapter.PureViewPagerAdapter;
import com.hhly.mlottery.frame.basketballframe.FocusBasketballFragment;
import com.hhly.mlottery.frame.basketballframe.ImmedBasketballFragment;
import com.hhly.mlottery.frame.basketballframe.ResultBasketballFragment;
import com.hhly.mlottery.frame.basketballframe.ScheduleBasketballFragment;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.PreferenceUtil;
import com.hhly.mlottery.widget.BallSelectArrayAdapter;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yixq
 * @ClassName: BasketScoresFragment
 * @Description: 篮球比分
 */
@SuppressLint("NewApi")
public class BasketScoresFragment extends Fragment implements View.OnClickListener {

    private final int IMMEDIA_FRAGMENT = 0;
    private final int RESULT_FRAGMENT = 1;
    private final int SCHEDULE_FRAGMENT = 2;
    private final int FOCUS_FRAGMENT = 3;

    private final static String TAG = "BasketScoresFragment";
    public static List<String> titles;

    private View mView;
    private Context mContext;
    private Intent mIntent;

//    private int isLoad ;
//    private ImageView mSetting;//设置按钮
//    private ImageView mIb_back;//返回按钮
//    private TextView mTittle; //标题头
//    private ImageView mFilterImgBtn;// 筛选
    /**
     * 返回菜单
     */
    private ImageView mIb_back;// 返回菜单

    /**
     * 筛选
     */
    public static ImageView mFilterImgBtn;// 筛选

    public static ImageView getmFilterImgBtn() {
        return mFilterImgBtn;
    }

    /**
     * 设置
     */
    public static ImageView mSetting;// 设置

    public static ImageView getmSetting() {
        return mSetting;
    }
    /**
     * 中间标题
     */
    private TextView mTittle;// 标题

    /**
     * 当前处于哪个比赛fg
     */
    private int currentFragmentId = 0;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private PureViewPagerAdapter pureViewPagerAdapter;
    private List<Fragment> fragments;
    private Spinner mSpinner;
    private String[] mItems;

    @SuppressLint("ValidFragment")
    public BasketScoresFragment(Context context) {
        this.mContext = context;
    }

    public BasketScoresFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mContext = getActivity();
        mView = View.inflate(mContext, R.layout.frage_new_basketball, null);
        initView();
        setupViewPager();
        focusCallback();// 加载关注数
        initData();
        initEVent();
        return mView;
    }
    private void initEVent() {
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mContext.getResources().getString(R.string.football_frame_txt).equals(mItems[position])){// 选择足球
                    ((FootballActivity)mContext).ly_tab_bar.setVisibility(View.VISIBLE);
                    ((FootballActivity)mContext).switchFragment(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void initView() {
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        //返回
        mIb_back = (ImageView) mView.findViewById(R.id.public_img_back);
        mIb_back.setOnClickListener(this);

        //标题头部
        mTittle = (TextView) mView.findViewById(R.id.public_txt_title);
        mTittle.setVisibility(View.GONE);

        mSpinner = (Spinner) mView.findViewById(R.id.public_txt_left_spinner);
        mSpinner.setVisibility(View.VISIBLE);
        mItems = getResources().getStringArray(R.array.ball_select);
        BallSelectArrayAdapter mAdapter = new BallSelectArrayAdapter(mContext , mItems);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setSelection(1);

        // 筛选
        mFilterImgBtn = (ImageView) mView.findViewById(R.id.public_btn_filter);
        mFilterImgBtn.setOnClickListener(this);


        //设置按钮
        mSetting = (ImageView) mView.findViewById(R.id.public_btn_set);
        mSetting.setOnClickListener(this);
    }


    /**
     * 判断四个Fragment切换显示或隐藏的状态
     */
    private boolean isImmediateFragment = true;
    private boolean isImmediate = false;
    private boolean isResultFragment = false;
    private boolean isResult = false;
    private boolean isScheduleFragment = false;
    private boolean isSchedule = false;
    private boolean isFocusFragment = false;
    private boolean isFocus = false;

    private void setupViewPager() {
        mTabLayout = (TabLayout) mView.findViewById(R.id.sliding_tabs);
        titles = new ArrayList<>();
        titles.add(getString(R.string.foot_jishi_txt));
        titles.add(getString(R.string.foot_saiguo_txt));
        titles.add(getString(R.string.foot_saicheng_txt));
        titles.add(getString(R.string.foot_guanzhu_txt));

        fragments = new ArrayList<>();
        fragments.add(ImmedBasketballFragment.newInstance(IMMEDIA_FRAGMENT));
        fragments.add(ResultBasketballFragment.newInstance(RESULT_FRAGMENT));
        fragments.add(ScheduleBasketballFragment.newInstance(SCHEDULE_FRAGMENT));
        fragments.add(FocusBasketballFragment.newInstance(FOCUS_FRAGMENT));

        pureViewPagerAdapter = new PureViewPagerAdapter(fragments, titles, getChildFragmentManager());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                L.d(TAG, "onPageScrolled");
                L.d(TAG, "position = " + position);
                L.d(TAG, "positionOffset = " + positionOffset);
                L.d(TAG, "positionOffsetPixels = " + positionOffsetPixels);

                if (positionOffsetPixels == 0) {
                    switch (position) {
                        case IMMEDIA_FRAGMENT:
                            mFilterImgBtn.setVisibility(View.VISIBLE);
                            ((ImmedBasketballFragment) fragments.get(position)).LoadData();
                            break;
                        case RESULT_FRAGMENT:
                            mFilterImgBtn.setVisibility(View.VISIBLE);
                            ((ResultBasketballFragment) fragments.get(position)).updateAdapter();
                            break;
                        case SCHEDULE_FRAGMENT:
                            mFilterImgBtn.setVisibility(View.VISIBLE);
                            ((ScheduleBasketballFragment) fragments.get(position)).updateAdapter();
                            break;
                        case FOCUS_FRAGMENT:
                            mFilterImgBtn.setVisibility(View.GONE);
                            ((FocusBasketballFragment) fragments.get(position)).LoadData();
                            break;
                    }
                }
            }


            @Override
            public void onPageSelected(final int position) {
                /**判断四个Fragment切换显示或隐藏的状态 */
                switch (position) {
                    case IMMEDIA_FRAGMENT:
                        isImmediateFragment = true;
                        isResultFragment = false;
                        isScheduleFragment = false;
                        isFocusFragment = false;
                        break;
                    case RESULT_FRAGMENT:
                        isResultFragment = true;
                        isImmediateFragment = false;
                        isScheduleFragment = false;
                        isFocusFragment = false;
                        break;
                    case SCHEDULE_FRAGMENT:
                        isScheduleFragment = true;
                        isResultFragment = false;
                        isImmediateFragment = false;
                        isFocusFragment = false;
                        break;
                    case FOCUS_FRAGMENT:
                        isFocusFragment = true;
                        isScheduleFragment = false;
                        isResultFragment = false;
                        isImmediateFragment = false;
                        break;
                }
                if (isImmediateFragment) {
                    if (isResult) {
                        isResult = false;
                        L.d("xxx", "ResultFragment>>>隐藏");
                    }
                    if (isSchedule) {
                        isSchedule = false;
                        L.d("xxx", "ScheduleFragment>>>隐藏");
                    }
                    if (isFocus) {
                        isFocus = false;
                        L.d("xxx", "FocusFragment>>>隐藏");
                    }
                    isImmediate = true;
                    L.d("xxx", "ImmediateFragment>>>显示");
                }
                if (isResultFragment) {
                    if (isImmediate) {
                        isImmediate = false;
                        L.d("xxx", "ImmediateFragment>>>隐藏");
                    }
                    if (isSchedule) {
                        isSchedule = false;
                        L.d("xxx", "ScheduleFragment>>>隐藏");
                    }
                    if (isFocus) {
                        isFocus = false;
                        L.d("xxx", "FocusFragment>>>隐藏");
                    }
                    isResult = true;
                    L.d("xxx", "ResultFragment>>>显示");
                }
                if (isScheduleFragment) {
                    if (isImmediate) {
                        isImmediate = false;
                        L.d("xxx", "ImmediateFragment>>>隐藏");
                    }
                    if (isResult) {
                        isResult = false;
                        L.d("xxx", "ResultFragment>>>隐藏");
                    }
                    if (isFocus) {
                        isFocus = false;
                        L.d("xxx", "FocusFragment>>>隐藏");
                    }
                    isSchedule = true;
                    L.d("xxx", "ScheduleFragment>>>显示");
                }
                if (isFocusFragment) {
                    if (isImmediate) {
                        isImmediate = false;
                        L.d("xxx", "ImmediateFragment>>>隐藏");
                    }
                    if (isResult) {
                        isResult = false;
                        L.d("xxx", "ResultFragment>>>隐藏");
                    }
                    if (isSchedule) {
                        isSchedule = false;
                        L.d("xxx", "ScheduleFragment>>>隐藏");
                    }
                    isFocus = true;
                    L.d("xxx", "FocusFragment>>>显示");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(pureViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    private void initData() {
//        mBackImgBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().finish();
//                MobclickAgent.onEvent(mContext, "Football_Exit");
//            }
//        });

//        mFilterImgBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MobclickAgent.onEvent(mContext, "Football_Filtrate");
//                currentFragmentId = mViewPager.getCurrentItem();
//                if (currentFragmentId == IMMEDIA_FRAGMENT) {
//                    switch (ImmediateFragment.mLoadDataStatus) {
//                        case ImmediateFragment.LOAD_DATA_STATUS_ERROR:
//                            Intent intent = new Intent(getActivity(), FiltrateMatchConfigActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putBoolean(FiltrateMatchConfigActivity.NET_STATUS, ImmediateFragment.isNetSuccess);
//                            bundle.putInt("currentFragmentId", IMMEDIA_FRAGMENT);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                            break;
//                        case ImmediateFragment.LOAD_DATA_STATUS_SUCCESS:
//                            intent = new Intent(getActivity(), FiltrateMatchConfigActivity.class);
//                            bundle = new Bundle();
//                            LeagueCup[] allCups = ImmediateFragment.mCups.toArray(new LeagueCup[]{});
//                            bundle.putParcelableArray(FiltrateMatchConfigActivity.ALL_CUPS, allCups);// 传值到筛选页面的全部联赛，数据类型是LeagueCup[]
//                            bundle.putParcelableArray(FiltrateMatchConfigActivity.CHECKED_CUPS, ImmediateFragment.mCheckedCups);// 传值到筛选页面的已经选择的联赛，数据类型是LeagueCup[]
//                            bundle.putBoolean(FiltrateMatchConfigActivity.CHECKED_DEFUALT, ImmediateFragment.isCheckedDefualt);// 是否默认选择
//                            bundle.putBoolean(FiltrateMatchConfigActivity.NET_STATUS, ImmediateFragment.isNetSuccess);
//                            bundle.putInt("currentFragmentId", IMMEDIA_FRAGMENT);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                            break;
//                        case ImmediateFragment.LOAD_DATA_STATUS_INIT:
//                        case ImmediateFragment.LOAD_DATA_STATUS_LOADING:
//                            if (!ImmediateFragment.isPause) {
//                                Toast.makeText(getActivity(), R.string.toast_data_loading, Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                } else if (currentFragmentId == RESULT_FRAGMENT) {
//                    switch (ResultFragment.mLoadDataStatus) {
//                        case ResultFragment.LOAD_DATA_STATUS_ERROR:
//                            Intent mIntent = new Intent(getActivity(), FiltrateMatchConfigActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putBoolean(FiltrateMatchConfigActivity.NET_STATUS, ResultFragment.isNetSuccess);
//                            bundle.putInt("currentFragmentId", RESULT_FRAGMENT);
//                            mIntent.putExtras(bundle);
//                            startActivity(mIntent);
//                            break;
//                        case ResultFragment.LOAD_DATA_STATUS_SUCCESS:
//                            mIntent = new Intent(getActivity(), FiltrateMatchConfigActivity.class);
//                            bundle = new Bundle();
//                            bundle.putBoolean(FiltrateMatchConfigActivity.NET_STATUS, ResultFragment.isNetSuccess);
//                            LeagueCup[] allCups = ResultFragment.mCups.toArray(new LeagueCup[]{});
//                            //所有联赛
//                            bundle.putParcelableArray(FiltrateMatchConfigActivity.ALL_CUPS, allCups);
//                            //当前 选中联赛
//                            bundle.putParcelableArray(FiltrateMatchConfigActivity.CHECKED_CUPS, ResultFragment.mCheckedCups);
//                            // 是否默认选择  -- 显示选中（标红）
//                            bundle.putBoolean(FiltrateMatchConfigActivity.CHECKED_DEFUALT, ResultFragment.isCheckedDefualt);
//                            bundle.putInt("currentFragmentId", RESULT_FRAGMENT);
//                            mIntent.putExtras(bundle);
//                            startActivity(mIntent);
//                            //getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
//                            break;
//                        case ResultFragment.LOAD_DATA_STATUS_INIT:
//                        case ResultFragment.LOAD_DATA_STATUS_LOADING:
//                            Toast.makeText(getActivity(), R.string.toast_data_loading, Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                } else if (currentFragmentId == SCHEDULE_FRAGMENT) {
//                    if (ScheduleFragment.mLoadDataStatus == ScheduleFragment.LOAD_DATA_STATUS_SUCCESS) {
//                        Intent intent = new Intent(getActivity(), FiltrateMatchConfigActivity.class);
//                        Bundle bundle = new Bundle();
//                        LeagueCup[] allCups = ScheduleFragment.mAllCup.toArray(new LeagueCup[]{});
//                        bundle.putParcelableArray(FiltrateMatchConfigActivity.ALL_CUPS, allCups);
//                        bundle.putParcelableArray(FiltrateMatchConfigActivity.CHECKED_CUPS, ScheduleFragment.mCheckedCups);
//                        bundle.putBoolean(FiltrateMatchConfigActivity.NET_STATUS, ScheduleFragment.isNetSuccess);
//                        bundle.putBoolean(FiltrateMatchConfigActivity.CHECKED_DEFUALT, ScheduleFragment.isCheckedDefualt);
//                        bundle.putInt("currentFragmentId", SCHEDULE_FRAGMENT);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                    } else if (ScheduleFragment.mLoadDataStatus == ScheduleFragment.LOAD_DATA_STATUS_ERROR) {
//                        Intent intent = new Intent(getActivity(), FiltrateMatchConfigActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(FiltrateMatchConfigActivity.NET_STATUS, ScheduleFragment.isNetSuccess);
//                        bundle.putInt("currentFragmentId", SCHEDULE_FRAGMENT);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(getActivity(), R.string.toast_data_loading, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });


//        mSetImgBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MobclickAgent.onEvent(mContext, "Football_Setting");
//                currentFragmentId = mViewPager.getCurrentItem();
//                if (currentFragmentId == IMMEDIA_FRAGMENT) {
//                    Intent intent = new Intent(mContext, FootballTypeSettingActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("currentFragmentId", IMMEDIA_FRAGMENT);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//
//                } else if (currentFragmentId == RESULT_FRAGMENT) {
//                    Intent intent = new Intent(getActivity(), FootballTypeSettingActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("currentFragmentId", RESULT_FRAGMENT);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//
//                } else if (currentFragmentId == SCHEDULE_FRAGMENT) {
//                    Intent intent = new Intent(getActivity(), FootballTypeSettingActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("currentFragmentId", SCHEDULE_FRAGMENT);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                } else if (currentFragmentId == FOCUS_FRAGMENT) {
//                    Intent intent = new Intent(getActivity(), FootballTypeSettingActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("currentFragmentId", FOCUS_FRAGMENT);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//
//            }
//        });
    }


    public void focusCallback() {
        String focusIds = PreferenceUtil.getString("basket_focus_ids", "");
        String[] arrayId = focusIds.split("[,]");
        if ("".equals(focusIds) || arrayId.length == 0) {
            mTabLayout.getTabAt(3).setText(getString(R.string.foot_guanzhu_txt));
        } else {
            mTabLayout.getTabAt(3).setText(getString(R.string.foot_guanzhu_txt) + "(" + arrayId.length + ")");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onPause();
        } else {
            onResume();
            mSpinner.setSelection(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        L.d(TAG, "basket Fragment resume..");

        if (isImmediateFragment) {
            isImmediate = true;
            L.d("xxx", "ImmediateFragment>>>显示");
        }
        if (isResultFragment) {
            isResult = true;
            L.d("xxx", "ResultFragment>>>显示");
        }
        if (isScheduleFragment) {
            isSchedule = true;
            L.d("xxx", "ScheduleFragment>>>显示");
        }
        if (isFocusFragment) {
            isFocus = true;
            L.d("xxx", "FocusFragment>>>显示");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
            if (isImmediate) {
                isImmediate = false;
//                L.d("xxx", "ImmediateFragment>>>隐藏");
            }
            if (isResult) {
                isResult = false;
//                L.d("xxx", "ResultFragment>>>隐藏");
            }
            if (isSchedule) {
                isSchedule = false;
//                L.d("xxx", "ScheduleFragment>>>隐藏");
            }
            if (isFocus) {
                isFocus = false;
//                L.d("xxx", "FocusFragment>>>隐藏");
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.public_img_back:  //返回
                MobclickAgent.onEvent(mContext, "Basketball_Exit");
//                ((MainActivity) getActivity()).openLeftLayout();
                getActivity().finish();
                break;
            case R.id.public_btn_set:  //设置
                currentFragmentId = mViewPager.getCurrentItem();
                if (currentFragmentId == IMMEDIA_FRAGMENT) {
                    MobclickAgent.onEvent(mContext, "Basketball_Setting");
//                String s = getAppVersionCode(mContext);
                    mIntent = new Intent(getActivity(), BasketballSettingActivity.class);
//                getParentFragment().startActivityForResult(mIntent, REQUEST_SETTINGCODE);
//                getActivity().startActivityForResult(mIntent , REQUEST_SETTINGCODE);
                    Bundle bundleset = new Bundle();
                    bundleset.putInt("currentfragment", IMMEDIA_FRAGMENT);
                    mIntent.putExtras(bundleset);
                    startActivity(mIntent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                }else if (currentFragmentId == RESULT_FRAGMENT) {
                    MobclickAgent.onEvent(mContext, "Basketball_Setting");
//                String s = getAppVersionCode(mContext);
                    mIntent = new Intent(getActivity(), BasketballSettingActivity.class);
//                getParentFragment().startActivityForResult(mIntent, REQUEST_SETTINGCODE);
//                getActivity().startActivityForResult(mIntent , REQUEST_SETTINGCODE);
                    Bundle bundleset = new Bundle();
                    bundleset.putInt("currentfragment" , RESULT_FRAGMENT);
                    mIntent.putExtras(bundleset);
                    startActivity(mIntent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                }else if (currentFragmentId == SCHEDULE_FRAGMENT) {
                    MobclickAgent.onEvent(mContext, "Basketball_Setting");
//                String s = getAppVersionCode(mContext);
                    mIntent = new Intent(getActivity(), BasketballSettingActivity.class);
//                getParentFragment().startActivityForResult(mIntent, REQUEST_SETTINGCODE);
//                getActivity().startActivityForResult(mIntent , REQUEST_SETTINGCODE);
                    Bundle bundleset = new Bundle();
                    bundleset.putInt("currentfragment" , SCHEDULE_FRAGMENT);
                    mIntent.putExtras(bundleset);
                    startActivity(mIntent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                }else if(currentFragmentId == FOCUS_FRAGMENT){
                    MobclickAgent.onEvent(mContext, "Basketball_Setting");
//                String s = getAppVersionCode(mContext);
                    mIntent = new Intent(getActivity(), BasketballSettingActivity.class);
//                getParentFragment().startActivityForResult(mIntent, REQUEST_SETTINGCODE);
//                getActivity().startActivityForResult(mIntent , REQUEST_SETTINGCODE);
                    Bundle bundleset = new Bundle();
                    bundleset.putInt("currentfragment" , FOCUS_FRAGMENT);
                    mIntent.putExtras(bundleset);
                    startActivity(mIntent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                }

                break;
            case R.id.public_btn_filter: //筛选

                currentFragmentId = mViewPager.getCurrentItem();
                if (currentFragmentId == IMMEDIA_FRAGMENT) {
                    if (ImmedBasketballFragment.getIsLoad() == 1) {
                        MobclickAgent.onEvent(mContext, "Basketball_Filter");
                        Intent intent = new Intent(getActivity(), BasketFiltrateActivity.class);
                        intent.putExtra("MatchAllFilterDatas", (Serializable) ImmedBasketballFragment.mAllFilter);//Serializable 序列化传值（所有联赛数据）
                        intent.putExtra("MatchChickedFilterDatas", (Serializable) ImmedBasketballFragment.mChickedFilter);//Serializable 序列化传值（选中的联赛数据）
//                getParentFragment().startActivityForResult(intent, REQUEST_FILTERCODE);
                        intent.putExtra("currentfragment", IMMEDIA_FRAGMENT);
//                    getActivity().startActivityForResult(intent,ImmedBasketballFragment.REQUEST_FILTERCODE);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                    }else if(ImmedBasketballFragment.getIsLoad() == 0){
                        Toast.makeText(mContext, getResources().getText(R.string.immediate_unconection), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, getResources().getText(R.string.basket_loading_txt), Toast.LENGTH_SHORT).show();
                    }
                    L.d("mBasketballType jishi >>>>>>>>>>>","mBasketballType == >"+ IMMEDIA_FRAGMENT);
                }else if (currentFragmentId == RESULT_FRAGMENT) {
                    if (ResultBasketballFragment.isLoad == 1) {
                        MobclickAgent.onEvent(mContext, "Basketball_Filter");
                        Intent intent = new Intent(getActivity(), BasketFiltrateActivity.class);
                        intent.putExtra("MatchAllFilterDatas", (Serializable) ResultBasketballFragment.mAllFilter);//Serializable 序列化传值（所有联赛数据）
                        intent.putExtra("MatchChickedFilterDatas", (Serializable) ResultBasketballFragment.mChickedFilter);//Serializable 序列化传值（选中的联赛数据）
//                getParentFragment().startActivityForResult(intent, REQUEST_FILTERCODE);
//                    getActivity().startActivityForResult(intent, REQUEST_FILTERCODE);
                        Bundle bundle = new Bundle();
                        bundle.putInt("currentfragment" , RESULT_FRAGMENT);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                    }else if(ResultBasketballFragment.isLoad == 0){
                        Toast.makeText(mContext, getResources().getText(R.string.immediate_unconection), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, getResources().getText(R.string.basket_loading_txt), Toast.LENGTH_SHORT).show();
                    }
                    L.d("mBasketballType shaiguo >>>>>>>>>>>","mBasketballType == >"+RESULT_FRAGMENT);
                }else if (currentFragmentId == SCHEDULE_FRAGMENT) {
                    if (ScheduleBasketballFragment.isLoad == 1) {
                        MobclickAgent.onEvent(mContext, "Basketball_Filter");
                        Intent intent = new Intent(getActivity(), BasketFiltrateActivity.class);
                        intent.putExtra("MatchAllFilterDatas", (Serializable) ScheduleBasketballFragment.mAllFilter);//Serializable 序列化传值（所有联赛数据）
                        intent.putExtra("MatchChickedFilterDatas", (Serializable) ScheduleBasketballFragment.mChickedFilter);//Serializable 序列化传值（选中的联赛数据）
//                getParentFragment().startActivityForResult(intent, REQUEST_FILTERCODE);
//                    getActivity().startActivityForResult(intent, REQUEST_FILTERCODE);
                        Bundle bundle = new Bundle();
                        bundle.putInt("currentfragment" , SCHEDULE_FRAGMENT);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                    }else if(ScheduleBasketballFragment.isLoad == 0){
                        Toast.makeText(mContext, getResources().getText(R.string.immediate_unconection), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, getResources().getText(R.string.basket_loading_txt), Toast.LENGTH_SHORT).show();
                    }
                    L.d("mBasketballType shaicheng >>>>>>>>>>>","mBasketballType == >"+SCHEDULE_FRAGMENT);
                }
                break;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//    }
}