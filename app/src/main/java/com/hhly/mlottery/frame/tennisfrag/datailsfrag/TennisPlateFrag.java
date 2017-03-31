package com.hhly.mlottery.frame.tennisfrag.datailsfrag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hhly.mlottery.R;
import com.hhly.mlottery.activity.TennisCpiDetailsActivity;
import com.hhly.mlottery.adapter.tennisball.TennisDatailsPlateAdapter;
import com.hhly.mlottery.bean.tennisball.datails.odds.TennisOdds;
import com.hhly.mlottery.config.BaseURLs;
import com.hhly.mlottery.util.net.VolleyContentFast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 网球内页亚盘
 */
public class TennisPlateFrag extends Fragment {
    private static final String TENNIS_DATAILS_THIRDID = "tennis_details_third_id";
    private final String ARG_ODDTYPE = "oddType";
    private final String ARG_THIRDID = "thirdId";
    private final String ARG_INDEX = "index";
    private final String ARG_LEFT_NAME = "leftName";
    private final String ARG_COMPAN_NAME = "companName";
    private final int LOADING = 1;
    private final int SUCCESS = 2;
    private final int ERROR = 3;
    private final int NOTO_DATA = 4;

    private Activity mContext;
    private String mThirdId;

    private View mView, contentView, notDataView, ff_loading, ff_note_data;
    private RecyclerView mRecyclerView;

    private TennisOdds tennisOdds;
    private TennisDatailsPlateAdapter mAdapter;

    private ArrayList<String> nameList = new ArrayList<>();

    public TennisPlateFrag() {
    }

    public static TennisPlateFrag newInstance(String thirdId) {
        TennisPlateFrag fragment = new TennisPlateFrag();
        Bundle args = new Bundle();
        args.putString(TENNIS_DATAILS_THIRDID, thirdId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mThirdId = getArguments().getString(TENNIS_DATAILS_THIRDID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tennis_plate, container, false);

        initView();
        initData();
        initEvent();

        return mView;
    }

    private void initEvent() {
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(mContext, TennisCpiDetailsActivity.class);
                intent.putExtra(ARG_ODDTYPE, "1");
                intent.putExtra(ARG_THIRDID, mThirdId);
                intent.putExtra(ARG_INDEX, i);
                intent.putExtra(ARG_LEFT_NAME, nameList);
                intent.putExtra(ARG_COMPAN_NAME, tennisOdds.getData().get(i).getName());
                startActivity(intent);
            }
        });
    }

    private void initData() {

        setStatus(LOADING);

        Map<String, String> map = new HashMap<>();
        map.put("matchIds", mThirdId);

        VolleyContentFast.requestJsonByGet(BaseURLs.TENNIS_DATAILS_ODDS_URL, map, new VolleyContentFast.ResponseSuccessListener<TennisOdds>() {
            @Override
            public void onResponse(TennisOdds json) {
                tennisOdds = json;
                if (tennisOdds != null && tennisOdds.getData() != null) {
                    setStatus(SUCCESS);
                    nameList.clear();
                    for (int i = 0; i < tennisOdds.getData().size(); i++) {
                        // 添加所有公司name
                        nameList.add(tennisOdds.getData().get(i).getName());
                    }
                    mAdapter.addData(tennisOdds.getData());
                    mAdapter.notifyDataSetChanged();
                } else {
                    setStatus(NOTO_DATA);
                }
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                setStatus(ERROR);
            }
        }, TennisOdds.class);
    }

    private void initView() {
        contentView = mView.findViewById(R.id.tennis_datails_plate_content);
        notDataView = mView.findViewById(R.id.network_exception_layout);
        ff_loading = mView.findViewById(R.id.ff_loading);
        ff_note_data = mView.findViewById(R.id.ff_note_data);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.tennis_datails_plate_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new TennisDatailsPlateAdapter(R.layout.fragment_tennis_plate_item, null);
        mRecyclerView.setAdapter(mAdapter);
    }

    // 设置页面显示状态
    private void setStatus(int status) {
        notDataView.setVisibility(status == ERROR ? View.VISIBLE : View.GONE);
        contentView.setVisibility(status == SUCCESS ? View.VISIBLE : View.GONE);
        ff_loading.setVisibility(status == LOADING ? View.VISIBLE : View.GONE);
        ff_note_data.setVisibility(status == NOTO_DATA ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }
}
