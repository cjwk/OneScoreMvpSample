package com.hhly.mlottery.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhly.mlottery.R;
import com.hhly.mlottery.activity.BasketBallMatchSearchActivity;
import com.hhly.mlottery.activity.BasketDetailsActivityTest;
import com.hhly.mlottery.bean.BasketballItemSearchBean;
import com.hhly.mlottery.util.ImageLoader;
import com.hhly.mlottery.util.MyConstants;
import com.hhly.mlottery.util.PreferenceUtil;

import java.util.List;


/**
 * Created by yuely198 on 2017/3/26.
 */

public class BasketballMatchSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<BasketballItemSearchBean> mMatchdata;
    private LayoutInflater mLayoutInflater;

    private BasketballItemSearchBean.MatchOddsBean MatchOdds;
    private ImmediaViewHolder holder;
    private View view;
    private int mFocusType;


    public BasketballMatchSearchAdapter(Context mContext, List<BasketballItemSearchBean> mMatchdata) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mMatchdata = mMatchdata;
        this.mContext = mContext;
    }

    private BasketBallMatchSearchActivity.BasketFocusClickListener mFocus; //关注监听回掉

    public void setmFocus(BasketBallMatchSearchActivity.BasketFocusClickListener mFocus) {
        this.mFocus = mFocus;
    }

    /**
     * 赛事item click
     */
    public interface OnRecycleItemClickListener {
        void onItemClick(View view, BasketballItemSearchBean currData);
    }

    /**
     * 赛事item click
     */
    private OnRecycleItemClickListener mOnItemClickListener = null;

    public void setmOnItemClickListener(OnRecycleItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basketball_score_list_item, parent, false);
        holder = new ImmediaViewHolder(view);
        //将创建的View注册点击事件
     /*   view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (String) v.getTag());
                }
            }
        });*/
        return holder;
    }


    /**
     * 数据ViewHolder
     */

    static class ImmediaViewHolder extends RecyclerView.ViewHolder {
        CardView card_view;
        ImageView home_icon; //主队图标
        ImageView guest_icon; //客队图标
        ImageView mIv_guangzhu;//关注 星星
        TextView matches_name; //联赛名称
        TextView game_time; //比赛时间
        TextView st_time; //第几节
        TextView ongoing_time; // 单节时间
        TextView score_total;// 半全场比分
        TextView score_differ;//比分差
        TextView home_name;// 主队名称
        TextView guest_name;//客队名称
        TextView home_Ranking; //主队赛区
        TextView guest_Ranking; //客队赛区
        TextView basket_guest_all_score;
        //        TextView basket_score;
        TextView basket_home_all_score;
        //        TextView basket_half_score;// 主客队半场比分
        TextView basket_leftOdds;// 赔率 左
        TextView basket_rightOdds;// 赔率 右
        TextView basket_handicap;// 赔率盘口
        TextView backetball_total;//总
        TextView backetball_differ;//差
        TextView tv_a; // 赔率分割线
        TextView tv_b;
        TextView backetball_apos; // 秒针
        //        TextView basket_no_start; // 未开赛
        LinearLayout score_data;//比分显示部分
        TextView guest_score1;//单节比分
        TextView guest_score2;
        TextView guest_score3;
        TextView guest_score4;
        TextView home_score1;
        TextView home_score2;
        TextView home_score3;
        TextView home_score4;
        TextView guest_score_ot;//加时比分
        TextView home_score_ot;

        public ImmediaViewHolder(final View itemView) {
            super(itemView);
            card_view = (CardView) itemView.findViewById(R.id.basket_card_view);
            home_icon = (ImageView) itemView.findViewById(R.id.home_icon);
            guest_icon = (ImageView) itemView.findViewById(R.id.guest_icon);
            mIv_guangzhu = (ImageView) itemView.findViewById(R.id.Iv_guangzhu);
            matches_name = (TextView) itemView.findViewById(R.id.backetball_matches_name);
            game_time = (TextView) itemView.findViewById(R.id.backetball_game_time);
            st_time = (TextView) itemView.findViewById(R.id.backetball_st_time);
            ongoing_time = (TextView) itemView.findViewById(R.id.backetball_ongoing_time);
            score_total = (TextView) itemView.findViewById(R.id.backetball_score_total);
            score_differ = (TextView) itemView.findViewById(R.id.backetball_score_differ);
            home_name = (TextView) itemView.findViewById(R.id.home_name);
            guest_name = (TextView) itemView.findViewById(R.id.guest_name);
            home_Ranking = (TextView) itemView.findViewById(R.id.basket_home_Ranking);
            guest_Ranking = (TextView) itemView.findViewById(R.id.basket_guest_Ranking);
            basket_guest_all_score = (TextView) itemView.findViewById(R.id.basket_guest_all_score);
//            basket_score = (TextView) itemView.findViewById(R.id.basket_score);
            basket_home_all_score = (TextView) itemView.findViewById(R.id.basket_home_all_score);
//            basket_half_score = (TextView) itemView.findViewById(R.id.basket_half_score);
//            basket_no_start = (TextView) itemView.findViewById(R.id.basket_no_start);
            basket_leftOdds = (TextView) itemView.findViewById(R.id.basket_leftOdds);
            basket_rightOdds = (TextView) itemView.findViewById(R.id.basket_rightOdds);
            basket_handicap = (TextView) itemView.findViewById(R.id.basket_handicap);
            backetball_differ = (TextView) itemView.findViewById(R.id.backetball_differ);
            backetball_total = (TextView) itemView.findViewById(R.id.backetball_total);
            tv_a = (TextView) itemView.findViewById(R.id.tv_a);
            tv_b = (TextView) itemView.findViewById(R.id.tv_b);
            backetball_apos = (TextView) itemView.findViewById(R.id.backetball_apos);
            score_data = (LinearLayout) itemView.findViewById(R.id.score_data);
            guest_score1 = (TextView) itemView.findViewById(R.id.guest_score1);
            guest_score2 = (TextView) itemView.findViewById(R.id.guest_score2);
            guest_score3 = (TextView) itemView.findViewById(R.id.guest_score3);
            guest_score4 = (TextView) itemView.findViewById(R.id.guest_score4);
            guest_score_ot = (TextView) itemView.findViewById(R.id.guest_score_ot);
            home_score1 = (TextView) itemView.findViewById(R.id.home_score1);
            home_score2 = (TextView) itemView.findViewById(R.id.home_score2);
            home_score3 = (TextView) itemView.findViewById(R.id.home_score3);
            home_score4 = (TextView) itemView.findViewById(R.id.home_score4);
            home_score_ot = (TextView) itemView.findViewById(R.id.home_score_ot);

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BasketballItemSearchBean match = mMatchdata.get(position);

        convert((BasketballMatchSearchAdapter.ImmediaViewHolder) holder, match);
    }
/*

    private BasketBallMatchSearchActivity.BasketFocusClickListener mFocus; //关注监听回掉
    public void setmFocus(BasketBallMatchSearchActivity.BasketFocusClickListener mFocus) {
        this.mFocus = mFocus;
    }
*/

    //处理数据
    private void convert(final ImmediaViewHolder viewHolderList, final BasketballItemSearchBean DetailsData) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BasketDetailsActivityTest.class);
                intent.putExtra(BasketDetailsActivityTest.BASKET_THIRD_ID, DetailsData.getThirdId());//跳转到详情
                intent.putExtra(BasketDetailsActivityTest.BASKET_MATCH_STATUS, DetailsData.getMatchStatus());//跳转到详情
                //用getActivity().startActivityForResult();不走onActivityResult ;
//        startActivityForResult(intent, REQUEST_DETAILSCODE);
                intent.putExtra("currentfragment", 0);
                intent.putExtra(BasketDetailsActivityTest.BASKET_MATCH_LEAGUEID, DetailsData.getLeagueId());
                intent.putExtra(BasketDetailsActivityTest.BASKET_MATCH_MATCHTYPE, 1);
                mContext.startActivity(intent);
            }
        });

        /**
         * 关注 监听
         */
        String focusIds = PreferenceUtil.getString("basket_focus_ids", "");
        String[] Ids = focusIds.split("[,]");
        viewHolderList.mIv_guangzhu.setBackgroundResource(R.mipmap.iconfont_guanzhu);
        viewHolderList.mIv_guangzhu.setTag(false);
        for (String id : Ids) {
            if (id.equals(DetailsData.getThirdId())) {
                viewHolderList.mIv_guangzhu.setBackgroundResource(R.mipmap.iconfont_guanzhu_hover);
                Log.i("sds", "我进来了");
                viewHolderList.mIv_guangzhu.setTag(true);
                break;
            }
        }
        viewHolderList.mIv_guangzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFocus != null) {
                    mFocus.FocusOnClick(v, DetailsData);
                }
            }
        });

        viewHolderList.basket_guest_all_score.setTag(DetailsData.getThirdId());
        viewHolderList.basket_home_all_score.setTag(DetailsData.getThirdId());
        ImageLoader.load(mContext, null, R.mipmap.basket_default).into(viewHolderList.home_icon);
        ImageLoader.load(mContext, null, R.mipmap.basket_default).into(viewHolderList.guest_icon);

        //赔率设置
        MatchOdds = DetailsData.getMatchOdds();
        if (MatchOdds != null) {
            boolean asize = PreferenceUtil.getBoolean(MyConstants.BASKETBALL_rbSizeBall, false); //大小球
            boolean eur = PreferenceUtil.getBoolean(MyConstants.BASKETBALL_RBOCOMPENSATE, false);//欧赔
            boolean alet = PreferenceUtil.getBoolean(MyConstants.BASKETBALL_RBSECOND, true); //亚盘
            boolean noshow = PreferenceUtil.getBoolean(MyConstants.BASKETBALL_RBNOTSHOW, false);//不显示

            /**
             * 大小球
             */
            if (MatchOdds.getAsiaSize() != null) {
                if (MatchOdds.getAsiaSize().getCrown() == null) {
                    BasketballItemSearchBean.MatchOddsBean.AsiaSizeBean.CrownBean mAsize = MatchOdds.getAsiaSize().getCrown();

                    if (mAsize == null) {
                        viewHolderList.basket_leftOdds.setText("");
                        viewHolderList.basket_rightOdds.setText("");
                        viewHolderList.basket_handicap.setText("");
                        viewHolderList.tv_a.setText("");
                        viewHolderList.tv_b.setText("");
                    } else if (mAsize != null) {

                        if (mAsize.getLeftOdds() != null && mAsize.getRightOdds() != null && mAsize.getHandicapValue() != null) {
                            if (mAsize.getLeftOdds().equals("-") || mAsize.getRightOdds().equals("-") || mAsize.getHandicapValue().equals("-")) {
                                viewHolderList.basket_leftOdds.setText("");
                                viewHolderList.basket_rightOdds.setText("");
                                viewHolderList.basket_handicap.setText(R.string.basket_handicap_feng);
                                viewHolderList.tv_a.setText("");
                                viewHolderList.tv_b.setText("");
                            } else {
                                viewHolderList.basket_leftOdds.setText(mAsize.getLeftOdds());
                                viewHolderList.basket_rightOdds.setText(mAsize.getRightOdds());
                                viewHolderList.basket_handicap.setText(mContext.getString(R.string.basket_odds_asize) + mAsize.getHandicapValue());
                                viewHolderList.tv_a.setText("|");
                                viewHolderList.tv_b.setText("|");
                            }
                        } else {
                            viewHolderList.basket_leftOdds.setText("");
                            viewHolderList.basket_rightOdds.setText("");
                            viewHolderList.basket_handicap.setText("");
                            viewHolderList.tv_a.setText("");
                            viewHolderList.tv_b.setText("");
                        }
                    }
                } else {
                    viewHolderList.basket_leftOdds.setText("");
                    viewHolderList.basket_rightOdds.setText("");
                    viewHolderList.basket_handicap.setText("");
                    viewHolderList.tv_a.setText("");
                    viewHolderList.tv_b.setText("");
                }
            } else {
                viewHolderList.basket_leftOdds.setText("");
                viewHolderList.basket_rightOdds.setText("");
                viewHolderList.basket_handicap.setText("");
                viewHolderList.tv_a.setText("");
                viewHolderList.tv_b.setText("");
            }

            /**
             * 不显示
             */
            if (noshow) {
                viewHolderList.basket_leftOdds.setText("");
                viewHolderList.basket_rightOdds.setText("");
                viewHolderList.basket_handicap.setText("");
                viewHolderList.tv_a.setText("");
                viewHolderList.tv_b.setText("");
            }
        } else if (MatchOdds == null) {
            viewHolderList.basket_leftOdds.setText("");
            viewHolderList.basket_rightOdds.setText("");
            viewHolderList.basket_handicap.setText("");
            viewHolderList.tv_a.setText("");
            viewHolderList.tv_b.setText("");
        }
        //比分数据设置
        if (DetailsData.getLeagueName() != null) {
            viewHolderList.matches_name.setText(DetailsData.getLeagueName());
            viewHolderList.matches_name.setTextColor(Color.parseColor(DetailsData.getLeagueColor()));
        }
        viewHolderList.game_time.setText(DetailsData.getTime());
        /**
         * 去除两端的空格，trim()；  防止-->  "XX   ..."情况
         */
        if (DetailsData.getHomeTeam() != null) {
            viewHolderList.home_name.setText(DetailsData.getHomeTeam().trim());
        } else {
            viewHolderList.home_name.setText("--");
        }
        if (DetailsData.getGuestTeam() != null) {
            viewHolderList.guest_name.setText(DetailsData.getGuestTeam().trim());
        } else {
            viewHolderList.guest_name.setText("--");
        }

        Integer score = Integer.parseInt(String.valueOf(DetailsData.getMatchStatus()));

        if (DetailsData.getMatchStatus() != null) {

            int scorehome;
            int scoreguest;
            int scorehomehalf;
            int scoreguesthalf;

            if (DetailsData.getMatchScore() != null) {
                if (DetailsData.getMatchScore().getRemainTime() == null) {
//                    viewHolderList.ongoing_time.setText("进行中");
                    viewHolderList.ongoing_time.setText("");
                } else {
                    viewHolderList.ongoing_time.setText(DetailsData.getMatchScore().getRemainTime());
                }
            } else {
                viewHolderList.ongoing_time.setText("");
            }

            //0:未开赛,1:一节,2:二节,5:1'OT，以此类推 -1:完场,-2:待定,-3:中断,-4:取消,-5:推迟,50中场
            if (score == 0 || score == -2 || score == -3 || score == -4 || score == -5) {

                scorehome = 0;
                scoreguest = 0;
                scorehomehalf = 0;
                scoreguesthalf = 0;
            } else {

                if (DetailsData.getMatchScore() == null) {
                    scorehome = 0;
                    scoreguest = 0;
                    scorehomehalf = 0;
                    scoreguesthalf = 0;
                } else {

                    scorehome = DetailsData.getMatchScore().getHomeScore();//主队分数
                    scoreguest = DetailsData.getMatchScore().getGuestScore();//客队分数

                    if (DetailsData.getSection() == 2) { // 只有两节比赛的情况
                        scorehomehalf = DetailsData.getMatchScore().getHome1();//主队半场得分
                        scoreguesthalf = DetailsData.getMatchScore().getGuest1();//客队半场得分
                    } else {
                        scorehomehalf = DetailsData.getMatchScore().getHome1() + DetailsData.getMatchScore().getHome2();//主队半场得分
                        scoreguesthalf = DetailsData.getMatchScore().getGuest1() + DetailsData.getMatchScore().getGuest2();//客队半场得分
                    }
                }
            }

            switch (score) {
                case 0: //未开赛
                    viewHolderList.backetball_differ.setVisibility(View.INVISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_differ.setVisibility(View.INVISIBLE);
//                            viewHolderList.basket_score.setText("");
//                            viewHolderList.basket_no_start.setVisibility(View.VISIBLE);
                    viewHolderList.basket_guest_all_score.setText("");
                    viewHolderList.basket_home_all_score.setText("");
                    viewHolderList.basket_guest_all_score.setVisibility(View.GONE);
                    viewHolderList.basket_home_all_score.setVisibility(View.GONE);
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_vs));
//                            viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
                    viewHolderList.st_time.setText(R.string.games_no_start);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.basket_score_gray));
                    settingdata(viewHolderList, 0);

                    viewHolderList.score_data.setVisibility(View.INVISIBLE);
                    break;
                case 1: //一节
                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));//#0085E1
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);

                    if (DetailsData.getMatchScore() != null) {

                        viewHolderList.score_total.setText(scorehome + scoreguest + "");
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }

                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");

//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
//                                viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest1() + ":" + DetailsData.getMatchScore().getHome1() + ")");
                        if (DetailsData.getSection() == 2) {
                            viewHolderList.st_time.setText(R.string.basket_1st_half);
                        } else {
                            viewHolderList.st_time.setText(R.string.basket_1st);
                        }
                        viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");

//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 1);
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case 2: //二节
                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText(scorehome + scoreguest + "");
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }

                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
                        if (DetailsData.getSection() == 2) {
                            viewHolderList.st_time.setText(R.string.basket_1st_half);
                        } else {
                            viewHolderList.st_time.setText(R.string.basket_2nd);
                        }
//                                viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest2() + ":" + DetailsData.getMatchScore().getHome2() + ")");
                        viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 2);
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case 3: //三节
                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);

                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }

                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");

//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
                        if (DetailsData.getSection() == 2) {
                            viewHolderList.st_time.setText(R.string.basket_2nd_half);
//                                    viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest3() + ":" + DetailsData.getMatchScore().getHome3() + ")");
                        } else {
                            viewHolderList.st_time.setText(R.string.basket_3rd);
//                                    viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest3() + ":" + DetailsData.getMatchScore().getHome3() + ")");
                        }
                        viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");

//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 3);
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case 4: //四节

                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }

                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
//                                viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest4() + ":" + DetailsData.getMatchScore().getHome4() + ")");
                        if (DetailsData.getSection() == 2) {
                            viewHolderList.st_time.setText(R.string.basket_2nd_half);
//                                    viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest3() + ":" + DetailsData.getMatchScore().getHome3() + ")");
                        } else {
                            viewHolderList.st_time.setText(R.string.basket_4th);
//                                    viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuest4() + ":" + DetailsData.getMatchScore().getHome4() + ")");
                        }
                        viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 4);
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case 5: //加时1

                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }


                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");

//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
//                                viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuestOt1() + ":" + DetailsData.getMatchScore().getHomeOt1() + ")");
                        viewHolderList.st_time.setText(R.string.basket_OT1);
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 5);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setText(DetailsData.getMatchScore().getGuestOt1() + "");//加时比分
                    viewHolderList.home_score_ot.setText(DetailsData.getMatchScore().getHomeOt1() + "");
                    viewHolderList.guest_score_ot.setVisibility(View.VISIBLE);
                    viewHolderList.home_score_ot.setVisibility(View.VISIBLE);
                    break;
                case 6: //加时2

                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }

                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
//                                viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuestOt2() + ":" + DetailsData.getMatchScore().getHomeOt2() + ")");
                        viewHolderList.st_time.setText(R.string.basket_OT2);
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 6);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setText(DetailsData.getMatchScore().getGuestOt2() + "");//加时比分
                    viewHolderList.home_score_ot.setText(DetailsData.getMatchScore().getHomeOt2() + "");
                    viewHolderList.guest_score_ot.setVisibility(View.VISIBLE);
                    viewHolderList.home_score_ot.setVisibility(View.VISIBLE);
                    break;
                case 7: //加时3
                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }

                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
//                                viewHolderList.basket_half_score.setVisibility(View.VISIBLE);
//                                viewHolderList.basket_half_score.setText("(" + DetailsData.getMatchScore().getGuestOt3() + ":" + DetailsData.getMatchScore().getHomeOt3() + ")");
                        viewHolderList.st_time.setText(R.string.basket_OT3);
                        viewHolderList.ongoing_time.setVisibility(View.VISIBLE);
                        viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
//                                viewHolderList.basket_half_score.setText("");
                        viewHolderList.ongoing_time.setVisibility(View.GONE);
                        viewHolderList.backetball_apos.setVisibility(View.GONE);
                    }
                    settingdata(viewHolderList, 7);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setText(DetailsData.getMatchScore().getGuestOt3() + "");//加时比分
                    viewHolderList.home_score_ot.setText(DetailsData.getMatchScore().getHomeOt3() + "");
                    viewHolderList.guest_score_ot.setVisibility(View.VISIBLE);
                    viewHolderList.home_score_ot.setVisibility(View.VISIBLE);
                    break;
                case 50: //中场

                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }
                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
//                                viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                        viewHolderList.st_time.setText(R.string.basket_half_time);
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
//                                viewHolderList.basket_half_score.setVisibility(View.GONE);
                    }
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
                    settingdata(viewHolderList, 50);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case -1: //完场
                    viewHolderList.backetball_differ.setVisibility(View.VISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_total.setVisibility(View.VISIBLE);
                    viewHolderList.score_differ.setVisibility(View.VISIBLE);

                    //当借口无数据时 显示“--”
                    if (DetailsData.getMatchScore() != null) {
                        viewHolderList.score_total.setText((scorehomehalf + scoreguesthalf) + "/" + (scorehome + scoreguest));
                        if (scorehome - scoreguest > 0) {
                            viewHolderList.score_differ.setText("" + (scorehome - scoreguest)); // 不取绝对值
                        } else {
                            viewHolderList.score_differ.setText(scorehome - scoreguest + "");
                        }
                        viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                        viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
                    } else {
                        viewHolderList.score_total.setText("--");
                        viewHolderList.score_differ.setText("--");
                        viewHolderList.basket_guest_all_score.setText("--");
                        viewHolderList.basket_home_all_score.setText("--");
                    }
//                            viewHolderList.basket_score.setText(":");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
                    viewHolderList.basket_guest_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_over_score));
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_over_score));
                    viewHolderList.basket_home_all_score.setTextColor(mContext.getResources().getColor(R.color.basket_over_score));
                    viewHolderList.basket_guest_all_score.setVisibility(View.VISIBLE);
                    viewHolderList.basket_home_all_score.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                    viewHolderList.st_time.setText(R.string.snooker_state_over_game);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.basket_over_score));
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
                    settingdata(viewHolderList, -1);

                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case -2: //待定
                    viewHolderList.backetball_differ.setVisibility(View.INVISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_differ.setVisibility(View.INVISIBLE);
//                            viewHolderList.basket_score.setText("");
//                            viewHolderList.basket_no_start.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_vs));
                    viewHolderList.basket_guest_all_score.setText("");
                    viewHolderList.basket_home_all_score.setText("");
                    viewHolderList.basket_guest_all_score.setVisibility(View.GONE);
                    viewHolderList.basket_home_all_score.setVisibility(View.GONE);
//                            viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                    viewHolderList.st_time.setText(R.string.basket_undetermined);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));//#0085E1
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
                    settingdata(viewHolderList, -2);
                    viewHolderList.score_data.setVisibility(View.INVISIBLE);
                    break;
                case -3: //中断
                    viewHolderList.backetball_differ.setVisibility(View.INVISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.INVISIBLE);
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
//                            viewHolderList.basket_score.setText("");
//                            viewHolderList.basket_no_start.setVisibility(View.GONE);
//                            viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                    viewHolderList.basket_guest_all_score.setText(DetailsData.getMatchScore().getGuestScore() + "");
                    viewHolderList.basket_home_all_score.setText(DetailsData.getMatchScore().getHomeScore() + "");
                    //TODO--------********************************
                    viewHolderList.st_time.setText(R.string.basket_interrupt);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));//#0085E1
                    settingdata(viewHolderList, -3);
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case -4: //取消
                    viewHolderList.backetball_differ.setVisibility(View.INVISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_differ.setVisibility(View.INVISIBLE);
//                            viewHolderList.basket_score.setText("");
//                            viewHolderList.basket_no_start.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_vs));
                    viewHolderList.basket_guest_all_score.setText("");
                    viewHolderList.basket_home_all_score.setText("");
                    viewHolderList.basket_guest_all_score.setVisibility(View.GONE);
                    viewHolderList.basket_home_all_score.setVisibility(View.GONE);
//                            viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                    viewHolderList.st_time.setText(R.string.basket_cancel);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));//#0085E1
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
                    settingdata(viewHolderList, -4);
                    viewHolderList.score_data.setVisibility(View.VISIBLE);
                    setSingleScore(viewHolderList, DetailsData.getMatchScore());
                    setVisible(viewHolderList, DetailsData.getSection());
                    viewHolderList.guest_score_ot.setVisibility(View.GONE);
                    viewHolderList.home_score_ot.setVisibility(View.GONE);
                    break;
                case -5: //推迟
                    viewHolderList.backetball_differ.setVisibility(View.INVISIBLE);
                    viewHolderList.backetball_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_total.setVisibility(View.INVISIBLE);
                    viewHolderList.score_differ.setVisibility(View.INVISIBLE);
//                            viewHolderList.basket_score.setText("");
//                            viewHolderList.basket_no_start.setVisibility(View.VISIBLE);
//                            viewHolderList.basket_score.setTextColor(mContext.getResources().getColor(R.color.basket_vs));
                    viewHolderList.basket_guest_all_score.setText("");
                    viewHolderList.basket_home_all_score.setText("");
                    viewHolderList.basket_guest_all_score.setVisibility(View.GONE);
                    viewHolderList.basket_home_all_score.setVisibility(View.GONE);
//                            viewHolderList.basket_half_score.setVisibility(View.INVISIBLE);
                    viewHolderList.st_time.setText(R.string.basket_postpone);
                    viewHolderList.st_time.setTextColor(mContext.getResources().getColor(R.color.football_keeptime));
                    viewHolderList.ongoing_time.setVisibility(View.GONE);
                    settingdata(viewHolderList, -5);
                    viewHolderList.score_data.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }

        if (score > 0 && score < 8 && DetailsData.getMatchScore() != null && DetailsData.getMatchScore().getRemainTime() != null) {// 显示秒的闪烁
            viewHolderList.backetball_apos.setText("\'");
            viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
            final AlphaAnimation anim1 = new AlphaAnimation(1, 1);
            anim1.setDuration(500);
            final AlphaAnimation anim2 = new AlphaAnimation(0, 0);
            anim2.setDuration(500);
            anim1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewHolderList.backetball_apos.startAnimation(anim2);
                }
            });
            anim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewHolderList.backetball_apos.startAnimation(anim1);
                }
            });
            viewHolderList.backetball_apos.startAnimation(anim1);
        } else {
            viewHolderList.backetball_apos.setText("");
            viewHolderList.backetball_apos.setVisibility(View.VISIBLE);
        }
        //将创建的View注册点击事件
        viewHolderList.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, DetailsData);
                }
            }
        });


    }

    //设置界面score
    public void settingdata(ImmediaViewHolder holder, int score) {

        boolean fullscore = PreferenceUtil.getBoolean(MyConstants.HALF_FULL_SCORE, true); //半全场比分
        boolean differscore = PreferenceUtil.getBoolean(MyConstants.SCORE_DIFFERENCE, true);//总分差
        boolean singscore = PreferenceUtil.getBoolean(MyConstants.SINGLE_SCORE, true); //单节比分
        boolean ranking = PreferenceUtil.getBoolean(MyConstants.HOST_RANKING, true);//排名
        // 0:未开赛,1:一节,2:二节,5:1'OT，以此类推 -1:完场,-2:待定,-3:中断,-4:取消,-5:推迟,50中场
        if (fullscore) {
            if (score == 0 || score == -2 || score == -3 || score == -4 || score == -5) {
                holder.backetball_total.setVisibility(View.GONE);
                holder.score_total.setVisibility(View.GONE);
            } else {
                holder.backetball_total.setVisibility(View.VISIBLE);
                holder.score_total.setVisibility(View.VISIBLE);
            }
        } else {
            holder.backetball_total.setVisibility(View.GONE);
            holder.score_total.setVisibility(View.GONE);
        }
        if (differscore) {

            if (score == 0 || score == -2 || score == -3 || score == -4 || score == -5) {
                holder.backetball_differ.setVisibility(View.INVISIBLE);
                holder.score_differ.setVisibility(View.INVISIBLE);
            } else {
                holder.backetball_differ.setVisibility(View.VISIBLE);
                holder.score_differ.setVisibility(View.VISIBLE);
            }
        } else {
            holder.backetball_differ.setVisibility(View.INVISIBLE);
            holder.score_differ.setVisibility(View.INVISIBLE);
        }
        if (singscore) {
            if (score == 0 || score == -2 || score == -3 || score == -4 || score == -5 || score == -1 || score == 50) {
//                holder.basket_half_score.setVisibility(View.INVISIBLE);
            } else {
//                holder.basket_half_score.setVisibility(View.VISIBLE);
            }
        } else {
//            holder.basket_half_score.setVisibility(View.INVISIBLE);
        }

    }

    private void setSingleScore(ImmediaViewHolder viewHolderList, BasketballItemSearchBean.MatchScoreBean matchScore) {
        if (matchScore != null) {
            viewHolderList.guest_score1.setText(matchScore.getGuest1() + "");
            viewHolderList.guest_score2.setText(matchScore.getGuest2() + "");
            viewHolderList.guest_score3.setText(matchScore.getGuest3() + "");
            viewHolderList.guest_score4.setText(matchScore.getGuest4() + "");
            viewHolderList.home_score1.setText(matchScore.getHome1() + "");
            viewHolderList.home_score2.setText(matchScore.getHome2() + "");
            viewHolderList.home_score3.setText(matchScore.getHome3() + "");
            viewHolderList.home_score4.setText(matchScore.getHome4() + "");
        }
    }

    private void setVisible(ImmediaViewHolder viewHolderList, int section) {
        if (section == 2) {
            viewHolderList.guest_score1.setVisibility(View.VISIBLE);
            viewHolderList.guest_score2.setVisibility(View.INVISIBLE);
            viewHolderList.guest_score3.setVisibility(View.VISIBLE);
            viewHolderList.guest_score4.setVisibility(View.INVISIBLE);
            viewHolderList.home_score1.setVisibility(View.VISIBLE);
            viewHolderList.home_score2.setVisibility(View.INVISIBLE);
            viewHolderList.home_score3.setVisibility(View.VISIBLE);
            viewHolderList.home_score4.setVisibility(View.INVISIBLE);
        } else if (section == 4) {
            viewHolderList.guest_score1.setVisibility(View.VISIBLE);
            viewHolderList.guest_score2.setVisibility(View.VISIBLE);
            viewHolderList.guest_score3.setVisibility(View.VISIBLE);
            viewHolderList.guest_score4.setVisibility(View.VISIBLE);
            viewHolderList.home_score1.setVisibility(View.VISIBLE);
            viewHolderList.home_score2.setVisibility(View.VISIBLE);
            viewHolderList.home_score3.setVisibility(View.VISIBLE);
            viewHolderList.home_score4.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mMatchdata.size();
    }


}

