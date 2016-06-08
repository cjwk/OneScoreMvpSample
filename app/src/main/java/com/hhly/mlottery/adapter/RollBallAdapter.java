package com.hhly.mlottery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhly.mlottery.R;
import com.hhly.mlottery.adapter.core.BaseRecyclerViewAdapter;
import com.hhly.mlottery.adapter.core.BaseRecyclerViewHolder;
import com.hhly.mlottery.bean.Match;
import com.hhly.mlottery.bean.MatchOdd;
import com.hhly.mlottery.bean.websocket.WebSocketMatchChange;
import com.hhly.mlottery.bean.websocket.WebSocketMatchEvent;
import com.hhly.mlottery.bean.websocket.WebSocketMatchOdd;
import com.hhly.mlottery.bean.websocket.WebSocketMatchStatus;
import com.hhly.mlottery.util.PreferenceUtil;
import com.hhly.mlottery.util.RxBus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RollBallAdapter extends BaseRecyclerViewAdapter {

    public static final int VIEW_TYPE_DEFAULT = 1;

    private static int topDataCount = 1;
    private static int A = 1;
    private int handicap;
    private Map<Match, Boolean> isTopDataCacheMaps = new HashMap<>();

    private Context context;
    private boolean resetColor;

    public RollBallAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setList(List list) {
        getList().clear();
        if (list == null) return;
        getList().addAll(list);
        for (int i = 0; i < list.size(); i++) {
            isTopDataCacheMaps.put((Match) list.get(i), false);
        }
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.item_football_roll};
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        int itemViewType = getRecycleViewItemType(position);
        switch (itemViewType) {
            case VIEW_TYPE_DEFAULT:
                this.bindDefaultView(viewHolder, position);
                break;
        }
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @SuppressLint("SetTextI18n")
    private void bindDefaultView(final BaseRecyclerViewHolder viewHolder, final int position) {
        final Match data = getItemByPosition(position);
        if (data == null) return;

        A++;
        final CardView container = viewHolder.findViewById(R.id.container);
        RelativeLayout rlHalfContainer = viewHolder.findViewById(R.id.rlHalfContainer);
        TextView tvRaceName = viewHolder.findViewById(R.id.tvRaceName);
        TextView tvHomeScore = viewHolder.findViewById(R.id.tvHomeScore);
        TextView tvTime = viewHolder.findViewById(R.id.tvTime);
        TextView tvKeepTime = viewHolder.findViewById(R.id.tvKeepTime);
        TextView tvGuestScore = viewHolder.findViewById(R.id.tvGuestScore);
        LinearLayout itemPositionControl = viewHolder.findViewById(R.id.ll1);
        final ImageView ivItemPositionControl = viewHolder.findViewById(R.id.ivItemPositionControl);
        TextView tvHomeTeam = viewHolder.findViewById(R.id.tvHomeTeam);
        TextView tvHomeYellowCard = viewHolder.findViewById(R.id.tvHomeYellowCard);
        TextView tvHomeRedCard = viewHolder.findViewById(R.id.tvHomeRedCard);
        TextView tvHomeHalfScore = viewHolder.findViewById(R.id.tvHomeHalfScore);
        TextView tvGuestHalfScore = viewHolder.findViewById(R.id.tvGuestHalfScore);
        TextView tvGuestTeam = viewHolder.findViewById(R.id.tvGuestTeam);
        TextView tvGuestYelloCard = viewHolder.findViewById(R.id.tvGuestYelloCard);
        TextView tvGuestRedCard = viewHolder.findViewById(R.id.tvGuestRedCard);
        TextView tvLeftOdds_YA = viewHolder.findViewById(R.id.tvLeftOdds_YA);
        TextView tvHandicapValue_YA_BLACK = viewHolder.findViewById(R.id.tvHandicapValue_YA_BLACK);
        TextView tvRightOdds_YA = viewHolder.findViewById(R.id.tvRightOdds_YA);
        TextView tvLeftOdds_DA = viewHolder.findViewById(R.id.tvLeftOdds_DA);
        TextView tvHandicapValue_DA_BLACK = viewHolder.findViewById(R.id.tvHandicapValue_DA_BLACK);
        TextView tvRightOdds_DA = viewHolder.findViewById(R.id.tvRightOdds_DA);
        TextView tvLeftOdds_EU = viewHolder.findViewById(R.id.tvLeftOdds_EU);
        TextView tvMediumOdds_EU = viewHolder.findViewById(R.id.tvMediumOdds_EU);
        TextView tvRightOdds_EU = viewHolder.findViewById(R.id.tvRightOdds_EU);

        // 初始化数据
        this.setVisiableStateOfThisViews(container, rlHalfContainer, tvHomeScore, tvGuestScore);
        this.initializedTextColor(tvHandicapValue_DA_BLACK, tvHandicapValue_YA_BLACK, tvLeftOdds_DA, tvLeftOdds_YA, tvLeftOdds_EU, tvMediumOdds_EU, tvRightOdds_DA, tvRightOdds_YA, tvRightOdds_EU);
        tvKeepTime.setTextColor(context.getResources().getColor(R.color.text_about_color));

        // 置顶
        if (data.getIsTopData() > 0 || isTopDataCacheMaps.get(data)) {
            ivItemPositionControl.setImageResource(R.mipmap.roll_default);
            RollBallAdapter.this.showShadow(container, 0.8f);
        } else {
            ivItemPositionControl.setImageResource(R.mipmap.roll_top);
            RollBallAdapter.this.showShadow(container, 1f);
        }

        // WebSocket推送时，赔率的颜色变化
        switch (handicap) {
            // 亚盘
            case 1:
                setupOddTextColor(data, tvLeftOdds_YA, tvHandicapValue_YA_BLACK, tvRightOdds_YA);
                break;
            // 大小球
            case 2:
                setupOddTextColor(data, tvLeftOdds_DA, tvHandicapValue_DA_BLACK, tvRightOdds_DA);
                break;
            // 欧赔
            case 3:
                setupOddTextColor(data, tvLeftOdds_EU, tvMediumOdds_EU, tvRightOdds_EU);
                break;
        }

        // TODO:后台数据应该将默认值为null情况，改成0更合理；
        if (TextUtils.isEmpty(data.getKeepTime())) {
            data.setKeepTime("0");
        }

        // 比赛状态
        // TODO: -11 待定 -13 中断没有写
        switch (data.getStatusOrigin()) {
            case "0": // 未开赛
                rlHalfContainer.setVisibility(View.INVISIBLE);
                tvHomeScore.setVisibility(View.INVISIBLE);
                tvGuestScore.setVisibility(View.INVISIBLE);
                tvKeepTime.setText("VS");
                tvKeepTime.setTextColor(context.getResources().getColor(R.color.res_pl_color));
                break;
            case "1": // 上半场进行时间
                if (Integer.parseInt(data.getKeepTime()) > 45) tvKeepTime.setText("45+");
                else tvKeepTime.setText(data.getKeepTime() + "'");
                break;
            case "3": // 下半场进行时间
                if (Integer.parseInt(data.getKeepTime()) > 90) tvKeepTime.setText("90+");
                else tvKeepTime.setText(data.getKeepTime() + "'");
                break;
            case "2": // 中场
                tvKeepTime.setText("中场");
                break;
            case "4": // 加时
                tvKeepTime.setText("加时");
                break;
            case "5": // 点球
                tvKeepTime.setText("点球");
                break;
        }

        // 赔率
        Map<String, MatchOdd> matchOddMap = data.getMatchOdds();
        MatchOdd asiaSize = null, euro = null, asiaLet = null;
        if (matchOddMap != null && matchOddMap.size() > 0) {
            for (Map.Entry<String, MatchOdd> matchOdd : matchOddMap.entrySet()) {
                switch (matchOdd.getKey()) {
                    case "asiaSize":// 大小盘数据
                        asiaSize = matchOddMap.get("asiaSize");
                        break;
                    case "euro":// 欧赔数据
                        euro = matchOddMap.get("euro");
                        break;
                    case "asiaLet":// 亚赔数据
                        asiaLet = matchOddMap.get("asiaLet");
                        break;
                }
            }
        }

        // 杯赛名称
        tvRaceName.setText(data.getRacename());
        // 主队分数
        tvHomeScore.setText(data.getHomeScore());
        // 开赛时间
        tvTime.setText(data.getTime());
        // 客队分数
        tvGuestScore.setText(data.getGuestScore());
        // 主队名称
        tvHomeTeam.setText(data.getHometeam());
        // 主队黄牌数
        tvHomeYellowCard.setText(data.getHome_yc());
        // 主队红牌数
        tvHomeRedCard.setText(data.getHome_rc());
        // 半场主队得分数
        tvHomeHalfScore.setText(data.getHomeHalfScore());
        // 半场客服的分数
        tvGuestHalfScore.setText(data.getGuestHalfScore());
        // 客队名称
        tvGuestTeam.setText(data.getGuestteam());
        // 客队黄牌数
        tvGuestYelloCard.setText(data.getGuest_yc());
        // 客队红牌数
        tvGuestRedCard.setText(data.getGuest_rc());
        // 亚盘赔率
        tvLeftOdds_YA.setText(asiaLet != null ? asiaLet.getLeftOdds() : "-");
        tvHandicapValue_YA_BLACK.setText(asiaLet != null ? asiaLet.getHandicapValue(): "-");
        tvRightOdds_YA.setText(asiaLet != null ? asiaLet.getRightOdds() : "-");
        // 大小盘赔率
        tvLeftOdds_DA.setText(asiaSize != null ? asiaSize.getLeftOdds() : "-");
        tvHandicapValue_DA_BLACK.setText(asiaSize != null ? asiaSize.getHandicapValue(): "-");
        tvRightOdds_DA.setText(asiaSize != null ? asiaSize.getRightOdds() : "-");
        // 欧盘赔率
        tvLeftOdds_EU.setText(euro != null ? euro.getLeftOdds() : "-");
        tvMediumOdds_EU.setText(euro != null ?  euro.getMediumOdds(): "-");
        tvRightOdds_EU.setText(euro != null ? euro.getRightOdds() : "-");

        // 控制器
        itemPositionControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getIsTopData() > 0) {
                    data.setIsTopData(0);
                    if (isTopDataCacheMaps.containsKey(data)) isTopDataCacheMaps.put(data, false);
                } else {
                    data.setIsTopData(topDataCount);
                    if (isTopDataCacheMaps.containsKey(data)) isTopDataCacheMaps.put(data, true);
                    topDataCount++;
                }
                RollBallAdapter.this.transformMapper(position);
            }
        });
    }

    private void setVisiableStateOfThisViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void showShadow(final View view, float alpha) {
        view.setAlpha(alpha);
//        ObjectAnimator.ofFloat(view, View.ALPHA, 0, 0.8f).setDuration(400).start();
    }

    private void transformMapper(int position) {
        List<Match> list = getList();
        Match data = list.get(position);
        list.remove(data);
        list.add(0, data);
        Collections.sort(list, new Match());
        notifyDataSetChanged();
    }

    public void updateItemFromWebSocket(Object o) {
        List<Match> matchList = getList();
        for (int i = 0; i < matchList.size(); i++) {
            Match match = matchList.get(i);
            if (o instanceof WebSocketMatchStatus) {
                this.updateTypeStatus(match, i, (WebSocketMatchStatus) o);
            } else if (o instanceof WebSocketMatchOdd) {
                this.updateTypeOdds(match, i, (WebSocketMatchOdd) o);
            } else if (o instanceof WebSocketMatchEvent) {
                this.updateTypeEvent(match, i, (WebSocketMatchEvent) o);
            } else if (o instanceof WebSocketMatchChange) {
                this.updateTypeMatchChange(match, i, (WebSocketMatchChange) o);
            }
        }
    }

    // 状态推送
    private void updateTypeStatus(Match match, int position, WebSocketMatchStatus webSocketMatchStatus) {
        final Match[] target = new Match[1];
        Map<String, String> data = webSocketMatchStatus.getData();
        if (match.getThirdId().equals(webSocketMatchStatus.getThirdId())) {
            target[0] = match;
            match.setStatusOrigin(data.get("statusOrigin"));
            if ("0".equals(match.getStatusOrigin()) && "1".equals(match.getStatusOrigin())) {// 未开场变成开场
                match.setHomeScore("0");
                match.setGuestScore("0");
            }
            if (data.get("keepTime") != null) {
                match.setKeepTime(data.get("keepTime"));
            }
            if (data.get("home_yc") != null) {
                match.setHome_yc(data.get("home_yc"));
            }
            if (data.get("guest_yc") != null) {
                match.setGuest_yc(data.get("guest_yc"));
            }
            if (data.get("homeHalfScore") != null) {
                match.setHomeHalfScore(data.get("homeHalfScore"));
            }
            if (data.get("guestHalfScore") != null) {
                match.setGuestHalfScore(data.get("guestHalfScore"));
            }

            this.WebSocketMarkTopStatu(match);
            getList().set(position, match);
//            notifyItemChanged(position);
            notifyDataSetChanged();
        }

        // 界面1分钟后销毁
        if (target[0] != null) {
            /**
             * 0：未开，页面显示VS
             1：上半场，页面显示KeepTime（注：KeepTime>45，显示45+）
             3：下半场，显示KeepTime（注： KeepTime>90，显示90+）
             2：中场
             4：加时
             5：点球
             -1：完场，在KeepTime位置显示半场比分(1分钟后清除)
             -10：取消，1分钟后清除
             -11：待定
             -12：腰斩，1分钟后清除
             -13：中断
             -14：推迟，1分钟后清除
             */
            if ("-1".equals(target[0].getStatusOrigin()) || "-10".equals(target[0].getStatusOrigin()) || "-12".equals(target[0].getStatusOrigin()) || "-14".equals(target[0].getStatusOrigin())) {
                RxBus.getDefault().post(target[0]);
            }
        }
    }

    // 赔率推送
    private void updateTypeOdds(final Match match, final int position, WebSocketMatchOdd webSocketMatchOdd) {
        if (match.getThirdId().equals(webSocketMatchOdd.getThirdId())) {
            final List<Map<String, String>> matchOddDataLists = webSocketMatchOdd.getData();
            for (Map<String, String> oddDataMaps : matchOddDataLists) {
                // 亚赔
                if (oddDataMaps.get("handicap").equals("asiaLet")) {
                    handicap = 1;
                    MatchOdd asiaLetOdd = this.getMatchOdd(match, "asiaLet");
                    this.changeOddTextColor(match, asiaLetOdd.getLeftOdds(), oddDataMaps.get("leftOdds"), asiaLetOdd.getRightOdds(), oddDataMaps.get("rightOdds"), asiaLetOdd.getHandicapValue(), oddDataMaps.get("mediumOdds"));
                    this.updateMatchOdd(match, asiaLetOdd, oddDataMaps, "asiaLet");
                    // 欧赔
                } else if (oddDataMaps.get("handicap").equals("euro")) {
                    handicap = 3;
                    MatchOdd euroOdd = this.getMatchOdd(match, "euro");
                    this.changeOddTextColor(match, euroOdd.getLeftOdds(), oddDataMaps.get("leftOdds"), euroOdd.getRightOdds(), oddDataMaps.get("rightOdds"), euroOdd.getMediumOdds(), oddDataMaps.get("mediumOdds"));
                    this.updateMatchOdd(match, euroOdd, oddDataMaps, "euro");
                    // 大小
                } else if (oddDataMaps.get("handicap").equals("asiaSize")) {
                    handicap = 2;
                    MatchOdd asiaSizeOdd = this.getMatchOdd(match, "asiaSize");
                    this.changeOddTextColor(match, asiaSizeOdd.getLeftOdds(), oddDataMaps.get("leftOdds"), asiaSizeOdd.getRightOdds(), oddDataMaps.get("rightOdds"), asiaSizeOdd.getHandicapValue(), oddDataMaps.get("mediumOdds"));
                    this.updateMatchOdd(match, asiaSizeOdd, oddDataMaps, "asiaSize");
                }
            }
            this.WebSocketMarkTopStatu(match);
            getList().set(position, match);
            notifyItemChanged(position);

            Observable.timer(5000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    resetColor = true;
                    notifyItemChanged(position);
                }
            });
        }
    }

    // 事件推送
    private void updateTypeEvent(final Match match, final int position, WebSocketMatchEvent webSocketMatchEvent) {
        if (match.getThirdId().equals(webSocketMatchEvent.getThirdId())) {
            match.setItemBackGroundColorId(R.color.item_football_event_yellow);
            String eventType = webSocketMatchEvent.getData().get("eventType");
            if ("1".equals(eventType) || "2".equals(eventType) || "5".equals(eventType) || "6".equals(eventType)) {// 主队有效进球传
                // 客队有效进球5or客队进球取消6
                if (webSocketMatchEvent.getData().get("homeScore") != null) {
                    match.setHomeScore(webSocketMatchEvent.getData().get("homeScore"));
                }
                if (webSocketMatchEvent.getData().get("guestScore") != null) {
                    match.setGuestScore(webSocketMatchEvent.getData().get("guestScore"));
                }
            } else if ("3".equals(eventType) || "4".equals(eventType) || "7".equals(eventType) || "8".equals(
                    eventType)) {// 主队红牌3or主队红牌取消4
                if (webSocketMatchEvent.getData().get("home_rc") != null) {
                    match.setHome_rc(webSocketMatchEvent.getData().get("home_rc"));
                }
                if (webSocketMatchEvent.getData().get("guest_rc") != null) {
                    match.setGuest_rc(webSocketMatchEvent.getData().get("guest_rc"));
                }
                if (webSocketMatchEvent.getData().get("home_yc") != null) {
                    match.setHome_yc(webSocketMatchEvent.getData().get("home_yc"));
                }
                if (webSocketMatchEvent.getData().get("guest_yc") != null) {
                    match.setGuest_yc(webSocketMatchEvent.getData().get("guest_yc"));
                }
            }

            if ("1".equals(eventType) || "2".equals(eventType) || "3".equals(eventType) || "4".equals(eventType)) {// 主场红名
                match.setHomeTeamTextColorId(R.color.red);
            } else if ("5".equals(eventType) || "6".equals(eventType) || "7".equals(eventType) || "8".equals(eventType)) {// 客场红名
                match.setGuestTeamTextColorId(R.color.red);
            }
            this.WebSocketMarkTopStatu(match);
            getList().set(position, match);
            notifyItemChanged(position);// 先换颜色

            // 五秒后把颜色修改回来
            Observable.timer(5, TimeUnit.MINUTES).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    resetColor = true;
                    notifyItemChanged(position);
                }
            });
        }
    }

    // 比赛变换推送
    private void updateTypeMatchChange(Match match, int position, WebSocketMatchChange webSocketMatchChange) {
        if (match.getThirdId().equals(webSocketMatchChange.getThirdId())) {
            String language = PreferenceUtil.getString("language", "rCN");
            if ((webSocketMatchChange.getData().get("region").equals("zh") && language.equals(
                    "rCN")) || (webSocketMatchChange.getData().get("region").equals("zh-TW") && language.equals(
                    "rTW")) || (webSocketMatchChange.getData().get("region").equals("en") && language.equals(
                    "rEN")) || (webSocketMatchChange.getData().get("region").equals("ko") && language.equals(
                    "rKO")) || (webSocketMatchChange.getData().get("region").equals("id") && language.equals(
                    "rID")) || (webSocketMatchChange.getData().get("region").equals("th") && language.equals(
                    "rTH")) || (webSocketMatchChange.getData().get("region").equals("vi") && language.equals("rVI"))) {

                if (webSocketMatchChange.getData().get("hometeam") != null) {
                    match.setHometeam(webSocketMatchChange.getData().get("hometeam"));
                }

                if (webSocketMatchChange.getData().get("guestteam") != null) {
                    match.setGuestteam(webSocketMatchChange.getData().get("guestteam"));
                }

                if (webSocketMatchChange.getData().get("racename") != null) {
                    match.setRacename(webSocketMatchChange.getData().get("racename"));
                }
                this.WebSocketMarkTopStatu(match);
                getList().set(position, match);
//                notifyItemChanged(position);
                notifyDataSetChanged();
            }
        }
    }

    // 当webSocket推送新消息过来时，要将原来置顶的消息继续保持置顶状态
    private void WebSocketMarkTopStatu(Match match) {
        for (Map.Entry<Match, Boolean> entry : isTopDataCacheMaps.entrySet()) {
            if (entry.getKey().equals(match)) {
                isTopDataCacheMaps.put(match, entry.getValue());
            }
        }
    }

    private MatchOdd getMatchOdd(Match match, String handicap) {
        return match.getMatchOdds().get(handicap);
    }

    // 赔率推送消息
    private void changeOddTextColor(Match match, String oldLeftOdds, String newLeftOdds, String oldRightOdds, String newRightOdds, String oldHandicapValue, String newHandicapValue) {
        float leftMatchOddF = oldLeftOdds != null && !oldLeftOdds.equals("-") ? Float.parseFloat(oldLeftOdds) : (float) 0.00;
        float rightMatchOddF = oldRightOdds != null && !oldLeftOdds.equals("-") ? Float.parseFloat(oldRightOdds) : (float) 0.00;
        float leftMapOddF = Float.parseFloat(!newLeftOdds.equals("-") ? newLeftOdds : "0.00");
        float rightMapOddF = Float.parseFloat(!newRightOdds.equals("-") ? newRightOdds : "0.00");
        float midMatchOddF = oldHandicapValue != null && !oldLeftOdds.equals("-") ? Float.parseFloat(oldHandicapValue) : (float) 0.00;
        float midMapOddF = Float.parseFloat(!newHandicapValue.equals("-") ? newHandicapValue : "0.00");

        if (leftMatchOddF < leftMapOddF) {// 左边的值升了
            match.setLeftOddTextColorId(R.color.odd_rise_red);
        } else if (leftMatchOddF > leftMapOddF) {// 左边的值降了
            match.setLeftOddTextColorId(R.color.odd_drop_green);
        } else {
            match.setLeftOddTextColorId(R.color.white);
        }

        if (rightMatchOddF < rightMapOddF) {// 右边的值升了
            match.setRightOddTextColorId(R.color.odd_rise_red);
        } else if (rightMatchOddF > rightMapOddF) {// 右边的值降了
            match.setRightOddTextColorId(R.color.odd_drop_green);
        } else {
            match.setRightOddTextColorId(R.color.white);
        }

        if (midMatchOddF < midMapOddF) {// 中间的值升了
            match.setMidOddTextColorId(R.color.odd_rise_red);
        } else if (midMatchOddF > midMapOddF) {// 中间的值降了
            match.setMidOddTextColorId(R.color.odd_drop_green);
        } else {
            match.setMidOddTextColorId(R.color.white);
        }
    }

    private void setupOddTextColor(Match data, TextView leftOdds, TextView midOdds, TextView rightOdds) {
        if (!resetColor) {
            if (data.getLeftOddTextColorId() != 0) {
                leftOdds.setBackgroundResource(data.getLeftOddTextColorId());
                data.setLeftOddTextColorId(0);
            }
            if (data.getRightOddTextColorId() != 0) {
                rightOdds.setBackgroundResource(data.getRightOddTextColorId());
                data.setRightOddTextColorId(0);
            }
            if (data.getMidOddTextColorId() != 0) {
                midOdds.setBackgroundResource(data.getMidOddTextColorId());
                data.setMidOddTextColorId(0);
            }
        } else {
            resetColor = false;
            leftOdds.setBackgroundResource(R.color.white);
            midOdds.setBackgroundResource(R.color.white);
            rightOdds.setBackgroundResource(R.color.white);
        }
    }

    private void initializedTextColor(/*TextView tvHandicapValue_DA_BLACK, TextView tvHandicapValue_YA_BLACK, */TextView... textViews) {
//        tvHandicapValue_DA_BLACK.setTextColor(context.getResources().getColor(R.color.res_name_color));
//        tvHandicapValue_YA_BLACK.setTextColor(context.getResources().getColor(R.color.res_name_color));
        for (TextView tv : textViews) {
//            tv.setTextColor(context.getResources().getColor(R.color.res_pl_color));
            tv.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }


    private void updateMatchOdd(Match match, MatchOdd matchOdd, Map<String, String> oddDataMaps, String key) {
        if (matchOdd == null) {
            matchOdd = new MatchOdd(key, oddDataMaps.get("mediumOdds"), oddDataMaps.get("rightOdds"), oddDataMaps.get("leftOdds"));
            match.getMatchOdds().put(key, matchOdd);
        } else {
            if (!key.equals("euro")) {
                matchOdd.setTypeOddds(oddDataMaps.get("mediumOdds"), oddDataMaps.get("rightOdds"), oddDataMaps.get("leftOdds"));
            } else {
                matchOdd.setEuroTypeOdds(oddDataMaps.get("mediumOdds"), oddDataMaps.get("rightOdds"), oddDataMaps.get("leftOdds"));
            }
        }
    }
}
