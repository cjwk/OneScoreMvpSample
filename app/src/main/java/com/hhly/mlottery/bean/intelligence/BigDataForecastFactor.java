package com.hhly.mlottery.bean.intelligence;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描    述：
 * 作    者：longs@13322.com
 * 时    间：2016/7/19.
 */
public class BigDataForecastFactor implements Parcelable {

    private BigDataForecastFactorItem host;
    private BigDataForecastFactorItem size;
    private BigDataForecastFactorItem asia;

    public BigDataForecastFactor() {
        host = new BigDataForecastFactorItem();
        size = new BigDataForecastFactorItem();
        asia = new BigDataForecastFactorItem();
    }

    public BigDataForecastFactorItem getHost() {
        return host;
    }

    public void setHost(BigDataForecastFactorItem host) {
        this.host = host;
    }

    public BigDataForecastFactorItem getSize() {
        return size;
    }

    public void setSize(BigDataForecastFactorItem size) {
        this.size = size;
    }

    public BigDataForecastFactorItem getAsia() {
        return asia;
    }

    public void setAsia(BigDataForecastFactorItem asia) {
        this.asia = asia;
    }

    /**
     * 计算主胜率
     *
     * @param forecast
     * @return
     */
    public double computeHostWinRate(BigDataForecast forecast) {
        return computeHostWinRate(forecast, false);
    }

    /**
     * 计算主胜率
     *
     * @param forecast
     * @param useTemp
     * @return
     */
    public double computeHostWinRate(BigDataForecast forecast, boolean useTemp) {

        Double historyHomeWinPercent = getHomeWinPercent(forecast.getBattleHistory());
        Double hostHomeWinPercent = getHomeWinPercent(forecast.getHomeRecent());
        Double guestHomeLosePercent = getHomeLosePercent(forecast.getGuestRecent());

        return historyHomeWinPercent * (useTemp ? host.getHistoryTemp() : host.getHistory())
                + hostHomeWinPercent * (useTemp ? host.getHomeTemp() : host.getHome())
                + guestHomeLosePercent * (useTemp ? host.getGuestTemp() : host.getGuest());
    }

    /**
     * 计算大球率
     *
     * @param forecast
     * @return
     */
    public double computeSizeWinRate(BigDataForecast forecast) {
        return computeSizeWinRate(forecast, false);
    }

    /**
     * 计算主胜率
     *
     * @param forecast
     * @param useTemp
     * @return
     */
    public double computeSizeWinRate(BigDataForecast forecast, boolean useTemp) {

        Double historySizeWinPercent = getSizeWinPercent(forecast.getBattleHistory());
        Double hostSizeWinPercent = getSizeWinPercent(forecast.getHomeRecent());
        Double guestSizeWinPercent = getSizeWinPercent(forecast.getGuestRecent());

        return historySizeWinPercent * (useTemp ? size.getHistoryTemp() : size.getHistory())
                + hostSizeWinPercent * (useTemp ? size.getHomeTemp() : size.getHome())
                + guestSizeWinPercent * (useTemp ? size.getGuestTemp() : size.getGuest());
    }

    /**
     * 计算主胜率
     *
     * @param forecast
     * @return
     */
    public double computeAsiaWinRate(BigDataForecast forecast) {
        return computeAsiaWinRate(forecast, false);
    }

    /**
     * 计算主胜率
     *
     * @param forecast
     * @param useTemp
     * @return
     */
    public double computeAsiaWinRate(BigDataForecast forecast, boolean useTemp) {

        BigDataForecastData battleHistory = forecast.getBattleHistory();
        BigDataForecastData homeRecent = forecast.getHomeRecent();
        BigDataForecastData guestRecent = forecast.getGuestRecent();

        Double historyAsiaWinPercent = getAsiaWinPercent(battleHistory);
        Double hostAsiaWinPercent = getAsiaWinPercent(homeRecent);
        Double guestAsiaLosePercent = getAsiaLosePercent(guestRecent);

        return historyAsiaWinPercent * (useTemp ? asia.getHistoryTemp() : asia.getHistory())
                + hostAsiaWinPercent * (useTemp ? asia.getHomeTemp() : asia.getHome())
                + guestAsiaLosePercent * (useTemp ? asia.getGuestTemp() : asia.getGuest());
    }

    /**
     * 更新缓存到本体数据
     */
    public void updateTemp() {
        host.updateTemp();
        size.updateTemp();
        asia.updateTemp();
    }

    /**
     * 刷掉缓存
     */
    public void refreshTemp() {
        host.refreshTemp();
        size.refreshTemp();
        asia.refreshTemp();
    }

    private Double checkNotNull(Double d) {
        return d == null ? 0D : d;
    }

    private Double getHomeWinPercent(BigDataForecastData data) {
        if (data == null) return 0D;
        return checkNotNull(data.getHomeWinPercent());
    }

    private Double getHomeLosePercent(BigDataForecastData data) {
        if (data == null) return 0D;
        return checkNotNull(data.getHomeLosePercent());
    }

    private Double getSizeWinPercent(BigDataForecastData data) {
        if (data == null) return 0D;
        return checkNotNull(data.getSizeWinPercent());
    }

    private Double getSizeLosePercent(BigDataForecastData data) {
        if (data == null) return 0D;
        return checkNotNull(data.getSizeLosePercent());
    }

    private Double getAsiaWinPercent(BigDataForecastData data) {
        if (data == null) return 0D;
        return checkNotNull(data.getAsiaWinPercent());
    }

    private Double getAsiaLosePercent(BigDataForecastData data) {
        if (data == null) return 0D;
        return checkNotNull(data.getAsiaLosePercent());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.host, flags);
        dest.writeParcelable(this.size, flags);
        dest.writeParcelable(this.asia, flags);
    }

    protected BigDataForecastFactor(Parcel in) {
        this.host = in.readParcelable(BigDataForecastFactorItem.class.getClassLoader());
        this.size = in.readParcelable(BigDataForecastFactorItem.class.getClassLoader());
        this.asia = in.readParcelable(BigDataForecastFactorItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<BigDataForecastFactor> CREATOR = new Parcelable.Creator<BigDataForecastFactor>() {
        @Override
        public BigDataForecastFactor createFromParcel(Parcel source) {
            return new BigDataForecastFactor(source);
        }

        @Override
        public BigDataForecastFactor[] newArray(int size) {
            return new BigDataForecastFactor[size];
        }
    };
}
