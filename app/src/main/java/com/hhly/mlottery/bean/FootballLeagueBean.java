package com.hhly.mlottery.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ClassName: OneScoreGit
 * @author:Administrator luyao
 * @Description:
 * @data: 2016/9/9 15:01
 */
public class FootballLeagueBean implements Parcelable {

    private String kind;
    private String lgId;
    private String lgName;
    private String pic;
    public FootballLeagueBean() {

    }

    public FootballLeagueBean(String kind, String leagueId, String pic, String lgName) {
        this.kind = kind;
        this.lgId = leagueId;
        this.pic = pic;
        this.lgName = lgName;
    }

    public String getLeagueId() {
        return lgId;
    }

    public void setLeagueId(String leagueId) {
        this.lgId = leagueId;
    }

    public String getLgName() {
        return lgName;
    }

    public void setLgName(String lgName) {
        this.lgName = lgName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.kind);
        dest.writeValue(this.lgId);
        dest.writeString(this.lgName);
        dest.writeString(this.pic);
    }

    protected FootballLeagueBean(Parcel in) {
        this.kind = in.readString();
        this.lgId = in.readString();
        this.lgName = in.readString();
        this.pic = in.readString();
    }

    public static final Parcelable.Creator<FootballLeagueBean> CREATOR = new Parcelable.Creator<FootballLeagueBean>() {
        @Override
        public FootballLeagueBean createFromParcel(Parcel source) {
            return new FootballLeagueBean(source);
        }

        @Override
        public FootballLeagueBean[] newArray(int size) {
            return new FootballLeagueBean[size];
        }
    };
}
