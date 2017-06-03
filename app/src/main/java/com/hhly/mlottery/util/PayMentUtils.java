package com.hhly.mlottery.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hhly.mlottery.R;
import com.hhly.mlottery.mvp.bettingmvp.eventbusconfig.PayMentZFBResultEventBusEntity;
import com.hhly.mlottery.alipay.PayResult;
import com.hhly.mlottery.bean.bettingbean.PaymentWeiXinBean;
import com.hhly.mlottery.bean.bettingbean.PaymentZFBBean;
import com.hhly.mlottery.config.ConstantPool;
import com.hhly.mlottery.util.net.VolleyContentFast;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by：XQyi on 2017/6/2 15:22
 * Use: 支付的调用工具类（WeiXin、zfb）
 */
public class PayMentUtils {

    private static final int SDK_PAY_FLAG = 1;
    private static Context mContext;

    /* -----------------------------支付宝相关方法 start------------------------------------------------------------------------*/
    /**
     * 支付宝参数接口
     */
    public static void ALiPayData(Context context, final Activity activity, String payUrl, Map<String, String> payMap) {

        mContext = context;

        VolleyContentFast.requestJsonByPost(payUrl, payMap, new VolleyContentFast.ResponseSuccessListener<PaymentZFBBean>() {
            @Override
            public void onResponse(PaymentZFBBean jsonBean) {

                if (jsonBean == null || jsonBean.getData() == null) {
                    return;
                }
                if (jsonBean.getData().getBody() != null) {
                    L.d("qwer_AliPay===>>", "body = " + jsonBean.getData().getBody());
                    toAliPay(jsonBean.getData().getBody(), activity);
                }
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                L.d("qwer_AliPay===>>", "error");
                Toast.makeText(mContext, "后台接口访问失败", Toast.LENGTH_SHORT).show();
            }
        }, PaymentZFBBean.class);

    }

    /**
     * 调用支付宝支付
     */

    private static void toAliPay(final String orderInfo, final Activity payActivity) {

        Runnable aliPayRun = new Runnable() {
            @Override
            public void run() {
                PayTask aliPay = new PayTask(payActivity);
                Map<String, String> payMap = aliPay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = payMap;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(aliPayRun);
        payThread.start();
    }

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult mResult = new PayResult((Map<String, String>) msg.obj);
                    String result = mResult.getResult();
                    String resultStatus = mResult.getResultStatus();

//                    mPayResult = resultStatus; //赋值返回码
//                    L.d("qwer_asd ", result + " == " + resultStatus);
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(mContext, "支付成功 > " + resultStatus, Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(mContext, "结果确认中 > " + resultStatus, Toast.LENGTH_SHORT).show();
//                        } else if (TextUtils.equals(resultStatus, "6001")) {
//                            Toast.makeText(mContext, "支付取消 > " + resultStatus, Toast.LENGTH_SHORT).show();
//                        } else if (TextUtils.equals(resultStatus, "6002")) {
//                            Toast.makeText(mContext, "网络异常 > " + resultStatus, Toast.LENGTH_SHORT).show();
//                        } else if (TextUtils.equals(resultStatus, "5000")) {
//                            Toast.makeText(mContext, "重复请求 > " + resultStatus, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(mContext, "支付失败 > " + resultStatus, Toast.LENGTH_SHORT).show();
//                        }
//                    }

                    EventBus.getDefault().post(new PayMentZFBResultEventBusEntity(resultStatus));
                }
                break;
                default:
                    break;
            }
        }
    };

    private static String mPayResult = "";
    public static String getPayResult(){
        return mPayResult;
    }

    /**
     * 判断是否安装有支付宝客户端
     *
     * @param context
     * @return
     */
    public static boolean checkAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }
    /* -----------------------------支付宝相关方法 end------------------------------------------------------------------------*/


    /* ------------------------------微信相关方法 start----------------------------------------------------------------*/

    /**
     * 微信支付参数接口
     */
    public static void WeiXinPayData(Context context, String payUrl, Map<String, String> payMap) {

        mContext = context;

//        Map<String, String> params = new HashMap<>();
//        String userid = AppConstants.register.getData().getUser().getUserId();
//        String token = AppConstants.deviceToken;
//        String sign = AppConstants.SIGN_KEY;
//
//        params.put("userId" , userid);//用户ID
//        params.put("service" , "3");//3 微信 4 支付宝
//        params.put("tradeAmount" , "1");//金额 分
//        params.put("token" , token);//登陆的token
//        params.put("sign" , sign);//签名 和 登陆的时候签名一样

//        String url = "http://192.168.31.207:8092/pay/recharge"; //?userId=HHLY00000166&service=3&tradeAmount=1&token=a&sign=aa
        VolleyContentFast.requestJsonByPost(payUrl, payMap, new VolleyContentFast.ResponseSuccessListener<PaymentWeiXinBean>() {
            @Override
            public void onResponse(PaymentWeiXinBean jsondata) {
                if (jsondata == null || jsondata.getData() == null) {
                    return;
                }
                PaymentWeiXinBean.DataDetailsBean payData = jsondata.getData();
                toWeiXinPay(payData);
            }
        }, new VolleyContentFast.ResponseErrorListener() {
            @Override
            public void onErrorResponse(VolleyContentFast.VolleyException exception) {
                Toast.makeText(mContext, "后台接口访问失败", Toast.LENGTH_SHORT).show();
            }
        }, PaymentWeiXinBean.class);
    }

    /**
     * 调用微信支付
     *
     * @param payInfo
     */
    private static void toWeiXinPay(PaymentWeiXinBean.DataDetailsBean payInfo) {
        //注册appid
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, ConstantPool.APP_ID);// 通过WXAPIFactory工厂，获取IWXAPI的实例
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.share_uninstall_webchat), Toast.LENGTH_SHORT).show();
        }
        api.registerApp(ConstantPool.APP_ID); //将appid注册到微信

        //赋值微信支付所需参数
        PayReq payReq = new PayReq();
        payReq.appId = payInfo.getAppid();
        payReq.partnerId = payInfo.getPartnerid();
        payReq.prepayId = payInfo.getPrepayid();
        payReq.packageValue = payInfo.getPackages();
        payReq.nonceStr = payInfo.getNoncestr();
        payReq.timeStamp = payInfo.getTimestamp();
        payReq.sign = payInfo.getSign();
        api.sendReq(payReq);
    }
    /* ------------------------------微信相关方法 end----------------------------------------------------------------*/
}
