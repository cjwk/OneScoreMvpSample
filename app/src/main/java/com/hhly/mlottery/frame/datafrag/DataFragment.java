package com.hhly.mlottery.frame.datafrag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hhly.mlottery.R;
import com.hhly.mlottery.activity.BasketballInformationSerachActivity;
import com.hhly.mlottery.activity.FootballInformationSerachActivity;
import com.hhly.mlottery.activity.SnookerInformationSerachActivity;
import com.hhly.mlottery.frame.BallType;
import com.hhly.mlottery.util.FragmentUtils;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.PreferenceUtil;
import com.hhly.mlottery.widget.BallChoiceArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.hhly.mlottery.frame.BallType.BASKETBALL;
import static com.hhly.mlottery.frame.BallType.FOOTBALL;
import static com.hhly.mlottery.frame.BallType.SNOOKER;

/**
 * 资料库
 * wangg
 */
public class DataFragment extends Fragment implements View.OnClickListener {


    private ImageView publicBtnSet;


    private View mView;

    private Context mContext;
    private String[] mItems;

    private int fragmentIndex = 0;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private List<Fragment> fragments = new ArrayList<>();

    private LinearLayout d_header;
    private LinearLayout ll_match_select;

    private TextView tv_match_name;
    private ImageView iv_match;

    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_data, container, false);
        mContext = mActivity;
        initView();
        initEvent();
        return mView;
    }

    private void initView() {

        mItems = getResources().getStringArray(R.array.ziliaoku_select);


        ll_match_select = (LinearLayout) mView.findViewById(R.id.ll_match_select);
        tv_match_name = (TextView) mView.findViewById(R.id.tv_match_name);
        iv_match = (ImageView) mView.findViewById(R.id.iv_match);
        d_header = (LinearLayout) mView.findViewById(R.id.d_heasder);


        publicBtnSet = (ImageView) mView.findViewById(R.id.public_btn_set);
        publicBtnSet.setOnClickListener(this);
        publicBtnSet.setImageResource(R.mipmap.info_search);

        fragments.add(new FootballInfomationFragment());
        fragments.add(new BasketballInfomationFragment());
        fragments.add(new SnookerInfomationFragment());


        /**
         * 默认先选择足球
         */
        choiceMatchType();
    }

    private void choiceMatchType() {
        int matchChoiceType = PreferenceUtil.getInt("matchChoiceType", BallType.FOOTBALL);

        if (matchChoiceType == BallType.FOOTBALL) {
            tv_match_name.setText(getResources().getString(R.string.football_txt));
            switchFragment(BallType.FOOTBALL);
        } else if (matchChoiceType == BallType.BASKETBALL) {
            tv_match_name.setText(getResources().getString(R.string.basketball_txt));
            switchFragment(BallType.BASKETBALL);
        } else if (matchChoiceType == BallType.SNOOKER) {
            tv_match_name.setText(getResources().getString(R.string.snooker_txt));
            switchFragment(BallType.SNOOKER);
        } else {
            tv_match_name.setText(getResources().getString(R.string.football_txt));
            switchFragment(BallType.FOOTBALL);
        }
    }

    private void initEvent() {


        ll_match_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_match.setImageResource(R.mipmap.nav_icon_up);
                backgroundAlpha(getActivity(), 0.5f);
                popWindow(v);
            }
        });


    }

    private void popWindow(final View v) {
        final View mView = View.inflate(mContext, R.layout.pop_select, null);
        // 创建ArrayAdapter对象
        BallChoiceArrayAdapter mAdapter = new BallChoiceArrayAdapter(mContext, mItems, fragmentIndex);

        ListView listview = (ListView) mView.findViewById(R.id.match_type);
        listview.setAdapter(mAdapter);


        final PopupWindow popupWindow = new PopupWindow(mView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.showAsDropDown(d_header);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_match_name.setText(((TextView) view.findViewById(R.id.tv)).getText().toString());
                iv_match.setImageResource(R.mipmap.nav_icon_cbb);
                switchFragment(position);
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iv_match.setImageResource(R.mipmap.nav_icon_cbb);
                backgroundAlpha(getActivity(), 1f);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void switchFragment(int position) {
        fragmentIndex = position;// 当前fragment下标
        L.d("xxx", "当前Fragment下标：" + fragmentIndex);
        fragmentManager = getChildFragmentManager();
        currentFragment = FragmentUtils.switchFragment(fragmentManager, R.id.ly_content_data, currentFragment, fragments.get(position).getClass(), null, false, fragments.get(position).getClass().getSimpleName() + position, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_btn_set:
                if (fragmentIndex == FOOTBALL) {
                    Intent intent = new Intent(mContext, FootballInformationSerachActivity.class);
                    startActivity(intent);
                } else if (fragmentIndex == BASKETBALL) {
                    Intent intent = new Intent(mContext, BasketballInformationSerachActivity.class);
                    startActivity(intent);
                } else if (fragmentIndex == SNOOKER) {
                    Intent intent = new Intent(mContext, SnookerInformationSerachActivity.class);
                    startActivity(intent);
                }
                break;
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            int matchChoiceType = PreferenceUtil.getInt("matchChoiceType", FOOTBALL);
            if (matchChoiceType != fragmentIndex) {
                if (matchChoiceType == FOOTBALL) {

                    tv_match_name.setText(getResources().getString(R.string.football_txt));
                    switchFragment(FOOTBALL);
                } else if (matchChoiceType == BASKETBALL) {
                    tv_match_name.setText(getResources().getString(R.string.basketball_txt));
                    switchFragment(BASKETBALL);
                } else if (matchChoiceType == SNOOKER) {
                    tv_match_name.setText(getResources().getString(R.string.snooker_txt));
                    switchFragment(SNOOKER);
                } else {
                    tv_match_name.setText(getResources().getString(R.string.football_txt));
                    switchFragment(FOOTBALL);
                }
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}



