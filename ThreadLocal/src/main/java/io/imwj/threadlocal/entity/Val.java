package io.imwj.threadlocal.entity;

/**
 * @author langao_q
 * @since 2021-03-26 17:05
 */
public class Val<T> {

    T val;

    public void set(T _val) {
        val = _val;
    }

    public T get() {
        return val;
    }

}
