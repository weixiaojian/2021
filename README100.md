# Java面试
> B站地址：[https://www.bilibili.com/video/BV1Eb4y1R7zd?spm_id_from=333.999.0.0](https://www.bilibili.com/video/BV1Eb4y1R7zd?spm_id_from=333.999.0.0)

## 1.面向对象
* 面向对象注重于事情有那些参与者（易复用、扩展和维护）、面向过程更注重每个步骤及顺序（直接而高效）
* 封装：封装的意义，在于明确标识出允许外部使用的所有成员函数和数据项（1.Java属性私有，2.orm操作数据库）
* 继承：继承基类的方法，做出自己的改变和扩展
* 多态：基于对象所属类的不同，外部对同一个方法的调用，实际执行逻辑不同（更易于程序做扩展）

## JDK、JRE、JVM
* JDK：Java的开发工具
* JRE：Java运行时的环境
* JVM：Java的虚拟机

## ==和equals的区别
* ==：基本类型对比的是变量值，引入类型对比的是堆中的内存地址
* equals：Object的方法中默认还是使用==，String会重写该方法比对变量值

## final
* final作用：修饰类(不可继承)、修饰方法(不能重写 可以重载)、修饰变量(值不可更改)
* final在成员变量时一定要在声明时或者初始化代码块赋值，局部变量不一定要在声明时赋值 在使用前一定要赋值
* 为什么局部类和内部类只能访问final变量：因为编译之后会产生多个class文件，当外部类方法结束时局部变量就被销毁了，但内部类可能还存在(内部类需要访问局部变量)实际访问的是局部变量的copy，复制就必须要保证两个变量是一致的

## String、StringBuffer、StringBuilder
* String是final修饰不可变的，每次都会产生新对象
* StringBuffer：会在原有的字符串上进行操作，线程安全（每个方法都是synchronized修饰）
* StringBuilder：线程不安全（性能最好 优先使用）

## 重载和重写
* 重载：同一类中，方法名必须相同、参数类型不同、个数不同、顺序不同，返回值和修饰符可以不同（与返回值、修饰符无关 且会报错）
* 重写：父类和子类中，方法名、参数列表必须相同，返回值范围小于等于父类，访问修饰符大于等于父类（private方法不能写）

## 接口和抽象类
* 抽象类是单继承，接口是多实现
* 抽象类可以存在普通成员函数 可以有实现方法，接口中只能存在public abstract方法jdk1.8之后也可以
* 抽象类中的成员变量可以是各种类型的，而接口中只能是public static final
* 抽象类设计目的：代码复用is a；接口的设计目的：针对类的行为进行约束like a

## list和set
* list：有序可重复，按对象进入的顺序保存，允许多个null，可以通过下标访问
* set：无序不可重复，最多允许一个null，只能通过Iterator接口获取所有元素逐一遍历

## hashCode与equals
* hashCode：获取哈希码，实际上返回的一个int整数，是用来确定对象在哈希表中的索引位置
* 以HashSet为例：存入对象的时候会计算hashCode来确定该位置是否有值，如果有值就调用equals比对两个对象是否相同（相同就不让加入，不同就重新散列到其他位置），减少了equals的次数从而提高性能
* equals相同，但是hashCode可能不同的

## ArrayList和LinkedList区别
* ArrayList：底层是动态数组，连续的内存空间存储，相对查询性能高、线程不安全、轻量级、初始化10，每次扩容为x1.5
* LinkedList：底层是双向循环链表，相对增删效率高，线程不安全，轻量级;同时实现了Deque接口 可以当作双端队列使用

## HashMap和HashTable有什么区别？其底层实现是什么？
* HashMap方法没有synchronized修饰，线程非安全，HashTable线程安全
* HashMap允许key和value为null，而HashTable不允许
* 数组+链表实现，jdk8开始链表高度到8、数组长度超过64，链表转变为红黑树，元素以内部类Node节点存在；
```
1.计算key的hash值，二次hash然后对数组长度取模，对应到数组下标，
2.如果没有产生hash冲突(下标位置没有元素)，则直接创建Node存入数组，
3.如果产生hash冲突，先进行equal比较，相同则取代该元素，不同，则判断链表高度插入链表，链表高度达到8，并且数组长度到64则转变为红黑树，长度低于6则将红黑树转回链表
4.key为null，存在下标0的位置
```

## ConcurrentHashMap原理，jdk7和jdk8版本的区别
* jdk7：基于ReentrantLock+Segment+HashEntry，一个Segment中包含一个HashEntry数组，每个HashEntry又是一个链表结构
* jdk8：基于synchronized+CAS+Node+红黑树，Node的val和next都用volatile修饰，保证可见性查找，替换，赋值操作都使用CAS

## 什么是字节码？采用字节码的好处是什么
* java编译后产生的class文件，它不面向任何特定的处理器，只面向虚拟机。
* 在一定程度上解决了传统解释型语言执行效率低的问题、保留了解释型语言可移植的特点、运行时比较高效、无须重新编译便可在多种不同的计算机上运行。

## Java类加载器
* JDK自带有三个类加载器：bootstrap ClassLoader、ExtClassLoader、AppClassLoader。
* bootstrap ClassLoader加载jdk自带的jar包和class文件，ExtClassLoader加载jdk下ext目录中的扩展jar包和class文件，AppClassLoader加载开发中的classpath目录下的类文件
* 自定义类加载器：继承ClassLoader实现自定义类加载器

## 双亲委派模型
* 向上委派（上级加载器排查是否有加载过 有就直接返回）、向下查找（根据路径排查 有就直接返回）
* 主要是为了安全性，避免用户自己编写的类动态替换 Java的一些核心类，比如 String。
* 同时也避免了类的重复加载，因为 JVM中区分不同类，不仅仅是根据类名，相同的 class文件被不同的 ClassLoader加载就是不同的两个类

## Java中的异常体系
* Java中的所有异常都来自顶级父类Throwable，Throwable下有两个子类Exception和Error；
* Error是程序无法处理的错误，一旦出现这个错误，则程序将被迫停止运行（OOM）
* Exception不会导致程序停止，又分为两个部分RunTimeException运行时异常和CheckedException检查异常（NullpointerException、IndexOutOfBoundsException、ClassCastException）

## GC如何判断对象可以被回收
* 引用计数法：每个对象有一个引用计数属性，新增一个引用时计数加1，引用释放时计数减1，计数为0时可以回收（会有循环引用的问题）
* 可达性分析法：从 GC Roots 开始向下搜索，搜索所走过的路径称为引用链。当一个对象到 GC Roots 没有任何引用链相连时，则证明此对象是不可用的，那么虚拟机就判断是可回收对象。
* GC Roots：虚拟机栈中引用的对象(正在运行的)、方法区中类静态属性引用的对象、方法区中常量引用的对象、本地方法栈中JNI(即一般说的Native方法)引用的对象

## 线程的生命周期
* 创建，就绪，运行、阻塞和死亡状态。
* 阻塞：等待阻塞(执行wait方法)、同步阻塞()、其他阻塞(执行sleep或join方法)

## sleep()、wait()、join()、yield()的区别
* 锁池：所有需要竞争同步锁的线程都会放到锁池当中 然后去竞争获取同步锁
* 等待池：当我们调用wait()方法时 线程就会进入到等待池中，等待池中的线程是不会去竞争同步锁，只有在调用了notify()/notifyAll()之后才会去竞争同步锁，notify()是随机唤醒 notifyAll是唤醒等待池中的所有线程
```
1.sleep是Thread类的静态方法，wait是Object的方法
2.sleep不会释放锁，但wait会释放 而且会进入到等待队列中
3.sleep不依赖同步器synchronized，wait依赖synchronized
4.sleep休眠结束之后会自动推出阻塞，wait则需要被唤醒
5.sleep一般用于当前线程休眠或者轮询暂停操作，wait则用于多线程之间的通信
6.sleep会让出CPU执行时间且强制上下文切换，而wait则不一定 wait之后有可能重新竞争到锁继续执行
```
* yield（）执行后线程直接进入就绪状态，马上释放了cpu的执行权，但是依然保留了cpu的执行资格，所以有可能cpu下次进行线程调度还会让这个线程获取到执行权继续执行
* join（）执行后线程进入阻塞状态，例如在线程B中调用线程A的join（），那线程B会进入到阻塞队列，直到线程A结束或中断线程

## 对线程安全的理解
* 当多个线程访问一个对象时，如果不需要进行额外的同步控制 调用这个对象都可以获得正确的结果，我们就任务这个对象是线程安全的

## Thread、Runable的区别
* Thread和Runnable的实质是继承关系，没有可比性
* 如果有复杂的线程操作需求，那就选择继承Thread，如果只是简单的执行一个任务，那就实现runnable。

## ThreadLocal的原理和使用场景
* ThreadLocal：每一个Thread对象都含有一个ThreadLocalMap类型的成员变量，它存储本线程中所有TheadLocal对象及其对应的值
```
    static HashMap<Thread, HashMap<Integer, Object>> threadLocalMap = new HashMap();
```
* 使用场景：在对象进行跨层传递时，使用ThreadLocal可以避免多次传递；线程间数据隔离；进行事务操作 用于存储线程事务信息；数据库连接 Session会话管理

## ThreadLocal内存泄露原因，如何避免
* 强引用：如平常的new一个对象，一个对象具有强引用的话 就不会被垃圾回收器回收（除非将这个对象指向null）
* 弱引用：用java.lang.ref.WeakReference类来表示，在jvm进行垃圾回收的时候 弱引用就会被回收掉
* ThreadLocal内存溢出的根本原因是：由于ThreadLocalMap的生命周期和Thread一样长，如果没有手动删除对应的key那么就会导致内存泄漏
```
* 每次使用完ThreadLocal都调用它的remove()方法清除数据
* 将ThreadLocal变量定义成private static，这样就一直存在ThreadLocal的强引用，也就能保证任何时候都能通过ThreadLocal的弱引用访问到Entry的value值，进而清除掉 。
```

## 并发的三大特性
* 原子性：原子性是指在一个操作中cpu不可以在中途暂停然后再调度，即不被中断操作，要不全部执行完成，要不都不执行
* 可见性：当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值
* 有序性：虚拟机在进行代码编译时，对于那些改变顺序之后不会对最终结果造成影响的代码，虚拟机不一定会按照我们写的代码的顺序来执行，有可能将他们重排序

## 为什么用线程池？解释下线程池参数？
* 降低资源消耗；提高线程利用率，降低创建和销毁线程的消耗。
* 提高响应速度；任务来了，直接有线程可用可执行，而不是先创建线程，再执行。
* 提高线程的可管理性；线程是稀缺资源，使用线程池可以统一分配调优监控
* corePoolSize：核心线程数，线程池钟常驻核心线程数
* maxinumPoolSize：最大线程数，线程池能够容纳同时执行的最大线程数（注意：只有在阻塞队列满了之后才会开始创建多余线程）
* keepAliveTime：超出核心线程数之外的线程的空闲存活时间
* unit：时间单位
* workQueue：用来存放待执行的任务
* ThreadFactory：线程工厂，用来生产线程执行任务
* Handler：任务拒绝策略，当线程队列满了且工作线程大于maximumPoolSize时如何拒绝

## 线程池工作流程
* 1.在创建线程池后，等待提交过来的任务请求
* 2.当调用execute()方法添加一个任务请求时，线程池会马上创建线程运行这个任务
* 2.1如果正在运行的线程数量小于corePoolSize，那么马上创建线程运行这个任务
* 2.2如果正在运行的线程数量大于或等于corePoolSize，那么将这个任务放入队列
* 2.3如果队列满了且正在运行的线程数量小于maximumPoolSize，那么会创建非核心线程运行这个任务
* 2.4如果队列满了且正在运行的线程数量大于或等于maximumPoolSize，那么线程池会启动饱和和拒绝策略
* 3.当一个线程完成任务时，它会从队列中取下一个任务来执行
* 4.当一个线程闲置超过keepAliveTime的时间，线程池会判断
* 4.1如果当前线程数大于corePoolSize，那么这个线程会被停掉
* 4.2线程池的所有任务完成后它最终会收缩到corePoolSize的大小

## 线程池中阻塞队列的作用？为什么是先添加列队而不是先创建最大线程？
* 一般的队列只能保证作为一个有限长度的缓冲区，如果超出了缓冲长度，就无法保留当前的任务了，阻塞队列通过阻塞可以保留住当前想要继续入队的任务
* 在创建新线程的时候，是要获取全局锁的，这个时候其它的就得阻塞，影响了整体效率（宁愿阻塞任务 也不去创建多余线程，除非阻塞队列都满了）

## 线程池中线程复用原理
* 在线程池中，同一个线程可以从阻塞队列中不断获取新任务来执行，其核心原理在于线程池对Thread 进行了封装，并不是每次执行任务都会调用 Thread.start() 来创建新线程，而是让每个线程去执行一个“循环任务”，
* 在这个“循环任务”中不停检查是否有任务需要被执行，如果有则直接执行，也就调用任务中的 run 方法，将 run 方法当成一个普通的方法执行，通过这种方式只使用固定的线程就将所有任务的 run 方法串联起来

## 什么是spring
* Spring是一个轻量级的控制反转（IoC)和面向切面（AOP）的容器框架，通过控制反转(IoC)的技术达到松耦合的目的 提供了面向切面编程的丰富支持
* 包含并管理应用对象(Bean)的配置和生命周期，这个意义上是一个容器
* 将简单的组件配置、组合成为复杂的应用，这个意义上是一个框架。

## 如何实现一个IOC容器
* 配置文件指定需要扫描的包路径
* 定义一些注解，分别表示访问控制层、业务服务层、数据持久层、依赖注入注解
* 从配置文件中获取需要扫描的包路径，获取到当前路径下的文件信息及文件夹信息，我们将当前路径下所有以.class结尾的文件添加到一个Set集合中进行存储
* 遍历这个set集合，获取在类上有指定注解的类，并将其交给IOC容器，定义一个安全的Map用来存储这些对象
* 遍历这个IOC容器，获取到每一个类的实例，判断里面是有有依赖其他的类的实例，然后进行递归注入

## 对AOP的理解
* 将程序中的交叉业务逻辑（比如安全，日志，事务等），封装成一个切面，然后注入到目标对象（具体业务逻辑）中去。
* AOP可以对某个对象或某些对象的功能进行增强，比如对象中的方法进行增强，可以在执行某个方法之前额外的做一些事情，在某个方法执行之后额外的做一些事情

## 对IOC的理解
* ioc容器：实际上就是个map（key，value），里面存的是各种对象，在项目启动的时候会读取配置文件里面的bean节点，根据全限定类名使用反射创建对象放到map里、扫描到打上上述注解的类还是通过反射创建对象放到map里。
* 控制反转：未引入IOC容器前 需要在对象中主动的去new一个对象（控制权是在自己），引入IOC容器后 对象与对象之间就失去了联系 对象都放到了IOC容器中，当A需要B对象时 IOC就会主动创建一个对象注入到A中（控制权都在IOC）
* DI：依赖注入 就是由IOC容器在运行期间，动态地将某种依赖关系注入到对象之中。

## BeanFactory和ApplicationContext有什么区别
* ApplicationContext是BeanFactory的子接口，前者提供了更完整的功能
    1. 统一的资源文件访问方式。
    2. 继承MessageSource，因此支持国际化
    3. 提供在监听器中注册bean的事件。
    4. 同时加载多个配置文件
* BeanFactroy采用的是延迟加载形式来注入Bean的 只有在使用时才对该Bean进行加载实例化；ApplicationContext，它是在容器启动时，一次性创建了所有的Bean
* ApplicationContext唯一的不足是占用内存空间。当应用程序配置Bean较多时，程序启动较慢。
* BeanFactory通常以编程的方式被创建，ApplicationContext还能以声明的方式创建，如使用ContextLoader。
* BeanFactory和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，但两者之间的区别是：BeanFactory需要手动注册，而ApplicationContext则是自动注册。

## SpringBean的生命周期
* 解析类得到BeanDefinition
* 如果有多个构造方法，则要推断构造方法
* 确定好构造方法之后，进行实例化得到一个对象
* 对对象中加了@Autowired注解的属性进行属性填充
* 回调Aware方法，比如BeanNameAware、BeanFactoryAware
* 调用BeanPostProcessor的初始化前的方法
* 调用初始化方法
* 调用BeanPostProcessor的初始化后的方法，在这里回进行AOP
* 如果当前创建的bean是单例的话则会把bean放到单例池中
* 使用bean
* Spring容器关闭时调用DisposableBean中的destory()方法

## SpringBean的几种作用域
* sinleton：默认，每个容器中只有一个bean的实例，单例的模式由BeanFactory自身来维护 生命周期和IOC容器的生命周期是一致的（但只有在第一次注入时才会创建）
* prototype：为每一个bean请求都会提供一个实例，每次注入都会创建一个新的对象
* request：bean被定义为在每个HTTP请求中创建一个单例对象，也就是说单个请求都会复用这个对象
* session：与request类似，确保每个session中有一个单例对象  session过期后bean就会失效
* application：bean被定义在ServletContext的生命周期中复用一个单例对象
* websocket：bean被定义在websocket的生命周期中复用一个单例对象

## Spring中的单例Bean是线程安全的吗
* 是不安全的，Spring没有对bean进行多线程的封装处理
* 作用域是原型的话每次创建一个新对象 线程之间不存在bean共享，也就不存在线程安全的问题
* 作用域是单例的话 所有线程共享一个实例就会存在资源竞争， 对此可以使用ThreadLocal来解决线程安全的问题（ThreadLocal为每个线程保存线程私有数据）

## Spring中使用了那些涉及模式
* 简单工厂：由一个工厂类根据传入参数，动态去决定创建哪一个产品类； Spring中的BeanFactory就是简单工厂模式的实现 根据传入的一个唯一标识来获得Bean对象
* 工厂模式：实现了BeanFactory接口的bean是一类叫做factory的bean，特点是spring会在使用getBean()获取该bean时 会自动调用这个bean的getObject()方法（实际返回的时getObject()方法的返回值）
* 单例模式：保证一个类只有一个实例 并提供一个访问它的全局访问点；spring中就是提供了全局访问点BeanFactory
* 适配器模式：Spring定义了一个适配接口，使每一种controller有一种对应的适配器实现类 让适配器代替controller执行相应的方法
* 装饰器模式：动态的给一个对象添加一些额外的职责；所有类名中含有Wrapper和Decorator的都是用了装饰器模式
* 动态代理：切面在应用运行时动态的织入；spring中的AOP就是会为目标对象动态的创建一个代理对象然后进行织入
* 观察者模式：spring事件驱动模型使用的就是观察者模式；如spring中的listener的实现
* 策略模式：spring的资源访问Resource接口，spring本身就大量的使用了Resource接口来访问底层资源

## Spring事务实现方式、原理、隔离级别
* 编程式（begin end）、申明式@Transactional注解
* 原理(AOP)：在一个方法上加了@Transactional注解后，spring会基于这个类生成一个代理对象 会将这个代理对象作为bean；代理逻辑会将事务自动提交关闭 然后再执行原本的业务逻辑方法，如果业务方法没有出现异常再将事务提交
* read uncommitted（未提交读）、read committed（提交读、不可重复读）、repeatable read（可重复读）、serializable（可串行化）
* 如果spring和数据库的事务级别不一致 则以spring为准，当spring的事务级别数据库不支持是 以数据库的为准

## 事务的传播机制
* REQUIRED(spring默认)：如果当前存在事务 则加入该事务；如果当前没有事务，则自己新建一个事务
* SUPPORTS：当前存在事务 则加入当前事务；如果当前没有事务 则以非事务方法执行
* MANDATORY：当前存在事务 则加入当前事务；当前不存在事务 则抛出异常
* REQUIRED_NEW：如果存在当前事务 则挂起该事务，然后创建一个新的事务
* NOT_SUPPORTS： 如果存在当前事务 则挂起该事务，然后自己是非事务方法运行
* NEVER：如果存在当前事务 则抛出异常，然后自己不使用事务
* NESTED：如果当前事务存在 则在嵌套事务中执行，如果当前没有事务，则自己新建一个事务；、嵌套：父子事务，父事务异常子事务一定会回滚 子事务异常父事务不一定回滚(取决子事务是否抛出异常)

## spring事务什么时候会失效
* spring事务的原理就是AOP进行了切面增强，AOP不起作用时就会失效 如下：
* 发生自调用：类中使用this调用本来方法
* 方法不是public（@Transactional只支持public）,如果非要非public的话可以开启Aspectj代理模式
* 数据库不支持事务
* 类没有交给spring管理
* 异常没有抛出

## bean的自动装配，以及装配方式
* 开启自动装配，只需要在xml配置文件中定义“autowire”属性`<bean id="cutomer" class="com.xxx.xxx.Customer" autowire="" />`
* no(默认)：手动装配 需要通过ref属性手动设定
* byName：根据bean的属性名称进行自动装配
* byType：根据bean的类型进行自动装配(如果找到多个bean会报错)
* constructor：类似byType 不过是应用月构造器参数
* autodetecy：如果有默认的构造器则通过构造器 没有构造器则通过byType的方式装配

## spring boot、spring mvc、spring区别
* spring boot是spring提供的一个快速开发工具包，简化了配置让程序开发更方便(约定了默认配置)，整合了一系列的解决方案(starter机制)
* spring mvc是spring对web框架的一个解决方法，提供了一个总的前端控制器Servlet 用来接收请求 然后定义了一套路由策略及适配器执行handle
* spring是一个IOC容器 用来管理bean，提供了AOP机制、自动注入给方法执行等等

## spring mvc工作流程
* 用户发送请求到前端控制器DispatcherServlet
* DispatcherServlet收到请求后调用HandleMappering处理器映射器
* 处理器映射器找到具体的处理器，生成处理器及拦截器 然后一并返回给前端控制器DispatcherServlet
* DispatcherServlet调用HandlerAdapter处理器适配器
* HandlerAdapter经过适配器调用具体的处理器(通过反射调用controller)
* Controller执行完成后返回ModelAndView
* HandlerAdapter将controller执行结果返回给DisptchServlet
* DisptchServlet将ModelAndView传给ViewResolver视图解析器
* ViewResolver解析后返回具体的view
* DispatcherServlet根据view进行渲染视图
* DispatcherServlet响应给用户

## spring mvc九大组件

## spring boot自动装配

## Spring Boot 中的 Starter
* starter就是定义一个starter的jar包，写一个@Configuration配置类、将这些bean定义在里面，然后在starter包的META-INF/spring.factories中写入该配置类，springboot会按照约定来加载该配置类开发人员只需要将相应的starter包依赖进应用，进行相应的属性配置（使用默认配置时，不需要配置），就可以直接进行代码开发，使用对应的功能了
* starter的@Configuration配置类里面会写好所有要加载bean   然后springboot会约定好加载spring.factories文件下的全路径类，这样starter的所有bean就加入到了spring了的IOC中了

## 嵌入式服务器
* springboot已经内置了tomcat.jar，运行main方法时会去启动tomcat，并利用tomcat的spi机制加载springmvc
* 节省了下载安装tomcat，只需要一个安装了 Java 的虚拟机，就可以直接在上面部署应用程序了

## mybatis的优缺点
* 基于 SQL 语句编程，相当灵活，不会对应用程序或者数据库的现有设计造成任何影响，SQL 写在XML 里，解除 sql 与程序代码的耦合，便于统一管理；提供 XML 标签， 支持编写动态 SQL 语句， 并可重用。
* 与 JDBC 相比，减少了 50%以上的代码量，消除了 JDBC 大量冗余的代码，不需要手动开关连接；
* 很好的与各种数据库兼容
* 能够与 Spring 很好的集成
* 提供映射标签， 支持对象与数据库的 ORM 字段关系映射； 提供对象关系映射标签， 支持对象关系组件维护。
* SQL 语句的编写工作量较大， 尤其当字段多、关联表多时， 对开发人员编写SQL 语句的功底有一定要求
* SQL 语句依赖于数据库， 导致数据库移植性差， 不能随意更换数据库。

## MyBatis与Jpa
* 本质上是SQL 和 ORM 的争论
* 

## #{}和${}的区别是
* 前者#{}是预编译处理、是占位符， ${}是字符串替换、是拼接符
* Mybatis 在处理#{}时，会将 sql 中的#{}替换为?号，调用 PreparedStatement 来赋值；在处理${}时， 就是把${}替换成变量的值，调用 Statement 来赋值；
* 使用#{}可以有效的防止 SQL 注入， 提高系统安全性。

## Mybatis插件运行原理（分页）
* Mybatis只支持针对ParameterHandler、ResultSetHandler、StatementHandler、Executor这四种接口的插件
* 内部使用的是JDK的动态代理，为需要拦截的接口生成代理对象以实现接口方法拦截的功能，每当执行这四种接口对象的方法时 就会进入到拦截方法
```
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
            @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })})
public class MybatisSqlOutInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue = null
        //拦截方法前
        returnValue = invocation.proceed();//具体的业务执行逻辑
        //拦截方法后
        return returnValue;
    }

}
```

## 索引的基本原理
* 将无序的数据变成有序的查询，用来快速的查找具有特定值的记录(如：字典的目录)，如果没有索引那么遍历的就是整个表
  1. 把创建了索引的列的内容进行排序
  2. 对排序结果生成倒排表
  3. 在倒排表内容上拼接数据地址链
  4. 在查询的时候先拿到倒排表的内容，然后再取出数据地址链 从而拿到数据

## mysql聚簇索引和非聚簇索引区别
> 都是B+树的数据结构
* 聚簇索引：将数据储存与索引放到一块、并且都是是按照一定的顺序组织的、数据的存放顺序和索引的存放顺序也是一致的，找到了索引就找到了数据（适合范围查询）
* 非聚簇索引：叶子节点不存储数据、存储的是数据行地址，也就是说根据索引查找到数据行地址后再去磁盘查询数据
* 区别：
  1. 聚簇索引可以直接获取到数据(效率高)，非聚簇索引需要二次查询
  2. 聚簇索引范围查询效率高
  3. 聚簇索引适合排序的场合(其数据是按照大小排列)
* InnoDB中一定有主键，主键一定是聚簇索引 不手动设置则会使用unique索引，如果没有unique索引的话则使用数据库内部的一个行的隐藏id来当作主键
* MyISM使用给到是非聚簇索引，没有聚簇索引，非聚簇索引的两颗B+树看上去没有什么不同，节点的结构完全一致只是存储的内容不同而已
* 如果涉及到大数据量的排序、全表扫描、count之类的操作的话MyISM更快，因为索引占的空间小 这些操作都是在内存中完成的

## mysql索引的数据结构及区别
* mysql常见的索引有Hash索引、B+索引等，InnoDB存储引擎的默认索引实现为B+树
* B+树：是一颗平衡的多叉树，从根节点到每个叶子节点的高度差值不超过1，而且同级的节点间有指针相互连接
* Hash索引：采用一定的哈希算法，把键值换算成新的哈希值，检索时不需要类似B+树那样从根节点到叶子节点逐级查询 只需要一次哈希算法就可以确定数据行地址

## 索引设计原则
* 适合索引的列是出现在where子句中的列 或者子连接子句中的列
* 基数较小的表，索引效果会较差 也就没必要建立索引
* 使用短索引，如果对长字符串列进行索引 应该要指定一个前缀长度以节省索引空间
* 不要过度索引，索引需要额外的磁盘空间 且会降低写操作的性能
* 定义有外键的列则一定要建立索引
* 更新频繁的字段不适合建索引
* 不能有效区分数据的列不适合建立索引（如：性别）
* 尽量的扩展索引 而不是新建索引（联合索引）
* 对于类型为text、image、bit数据列没必要建立索引

## mysql锁的类型有那些
* 基于锁属性：共享锁(读锁)、排他锁(写锁)
* 基于锁的粒度分类：行级锁(INNODB)、表级锁(INNODB、MYISAM)、页级锁(BDB引擎 )、记录锁、间隙锁、临键锁。
* 基于锁的状态分类：意向共享锁、意向排它锁。

## mysql的执行计划
```
EXPLAIN SELECT * from A where X=? and Y=?
```
* type：判断sql性能和优化程度重要指标，执行效率ALL < index < range< ref < eq_ref < const < system
  1. const：通过索引一次命中，匹配一行数据
  2. system: 表中只有一行记录，相当于系统表
  3. eq_ref：唯一性索引扫描，对于每个索引键，表中只有一条记录与之匹配
  4. ref: 非唯一性索引扫描,返回匹配某个值的所有
  5. range: 只检索给定范围的行，使用一个索引来选择行，一般用于between、<、>；
  6. index: 只遍历索引树；
  7. ALL: 表示全表扫描
* possible_keys：它表示Mysql在执行该sql语句的时候，可能用到的索引信息，仅仅是可能，实际不一定会用到
* key：此字段是 mysql 在当前查询时所真正使用到的索引
* key_len：表示查询优化器使用了索引的字节数，这个字段可以评估组合索引是否完全被使用，
* Rows MySQL估计的需要扫描的行数，只是估计，越多表示查询的行数越大，自然越慢
* extra
  1. using filesort：表示 mysql 对结果集进行外部排序，不能通过索引顺序达到排序效果
  2. using index：覆盖索引扫描，表示查询在索引树中就可查找所需数据，不用扫描表数据文件
  3. using temporary：查询有使用临时表, 一般出现于排序， 分组和多表 join 的情况， 查询效率不高
  4. using where：sql使用了where过滤,效率较高
  
## 事务的基本特性和隔离级别
* 事务的基本特性是ACID：原子性、一致性、隔离性、持久性
* 原子性：一个事务中的操作要么全部成功，要么全部失败。
* 一致性：数据库总是从一个一致性的状态转换到另外一个一致性的状态
* 隔离性：是一个事务的修改在最终提交前，对其他事务是不可见的。
* 持久性：一旦事务提交，所做的修改就会永久保存到数据库中，无论什么情况 事务的数据一定会回写到数据库

## ACID是如何保证的
* 原子性是由undo log日志保证，记录需要回滚的日志信息（与我们执行sql相反的sql）
* 一致性由其他三大特性保证、程序代码要保证业务上的唯一性
* 隔离性由MVCC来保证
* 持久性由内存 + redo log来保证，mysql修改数据同时在内存和redo log记录这次操作 宕机时可以从redi log

## 慢查询处理
* 分析语句 看是否加载了额外的的数据，例如查询了多余的数据列
* 分析语句执行计划，然后获得索引的使用情况 使语句尽可能的使用高效的索引
* 如果语句已经无法优化 则排查以下是否是表中的数据量太大，就要考虑分库分表了

## MVCC多版本并发控制
* 读取数据时通过一种类似快照的方式将苏剧保存下来，这样读锁和写锁不冲突了 不同的事务seesion会看到自己特定版本的数据，版本链

## mysql主从同步
* Mysql的主从复制中主要有三个线程： master（binlog dump thread）、slave（I/O thread 、SQL thread） ，Master一条线程和Slave中的两条线程。
* 1.主节点binlog，主从复制的基础是主库记录数据库的所有变更都记录到binlog；
* 2.主节点 log dump 线程，当 binlog 有变动时，log dump 线程读取其内容并发送给从节点。
* 3.从节点 I/O线程接收 binlog 内容，并将其写入到 relay log 文件中。
* 4.从节点的SQL 线程读取 relay log 文件内容对数据更新进行重放，最终保证主从数据库的一致性。
* 注：主从节点使用 binglog 文件 + position 偏移量来定位主从同步的位置，从节点会保存其已接收到的偏移量，如果从节点发生宕机重启，则会自动从 position 的位置发起同步。
* 全同步复制：主库写入binlog后强制同步日志到从库 所有的从库执行完成后才返回给客户端
* 半同步复制：从库写入日志成功后返回ACK确认给主库，主库收到至少一个从库的确认就认为写操作完成

## InnoDB和MyISAM的区别
* InnoDB适合做写操作 MyISAM适合做查询，读写分离的时候 写库一般用InnoDB 读库用MyISAM
* InnoDB：支持ACID事务；支持行级锁及外键约束(支持写并发)；一个InnoDB引擎存储在一个文件空间；主键索引采用聚簇索引(主键是和数据在一起的)；一般建议使用自增主键更好
* MyISAM：不支持事务 每次查询都是原子性的；支持表级锁(每次操作都是对整个表加锁)；一个MyISAM有三个文件(索引文件、表结构文件、数据文件)

## mysql中的索引类型及性能影响
* 普通索引：允许被索引的数据列包含重复值
* 唯一索引：可以保证数据的唯一性
* 主键：一个特殊的唯一索引，在一张表中只能定义一个主键；在InnoDB中主键是一个聚簇索引(主键和行数据是在一起的)
* 联合索引：索引可以覆盖多个数据列
* 全文索引：通过建立 倒排索引 ,可以极大的提升检索效率,解决判断字段是否包含的问题，FULLTEXT
* 性能影响：索引可以极大的提高数据查询速度，通过使用索引可以在查询过程中使用优化隐藏器 提高系统性能；但是会降低插入、删除、更新表的速度(写数据的时候还要维护索引文件)，索引还会占用一定的额外空间

## Redis的RDB 和 AOF 机制
* RDB：在指定时间间隔内将内存中的数据集快照写入磁盘，实际会fork一个子进程先将数据集写入临时文件，写入成功之后再替换之前的文件(用二进制压缩)
* 优点：整个redis数据库中只包含一个dump.rdb，方便持久化；容灾性好 方便备份；性能最大化 是fork子进程的来完成写操作 主进程不受影响
* 缺点：数据安全性低(是有时间间隔的 比如刚好在一定时间宕机)；由于RDB是通过fork子进程完成持久化工作的，当数据量较大时 会导致服务暂停一定时间(几百毫秒)
* AOF：以日志的形式记录服务器所处理的每一个写、删除操作(以文本方式记录 类似mysql的binlog)
* 优点：数据安全 记录了每次的操作记录，提供了3种同步策略(每秒同步、每修改同步、不同步)；通过append方式写文件 即使中途宕机也不会破坏已经存在的内容；AOF机制的rewrite模式(定期对AOF文件重写合并 实现压缩)
* 缺点：运行效率没有RDB高；AOF文件比RDB文件大，且恢复慢；数据集大的时候 比RDB启动效率低；
* 注意：优先使用AOF还原数据，如果两个都配了优先加载AOF

## Redis的过期键删除策略
* 惰性过期：只有当访问一个key时，才会判断该key是否过期 过期则清除；该策略可以最大化的节省CPU资源 但对内存不友好
* 定期过期：每隔一定时间 会扫描一定数量的数据库key，然后清除掉已过期key（通过调整定时扫描的时间间隔和每次扫描的限定耗时达到最优效果）
* redis中同时使用了以上两种过期策略 达到最好的平衡

## Redis的线程模型，单线程为什么这么快
* Redis基于Reactor模式开发了网络事件处理器，这个处理器叫做文件事件处理器 file event handler。采用IO多路复用机制来同时监听多个Socket，根据Socket上的事件类型来选择对应的事件处理器来处理这个事件
* 单线程快的原因：纯内存操作；核心是基于非阻塞的IO多路复用；单线程避免了多线程的频繁上下文切换带来的性能问题

## 缓存雪崩、缓存穿透、缓存击穿
* 缓存雪崩：缓存同一时间大面积失效，后续的请求都会落到数据库上 造成短时间内承受大量的请求而崩掉（redis数据过期、redis刚刚启动都会出现这种情况）
* 解决方案：缓存数据的过期时间设置为随机 防止大量的数据同时过期；缓存预热；互斥锁(在操作数据库的时候给指定数据加上锁)
* 缓存穿透：指缓存和数据库中都没有数据，导致请求都落到数据库上（一般来源攻击请求）
* 解决方案：接口层增加校验；从缓存获取不到的数据在数据库中也没有取到的话，可以将key-value写为key-null-30s；采用布隆过滤器 将所有可能存在的数据哈希到一个足够大的 bitmap 中，一个一定不存在的数据会被这个 bitmap 拦截掉，从而避免了对底层存储系统的查询压力
* 缓存击穿：缓存中没有 数据库中有（一般是缓存时间到期），这时由于并发用户特别多，同时读缓存没有读到数据 又同时去查数据库(一般都是查同一条数据)
* 解决方案：设置热点数据永不过期；加互斥锁（一个线程去查数据库 其他线程阻塞等待）

## Redis事务实现
* 事务开始：MULTI命令的执行，标识着一个事务的开始
* 命令入队：当一个客户端切换到事务状态之后，服务器会根据这个客户端发送来的命令执行不同的操作。
* 事务执行：客户端发送EXEC命令，服务器执行EXEC命令逻辑

## Redis集群方案
* 1.哨兵模式(Redis Cluster)：假设五个redis服务器有一个出错了，其他四个会进行投票，超过半数就说明该服务器出错，所以redis集群最少为三个redis服务器，每个服务器还要有一个备份服务器（也就是需要六个redis服务器）
* Redis 集群中内置了 16384 个哈希槽，当需要在 Redis 集群中放置一个 key-value 时，redis 先对 key 使用 crc16 算法算出一个结果，然后把结果对 16384 求余数，这样每个 key 都会对应一个编号在 0-16383 之间的哈希槽，redis 会根据节点数量大致均等的将哈希槽映射到不同的节点

* 3.Redis Sharding：客服端分片

## Redis主从复制


## 分布式CAP/BASE理论
* 一致性：所有节点在同一时间的数据完全一致
* 可用性：即服务一直可用 不会出现失败和超时的情况
* 分区容错性：即分布式系统在遇到某个节点或者分区故障的时候，仍然能够对外提供服务 满足一致性和可用性的服务
* BASE：BA基本可用(响应时间的损失/系统功能上的损失)、S软状态(数据同步允许一定延迟)、E最终一致性(系统中的所有分区在一定时间同步后最终能达到一致状态)

## 负载均衡算法/类型
* 算法：轮询法(顺序的轮流分配到各个服务器)、随机法(根据系统随机算法 随机到服务器上)、源地址哈希法(获取到客户端的ip 通过hash之后对服务器列表数取模，然后指定到服务器上)、加权轮询法、加权随机法、最小连接数法(根据当前服务器连接情况 动态选择取其中当前)
* 类型：DNS方式、硬件方式、软件方式(nginx、HAproxy、LVS)
* nginx：七层负载均衡，支持HTTP、E-mail协议，同时也支持四层负载均衡

## 分布式架构下session共享方案
* 采用无状态的服务，抛弃session（采用token机制等）
* 存入cookie（因数据是在客户端 会有安全风险）
* 服务器之间进行seesion同步（同步有延迟）
* IP绑定策略：使用nginx中的ip绑定策略，同一个ip只能在指定的同一个机器访问（当有一台服务器挂掉后 就失去了负载均衡的意义了）
* 使用Redis来存储session(推荐)：实现session共享、可以水平扩展、服务器重启Session不丢失、可以跨平台

## RPC远程调用
* 在本地调用远程函数，远程调用可以跨语言实现

## 分布式id生成方案
* uuid：当前时间戳+计数器+全局唯一的机器识别号；优点：代码简单 性能好 保证唯一(重复率极低)；缺点：每次生成的id是无序的，只能是字符串 且长度过长不适合储存，没有业务含义 可读性差
* 数据库自增序列：优点：实现简单 依靠数据库成本小，id数字化 单调自增，具有一定的业务含义；缺点：依赖数据库，db生成id性能有限，有信息安全问题
* Leaf-segment：采用每次获取一个ID区间段的方式来解决，区间段用完之后再去数据库获取新的号段，这样一来可以大 大减轻数据库的压力；优点：降低了db的请求次数 扩展灵活；缺点：可能存在多个节点同时请求id区间情况 依赖db
* 双buffer：上面Leaf-segment的优化方案，将获取一个号段的方式优化成获取两个号段，在一个号段用完之后不用立马去更新号段，还 有一个缓存号段备用，这样能够有效解决这种冲突问题，而且采用双buffer的方式，在当前号段消耗了 10%的时候就去检查下一个号段有没有准备好，如果没有准备好就去更新下一个号段
* 基于redis、mongodb、zk等中间件生成
* 雪花算法：生成一个64bit的整性数字（第一位符号位固定为0，41位时间戳，10位workId，12位序列号）；优点：性能好 不够可以变动位数来增加，整个ID是趋势递增，灵活度高；缺点：强依赖于机器时钟，如果时钟回拨，会导致重复的ID生成

## 分布式锁解决方案-需要这个锁独立于每一个服务之外
* 数据库：利用主键冲突控制一次只有一个线程能获取锁（多个线程去插入同一个id的数据 能插入表示拿到锁了），非阻塞、不可重入、单点、失效时间
* Zookeeper分布式锁：zk通过临时节点，解决了死锁的问题，一旦客户端获取到锁之后突然挂掉（Session连接断开），那么这个临 时节点就会自动删除掉，其他客户端自动获取锁。临时顺序节点解决惊群效应
* Redis分布式锁：setNX 所有服务节点设置相同的key，返回为0、则锁获取失败，单线程处理网络请求，不需要考虑并发安全性；缺点：早期版本没有超时参数，需要单独设置，存在死锁问题；存在任务超时，锁自动释放，导致并发问题，加锁与释放锁不是同一线程问题

## 分布式事务解决方案
* 两阶段协议
* 三阶段协议
* TCC补偿事务
* 消息队列的事务消息
* Spring Cloud Alibaba Seata
 
## 如何实现接口的幂等性
* 唯一id：每次操作都根据内容生成唯一id 在执行前判断id是否存在(id保存到数据库或者redis)
* 服务端提供发送的token，业务调用前先获取token  业务请求时把token带过去判断是否存在于redis中
* 建立去重表：将业务中的唯一标识保存到去重表 如果表中存在表示已经处理过了
* 版本控制：增加版本号 当前版本号符合才能更新数据
* 状态控制：例如订单中的状态只能由未支付 > 已支付，不能处理 已支付 > 已支付/未支付

## ZAB协议简述（消息广播、崩溃恢复）
* 是为分布式协调服务Zookeeper设计的一种支持崩溃恢复的原子广播协议 实现分布式数据一致性所有客户端请求都写入到Leader进程，由Leader同步到其他节点
* 消息广播：集群中的所有事务请求都由Leader来处理，再由Leader将客户端请求转换为事务Proposal 广播到所有的子节点Follower，完成广播之后 等有过半数的Follower返回信息后 Leader再次广播Commit
* 崩溃与恢复：Leader崩溃后 会开启新一轮的选举，选举产生的Leader会与过半的Follower进行同步使数据一致，当与过半机器同步完成之后就退出恢复模式 进入广播模式
* Zxid：ZAB协议的一个事务编号，64位的数字
* ZAB节点的三种状态：leading：负责协调事务、following：服从leader的命令、election/looking：选举状态

## zk的数据模型和节点类型
* 数据模型：树形结构 Znode兼具文件和目录两种特点
* 节点类型：持久节点、临时节点、有序节点
  1. 一旦创建该节点会一致存储在zk服务器上，即使创建节点的服务器关闭了 节点也不会删除
  2. 当创建该节点的客户端因超时或异常关闭时，该节点也会删除
  3. 不是一种单独的节点，而是在持久节点和临时节点的基础上增加了一个有序的性质

## zk的命名服务、配置管理、集群管理
* 命名服务：通过指定的名字来获取资源或者服务地址（通过名字拿到url地址进行调用），zk可以创建一个全局唯一的路径 这个路径就可以作为一个名字
* 配置管理：实际开发中经常使用yml等配置文件，程序可以将这些信息保存到zk的znode节点下 当znode发生改变时 利用watcher通知给各个客户端
* 集群管理：包含集群监控（监控集群机器状态）、集群控制（剔除和加入机器）

## zk的watcher机制
* 客户端：可以通过在znode设置watcher，实时监听znode的变化（父节点的创建、修改、删除，子节点的创建、删除都会触发）
* watcher包含三个角色：客户端线程、客户端的WatcherMan、zookeeper服务器

## zk和eureka的区别
* CAP：一致性、可用性、分区容错性
* zk：CP设计（强一致性）
* eureka：ap设计（高可用性）

## spring cloud和dubbo的区别
* 底层协议：spring cloud基于http协议，dubbo基于tcp协议  dubbo的性能更好一点
* 注册中心：spring cloud使用eureka，dubbo使用的zookeeper
* 模型定义：dubbo是将一个接口定义为一个服务，spring cloud则是将一个应用定义为一个服务
* spring cloud是一整个生态 包含：服务调用、负载均很、服务降级、服务熔断、配置中心、网关等等

## 服务熔断、服务降级、服务监控
* 服务熔断：是应对雪崩效应的一种微服务链路保护机制，当扇出链路的某个微服务不可用或者响应时间太长时，熔断该节点微服务的调用， 快速返回"错误"的响应信息。如同 “保险丝”
* 服务降级：整体资源快不够了，忍痛将某些服务先关掉，待渡过难关，再开启回来

## spring cloud核心组件
* Eureka：服务的注册和发现，每个服务都向Eureka注册自己的元数据(ip、端口、版本等等)，后面使用nacos会更好(默认ap  同时支持cp、ap)
* Ribbon：服务间发起请求的时候，基于Ribbon做负载均衡，从⼀个服务的多台机器中选择⼀台
* Feign：基于Feign的动态代理机制，根据注解和选择的机器，拼接请求URL地址，发起请求 ，简化服务间的调用，在Ribbon的基础上进行了进一步的封装
* Hystrix：发起请求是通过Hystrix的线程池来⾛的，不同的服务⾛不同的线程池，实现了不同服务调⽤的隔离，通过统计接口超时次数返回默认值，实现服务熔断和降级
* Zuul：如果前端、移动端要调⽤后端系统，统⼀从Zuul⽹关进⼊，由Zuul⽹关转发请求给对应的服务，通过与Eureka进行整合，将自身注册为Eureka下的应用，从Eureka下获取所有服务的实例，来进行服务的路由。

## dubbo

## RabbitMQ
* Broker：rabbitmq的服务节点
* Queue：队列，是RabbitMQ的内部对象，用于储存消息；多个消费者可以订阅同一个队列 这时队列中的消息会被平均分摊给多个消费者(而不是每个消费者都能收到所有消费)
* Exchange：交换器，生产者将消息发送到交换机，由交换机路由到一个或者多个队列中（路由不到则会返回给生产者或者丢弃）
* Routingke：路由key，生产者将消息发送给交换器的时候一般会指定路由key 用来指定这个消息的路由规则
* Binding：通过绑定将交换器和队列关联起来，在绑定的时候一般会指定一个绑定key
* 信道：信道是建立在Connection之上的虚拟连接

## RabbitMQ如何确保消息的发送和接收
* 发送方确认机制：信道需设置为confirm模式，则所有在信道上发布的消息都会分配一个唯一ID；一旦消息消息被投递刀queue 信道会发送一个ack确认给生产者，如果rabbitMQ发送内部错误从而导致消息丢失则会发送一个nack给生产者(ack/nack返回的时间没有保证的)；确认模式是异步的 生产者在等待确认过程可以继续发送消息
  1. ConfirmCallback接口：只确认是否正确到达 Exchange 中，成功到达则回调
  2. ReturnCallback接口：消息失败返回时回调
  3. 不用确认也可以发送下一条消息
* 接收方确认机制：消费者在声明队列的时候 可以指定noAck，当noAck=false时 RabbitMQ会等待消费者显示发回ack才从内存/磁盘中移除消息；消费者每接收一条消息都要进行确认；同时RabbitMQ不会为未ack的消息设置超时时间 只有前面的消息ack之后才能消费下一条消息；如果消费者返回ack之前断掉连接 RabbitMQ会重新发给下一个订阅的消费者
  1. 确保消息会被消费掉，同时起到一个限流作用
  2. 可能存在消息重复消费的隐患，需要去重
  3. 必须要确认之后才能消费下一条消息

## RabbitMQ事务消息
* 生产者：
    1. channel.txSelect()；通知服务器开启事务模式；服务端会返回Tx.Select-Ok
    2. channel.basicPublish；发送消息，可以是多条，可以是消费消息提交ack
    3. channel.txCommit()提交事务；
    4. channel.txRollback()回滚事务；
* 消费者：
    1. autoAck=false，手动提交ack，以事务提交或回滚为准；
    2. autoAck=true，不支持事务的，也就是说你即使在收到消息之后在回滚事务也是于事无补的，队列已经把消息移除了
* 如果其中任意一个环节出现问题，就会抛出IoException异常，用户可以拦截异常进行事务回滚，或决定要不要重复消息。
* 事务消息会降低rabbitmq的性能

## RabbitMQ死信队列、延时队列
* TTL：一条消息或者该队列中的所有消息的最大存活时间
* 死信队列：消息被消费方否定确认；消息在队列的存活时间超过设置的TTL时间；消息队列的消息数量已经超过最大队列长度
  1. “死信”消息会被RabbitMQ进行特殊处理，如果配置了死信队列信息，那么该消息将会被丢进死信队列中，如果没有配置，则该消息将会被丢弃
  2. 为每个需要使用死信的业务队列配置一个死信交换机，这里同一个项目的死信交换机可以共用一个(和普通交换机没有区别)
* 延时队列：生产者希望这条消息不会被马上消费掉，需要一定时间之后才被消费者消费
  1. 实现方案：先将消息放入一个没有消费者监听且设置了TTL的队列中，TTL超时之后消息就会进入到死信队列中 此时消费者再去监听这个死信队列那么也就实现了延时队列了
  
## kafka架构设计
* 消费者组：消费者组内每个消费者负责消费不同分区的数据，提高消费能力（如果一个消费者组里面有两个消费者那么一条消息只会被一个消费者消费）
* Topic：可以理解为一个队列，Topic 将消息分类，生产者和消费者面向的是同一个 Topic。
* Partition：分区，为了实现扩展性，提高并发能力，一个Topic 以多个Partition的方式分布到多个 Broker上，每个 Partition 是一个 有序的队列。
  1. 生产者发送数据的对象，以及消费者消费数据的对象，都是 Leader。
  2. Follower负责实时从 Leader 中同步数据，保持和 Leader 数据的同步。
  3. Leader 发生故障时，某个Follower 还会成为新的 Leader。
* Offset：消费者消费的位置信息，监控数据消费到什么位置，当消费者挂掉再重新恢复的时候，可以从消费位置继续消费。
* Zookeeper：Kafka 集群能够正常工作，需要依赖于 Zookeeper，Zookeeper 帮助 Kafka 存储和管理集群信息。

## kafka消息丢失解决方案
* 消息发送：ack=0 producer不等待broker的ack；ack=1 producer等待broker的ack，partition的leader落盘成功后返回ack；ack=-1(all) producer等待broker的ack，partition的leader和follower全部落盘成功后才返回ack
* 解决方案：
  1. 配置ack=all：producer发送消息完，等待follower同步完再返回，如果异常则重试，不允许选举ISR以外的副本作为leader；
  2. 配置：min.insync.replicas > 1：副本指定必须确认写操作成功的最小副本数量，min.insync.replicas和ack更大的持久性保证。
  3. 失败的offset单独记录：producer发送消息，会自动重试，遇到不可恢复异常会抛出，这时可以捕获异常记录到数据库或缓存，进行 单独处理
* 消费者：先commit再处理消息（如果在处理消息的时候异常了，但是offset 已经提交了，这条消息对于该消费者来 说就是丢失了，再也不会消费到了）；先处理消息再commit（可能会出现重复消费 所以只要保证接口幂等性就可以了）
* broker的刷盘：减少刷盘的间隔

## kafka的消息是pull、还是push（kafka是pull模式）
* pull：由消费端主动去拉取；优点：根据消费者的消费能力进行拉取，也可以指定拉取的数量，数据可以设置不同确认消费提交方式；缺点：如果kafka没有数据会导致消费者空循环消耗资源(通过参数设置 消费者拉取数据为空或者没有拉取到指定的条数就进行阻塞)
* push：由服务端主动推送数据到消费者；优点：不会导致消费者空循环；缺点：速率固定、忽略了消费者的消费能力 可能会导致拒绝服务或者网络堵塞的情况

## kafka中zookeeper的作用
* /brokers/ids：临时节点 保存所有borker节点信息，存储broker的物理地址、信息等，节点名称为broker ID，broker会定时发送心跳到zk 如果断开则该broker ID会删除
* /brokers/topics：临时节点 保存broker节点下的所有topic信息，每个topic节点下面包含一个固定的partitions节点(就是topic的分区)，每个分区下面保存一个state节点 保存着当前leader分区和ISR的brokerID(确定leader)，state节点由leader创建，若leader宕机该节点会被删除，直到有新的leader选举产生、重新生成state节点
* /consumers/[group_id]/owners/[topic]/[broker_id-partition_id]：维护消费者和分区的注册关系
* /consumers/[group_id]/offsets/[topic]/[broker_id-partition_id]：分区消息的消费进度Offset
* client通过topic找到topic树下的state节点、获取leader的brokerID，到broker树中找到broker的物理地址，但是client不会直连zk，而是通过配置的broker获取到zk中的信息

## kafka的高性能读写（顺序写、零拷贝）
* kafak不基于内存 而是硬盘存储，所以消息堆积能力更强
* 顺序写：利用磁盘的顺序写访问速度就可以接近内存，kafka的消息操作都是append操作，partition是有序的 节省了磁盘的寻道时间，同时通过批量操作、节省写入次数，partition物理上分为多个segment存储 方便删除
* 零拷贝：直接将内核缓冲区的数据发送到网卡传输，使用的是操作系统的指令支持，kafka不太依赖jvm，主要理由操作系统的pageCache，如果生产消费速率相当，则直接用pageCache交换数据，不需要经过磁盘IO

## kafka的rebalance机制
* 消费者组中的消费者与topic下的partion重新匹配的过程
  1. 消费者组中的消费者成员数量发生变化
  2. 消费者超时
  3. 消费者组订阅的topic个数发生变化
  4. 消费者组订阅的topic的分区数发生变化
* coordinator(协调者)：通常是partition的leader节点所在的broker，负责监控group中consumer的存活，consumer维持到coordinator的心跳，判断consumer的消费超时
  1. coordinator通过心跳返回通知consumer进行rebalance
  2. consumer请求coordinator加入组，coordinator选举产生leader consumer
  3. leader consumer从coordinator获取所有的consumer，发送syncGroup(分配信息)给到coordinator
  4. coordinator通过心跳机制将syncGroup下发给consumer完成rebalance