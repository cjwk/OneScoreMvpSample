package com.hhly.mlottery.frame;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.hhly.mlottery.R;
import com.hhly.mlottery.bean.FirstEvent;
import com.hhly.mlottery.util.AppConstants;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.RongYunUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 描  述：融云会话界面
 * 作  者：tangrr@13322.com
 * 时  间：2016/8/5
 */
public class ConversationFragment extends FragmentActivity implements View.OnClickListener, RongIM.UserInfoProvider {

    private UserInfo mUserInfo;// 当前用户信息

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setFinishOnTouchOutside(true);//设置为true点击区域外消失

        setContentView(R.layout.conversationlist);
        // 设置聊天室背景色为透明
        findViewById(R.id.conversation).setBackgroundColor(getResources().getColor(R.color.transparency));

        this.overridePendingTransition(R.anim.slide_in_from_bottom, 0);// 开启动画

        findViewById(R.id.home_like).setOnClickListener(this);
        findViewById(R.id.guest_like).setOnClickListener(this);
        findViewById(R.id.bt_comment).setOnClickListener(this);
        findViewById(R.id.ll_other).setOnClickListener(this);

        String userTestPhoto = "http://m.1332255.com/news/upload/shortcut/69c426f0f0974d4b8aae0826da71f751.png";// 用户使用测试头像
//        String userTestPhoto = "http://m.1332255.com/oms/upload/shortcut/90b503d1d34d4bc19d4f2e8a0ee43b63.png";// 用户使用测试头像
        mUserInfo = new UserInfo(AppConstants.register.getData().getUser().getUserId(), AppConstants.register.getData().getUser().getNickName(), Uri.parse(userTestPhoto));

        RongIM.setUserInfoProvider(this, true);
        RongIM.getInstance().setCurrentUserInfo(mUserInfo);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }

    @Override
    public void finish() {
        L.d("xxx", "关闭聊天界面!!!!");
        super.finish();
        EventBus.getDefault().post(new FirstEvent("3"));
        this.overridePendingTransition(R.anim.slide_in_from_top, 0);// 关闭动画

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongYunUtils.quitChatRoom();// 退出聊天室
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_like: // 主队点赞
                EventBus.getDefault().post(new FirstEvent("1"));
                break;
            case R.id.guest_like: // 客队点赞
                EventBus.getDefault().post(new FirstEvent("2"));
                break;
            case R.id.bt_comment: // 评论按钮
            case R.id.ll_other: // 点击外部，关闭聊天界面
                this.finish();
                break;
        }
    }

    /**
     * 如果向右滑动，也关闭聊天界面
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return super.onTouchEvent(event);
    }

    /**
     * 返回用户信息
     *
     * @param s
     * @return
     */
    @Override
    public UserInfo getUserInfo(String s) {
        return mUserInfo;
    }
}
