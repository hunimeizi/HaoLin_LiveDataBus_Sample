package com.haolin.livedatabus.sample;

import android.arch.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：haoLin_Lee on 2019/04/25 22:28
 * 邮箱：Lhaolin0304@sina.com
 * class:LiveDataBus
 */
public final class LiveDataBus {

    private final Map<String, MutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus LIVE_DATA_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.LIVE_DATA_BUS;
    }
    public synchronized <T> MutableLiveData<T> with(String key,Class<T> type){
        if (!bus.containsKey(key)){
            bus.put(key,new MutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }
}
