package com.hhly.mlottery.frame.footframe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hhly.mlottery.R;
import com.hhly.mlottery.adapter.basketball.BasketballDatabaseScheduleSectionAdapter;
import com.hhly.mlottery.adapter.basketball.SportsDialogAdapter;
import com.hhly.mlottery.adapter.football.FootballDatabaseScheduleSectionAdapter;
import com.hhly.mlottery.bean.footballDetails.database.DataBaseBean;
import com.hhly.mlottery.bean.footballDetails.footballdatabasebean.ScheduleBean;
import com.hhly.mlottery.bean.footballDetails.footballdatabasebean.ScheduleDatasBean;
import com.hhly.mlottery.bean.footballDetails.footballdatabasebean.ScheduleRaceBean;
import com.hhly.mlottery.config.BaseURLs;
import com.hhly.mlottery.util.CollectionUtils;
import com.hhly.mlottery.util.DisplayUtil;
import com.hhly.mlottery.util.LocaleFactory;
import com.hhly.mlottery.util.net.VolleyContentFast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * description:
 * author: yixq
 * Created by A on 2016/9/2.
 * 足球资料库赛程
 */
public class FootballDatabaseScheduleNewFragment extends Fragment implements View.OnClickListener {

    private static final String LEAGUE = "league";
    private static final String PARAM_ID = "leagueId";
    private static final String PARAM_DATE = "leagueDate";
    private static final String PARAM_MATCH_ROUND = "leagueRound";//二级
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_LEAGUE_GROUP = "leagueGroup"; // 一级


    private static final int STATUS_LOADING = 1;
    private static final int STATUS_ERROR = 2;
    private static final int STATUS_NO_DATA = 3;

    View mButtonFrame;
    TextView mTitleTextView;
    ImageView mLeftButton;
    ImageView mRightButton;

    View mEmptyView;
    ProgressBar mProgressBar;
    View mErrorLayout;
    TextView mRefreshTextView;
    TextView mNoDataTextView;

    RecyclerView mRecyclerView;

    private DataBaseBean league;
    private String mLeagueDate = null;

    private List<BasketballDatabaseScheduleSectionAdapter.Section> mSections;
    private List<FootballDatabaseScheduleSectionAdapter.Section> mSectionsNew;
    private BasketballDatabaseScheduleSectionAdapter mAdapter;
    private FootballDatabaseScheduleSectionAdapter mAdapterNew;
    private String[] mRoundString;
    private String mLeagueRound = "";
    private String mLeagueGroup = "";
    private boolean isLoad = false;//是否可选择
    private String url ;
    private int currentPosition = -1; // 当前选中 （默认选中第一项）
    private int currentFirstPosition = -1;  //一级菜单选中
    private int currentSecondPosition = -1; //二级菜单选中
    private ScheduleBean mResultNew; // json 数据
    private RecyclerView mFirstRecyclerView;
    private RecyclerView mSecondRecyclerView;
    private List<String> mFirstData;
    private List<String> mSecondData = new ArrayList<>();
    private boolean chooseSecond = false;
    private String[] arr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            league = args.getParcelable(LEAGUE);
            mLeagueDate = args.getString(PARAM_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mEmptyView = inflater.inflate(R.layout.basket_database_empty_layout, container, false);
        return inflater.inflate(R.layout.fragment_basket_database_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonFrame = LayoutInflater.from(view.getContext())
                .inflate(R.layout.layout_basket_database_choose, (ViewGroup) view, false);
        ViewGroup.LayoutParams layoutParams = mEmptyView.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(getContext(), 178);
        mEmptyView.setLayoutParams(layoutParams);

        mTitleTextView = (TextView) mButtonFrame.findViewById(R.id.title_button);
        mLeftButton = (ImageView) mButtonFrame.findViewById(R.id.left_button);
        mRightButton = (ImageView) mButtonFrame.findViewById(R.id.right_button);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        initEmptyView();

        initRecycler();

        mTitleTextView.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);

        initData();
    }

    private void initEmptyView() {
        mProgressBar = (ProgressBar) mEmptyView.findViewById(R.id.progress);
        mErrorLayout = mEmptyView.findViewById(R.id.error_layout);
        mRefreshTextView = (TextView) mEmptyView.findViewById(R.id.reloading_txt);
        mNoDataTextView = (TextView) mEmptyView.findViewById(R.id.no_data_txt);

        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    /**
     * 加载前一项（左点击）
     * @param currPosition
     */
    private void loadLeft(int currPosition){
        url = BaseURLs.URL_FOOTBALL_DATABASE_SCHEDULE_UNFIRST; //选择后的URL
        if (chooseSecond) {
            currentSecondPosition = currPosition - 1;
            if (currentSecondPosition <= 0) {
                mLeagueRound = arr[0];
            }else{
                mLeagueRound = arr[currentSecondPosition];
            }
            handleHeadViewNew(arr ,currentSecondPosition);
            initData();
        }else{
            currentPosition = currPosition - 1;
            if (currentPosition <= 0) {
                mLeagueRound = mRoundString[0];
            }else{
                mLeagueRound = mRoundString[currentPosition];
            }
            handleHeadViewNew(mRoundString ,currentPosition);
            initData();
        }
    }
    /**
     * 加载后一项（右点击）
     * @param currPosition
     */
    private void loadRight(int currPosition){
        url = BaseURLs.URL_FOOTBALL_DATABASE_SCHEDULE_UNFIRST; //选择后的URL
        if (chooseSecond) {
            currentSecondPosition = currPosition + 1;
            if (currentSecondPosition >= arr.length-1) {
                mLeagueRound = arr[arr.length-1];
            }else{
                mLeagueRound = arr[currentSecondPosition];
            }
            handleHeadViewNew(arr ,currentSecondPosition);
            initData();
        }else{
            currentPosition = currPosition + 1;
            if (currentPosition >= mRoundString.length-1) {
                mLeagueRound = mRoundString[mRoundString.length-1];
            }else{
                mLeagueRound = mRoundString[currentPosition];
            }
            handleHeadViewNew(mRoundString ,currentPosition);
            initData();
        }
    }

    /**
     * 刷新数据
     */
    public void update() {
        initData();
    }

    /**
     * 设置状态
     *
     * @param status
     */
    public void setStatus(int status) {
        mNoDataTextView.setVisibility(status == STATUS_NO_DATA ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(status == STATUS_LOADING ? View.VISIBLE : View.GONE);
        mErrorLayout.setVisibility(status == STATUS_ERROR ? View.VISIBLE : View.GONE);
    }

    private void initData(){
        mSectionsNew.clear();
        mAdapterNew.notifyDataSetChanged();
        setStatus(STATUS_LOADING);

        // http://192.168.31.115:8888/mlottery/core/basketballData.findSchedule.do?lang=zh&leagueId=1&season=2015-2016
        //http://192.168.31.8:8080/mlottery/core/androidLeagueData.findAndroidLeagueRound.do?lang=zh&leagueId=36&type=0&leagueDate=2016-2017&leagueRound=4
        //http://192.168.31.8:8080/mlottery/core/androidLeagueData.findAndroidLeagueRound.do?lang=zh&leagueId=60&type=0&timeZone=8
//        String murl = "192.168.31.8:8080/mlottery/core/androidLeagueData.findAndroidLeagueRound.do";
        Map<String , String> map = new HashMap();
        map.put(PARAM_ID , league.getLeagueId());
//        map.put(PARAM_ID , "273");
        map.put(PARAM_TYPE , league.getKind());
//        map.put(PARAM_TYPE , "1");
        if (mLeagueDate != null) {
            map.put(PARAM_DATE , mLeagueDate);
        }
        if (mLeagueRound != null && !mLeagueRound.equals("")) {
            map.put(PARAM_MATCH_ROUND , mLeagueRound);
        }
        if (mLeagueGroup != null && !mLeagueGroup.equals("")) {
            map.put(PARAM_LEAGUE_GROUP , mLeagueGroup);
        }

        if (url == null || url == "") {
            url = BaseURLs.URL_FOOTBALL_DATABASE_SCHEDULE_FIRST; //第一次进入的url
        }
        VolleyContentFast.requestJsonByGet(url, map,
                new VolleyContentFast.ResponseSuccessListener<ScheduleBean>() {
                    @Override
                    public void onResponse(ScheduleBean result) {
                        if (result == null || result.getRace() == null) {
                            setStatus(STATUS_NO_DATA);
                            mButtonFrame.setVisibility(View.GONE);
                            return;
                        }
                        mButtonFrame.setVisibility(View.VISIBLE);

                        if (result.getCurrentGroup() != null && result.getType() == 1) {
                            mResultNew = result;
                            chooseSecond = true;
                        }

                        if (result.getData() != null) {
                            if (result.getCurrentGroup() != null) {
                                mRoundString = result.getData().get(result.getCurrentGroup());
                                arr =  result.getData().get(result.getCurrentGroup());
                            }else{
                                mRoundString = result.getData().get("emp");
                                arr =  result.getData().get(result.getCurrentGroup());
                            }

                            /**
                             * 获得一级菜单数据
                             */
                            mFirstData = new ArrayList<>();

                            Set key = result.getData().keySet();//得到map所有的key
                            Iterator iter = key.iterator();//遍历key ==》 list
                            while (iter.hasNext()) {
                                String keyName = (String)iter.next();
                                mFirstData.add(keyName);
                            }
                            if (mFirstData != null) {
                                for (int i = 0; i < mFirstData.size(); i++) {
                                    if (mFirstData.get(i).equals(result.getCurrentGroup())) {
                                        currentFirstPosition = i;
                                    }
                                }
                            }

                            /**
                             * 获得二级菜单数据
                             */
                            for (int i = 0; i < mRoundString.length; i++) {
                                mSecondData.add(mRoundString[i]);
                            }
                            currentSecondPosition = result.getCurrentRoundIndex()-1;
                        }

                        if (mRoundString != null && mRoundString.length != 0) {
                            isLoad = true;
                        }
                        if (currentPosition == -1) {
                            currentPosition = result.getCurrentRoundIndex()-1;
                            handleHeadViewNew(mRoundString ,currentPosition);
                        }
                        handleDataNew(result.getRace());
                        mAdapterNew.notifyDataSetChanged();
                    }
                }, new VolleyContentFast.ResponseErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyContentFast.VolleyException e) {
                        isLoad = false;
                        VolleyError error = e.getVolleyError();
                        if (error != null) error.printStackTrace();
                        setStatus(STATUS_ERROR);
                    }
                }, ScheduleBean.class);
    }

    /**
     *   选择按钮
     * @param data  阶段选择内容
     * @param currnIndex 当前选择的position
     */
    private void handleHeadViewNew(String[] data , Integer currnIndex){

        if (data == null || data.length == 0) {
            mTitleTextView.setText("");
        }else{
            mTitleTextView.setText(data[currnIndex]);
        }

        mLeftButton.setVisibility(currnIndex <= 0 ? View.GONE : View.VISIBLE);
        mRightButton.setVisibility(currnIndex >= data.length-1 ? View.GONE : View.VISIBLE);

    }

    /**
     * 列表数据
     * @param matchData
     */
    private void handleDataNew(List<ScheduleRaceBean> matchData) {
        if (CollectionUtils.notEmpty(matchData)) {
            for (ScheduleRaceBean matchDay : matchData) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E", LocaleFactory.get());
                mSectionsNew.add(new FootballDatabaseScheduleSectionAdapter
                        .Section(true, dateFormat.format(matchDay.getDay())));
                for (ScheduleDatasBean match : matchDay.getDatas()) {
                    mSectionsNew.add(new FootballDatabaseScheduleSectionAdapter
                            .Section(match));
                }
            }
        } else {
            setStatus(STATUS_NO_DATA);
            mButtonFrame.setVisibility(View.GONE);
        }
    }

    /**
     * 选中器弹框
     */
    public void setDialog(){
//        if (mResultNew == null) {
//            return;
//        }
        /**
         * type == 1 子联赛（二级菜单）
         */
        if (chooseSecond) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.basket_database_league_dialog, null);
            TextView titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(getString(R.string.football_database_details_stage));

            final AlertDialog mAlertDialog = mBuilder.create();
            mAlertDialog.setCanceledOnTouchOutside(true);//设置空白处点击 dialog消失

            mFirstRecyclerView = (RecyclerView) view.findViewById(R.id.first_recycler_view);
            GridLayoutManager firstGridLayoutManager =
                    new GridLayoutManager(getContext(), mResultNew.getData().size());
            mFirstRecyclerView.setLayoutManager(firstGridLayoutManager);
            final FirstAdapter firstAdapter = new FirstAdapter(mFirstData , currentFirstPosition);

            mFirstRecyclerView.setAdapter(firstAdapter);

            mSecondRecyclerView = (RecyclerView) view.findViewById(R.id.second_recycler_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
            mSecondRecyclerView.setLayoutManager(gridLayoutManager);
            final SecondAdapter secondAdapter = new SecondAdapter(mSecondData , currentSecondPosition);
            mSecondRecyclerView.setAdapter(secondAdapter);

            firstAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    currentFirstPosition=position;
                    firstAdapter.notifyDataSetChanged();

                    mSecondData.clear();
                    String[] newSecondData = mResultNew.getData().get(mFirstData.get(position));
                    for (int i = 0; i < newSecondData.length; i++) {
                        mSecondData.add(newSecondData[i]);
                    }
                    currentSecondPosition = 0;
                    secondAdapter.notifyDataSetChanged();
                }
            });

            secondAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    currentSecondPosition=position;
                    secondAdapter.notifyDataSetChanged();
                }
            });

            view.findViewById(R.id.text_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            view.findViewById(R.id.text_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    url = BaseURLs.URL_FOOTBALL_DATABASE_SCHEDULE_UNFIRST; //选择后的URL

                    mLeagueGroup = mFirstData.get(currentFirstPosition);
                    mLeagueRound = mSecondData.get(currentSecondPosition);
                    arr = mResultNew.getData().get(mFirstData.get(currentFirstPosition));

                    handleHeadViewNew(arr,currentSecondPosition);
                    initData();
                    mAlertDialog.dismiss();
                }
            });

            mAlertDialog.show();
            mAlertDialog.getWindow().setContentView(view);

        }else{
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.football_scheduledialog, null);
            TextView titleView = (TextView) view.findViewById(R.id.headerView);
            titleView.setText(getString(R.string.football_database_details_stage));

            final List<String> data = new ArrayList<>();
            Collections.addAll(data, mRoundString);

            final SportsDialogAdapter mDialogAdapter = new SportsDialogAdapter(data, getContext(), currentPosition);

            final AlertDialog mAlertDialog = mBuilder.create();
            mAlertDialog.setCanceledOnTouchOutside(true);//设置空白处点击 dialog消失

            /**
             * 根据List数据条数加载不同的ListView （数据多加载可滑动 ScrollTouchListview）
             */
            ScrollView scrollview = (ScrollView) view.findViewById(R.id.basket_sports_scroll);//数据多时显示
            ListView listview = (ListView) view.findViewById(R.id.sport_date);//数据少时显示
            listview.setAdapter(mDialogAdapter);
            listview.setSelection(currentPosition);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    currentPosition = position;
                    mDialogAdapter.updateDatas(position);
                    mDialogAdapter.notifyDataSetChanged();
                    mAlertDialog.dismiss();
                    String newData = mRoundString[currentPosition];
                    mLeagueRound = newData;
                    url = BaseURLs.URL_FOOTBALL_DATABASE_SCHEDULE_UNFIRST; //选择后的URL
                    handleHeadViewNew(mRoundString ,currentPosition);
                    initData();
                }
            });
            scrollview.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            mAlertDialog.show();
            mAlertDialog.getWindow().setContentView(view);
        }
    }

    private void initRecycler() {
        mSections = new ArrayList<>();
        mAdapter = new BasketballDatabaseScheduleSectionAdapter(mSections);
        mAdapter.addHeaderView(mButtonFrame);
        mAdapter.setEmptyView(true, mEmptyView);

        mSectionsNew = new ArrayList<>();
        mAdapterNew = new FootballDatabaseScheduleSectionAdapter(mSectionsNew);
        mAdapterNew.addHeaderView(mButtonFrame);
        mAdapterNew.setEmptyView(true , mEmptyView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapterNew);
        mRecyclerView.setLayoutManager(manager);
    }

    public static FootballDatabaseScheduleNewFragment newInstance(DataBaseBean leagueBean, String season) {

        Bundle args = new Bundle();
        args.putParcelable(LEAGUE, leagueBean);
        args.putString(PARAM_DATE, season);
        FootballDatabaseScheduleNewFragment fragment = new FootballDatabaseScheduleNewFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public void setSeason(String season) {
        this.mLeagueDate = season;
        Bundle args = getArguments();
        if (args != null) args.putString(PARAM_DATE, season);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_button:
                if (isLoad) {
                    setDialog();
                }
                break;
            case R.id.left_button:
                if (isLoad) {
                    if (chooseSecond) {
                        loadLeft(currentSecondPosition);
                    }else{
                        loadLeft(currentPosition);
                    }
                }
                break;
            case R.id.right_button:
                if (isLoad) {
                    if (chooseSecond) {
                        loadRight(currentSecondPosition);
                    }else{
                        loadRight(currentPosition);
                    }
                }
                break;
        }

    }

    class FirstAdapter extends BaseQuickAdapter<String> {

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }
        public FirstAdapter(List<String> data , int position) {
            super(R.layout.item_match_stage_first, data);
            currentFirstPosition = position;
        }
        @Override
        protected void convert(BaseViewHolder holder, final String match) {
            final TextView textView = holder.getView(R.id.first_text);

            final int position = holder.getAdapterPosition();
            if (position == 0) {
                textView.setBackgroundResource(R.drawable.bg_item_match_stage_first_left);
            } else if (position == mData.size() - 1) {
                textView.setBackgroundResource(R.drawable.bg_item_match_stage_first_right);
            } else {
                textView.setBackgroundResource(R.drawable.bg_item_match_stage_first_normal);
            }

            if (position == currentFirstPosition) {
                textView.setSelected(true);
            }else{
                textView.setSelected(false);
            }

            textView.setText(match);

            if (mOnItemClickListener != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(position);
                    }
                });
            }
        }
    }

    class SecondAdapter extends BaseQuickAdapter<String> {

        private OnItemClickListener mOnItemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }
        public SecondAdapter(List<String> data , int position) {
            super(R.layout.item_match_stage_second, data);
            currentSecondPosition = position;
        }
        @Override
        protected void convert(BaseViewHolder holder, final String match) {
            final TextView textView = holder.getView(R.id.second_text);
            final int position = holder.getAdapterPosition();
            if (position== currentSecondPosition) {
                textView.setSelected(true);
            }else{
                textView.setSelected(false);
            }
            textView.setText(match);
            if (mOnItemClickListener != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(position);
                    }
                });
            }
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
