package com.hhly.mlottery.frame.basketballframe;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhly.mlottery.R;
import com.hhly.mlottery.activity.BasketDetailsActivityTest;
import com.hhly.mlottery.activity.BasketFiltrateActivity;
import com.hhly.mlottery.activity.BasketballScoresActivity;
import com.hhly.mlottery.activity.BasketballSettingActivity;
import com.hhly.mlottery.activity.LoginActivity;
import com.hhly.mlottery.adapter.basketball.PinnedHeaderExpandableScheduleAdapter;
import com.hhly.mlottery.bean.basket.BasketMatchBean;
import com.hhly.mlottery.bean.basket.BasketMatchFilter;
import com.hhly.mlottery.bean.basket.BasketRoot;
import com.hhly.mlottery.bean.basket.BasketRootBean;
import com.hhly.mlottery.config.BaseURLs;
import com.hhly.mlottery.config.StaticValues;
import com.hhly.mlottery.util.AppConstants;
import com.hhly.mlottery.util.DateUtil;
import com.hhly.mlottery.util.DisplayUtil;
import com.hhly.mlottery.util.FiltrateCupsMap;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.PreferenceUtil;
import com.hhly.mlottery.util.ResultDateUtil;
import com.hhly.mlottery.util.net.VolleyContentFast;
import com.hhly.mlottery.view.PinnedHeaderExpandableListView;
import com.umeng.analytics.MobclickAgent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 篮球比分fragment （赛程）
 * Created by yixq on 2015/12/30.
 */
public class ScheduleBasketballFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ExpandableListView.OnChildClickListener {

    private static final String TAG = "ImmedBasketballFragment";
    private static final String PARAMS = "BASKET_PARAMS";

    public final static String BASKET_FOCUS_IDS = "basket_focus_ids";

    public static final int TYPE_IMMEDIATE = 0;
    public static final int TYPE_RESULT = 1;
    public static final int TYPE_SCHEDULE = 2;
    public static final int TYPE_FOCUS = 3;

    private PinnedHeaderExpandableListView explistview;

    //筛选的数据
    public static List<BasketMatchFilter> mChickedFilter = new ArrayList<>();//选中的
    public static List<BasketMatchFilter> mAllFilter = new ArrayList<>();//所有的联赛
    //内容数据
    private List<List<BasketMatchBean>> mAllMatchdata;//所有的比赛list
    private List<String> mAllGroupdata;//所有日期list
    private List<List<BasketMatchBean>> childrenDataList;//显示的比赛list
    private List<String> groupDataList;//显示日期list
    private List<BasketRootBean> mMatchdata = new ArrayList<>();//match的 内容(json)

    private PinnedHeaderExpandableScheduleAdapter adapter;

    private View mView;
    private Context mContext;

    private RelativeLayout mbasketball_unfocus;//暂无关注图标
    private RelativeLayout mbasket_unfiltrate; //暂无赛选图标


    private LinearLayout mLoadingLayout;
    private RelativeLayout mNoDataLayout;
    private LinearLayout mErrorLayout;
    private TextView mReloadTvBtn;// 刷新 控件

    private Intent mIntent;

    private boolean isLoadData = false; //加载是否成功
    Handler mLoadHandler = new Handler();

    public static final int REQUEST_FILTERCODE = 0x62;//筛选的标记
    public static final int REQUEST_SETTINGCODE = 0x61;//设置的标记
    public static final int REQUEST_DETAILSCODE = 0x66;

    private BasketFocusClickListener mFocusClickListener; //关注点击监听

    private URI mScoketuri = null;//推送 URI

    private boolean isError = false;

    private SwipeRefreshLayout mSwipeRefreshLayout; //下拉刷新

    private int mBasketballType; //判断是哪个fragment(即时 赛果 赛程)

    private boolean isFilter = false;  //是否赛选过
    private String url;

    public static int isLoad = -1;

    public static int getIsLoad() {
        return isLoad;
    }

    /**
     * 关注事件EventBus
     */
    public static EventBus BasketScheduleEventBus;

    /**
     * 切换后更新显示的fragment
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        updateAdapter();
    }
    /**
     * fragment 传参
     *
     * @param basketballType
     * @return
     */
    public static ScheduleBasketballFragment newInstance(int basketballType) {
        ScheduleBasketballFragment fragment = new ScheduleBasketballFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAMS, basketballType);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mBasketballType = getArguments().getInt(PARAMS);
            mBasketballType = 2;
        }
        BasketScheduleEventBus = new EventBus();
        BasketScheduleEventBus.register(this);
    }

    /**
     * fragment 选择
     */
    public void fragmentType() {

        switch (mBasketballType) {
            case TYPE_IMMEDIATE:
                url = BaseURLs.URL_BASKET_IMMEDIATE;
                break;
            case TYPE_RESULT:
                url = BaseURLs.URL_BASKET_RESULT;
                break;
            case TYPE_SCHEDULE:
                url = BaseURLs.URL_BASKET_SCHEDULE;
                break;
            case TYPE_FOCUS:
                url = BaseURLs.URL_BASKET_FOCUS;
                break;
            default:
                break;
        }
    }

    /**
     * 返回当前程序版本号 versioncode
     */
    public static String getAppVersionCode(Context context) {
        String versioncode = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//            versionName = pi.versionName;
            versioncode = pi.versionCode+"";
            if (versioncode == null || versioncode.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versioncode;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mView = inflater.inflate(R.layout.pinned_listview, container, false);

        fragmentType(); //fragment 入口

        initView();
//        initBroadCase();//添加监听
        try {
//            mScoketuri = new URI("ws://192.168.10.242:61634/ws");//推送 URI  "ws://m.1332255.com/ws"
            mScoketuri = new URI(BaseURLs.URL_BASKET_SOCKET);//推送 URI
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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

//    /**
//     * 启动广播监听
//     */
//    private void initBroadCase() {
//        if (getActivity() != null) {
//            mNetStateReceiver = new ImmediateStateBroadcastReceiver();
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//            getActivity().registerReceiver(mNetStateReceiver, filter);
//        }
//    }

    /**
     * 初始化VIEW
     */
    private void initView() {
        //暂无关注
        mbasketball_unfocus = (RelativeLayout) mView.findViewById(R.id.basketball_immediate_unfocus_pinned);

        //暂无筛选
        mbasket_unfiltrate = (RelativeLayout)mView.findViewById(R.id.basket_unfiltrate);

        explistview = (PinnedHeaderExpandableListView) mView.findViewById(R.id.explistview);
        explistview.setChildDivider(getContext().getResources().getDrawable(R.color.linecolor)); //设置分割线 适配魅族

        explistview.setOnChildClickListener(this);

        //设置悬浮头部VIEW
        explistview.setHeaderView(getActivity().getLayoutInflater().inflate(R.layout.basketball_riqi_head, explistview, false));//悬浮头

        // 下拉刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.basketball_swiperefreshlayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_header);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, DisplayUtil.dip2px(getContext(), StaticValues.REFRASH_OFFSET_END));

//        //标题头部
//        mTittle = (TextView) mView.findViewById(R.id.public_txt_title);
//        mTittle.setText(R.string.basket_tittle);

        //设置按钮
//        mSetting = (ImageView) mView.findViewById(R.id.public_btn_set);
//        mSetting.setOnClickListener(this);

        // 筛选
//        mFilterImgBtn = (ImageView) mView.findViewById(R.id.public_btn_filter);
//        if (mBasketballType == TYPE_FOCUS) {
//            mFilterImgBtn.setVisibility(View.GONE);
//        } else {
//            mFilterImgBtn.setVisibility(View.VISIBLE);
//            mFilterImgBtn.setOnClickListener(this);
//        }

        //返回
//        mIb_back = (ImageView) mView.findViewById(R.id.public_img_back);
//        mIb_back.setOnClickListener(this);

        // 加载状态图标
        mLoadingLayout = (LinearLayout) mView.findViewById(R.id.basketball_immediate_loading);

        mNoDataLayout = (RelativeLayout)mView.findViewById(R.id.basket_undata);

        mErrorLayout = (LinearLayout) mView.findViewById(R.id.basketball_immediate_error);
        mReloadTvBtn = (TextView) mView.findViewById(R.id.basketball_immediate_error_btn);
        mReloadTvBtn.setOnClickListener(this);

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
//        mFilterImgBtn.setClickable(false); // 默认设置不可点击，防止网络差时在数据请求过程中无数据时点击筛选出现空白显示情况
        /**
         * 数据访问成功时打开赛选开关
         */
//        mFilterImgBtn.setClickable(true);
    }

    private int mSize; //记录共有几天的数据

    private void initData() {
//        ((BasketScoresFragment)getParentFragment()).getBasketballUserConcern(); //获取关注列表
        BasketballScoresActivity parentActivity = (BasketballScoresActivity)getActivity();
        parentActivity.getBasketballUserConcern();

        Map<String, String> params = new HashMap<>();
        String version = getAppVersionCode(mContext);//获得当前版本号 android:versionCode="5"

        if (mBasketballType == TYPE_FOCUS) { //判断是否是关注页面

            String fucusid = PreferenceUtil.getString(BASKET_FOCUS_IDS, "");

            if ("".equals(fucusid) || ",".equals(fucusid) || fucusid.length() == 0) {
                mbasketball_unfocus.setVisibility(View.VISIBLE); // 暂无关注
                mLoadingLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                return;
            } else {
                params.put("favourite", fucusid);
            }
        }
        params.put("version",version);//接口添加 version=xx 字段
        params.put("appType","2");//接口添加 &appType=2 字段

        VolleyContentFast.requestJsonByGet(url, params, new VolleyContentFast.ResponseSuccessListener<BasketRoot>() {
            @Override
            public void onResponse(BasketRoot json) {

                if (getActivity() == null) {
                    return;
                }

                isLoad = 1;
                if (json == null || json.getMatchData() == null || json.getMatchData().size() == 0) {
                    if (mBasketballType == TYPE_FOCUS) {
                        PreferenceUtil.commitString(BASKET_FOCUS_IDS, "");
                    }
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mLoadingLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    return;
                }

                mMatchdata = json.getMatchData();

                if (mBasketballType != TYPE_FOCUS) { //非关注页面
                    mAllFilter = json.getMatchFilter();
                    // mChickedFilter = json.getMatchFilter();//默认选中全部 -->mChickedFilter不赋值 默认全不选
                }
                mSize = json.getMatchData().size();

                groupDataList = new ArrayList<String>();
                childrenDataList = new ArrayList<List<BasketMatchBean>>();
                mAllGroupdata = new ArrayList<>();
                mAllMatchdata = new ArrayList<>();
                for (int i = 0; i < mSize; i++) {
                    /**
                     *  获得星期
                     */
                    String week = ResultDateUtil.getWeekOfDate(DateUtil.parseDate(ResultDateUtil.getDate(0, json.getMatchData().get(i).getDate())));
                    /**
                     * 子view数据
                     */
                    //  childrenDataList.add(mMatchdata.get(i).getMatch());
                    mAllMatchdata.add(mMatchdata.get(i).getMatch());//显示的
                    /**
                     * 外层 view数据
                     */
                    // groupDataList.add(mMatchdata.get(i).getDate() + "," + week);
                    // isToday.add(mMatchdata.get(i).getDiffDays());// 获得日期状态码（昨天 今天 明天）
                    mAllGroupdata.add(mMatchdata.get(i).getDate() + "," + week + "," + mMatchdata.get(i).getDiffDays() + "");
                }

                /**
                 *判断是否 经过筛选
                 */
                if (isFilter) { //已筛选
                    for (List<BasketMatchBean> lists : mAllMatchdata) { // 遍历所有数据 得到筛选后的
                        List<BasketMatchBean> checkedMatchs = new ArrayList<>();
                        for (BasketMatchBean matchBean : lists) {
                            for (String checkedId : FiltrateCupsMap.basketImmedateCups) {
                                if (matchBean.getLeagueId().equals(checkedId)) {
                                    checkedMatchs.add(matchBean);
                                }
                            }
                        }
                        if (checkedMatchs.size() != 0) {
                            childrenDataList.add(checkedMatchs); //筛选后的比赛list
                            for (String groupdata : mAllGroupdata) {
                                String[] weekdatas = groupdata.split(",");
                                String datas = weekdatas[0];
                                if (checkedMatchs.get(0).getDate().equals(datas)) {
                                    groupDataList.add(groupdata);//赛选后的日期list
                                    break;
                                }
                            }
                        }
                    }
                } else { //未筛选
                    for (List<BasketMatchBean> lists : mAllMatchdata) {
                        childrenDataList.add(lists);
                    }
                    for (String groupdata : mAllGroupdata) {
                        groupDataList.add(groupdata);
                    }
                    mChickedFilter = mAllFilter;//默认选中全部
                }

                if (adapter == null || mBasketballType == TYPE_IMMEDIATE || mBasketballType == TYPE_FOCUS) {
                    adapter = new PinnedHeaderExpandableScheduleAdapter(childrenDataList, groupDataList, getActivity(), explistview);
                    explistview.setAdapter(adapter);

                    adapter.setmFocus(mFocusClickListener);//设置关注
                } else {
                    updateAdapter();
                }
//                /**
//                 * 判断 如果是即时和关注界面 则开启socket服务
//                 */
//                if (mBasketballType == TYPE_IMMEDIATE || mBasketballType == TYPE_FOCUS) {
//                    startWebsocket(); //启动socket
//                }
                //全部展开
                for (int i = 0; i < groupDataList.size(); i++) {
                    explistview.expandGroup(i);
                    /**
                     * 设置group的默认打开状态，解决默认全部打开后点击group第一次无效问题（点击两次才收起）
                     */
                    if (adapter != null) {
                        adapter.setGroupClickStatus(i, 1);//收起 ： 0  展开 ：1
                    }
                }
                isLoadData = true;

                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mLoadingLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);

            }
        }, new VolleyContentFast.ResponseErrorListener() {
            /**
             * 加载失败
             *
             * @param exception
             */
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                L.e(TAG, "exception.getErrorCode() = " + exception.getErrorCode());

                isLoad = 0;

                isLoadData = false;
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mLoadingLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        }, BasketRoot.class);
        fucusChicked();//点击关注监听
    }

    /**
     * 点击关注事件
     */
    public void fucusChicked() {
        mFocusClickListener = new BasketFocusClickListener() {
            @Override
            public void FocusOnClick(View v, BasketMatchBean root) {

                boolean isCheck = (Boolean) v.getTag();// 检查之前是否被选中

                if (!isCheck) {//未关注->关注
                    FocusBasketballFragment.addFocusId(root.getThirdId());
                    v.setTag(true);

                } else {//关注->未关注
                    FocusBasketballFragment.deleteFocusId(root.getThirdId());
                    v.setTag(false);
                    //判断 如果是关注页面
                    if (mBasketballType == TYPE_FOCUS) {
                        List<BasketMatchBean> emptyMatchs = null;
                        for (int i = 0; i < childrenDataList.size(); i++) {
                            childrenDataList.get(i).remove(root);
                            if (childrenDataList.get(i).size() == 0) {
                                groupDataList.remove(i);
                                emptyMatchs = childrenDataList.get(i);
                            }
                        }
                        if (emptyMatchs != null) {
                            childrenDataList.remove(emptyMatchs);
                        }
                        if (groupDataList.size() == 0) {
                            mbasketball_unfocus.setVisibility(View.VISIBLE); // 暂无关注
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            mLoadingLayout.setVisibility(View.GONE);
                        } else {
                            adapter = new PinnedHeaderExpandableScheduleAdapter(childrenDataList, groupDataList, getActivity(), explistview);
                            explistview.setAdapter(adapter);
                            adapter.setmFocus(mFocusClickListener);//设置关注
                        }
                        //全部展开
                        for (int i = 0; i < groupDataList.size(); i++) {
                            explistview.expandGroup(i);
                        }
//                        ((BasketballFragment) getParentFragment()).focusCallback();
//                        ((BasketListActivity) getActivity()).focusCallback();
//                        ((BasketScoresFragment)getParentFragment()).focusCallback();
                        ((BasketballScoresActivity) getActivity()).focusCallback();
                        return;
                    }
                }
                updateAdapter();//防止复用
//                ((BasketballFragment) getParentFragment()).focusCallback();
//                ((BasketListActivity) getActivity()).focusCallback();
//                ((BasketScoresFragment)getParentFragment()).focusCallback();
                ((BasketballScoresActivity) getActivity()).focusCallback();
            }
        };
    }
    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        isLoad = -1;
        mLoadHandler.postDelayed(mRun, 0);
    }

    /**
     * list点击事件
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(getActivity(), BasketDetailsActivityTest.class);
        intent.putExtra(BasketDetailsActivityTest.BASKET_THIRD_ID, childrenDataList.get(groupPosition).get(childPosition).getThirdId());//跳转到详情
        intent.putExtra(BasketDetailsActivityTest.BASKET_MATCH_STATUS, childrenDataList.get(groupPosition).get(childPosition).getMatchStatus());//跳转到详情
        //用getActivity().startActivityForResult();不走onActivityResult ;
//        startActivityForResult(intent, REQUEST_DETAILSCODE);
        intent.putExtra("currentfragment", mBasketballType);
        intent.putExtra(BasketDetailsActivityTest.BASKET_MATCH_LEAGUEID , childrenDataList.get(groupPosition).get(childPosition).getLeagueId());
        intent.putExtra(BasketDetailsActivityTest.BASKET_MATCH_MATCHTYPE , childrenDataList.get(groupPosition).get(childPosition).getMatchType());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
        MobclickAgent.onEvent(mContext,"Basketball_ListItem");
        return false;
    }

    // 定义关注监听
    public interface BasketFocusClickListener {
        void FocusOnClick(View view, BasketMatchBean root);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.public_btn_set:  //设置
                MobclickAgent.onEvent(mContext, "Basketball_Setting");
//                String s = getAppVersionCode(mContext);
                mIntent = new Intent(getActivity(), BasketballSettingActivity.class);
//                getParentFragment().startActivityForResult(mIntent, REQUEST_SETTINGCODE);
//                getActivity().startActivityForResult(mIntent , REQUEST_SETTINGCODE);
                Bundle bundleset = new Bundle();
                bundleset.putInt("currentfragment" , mBasketballType);
                mIntent.putExtras(bundleset);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_fix_out);
                break;

//
            case R.id.public_img_back:  //返回
                MobclickAgent.onEvent(mContext, "Basketball_Exit");
//                ((MainActivity) getActivity()).openLeftLayout();
                getActivity().finish();
                break;

            case R.id.basketball_immediate_error_btn:

                isLoad = -1;
                MobclickAgent.onEvent(mContext, "Basketball_Refresh");
                mLoadingLayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mLoadHandler.postDelayed(mRun, 0);
                break;
            default:
                break;
        }
    }

    public void updateAdapter() {
        if (adapter == null) {
            return;
        }

        adapter.updateDatas(childrenDataList, groupDataList);
        adapter.notifyDataSetChanged();
        //设置打开全部日期内容
//        for (int i = 0; i < groupDataList.size(); i++) {
//            explistview.expandGroup(i);
//        }
    }

    public void LoadData(){
        mLoadHandler.postDelayed(mRun, 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        L.v(TAG, "___onResume___");
        if (!isLoadData) {
            mLoadHandler.postDelayed(mRun, 0);
        }
    }

    /**
     * 设置返回
     */
    public void onEventMainThread(Integer currentFragmentId) {
        updateAdapter();
        L.d("设置返回 " , "000000");
        /**
         * 过滤筛选0场的情况，防止筛选0场时刷新 切换设置时出现异常
         */
        if (mChickedFilter.size() != 0 || mBasketballType == TYPE_FOCUS){
            if (childrenDataList != null) {
                if (childrenDataList.size() != 0) {
                    updateAdapter();
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{
                    mbasket_unfiltrate.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }else{
                mbasket_unfiltrate.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.GONE);
            }
        }else{

            mbasket_unfiltrate.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.GONE);
        }
    }
    /**
     * 筛选返回
     */
    public void onEventMainThread(Map<String,Object> map) {
        L.d("AAAAA-++++++--" ,"checkedIds.length");
        String[] checkedIds = (String[])((List)map.get(BasketFiltrateActivity.CHECKED_CUPS_IDS)).toArray(new String[]{});

        L.d("AAAAA------------------" ,checkedIds.length+"");
//        String[] checkedIds = (String[]) map.getCharSequenceArrayExtra(BasketFiltrateActivity.CHECKED_CUPS_IDS);// 返回数据是选择后的id字符串数组，数据类型String
        isFilter = true;

        FiltrateCupsMap.basketImmedateCups = checkedIds;
        if (checkedIds.length == 0) {
            List<BasketMatchFilter> noCheckedFilters = new ArrayList<>();
            mChickedFilter = noCheckedFilters;//筛选0场后，再次进入赛选页面 显示已选中0场（全部不选中）

            mbasket_unfiltrate.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.GONE);
        } else {

            L.d("123" ,"childrenDataList = " + childrenDataList.size());
            childrenDataList.clear();
            groupDataList.clear();
            for (List<BasketMatchBean> lists : mAllMatchdata) { // 遍历所有数据 得到筛选后的
                List<BasketMatchBean> checkedMatchs = new ArrayList<>();
                for (BasketMatchBean matchBean : lists) {
                    boolean isExistId = false;
                    for (String checkedId : checkedIds) {
                        if (matchBean.getLeagueId().equals(checkedId)) {
                            isExistId = true;
                            break;
                        }
                    }
                    if (isExistId) {
                        //groupDataList.add(aa.getDate()+","+"day");
                        //childrenDataList.add(lists);
                        checkedMatchs.add(matchBean);
                    }
                }
                if (checkedMatchs.size() != 0) {
                    childrenDataList.add(checkedMatchs);
                    for (String groupdata : mAllGroupdata) {
                        String[] weekdatas = groupdata.split(",");
                        String datas = weekdatas[0];
                        if (checkedMatchs.get(0).getDate().equals(datas)) {
                            groupDataList.add(groupdata);
                            break;
                        }
                    }
                }
            }
            List<BasketMatchFilter> checkedFilters = new ArrayList<>();
            // mChickedFilter.clear(); // 清除原来选中的
            for (BasketMatchFilter allFilter : mAllFilter) {
                for (String checkedId : checkedIds) {
                    if (allFilter.getLeagueId().equals(checkedId)) {
                        checkedFilters.add(allFilter);
                    }
                }
            }
            mChickedFilter = checkedFilters;
            mbasket_unfiltrate.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

            L.d("123" ,"childrenDataList >>>> = " + childrenDataList.size());
            updateAdapter();
            // 设置打开全部日期内容
            for (int i = 0; i < groupDataList.size(); i++) {
                explistview.expandGroup(i);
            }
        }
    }

    /**
     * 详情页面返回
     * @param id
     */
    public void onEventMainThread(String id) {
        //判断 关注页面重新请求 （详情中取消关注返回时关注页面跟着变化）
        if (mBasketballType == TYPE_FOCUS) {
            mLoadHandler.postDelayed(mRun, 0);
//            ((BasketListActivity) getActivity()).focusCallback();
//            ((BasketScoresFragment)getParentFragment()).focusCallback();
            ((BasketballScoresActivity) getActivity()).focusCallback();
        }else{
            updateAdapter();
//            ((BasketListActivity) getActivity()).focusCallback();
//            ((BasketScoresFragment)getParentFragment()).focusCallback();
            ((BasketballScoresActivity) getActivity()).focusCallback();
        }
//        ((BasketScoresFragment)getParentFragment()).focusCallback();
    }

}
