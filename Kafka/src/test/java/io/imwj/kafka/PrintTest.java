package io.imwj.kafka;

/**
 * @author langao_q
 * @since 2021-04-06 15:05
 */
public class PrintTest {

    private static int count = 100;

    public void printTest() {
        synchronized (this) {
            this.notify();
            while (count > 0) {
                System.out.println(Thread.currentThread().getName() + "：" + count);
                count--;
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
