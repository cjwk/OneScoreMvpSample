package com.hhly.mlottery.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhly.mlottery.MyApp;
import com.hhly.mlottery.R;
import com.hhly.mlottery.bean.account.Register;
import com.hhly.mlottery.bean.account.SendSmsCode;
import com.hhly.mlottery.config.BaseURLs;
import com.hhly.mlottery.util.AppConstants;
import com.hhly.mlottery.util.CommonUtils;
import com.hhly.mlottery.util.CountDown;
import com.hhly.mlottery.util.L;
import com.hhly.mlottery.util.UiUtils;
import com.hhly.mlottery.util.cipher.MD5Util;
import com.hhly.mlottery.util.net.VolleyContentFast;
import com.hhly.mlottery.util.net.account.AccountResultCode;
import com.hhly.mlottery.util.net.account.OperateType;
import com.hhly.mlottery.util.net.account.RegisterType;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 注册界面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText et_username , et_password ,et_verifycode;
    private TextView tv_register , tv_verycode;
    private ImageView iv_eye , iv_delete;

    /**
     * 倒计时 默认60s , 间隔1s
     */
    private CountDown countDown;
    private static final int TIMEOUT = 59699;
    private static final int TIMEOUT_INTERVEL = 1000;
    private ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initView();
        initData();
    }


    @Override
    protected void onResume() {
        /**友盟页面统计*/
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("RegisterActivity");
        super.onResume();
        et_username.setFocusable(true);
        et_username.setFocusableInTouchMode(true);
        et_username.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() { //让软键盘延时弹出，以更好的加载Activity
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) et_username.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et_username, 0);
            }

        }, 300);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**友盟页面统计*/
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("RegisterActivity");
    }

    private void initView() {

        progressBar = new ProgressDialog(this);
        progressBar.setMessage(getResources().getString(R.string.registering));

        findViewById(R.id.public_btn_filter).setVisibility(View.GONE);
        findViewById(R.id.public_btn_set).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.public_txt_title)).setText(R.string.register);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv_delete.setOnClickListener(this);

        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);
        findViewById(R.id.public_img_back).setOnClickListener(this);
        iv_eye = (ImageView) findViewById(R.id.iv_eye);
        iv_eye.setOnClickListener(this);

        et_username = (EditText) findViewById(R.id.et_username);
        et_username.addTextChangedListener(this);

        et_password = (EditText) findViewById(R.id.et_password);
        et_verifycode = (EditText) findViewById(R.id.et_verifycode);
        tv_verycode = (TextView) findViewById(R.id.tv_verycode);
        tv_verycode.setOnClickListener(this);

    }

    private void initData() {
        countDown = new CountDown(TIMEOUT, TIMEOUT_INTERVEL, new CountDown.CountDownCallback() {
            @Override
            public void onFinish() {
                enableVeryCode();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_verycode != null){
                    if (tv_verycode.isClickable())
                        tv_verycode.setClickable(false);

                    L.d(TAG,millisUntilFinished/ TIMEOUT_INTERVEL + "秒");
                    tv_verycode.setText(millisUntilFinished/ TIMEOUT_INTERVEL + "秒");
                }
            }
        });
    }

    /**
     * 重新获取验证码
     */
    private void enableVeryCode() {
        if (tv_verycode != null){
            tv_verycode.setText(R.string.resend);
            tv_verycode.setClickable(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.public_img_back: // 返回
                MobclickAgent.onEvent(mContext, "RegisterActivity_Exit");
                finish();
                break;
            case R.id.tv_register: // 注册
                MobclickAgent.onEvent(mContext, "RegisterActivity_RegisterOK");
                String userName = et_username.getText().toString();
                String passWord = et_password.getText().toString();
                String verifyCode = et_verifycode.getText().toString();

                if (UiUtils.isMobileNO(this ,userName)){
                    if (UiUtils.checkVerifyCode(this , verifyCode)){
                        if (UiUtils.checkPassword(this , passWord)){
                            // 登录
//                            UiUtils.toast(this,"register");
                            register(userName , verifyCode , passWord);
                        }
                    }
                }
                // TODO
                break;
            case R.id.iv_delete: // EditText 删除
                MobclickAgent.onEvent(mContext, "RegisterActivity_UserName_Delete");
                et_username.setText("");
                break;
            case R.id.iv_eye:  // 显示密码
                MobclickAgent.onEvent(mContext, "RegisterActivity_PassWord_isHide");
                int inputType = et_password.getInputType();
                if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iv_eye.setImageResource(R.mipmap.close_eye);
                }else{
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iv_eye.setImageResource(R.mipmap.open_eye);
                }
                // 光标移动到结尾
                String pwd = et_password.getText().toString();
                if(!TextUtils.isEmpty(pwd)){
                    et_password.setSelection(pwd.length());
                }

                break;
            case R.id.tv_verycode:
                MobclickAgent.onEvent(mContext, "RegisterActivity_VeryCode");
                getVerifyCode();
                break;
            default:
                break;
        }

    }

    /**
     * 注册
     * @param userName
     * @param verifyCode
     * @param passWord
     */
    private void register(String userName, String verifyCode, String passWord) {

        tv_register.setClickable(false);
        progressBar.show();

        String url = BaseURLs.URL_REGISTER;
        Map<String, String> param = new HashMap<>();
        param.put("account" , userName);
        param.put("password" , MD5Util.getMD5(passWord));
        param.put("registerType" , RegisterType.PHONE);
        param.put("smsCode" , verifyCode);
        param.put("deviceToken" , AppConstants.deviceToken);

        VolleyContentFast.requestJsonByPost(url,param, new VolleyContentFast.ResponseSuccessListener<Register>() {
            @Override
            public void onResponse(Register register) {

                tv_register.setClickable(true);
                progressBar.dismiss();

                if (register != null&& register.getResult() == AccountResultCode.SUCC){
                    CommonUtils.saveRegisterInfo(register);
                    UiUtils.toast(MyApp.getInstance(), R.string.register_succ);
                    L.d(TAG,"注册成功");
                    setResult(RESULT_OK);
                    finish();
                }else{
                    L.e(TAG,"成功请求，注册失败");
                    CommonUtils.handlerRequestResult(register.getResult());
                }
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                progressBar.dismiss();
                tv_register.setClickable(true);
                L.e(TAG,"注册失败");
                UiUtils.toast(RegisterActivity.this , R.string.immediate_unconection);
            }
        } , Register.class);
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        String phone = et_username.getText().toString();
        if (UiUtils.isMobileNO(this ,phone)){
            countDown.start();
            String url = BaseURLs.URL_SENDSMSCODE;
            Map<String, String> param = new HashMap<>();
            param.put("phone" , phone);
            param.put("operateType" , OperateType.TYPE_REGISTER);

            VolleyContentFast.requestJsonByPost(url,param, new VolleyContentFast.ResponseSuccessListener<SendSmsCode>() {
                @Override
                public void onResponse(SendSmsCode jsonObject) {
                    if (jsonObject.getResult() == AccountResultCode.PHONE_ALREADY_EXIST
                            || jsonObject.getResult() == AccountResultCode.PHONE_FORMAT_ERROR
                            || jsonObject.getResult() == AccountResultCode.MESSAGE_SEND_FAIL
                            ){
                        countDown.cancel();
                        enableVeryCode();
                    }

                    CommonUtils.handlerRequestResult(jsonObject.getResult());
                }
            }, new VolleyContentFast.ResponseErrorListener() {
                @Override
                public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                    L.e(TAG,"发送验证码失败");
                    countDown.cancel();
                    enableVeryCode();
                    UiUtils.toast(MyApp.getInstance() , R.string.immediate_unconection);
                }
            } , SendSmsCode.class);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDown != null){
            countDown.cancel();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)){
            iv_delete.setVisibility(View.GONE);
        }else{
            iv_delete.setVisibility(View.VISIBLE);
        }
    }
}
