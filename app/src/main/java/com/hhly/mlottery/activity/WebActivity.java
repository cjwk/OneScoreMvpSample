package com.hhly.mlottery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhly.mlottery.R;
import com.hhly.mlottery.bean.ShareBean;
import com.hhly.mlottery.frame.ShareFragment;
import com.hhly.mlottery.util.AppConstants;
import com.hhly.mlottery.util.CyUtils;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.ToastTools;
import com.hhly.mlottery.widget.ProgressWebView;
import com.umeng.analytics.MobclickAgent;


/**
 * WebView显示H5
 * 107
 */
public class WebActivity extends BaseActivity implements OnClickListener {

    private ProgressWebView mWebView;
    private TextView mTv_check_info;// 关联跳转按钮
    private ImageView mPublic_img_back;// 返回
    private TextView mPublic_txt_title;// 标题
    private String url;// 要显示的H5网址
    private String token;// 登录参数
    private String reqMethod;// 是否传参数
    private String imageurl;// 图片地址
    private String title;// 标题
    private String subtitle;// 副标题
    private int mType;// 关联赛事类型
    private String mThird;// 关联赛事ID
    private String infoTypeName;// 资讯类型名
    private static final String INTENT_PARAMS_TITLE = "title";
    private ImageView public_btn_set;

    private float y;

    private ShareFragment mShareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        initData();
        initEvent();
    }

    int x = 0;

    public void initAnimosion(int endy) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, x, x + endy);
        translateAnimation.setDuration(0);
        translateAnimation.setFillAfter(true);
        mTv_check_info.startAnimation(translateAnimation);
        x -= endy;
        ToastTools.ShowQuickCenter(WebActivity.this, x + "");

    }

    private void initEvent() {
        // 跳转关联赛事详情
        if (mType != 0 && !TextUtils.isEmpty(mThird)) {
            mTv_check_info.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (mType) {
                        case 1:// 篮球
                        {
                            Intent intent = new Intent(mContext, BasketDetailsActivity.class);
                            intent.putExtra("thirdId", mThird);
                            mContext.startActivity(intent);
                            break;
                        }
                        case 2:// 足球
                        {
                            Intent intent = new Intent(mContext, FootballMatchDetailActivity.class);
                            intent.putExtra("thirdId", mThird);
                            intent.putExtra("currentFragmentId", -1);
                            mContext.startActivity(intent);
                            break;
                        }
                    }
                }
            });
        }
    }


    private void initView() {
        ImageView public_btn_filter = (ImageView) findViewById(R.id.public_btn_filter);
        public_btn_filter.setVisibility(View.GONE);
        public_btn_set = (ImageView) findViewById(R.id.public_btn_set);
        public_btn_set.setImageResource(R.mipmap.share);
        public_btn_set.setVisibility(View.GONE);

        public_btn_set.setOnClickListener(this);

        mPublic_img_back = (ImageView) findViewById(R.id.public_img_back);
        mPublic_txt_title = (TextView) findViewById(R.id.public_txt_title);
        mWebView = (ProgressWebView) findViewById(R.id.baseweb_webview);
        mTv_check_info = (TextView) findViewById(R.id.tv_check_info);
//        startyY = mTv_check_info.getY();
        mTv_check_info.setVisibility(View.GONE);
        mPublic_img_back.setOnClickListener(this);
        mWebView.setOnCustomScroolChangeListener(new ProgressWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
                System.out.println("lzf+l=" + l + "t=" + t + "oldl=" + oldl + "oldt" + oldt);
                System.out.println("lzf+getContentHeight=" + mWebView.getContentHeight() * mWebView.getScale() + "getHeight=" + (mWebView.getHeight() + mWebView.getScrollY()));
                y = mWebView.getContentHeight() * mWebView.getScale() - (mWebView.getHeight() + mWebView.getScrollY());
                if (y < 10) {

                    //已经处于底端
//                    mTv_check_info.setVisibility(View.VISIBLE);
                    if (mType != 0 && !TextUtils.isEmpty(mThird)) {
                        mTv_check_info.setVisibility(View.VISIBLE);
                    } else {
                        mTv_check_info.setVisibility(View.GONE);
                    }
                } else {
                    if (mTv_check_info.getVisibility() == View.VISIBLE) {//给一个容差  避免临界点闪烁
                        if (y > 150) {
                            mTv_check_info.setVisibility(View.GONE);
//                            d();
                        } else {
                            mTv_check_info.setVisibility(View.VISIBLE);
                        }
                    }


                }
//                initAnimosion(t - oldt);
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        // 不用缓存
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        String us = webSettings.getUserAgentString();
        webSettings.setUserAgentString(us.replace("Android", "yibifen Android"));//给useagent加个标识yibifen
    }

    protected void initData() {
        try {
            Intent intent = getIntent();
            url = intent.getStringExtra("key");
            L.d("CommonUtils初始url" + url);
//            url = "http://192.168.33.14:8080/gameweb/h5/index";
//            url = "http://192.168.37.6:8080/gameweb/h5/index";
            imageurl = intent.getStringExtra("imageurl");
            title = intent.getStringExtra(INTENT_PARAMS_TITLE);
            subtitle = intent.getStringExtra("subtitle");//轮播图没有副标题，所以为null  请知悉
            mType = intent.getIntExtra("type", 0);
            mThird = intent.getStringExtra("thirdId");
            infoTypeName = intent.getStringExtra("infoTypeName");
            token = intent.getStringExtra("token");
            String deviceId = AppConstants.deviceToken;
            reqMethod = intent.getStringExtra("reqMethod");
            mPublic_txt_title.setText(infoTypeName);
//            if (!TextUtils.isEmpty(token) && reqMethod != null && reqMethod.equals("post")) {//不是新闻资讯的时候隐藏分享和评论
//                public_btn_set.setVisibility(View.GONE);
//            } else {//token为空，说明是资讯，显示分享和评论
//                public_btn_set.setVisibility(View.VISIBLE);
//                //添加评论功能  评论功能已单独封装成一个模块  调用的时候  只要以下代码就行
//                ChatFragment chatFragment = new ChatFragment();
//                CyUtils.addComment(chatFragment, url, title, false, false, getSupportFragmentManager(), R.id.comment);
//            }
            if (url != null && url.contains("comment")) {
                //添加评论功能  评论功能已单独封装成一个模块  调用的时候  只要以下代码就行
                ChatFragment chatFragment = new ChatFragment();
                CyUtils.addComment(chatFragment, url, title, false, false, getSupportFragmentManager(), R.id.comment);
            }
            if (url != null && url.contains("share")) {
                public_btn_set.setVisibility(View.VISIBLE);
            } else {
                public_btn_set.setVisibility(View.VISIBLE);
            }
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    // TODO Auto-generated method stub
//
//                    super.onPageFinished(view, url);
//                    L.i("lzftype=" + mType + "thirtid=" + mThird);
//                    if (mType != 0 && !TextUtils.isEmpty(mThird)) {
//                        mTv_check_info.setVisibility(View.VISIBLE);
//                    } else {
//                        mTv_check_info.setVisibility(View.GONE);
//                    }
//                }
            });
            //其他页传过来的reqMethod为post时，提交token  否则不提交
            if (reqMethod != null && token != null && reqMethod.equals("post")) {

//                mWebView.postUrl(url, token.getBytes("utf-8"));
//                url = url + "?loginToken=" + token + "&deviceToken=" + deviceId;
                url = url.replace("{loginToken}", token);
                url = url.replace("{deviceToken}", deviceId);
            }
            mWebView.loadUrl(url);
            L.d("lzf:" + "imageurl=" + imageurl + "title" + title + "subtitle" + subtitle);
            L.d("CommonUtils:" + "token=" + token + "reqMethod" + reqMethod + "url=" + url);


            L.d("lzf:" + "imageurl=" + imageurl + "title" + title + "subtitle" + subtitle);
        } catch (Exception e) {
            L.d("initData初始化失败:" + e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("WebActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("WebActivity");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_img_back://返回
                if (mWebView.canGoBack()) {
                    mWebView.goBack();// 返回上一页面

                } else {
                    MobclickAgent.onEvent(mContext, "Football_DataInfo_Exit");
                    finish();
                }
                break;
            case R.id.public_btn_set: //分享
                //  @style/AppTheme.BlackStatusBar.ColorGreen


                MobclickAgent.onEvent(mContext, "Football_DataInfo_Share");

                ShareBean shareBean = new ShareBean();

                String summary = "";
                if (subtitle == null || "".equals(subtitle)) {
                    summary = getString(R.string.share_summary_default);
                } else {
                    summary = subtitle;
                }
                shareBean.setTitle(title != null ? title : mContext.getResources().getString(R.string.share_recommend));
                shareBean.setSummary(summary);
                shareBean.setTarget_url(url != null ? url : "http://m.13322.com");
                shareBean.setImage_url(imageurl != null ? imageurl : "");
                shareBean.setCopy(url);

                mShareFragment = ShareFragment.newInstance(shareBean);
                mShareFragment.show(getSupportFragmentManager(), "bottomShare");
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();// 返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}