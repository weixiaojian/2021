package io.imwj.threadlocal.entity;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现自己的ThreadLocal
 *
 * @author langao_q
 * @since 2021-03-29 16:23
 */
public class MyThreadLocal<T> {

    static AtomicInteger atomic = new AtomicInteger();

    //高德纳提出的一个值（能够让散列更加平均）
    Integer threadLocalHash = atomic.addAndGet(0x61c88647);

    //储存所有Thread线程的Map
    static HashMap<Thread, HashMap<Integer, Object>> threadLocalMap = new HashMap();

    /**
     * 获取当前Thread线程的储存map（临界区需要加synchronized）
     *
     * @return
     */
    synchronized static HashMap<Integer, Object> getMap() {
        Thread thread = Thread.currentThread();
        if (!threadLocalMap.containsKey(thread)) {
            threadLocalMap.put(thread, new HashMap<Integer, Object>());
        }
        return threadLocalMap.get(thread);
    }

    protected T initialValue() {
        return null;
    }

    public T get() {
        HashMap<Integer, Object> map = getMap();
        if (!map.containsKey(this.threadLocalHash)) {
            map.put(this.threadLocalHash, initialValue());
        }
        return (T) map.get(this.threadLocalHash);
    }

    public void set(T t) {
        HashMap<Integer, Object> map = getMap();
        map.put(this.threadLocalHash, t);
    }

}
