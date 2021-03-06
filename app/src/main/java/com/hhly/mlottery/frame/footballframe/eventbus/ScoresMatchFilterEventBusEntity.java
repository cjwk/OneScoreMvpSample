package com.hhly.mlottery.frame.footballframe.eventbus;

import java.util.Map;

/**
 * @author: Wangg
 * @Name：ScoresMatchFilterEventBusEntity
 * @Description:筛选Activity返回筛选结果
 * @Created on:2016/12/1  14:52.
 */

public class ScoresMatchFilterEventBusEntity {
    private int fgIndex;
    private Map<String, Object> map;

    public ScoresMatchFilterEventBusEntity(int fgIndex, Map<String, Object> map) {
        this.fgIndex = fgIndex;
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public int getFgIndex() {
        return fgIndex;
    }

    public void setFgIndex(int fgIndex) {
        this.fgIndex = fgIndex;
    }
}
