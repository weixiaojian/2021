package io.imwj.threadlocal.test;

import io.imwj.threadlocal.entity.MyThreadLocal;

/**
 * @author langao_q
 * @since 2021-03-29 16:26
 */
public class Test {

    static MyThreadLocal<Long> threadLocal = new MyThreadLocal<Long>(){
        @Override
        protected Long initialValue(){
            return Thread.currentThread().getId();
        }
    };


    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                System.out.println(threadLocal.get());
            }, String.valueOf(i)).start();
        }
    }

}
