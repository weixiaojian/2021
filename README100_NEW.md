# Java基础
## 1.面向对象和面向过程的区别
* 面向过程：注重于事件的顺序及步骤，直接、高效
* 面向对象：注重于事件有那些参与者 以及各自需要做的事情，易于复用、扩展、维护
* 面向对象还包含：封装(在于明确标识出允许外部类使用的所有成员函数和数据项目，隐藏内部细节)、继承(继承基类的方法并做出自己的扩展)、多态(基于对象所属类的不同 外部对同一个方法调用，实际的执行逻辑不同)

## 2.==和equals
* ==：比较的是栈中的值，基本数据类型比较的是变量值 引用数据类型比较的是堆中内存地址
* equals：object中默认也是采用==比较，像String会重写 也就是比较变量值

## 3.hashCode和equals
* hashCode：获取哈希码，确定该对象在hash表中的索引位置；能够根据key快速的获取到对应的hash值 hashCode效率比较equals高
* 用处：以HashSet为例 在添加对象的时候优先获取到对象的hashCode，如果hashCode不等说明对象不重复，如果hashCode相等再用equals比较对象是否相等 相等则不添加 反之则添加到集合中。
* equals相等hashCode一定相等(规范)，hashCode相等equals不一定相等，equals被覆盖时hashCode也要重写

## 4.final
* 修饰类标识类不可被继承，修饰方法表示不可重写(可重载)，修饰变量表示值不可再改变(成员变量初始化就要赋值，局部变量使用前就要赋值，修饰引用变量时不能改变指向其他对象但可以改变指向对象的值)
* 内部类中使用外部变量的时候一定要final：内部类和外部类是属于同一级别的，外部类方法结束后 变量就会被销毁，而内部类中依然使用该变量就会将该变量copy一份到内部类中 为了确保copy的值一致不会改变所以要用final

## 5.String、StringBuilder、StringBuffer
* String是final修饰 不可变，每次操作都会产生新的String对象
* StringBuilder、StringBuffer都是在原对象上进行操作 不同是StringBuffer使用了Synchronized修饰，优先是用性能更好的StringBuilder

## 6.重载和重写
* 重载：发生在同一类中，方法名相同，参数类型、参数个数、顺序不同（访问修饰符和返回值不同编译时会报错）
* 重写：发生在父子类中，方法名、参数列表必须相同，两大一小(访问修饰符大于父类 返回值、异常范围小于等于父类)

## 7.接口和抽象类的区别
* 抽象类中可以有普通方法，接口中只能有抽象方法
* 抽象类是单继承 接口是多实现
* 抽象类中的属性可以是各种类型的，接口中的属性只能是public static final
* 抽象类的目的是对公共方法以及事物本质的抽取达到代码复用(like a)，接口的目的只是对类下行为进行约束(is a)
* 当关注事物本之的时候就用抽象类(消息项目中对消息进行各种过滤)，关注操作的时候就用接口(controller调用service的方法)

## 8.ArrayList和LingkedList区别
* ArrayList：基于动态数组(初始化10  扩容x1.5)，连续的内存储存 适合下标访问(获取元素效率高)；扩容和向中间添加元素时都要创建一个新的数组 然后将旧数组的数据copy过去（因此添加和删除元素效率低）
* LinkedList：基于链表 分散存储到内中，适合数据的插入和删除(直接操作node元素)，不适合查找元素(需要通过迭代器遍历然后equals)；lingkedList只能通过迭代器遍历

## 9.hashMap和hashTable
* HashMap：方法时没有synchronized修饰的 非线程安全，hashTable是synchronized修饰 线程安全的
* 底层实现：数组 + 链表，jdk8开始 链表高度到达8数组长度超过64就会转变未红黑树；元素以内部类Node节点存在(Node包含key/value)
* 添加元素：计算key的hash值 二次hash之后对数组长度取模得到对应的数组下标；如果没有产生hash冲突直接放到该数组位置；产生hash冲突后 equals比较相同则替换 不同则判断链表高度未超出8则插入链表 超出则转为红黑树；key为null存储在数组0的位置

## 10.ConcurrentHashMap原理，JDK7和JDK8的区别
* jdk7：数据结构是ReentrantLock + Segment + HashEntry分段存储的，一个Segment包含一个HashEntry数组 每个HashEntry又是一个链表结构
* 锁：Segment分段锁 锁定操作的Segment 其他的Segment不受影响，最大并发度为Segment个数，get方法是没有加锁的 通过Volatile保证
* jdk8：数据结构式Synchronized + CAS + Node + 红黑树，Node都用volatile保证 查找、替换、赋值等操作都使用CAS
* 锁：锁链表的head节点 不影响其他元素的读写，锁粒度更细 效率更高，扩容是阻塞所有的读写操作 并发扩容

## 11.Java中的一场体系
* Throwable > Exception(RunTimeException，CheckException)、Error(程序会直接终止 如OOM)
* 常见异常：NullPointerException空指针异常、NumberFormatException数字格式化异常、ArrayIndexOutOfBoundsException数组下标越界异常、ClassNotFoundException找不到类异常、SQLExceptionsql异常

## 12.Java类加载器、双亲委派模型
* BootstrapClassLoad(java lib目录)、ExtClassLoad(java ext目录)、AppClassLoad(用户自己编写的java文件)
* 双亲委派模型：向上委派(加载器并不会自己加载 实际上就是向上查找缓存，有则返会 没有则继续向上)；向下查找(加载器会查找加载路径 有则加载返回，没有则继续向下查找)
* 优点：避免核心类被篡改，避免重复加载

## 13.GC如何判断一个对象是否可回收
* 引用计数法：每个对象有引用时 计数增加一，引用释放时计数减一，计数为0时表示可以回收；缺点是循环引用 如a引用b b又引用了a
* 可达性分析法：从GC Roots向下搜索，搜索的路径为引用链 当一个对象没有任何引用链时表示可以回收(注意对象不是立即死亡的 第一次GC Roots之后第二次执行前会判断是否覆盖finalize方法[只能触发一次] 覆盖了就会执行，若对象有引用链则复活，没有则回收)
* GC Roots：正在运行中的对象(虚拟机栈)，方法区中静态属性引用对象，方法区中常量引用对象，本地方法栈中的对象

## 14.线程状态
* 新建、就绪、运行、阻塞、死亡
* 阻塞：等待阻塞(线程运行了wait方法进入阻塞状态，再调用notify方法唤醒)；同步阻塞(运行线程去获得锁时 锁被其他线程占用，此时该线程就会阻塞)；其他阻塞(运行的调用sleep/join方法 线程就会阻塞直到sleep/join超时、终止)

## 15.sleep、wait、join、yield的区别
* 锁池：所有需要竞争同步锁的线程都会放到锁池中，当一个线程获取到锁后 其他线程就继续等待
* 等待池：当我们调用了wait方法时 线程就会进入到等待池中，等待池的线程不会去竞争同步锁 只有调用了notify之后才会进入到锁池中(notify是释放一个线程 notifyAll释放所有线程)
* sleep是Thread的静态方法，wait是object的本地方法
* sleep方法不会释放锁(会将锁带入冻结状态中 其他线程获取不到)，wait则会释放锁 并且进入等待队列中
* sleep不需要手动释放，wait则需要notify释放
* join执行后线程进入阻塞状态，例如 B线程中执行A线程的join，那么B线程会阻塞直到A线程结束或中断
* yield执行后线程直接进入就绪状态

## 16.ThreadLocal
* 底层：每一个Thread都持有一个ThreadLocalMap对象，储存这本线程中所有的ThreadLoca对象及对应的值；get/set方法时 ThreadLocal会通过当前线程对象Thread作为key 获取到threadLocalMap，再以当前ThreadLocal为key 获取到对应的value值
```
    static HashMap<Thread, HashMap<Integer, Object>> threadLocalMap = new HashMap();
```
* 使用场景：1.在进行对象跨层传递时 保存指定值，打破层次约束；2.各个线程之间的数据隔离；3.进行事务操作 用于储存线程的事务信息；4.数据库连接 session会话管理

##  17.ThreadLocal内存泄漏
* 内存泄漏：程序申请内存后 无法释放已申请的空间，最终会导致内存溢出(OOM)；
* 强引用：如new的对象 当存在引用时不会被GC回收；弱引用：每次GC都会被回收掉
* ThreadLocal内存泄漏原因：ThreadLocal的生命周期是和线程一样长的，ThreadLocalMap中的key是弱引用 value是强引用，当ThreaLocal不存在外部强引用时 key就会被回收掉 但vaule还存在强引用线程不结束的话value就无法回收
* 解决方案：1.每次使用完ThreaLoca之后调用它的remove()方法清除数据；2.将ThreadLocal定义为private static(避免多次创建线程相关的变量，并且可以随时清除掉value值)

## 18.并发三大特性
* 原子性(synchronized)、可见性(volatile)、有序性(synchronized、volatile)

## 19.线程池
* 线程池优点：降低资源消耗，提高响应速度，提高线程的可管理性
* 线程池参数：核心线程数、最大线程数、线程空闲时间、线程空闲时间单位、阻塞队列(当队列已满的时候 继续添加就会阻塞)、线程工厂、拒绝策略(队列/线程已满、线程池关闭都会触发拒绝)
* 工作流程：创建核心线程 > 阻塞队列已满 > 创建临时线程达到最大线程数 > 任务拒绝策略

## 20.线程池中的阻塞队列
* 阻塞队列：当阻塞队列是空的时候，从队列中获取元素会被阻塞；当阻塞队列是满的时候，从队列中添加元素会被阻塞
* 线程池中为什么是先添满队列再创建临时线程：1.阻塞队列还自带阻塞和唤醒，阻塞队列可以保证在队列中没有任务时阻塞获取任务的线程 使其进入wait；2.线程创建和销毁是比较耗费资源的 先进入队列可以提高效率；

## 21.线程池中线程复用
* 线程池将线程和任务进行了解耦，摆脱了之前通过Thread创建线程时的一个任务对应一个线程的限制；更重要的是线程池对Thread进行了封装 并不是每次调用Thread.start来创建线程 而是将run方法当作一个普通方法来执行了

# Spring相关
## 22.spring是什么
* 轻量级的j2ee开源框架，是一个容器框架 用来装java对象；核心概念是IOC(控制反转)，AOP(切面编程)；将各个组件和其他框架组合成复杂的应用；

## 23.IOC的理解
* 容器：spring本身就是一个容器，项目启动时spring会读取配置文件中的bean然后创建好对象放入容器中；
* 控制反转：正常情况下A中有B对象的引用 必须要A去主动创建B，引入spring之后 项目启动时所有标记对象都会启动好 并将B注入到A中
* 依赖注入：IOC容器在运行期间 动态的将依赖关系注入到对象之中

## 24.IOC大致流程
* 配置文件中配置要扫描的包路径
* 定义一些注解，标识要放入容器的bean 以及依赖注入、获取配置信息等
* 项目启动时 读取配置文件中的包路径，将该路径下的所有的class文件添加到一个Set集合中
* 遍历上面的Set集合 获取到有标识需要放入容器注解的bean，将这些bean存放到IOC容器的Map中
* 遍历整个IOC容器 获取到每一个bean的实例，然后针对有标识依赖注入的bean进行递归依赖注入

## 25.AOP的理解
* 将程序中交叉的业务逻辑(日志、事务等)，封装成一个切面 然后注入到目标对象中；
* 可以对某些对象进行增强，也可以在某些方法执行前和执行后处理额外的事情

## 26.BeanFactory和ApplicationContext区别
* ApplicationContext是BeanFactory的子接口 功能也更强大(支持国际化、加载多个配置文件等等)
* BeanFactory是延迟加载的形式加载bean(使用时才创建)，ApplicationContext在初始化的时候就加载好了所有bean 这样更有利于检查依赖属性是否注入
* ApplicationContext占的内存更高 启动更慢，但还能以声明的方式创建(使用ContextLoad )

## 27.bean的生命周期
* 

## 28.bean的几种作用域
* 单例：默认，每个容器中只有一个bean的实例，单例模式有BeanFactory自身来维护在第一次注入时被创建
* 原型：为每个bean请求都创建一个bean实例，所以每次注入的时候都是一个新对象
* request：bean被定义在每次http请求中创建，单次请求中都是同一个bean对象
* session：与request相似，确保每个session中都有一个bean  会话过期后bean就会失效
* application：bean被定义在ServletContext周期中 复用一个对象实例
* websocket：bean被定义在webscoket周期中复用一个对象实例

## 29.bean的生命周期
* 1.解析类得到BeanDefinition
* 2.确定构造方法
* 3.进行实例化 得到一个对象
* 4.对对象中添加了@Autowired注解的属性进行属性填充
* 5.回调Aware 如：BeanNameAware、BeanFactoryAware
* 6.调用BeanPostProcessor的初始化前方法
* 7.调用初始化方法
* 8.调用BeanPostProcessor的初始化后方法 这里会进行AOP
* 9.如果bean是单例 还会将bean放到单例池里面

## 30.bean是线程安全的吗
* spring中的bean默认是单例的(线程不安全的)，同时框架也没有对bean做额外的处理 如果bean是无状态的那么自然就是线程安全的，bean是有状态的话那么就是线程不安全的
* 想要做到线程安全 可以将bean的作用域指定为“原型” 这样每次获得的bean都是新的

## 31.spring中用到了那些设计模式
* 简单工厂：工厂类根据传入参数 动态的决定创建类；BeanFactory就是简单工厂的一种实现，根据传入一个唯一标识来获得bean对象
* 单例模式：保证一个类只有一个实例 并提高全局访问点；spring中的单例模式就是一种实现 并提供了全局访问点BeanFactory
* 动态代理：切面在应用运行时被织入；spring中的AOP会为目标对象动态的创建一个代理对象 并织入相应的逻辑
* 模板方法：父类定义好了骨架 某些特定方法由子类实现；spring中的JdbcTemplater等就是用到了模板方法

## 32.spring事务实现以及隔离级别
* spring默认有两种事务方式（编程式、声明式），@Transactional注解就是声明式的；
* @Transactional原理：在方法上加上该注解后 spring会为基于这个类生成一个代理对象 将这个代理对象作为bean，代理逻辑中先将事务的自动提交设置为false 然后根据代码是否抛出异常来决定事务的提交；默认情况一下会对RunTimeException和Error进行回滚
* 事务失效的情况：发生自调用 不经过spring代理对象；bean没有被spring管理；方法不是public；没有抛出异常；数据库不支持事务；
* 事务隔离级别：read uncommitted(未提交读)；read committed(提交读 不可重复读 oracle默认)；repeatable read(可重复读 mysql默认)；serializable(可串行化)；

## 33.spring事务的传播机制
* REQUIRED(默认)：当前有事务就加入这个事务，当前没有事务则新建事务
* SUPPORTS：当前存在事务就加入这个事务，当前没有事务就以非事务方式执行
* MANDATORY：当前存在事务就加入这个事务，当前没有事务则抛出异常
* REQUIRED_NEW：当前存在事务就挂起该事务，自己新建一个事务
* NOT_SUPPORTS：当前存在事务就挂起该事务，自己以非事务方式运行
* NEVER：当前存在事务就抛出异常，没有事务就不使用事务
* NESTED：当前存在事务就嵌套在里面执行，没有事务就新建事务

## 34.springmvd工作流程
* 1.前端控制器DipatcherServlet收到用户请求
* 2.前端控制器调用处理器映射器HandleMappering，根据注解、xml配置等获取到处理器和拦截器 返回给前端控制器
* 3.前端控制器再调用处理器适配器HandleAdapter，经过适配得到具体的处理器Controller
* 4.处理器执行实际的业务逻辑 返回ModelAndView给到前端控制器
* 5.前端控制器再将ModelAndView传给视图解析器ViewReslover视图解析器，视图解析器解析数据返回view给前端控制器
* 6.前端控制器根据view渲染页面 响应给用户

## 35.springmvc的九大组件
* 处理器映射器：HandleMapping
* 处理器适配器：HandleAdpter
* 视图解析器：ViewReslover

## 36.springboot自动装配
* 自动装配的实现就是为了从spring.factories文件中获取到对应的bean对象 并且由IOC容器来进行管理
* @SpringBootConfiguration：springboot自己封装的Configuration
* @ComponentScan：指定要扫描的路径
* @EnableAutoConfiguration：@Import(AutoConfigurationImportSelector.class)  重点方法selectImports
* 1.当springboot程序启动的时候 会先创建SpringApplication对象，在这个过程中会加载程序中的spring.factories文件 并将文件放到缓存中
* 2.SpringApplication对象创建完成后 开始执行run方法，其中有两个核心方法(prepareContext、refreshContext)完成了自动装配的核心功能 包含：上下文对象的创建、banner打印、异常报告期
* 3.prepareContext方法主要未上下文对象的创建，其中的load方法会将启动类做未一个BeanDefinition注册到registry；同时会解析@SpringBootApplication，@EnableAutoConfiguration等注解
* 4.refreshContext方法主要会进行整个容器的刷新过程 调用spring中的refresh方法来完成整个spring应用的启动，同时解析@ComponentScan，@Bean，@Import等注解；
* 5.在解析@Import注解时 会把所有包含@Import注解的类都解析到，然后对import类进行分类 最后deferredImportSelectorHandler.process()完成整个EnableAutoConfiguration的加载