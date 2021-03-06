package com.hhly.mlottery.frame.footballframe;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hhly.mlottery.R;
import com.hhly.mlottery.bean.intelligence.BigDataForecast;
import com.hhly.mlottery.bean.intelligence.BigDataForecastData;
import com.hhly.mlottery.bean.intelligence.BigDataForecastFactor;
import com.hhly.mlottery.util.StringFormatUtils;
import com.hhly.mlottery.view.RoundProgressBar;
import com.hhly.mlottery.widget.TextWatcherAdapter;

import java.util.Locale;

/**
 * 大数据预测 DIY 算法对话框
 * <p/>
 * Created by Loshine on 2016/7/19.
 */
public class IntelligenceComputeMethodDialogFragment extends DialogFragment {

    private static final String KEY_BIG_DATA_FORECAST = "bigDataForecast";
    private static final String KEY_BIG_DATA_FACTOR = "bigDataFactor";

    RoundProgressBar mRoundProgressBar;

    TextView mMessage;

    RadioGroup mRadioGroup;

    EditText mHistoryEditText;
    EditText mHostEditText;
    EditText mGuestEditText;

    TextView mHistoryTextView;
    TextView mHostTextView;
    TextView mGuestTextView;


    private BigDataForecast mBigDataForecast;
    private BigDataForecastFactor mFactor;

    private int combatColor;
    private int hostColor;
    private int guestColor;

    private int currentPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mBigDataForecast = args.getParcelable(KEY_BIG_DATA_FORECAST);
            mFactor = args.getParcelable(KEY_BIG_DATA_FACTOR);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.dialog_intelligence_compute_method, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initViews(view);

        initRes();

        setData();

        return alertDialog;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (currentPosition != 0) {
            if (currentPosition == 1) mRadioGroup.check(R.id.center);
            if (currentPosition == 2) mRadioGroup.check(R.id.right);
        }
    }

    private void initRes() {
        combatColor = ContextCompat.getColor(getContext(), R.color.football_analyze_progress_color1);
        hostColor = ContextCompat.getColor(getContext(), R.color.football_analyze_progress_color2);
        guestColor = ContextCompat.getColor(getContext(), R.color.football_analyze_progress_color3);
    }

    /**
     * 设置数据
     */
    private void setData() {
        if (mBigDataForecast != null) {

            BigDataForecastData battleHistory = mBigDataForecast.getBattleHistory();
            BigDataForecastData homeRecent = mBigDataForecast.getHomeRecent();
            BigDataForecastData guestRecent = mBigDataForecast.getGuestRecent();

            switch (getCurrentRadioPosition()) {
                case 0:
                    mHistoryEditText.setText(StringFormatUtils.toString
                            (mFactor.getHost().getHistoryTemp()));
                    mHostEditText.setText(StringFormatUtils.toString
                            (mFactor.getHost().getHomeTemp()));
                    mGuestEditText.setText(StringFormatUtils.toString
                            (mFactor.getHost().getGuestTemp()));

                    Double historyHomeWin = BigDataForecastData.getHomeWinPercent(battleHistory);
                    Double homeRecentHomeWin = BigDataForecastData.getHomeWinPercent(homeRecent);
                    Double guestRecentHomeLose = BigDataForecastData.getHomeLosePercent(guestRecent);

                    mHistoryTextView.setText(StringFormatUtils.toString(historyHomeWin));
                    mHostTextView.setText(StringFormatUtils.toString(homeRecentHomeWin));
                    mGuestTextView.setText(StringFormatUtils.toString(guestRecentHomeLose));
                    break;
                case 1:
                    mHistoryEditText.setText(StringFormatUtils.toString
                            (mFactor.getSize().getHistoryTemp()));
                    mHostEditText.setText(StringFormatUtils.toString
                            (mFactor.getSize().getHomeTemp()));
                    mGuestEditText.setText(StringFormatUtils.toString
                            (mFactor.getSize().getGuestTemp()));

                    Double historySizeWin = BigDataForecastData.getSizeWinPercent(battleHistory);
                    Double homeRecentSizeWin = BigDataForecastData.getSizeWinPercent(homeRecent);
                    Double guestRecentSizeWin = BigDataForecastData.getSizeWinPercent(guestRecent);

                    mHistoryTextView.setText(StringFormatUtils.toString(historySizeWin));
                    mHostTextView.setText(StringFormatUtils.toString(homeRecentSizeWin));
                    mGuestTextView.setText(StringFormatUtils.toString(guestRecentSizeWin));
                    break;
                case 2:
                    mHistoryEditText.setText(StringFormatUtils.toString
                            (mFactor.getAsia().getHistoryTemp()));
                    mHostEditText.setText(StringFormatUtils.toString
                            (mFactor.getAsia().getHomeTemp()));
                    mGuestEditText.setText(StringFormatUtils.toString
                            (mFactor.getAsia().getGuestTemp()));

                    Double historyAsiaWin = BigDataForecastData.getAsiaWinPercent(battleHistory);
                    Double homeRecentAsiaWin = BigDataForecastData.getAsiaWinPercent(homeRecent);
                    Double guestRecentAsiaLose = BigDataForecastData.getAsiaLosePercent(guestRecent);

                    mHistoryTextView.setText(StringFormatUtils.toString(historyAsiaWin));
                    mHostTextView.setText(StringFormatUtils.toString(homeRecentAsiaWin));
                    mGuestTextView.setText(StringFormatUtils.toString(guestRecentAsiaLose));
                    break;
            }
        }
    }

    /**
     * 初始化 Views
     *
     * @param view view
     */
    private void initViews(View view) {
        mRoundProgressBar = (RoundProgressBar) view.findViewById(R.id.progress);

        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentPosition = getCurrentRadioPosition();
                setData();
            }
        });

        mMessage = (TextView) view.findViewById(R.id.message);

        mHistoryEditText = (EditText) view.findViewById(R.id.history_edt);
        mHistoryEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                double aDouble = StringFormatUtils.asDouble(s.toString());
                if (aDouble > 1) {
                    mHistoryEditText.setText("1");
                    mHistoryEditText.setSelection(1);
                    return;
                }
                switch (getCurrentRadioPosition()) {
                    case 0:
                        mFactor.getHost().setHistoryTemp(aDouble);
                        break;
                    case 1:
                        mFactor.getSize().setHistoryTemp(aDouble);
                        break;
                    case 2:
                        mFactor.getAsia().setHistoryTemp(aDouble);
                        break;
                }
                updateMessage();
            }
        });
        mHostEditText = (EditText) view.findViewById(R.id.host_edt);
        mHostEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                double aDouble = StringFormatUtils.asDouble(s.toString());
                if (aDouble > 1) {
                    mHostEditText.setText("1");
                    mHostEditText.setSelection(1);
                    return;
                }
                switch (getCurrentRadioPosition()) {
                    case 0:
                        mFactor.getHost().setHomeTemp(aDouble);
                        break;
                    case 1:
                        mFactor.getSize().setHomeTemp(aDouble);
                        break;
                    case 2:
                        mFactor.getAsia().setHomeTemp(aDouble);
                        break;
                }
                updateMessage();
            }
        });
        mGuestEditText = (EditText) view.findViewById(R.id.guest_edt);
        mGuestEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                double aDouble = StringFormatUtils.asDouble(s.toString());
                if (aDouble > 1) {
                    mGuestEditText.setText("1");
                    mGuestEditText.setSelection(1);
                    return;
                }
                switch (getCurrentRadioPosition()) {
                    case 0:
                        mFactor.getHost().setGuestTemp(aDouble);
                        break;
                    case 1:
                        mFactor.getSize().setGuestTemp(aDouble);
                        break;
                    case 2:
                        mFactor.getAsia().setGuestTemp(aDouble);
                        break;
                }
                updateMessage();
            }
        });

        mHistoryTextView = (TextView) view.findViewById(R.id.history_percent);
        mHostTextView = (TextView) view.findViewById(R.id.host_percent);
        mGuestTextView = (TextView) view.findViewById(R.id.guest_percent);

        view.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeInputMethod(getContext(), mGuestEditText);
                        getDialog().dismiss();
//                        mFactor.refreshTemp();
                    }
                });
/*
        view.findViewById(R.id.compute)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void notifyDataSetChanged(View v) {
                        double history = StringFormatUtils.asDouble(mHistoryEditText.getText().toString());
                        double host = StringFormatUtils.asDouble(mHostEditText.getText().toString());
                        double guest = StringFormatUtils.asDouble(mGuestEditText.getText().toString());
                        if (!checkNumbers(history, host, guest)) {
                            ToastTools.showQuick(getContext(), R.string.big_data_check_alert);
                            return;
                        }
                        closeInputMethod(getContext(), mGuestEditText);
                        getDialog().dismiss();
                        mFactor.updateTemp();
                        ((IntelligenceFragment) getParentFragment()).refreshFactorUI(true);
                    }
                });*/
    }

    /**
     * 获取当前在 RadioGroup 的哪个位置
     *
     * @return position
     */
    public int getCurrentRadioPosition() {
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.left:
                return 0;
            case R.id.center:
                return 1;
            case R.id.right:
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 更新提示信息
     */
    public void updateMessage() {
        String history = "";
        String home = "";
        String guest = "";
        String messageString = "";
        switch (getCurrentRadioPosition()) {
            case 0:
                history = StringFormatUtils.toString(mFactor.getHost().getHistoryTemp());
                home = StringFormatUtils.toString(mFactor.getHost().getHomeTemp());
                guest = StringFormatUtils.toString(mFactor.getHost().getGuestTemp());
                messageString = getString(R.string.diy_compute_method_message1);
                break;
            case 1:
                history = StringFormatUtils.toString(mFactor.getSize().getHistoryTemp());
                home = StringFormatUtils.toString(mFactor.getSize().getHomeTemp());
                guest = StringFormatUtils.toString(mFactor.getSize().getGuestTemp());
                messageString = getString(R.string.diy_compute_method_message2);
                break;
            case 2:
                history = StringFormatUtils.toString(mFactor.getAsia().getHistoryTemp());
                home = StringFormatUtils.toString(mFactor.getAsia().getHomeTemp());
                guest = StringFormatUtils.toString(mFactor.getAsia().getGuestTemp());
                messageString = getString(R.string.diy_compute_method_message3);
                break;
        }
        String unColoredMsg = String.format(Locale.getDefault(), messageString, history, home, guest);
        SpannableStringBuilder style = new SpannableStringBuilder(unColoredMsg);

//        style.setSpan(new ForegroundColorSpan(combatColor), messageString.indexOf("交"),
//                messageString.indexOf("交") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        style.setSpan(new ForegroundColorSpan(hostColor), messageString.indexOf("主"),
//                messageString.indexOf("主") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        style.setSpan(new ForegroundColorSpan(guestColor), messageString.indexOf("客"),
//                messageString.indexOf("客") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMessage.setText(unColoredMsg);
        setResult();
    }

    /**
     * 设置结果
     */
    private void setResult() {
        switch (getCurrentRadioPosition()) {
            case 0:
                mRoundProgressBar.setProgress(mFactor.computeHostWinRate(mBigDataForecast, true) * 100);
                break;
            case 1:
                mRoundProgressBar.setProgress(mFactor.computeSizeWinRate(mBigDataForecast, true) * 100);
                break;
            case 2:
                mRoundProgressBar.setProgress(mFactor.computeAsiaWinRate(mBigDataForecast, true) * 100);
                break;
        }
    }

    /**
     * 关闭输入法
     *
     * @param context  context
     * @param editText EditText
     */
    private void closeInputMethod(Context context, View editText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 检查数字合法性
     *
     * @param number 数字
     * @return 是否合法
     */
    private boolean checkNumber(double number) {
        return number >= 0 && number <= 1;
    }

    /**
     * 检查所有数字合法性
     *
     * @param numbers 数字
     * @return 是否合法
     */
    private boolean checkNumbers(double... numbers) {
        boolean isOk = true;
        for (double d : numbers) {
            isOk &= checkNumber(d);
        }
        return isOk;
    }

    public static IntelligenceComputeMethodDialogFragment newInstance(BigDataForecast bigDataForecast,
                                                                      BigDataForecastFactor factor) {

        Bundle args = new Bundle();
        args.putParcelable(KEY_BIG_DATA_FORECAST, bigDataForecast);
        args.putParcelable(KEY_BIG_DATA_FACTOR, factor);
        IntelligenceComputeMethodDialogFragment fragment = new IntelligenceComputeMethodDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
