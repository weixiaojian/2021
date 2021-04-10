package io.imwj.kafka;

/**
 * @author langao_q
 * @since 2021-04-06 15:05
 */
public class Main {

    public static void main(String[] args) {
        Integer a = 5;
        int b = 3;

        int c = a + b;
        System.out.println(c);

    }

    public void test(){
        PrintTest test = new PrintTest();
        Thread t1 = new Thread("线程一") {
            public void run() {
                test.printTest();
            }
        };
        Thread t2 = new Thread("线程二") {
            public void run() {
                test.printTest();
            }
        };
        t1.start();
        t2.start();
    }

}
