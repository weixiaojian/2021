# Java面试
> B站地址：[https://www.bilibili.com/video/BV1Tq4y1L7NY?p=1](https://www.bilibili.com/video/BV1Tq4y1L7NY?p=1)

## ArrayList和LinkedList区别
* ArrayList：底层是动态数组，连续的内存空间存储，相对查询性能高、线程不安全、轻量级、初始化10，每次扩容为x1.5
* LinkedList：底层是双向循环链表，相对增删效率高，线程不安全，轻量级;同时实现了Deque接口 可以当作双端队列使用


## HashMap的put方法(1.7/1.8)
* 流程：key通过hash计算得到数组下标，下标位置元素为空则将key/value封装成Entry放入该位置，不为空则区分1.7和1.8
* 1.7(数组 + 链表)：先判断是否需要扩容 扩容之后生成Entry对象，使用头插法放入链表中
* 1.8(数组 + 链表 + 红黑树)：先判断是红黑树还是链表，红黑树的话则封装好红黑树后添加到红黑树中，链表的话则使用尾插法插入到链表最后的位置 插入完成后会判断是否要转变为红黑树，插入完成之后再判断是否需要扩容
* 如果key已经存在则会覆盖value值；jdk8开始链表高度到8、数组长度超过64，链表转变为红黑树，元素以内部类Node节点存在；反之则会回退为链表；

## ThreadLocal
* ThreadLocal：每一个Thread对象都含有一个ThreadLocalMap类型的成员变量，它存储本线程中所有TheadLocal对象及其对应的值，Map的key为ThreadLocal对象，Map的value为需要缓存的值
* 使用场景：在对象进行跨层传递时，使用ThreadLocal可以避免多次传递；线程间数据隔离；进行事务操作 用于存储线程事务信息；数据库连接 Session会话管理
* 每次使用完ThreadLocal都调用它的remove()方法清除数据
* 将ThreadLocal变量定义成private static，这样就一直存在ThreadLocal的强引用，也就能保证任何时候都能通过ThreadLocal的弱引用访问到Entry的value值，进而清除掉

## JVM中那些是共享区，那些可以作为GC ROOT
* 共享区：方法区(类)、堆(对象)
* 线程独有：虚拟机栈、本地方法栈、程序计数器
* GC ROOT：可达性分析法中的根部，正在运行的线程、方法区中的静态变量、本地方法栈中的变量、栈中的本地变量

## JVM问题排查
* 正在运行的系统：jmap查看JVM各个区域的使用情况；jstack查看线程运行的情况；jstat查看垃圾回收情况(full gc)；
* 已经出现OOM的系统：通过jdk工具生成当时的dump文件；阿里的Arthas工具等

## 线程死锁的查看
* 可以使用jstack，阿里的Arthas工具等

## 线程间是如何通讯的
* 基于共享内存或基于网络
* 如果是共享内存的话则是wait(),notify()

## spring源码