package io.imwj.threadlocal.controller;

import io.imwj.threadlocal.entity.Val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

/**
 * 很难避免完全同步
 * 只能通过缩小需要同步的范围 + ThreadLocal解决问题（空间换时间）
 *
 * @author langao_q
 * @since 2021-03-26 16:17
 */
@RestController
public class StatController {

    static HashSet<Val<Integer>> set = new HashSet<>();

    private static synchronized void addSet(Val<Integer> v) {
        set.add(v);
    }

    static ThreadLocal<Val<Integer>> c = new ThreadLocal<Val<Integer>>() {
        @Override
        public Val<Integer> initialValue() {
            Val<Integer> v = new Val<>();
            v.set(0);
            //set.add(v); //线程不安全
            addSet(v); //线程安全
            return v;
        }
    };

    @RequestMapping("stat")
    public Integer stat() {
        return set.stream().map(x -> x.get()).reduce((a, x) -> a + x).get();
    }

    @RequestMapping("add")
    public Integer add() throws InterruptedException {
        Thread.sleep(100);
        Val<Integer> v = c.get();
        v.set(v.get() + 1);
        return 1;
    }

    @GetMapping("clear")
    public Integer clear() {
        set.stream().forEach(d -> d.set(0));
        return 1;
    }
}
