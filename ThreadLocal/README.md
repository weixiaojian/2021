## 一致性问题  
* 发生在多个主题对同一份数据无法达成共识  包括：分布式一致性我呢提、并发问题等    
* 解决方案：排队（如：锁、互斥量、管理、屏障  [效率低 开销大]），投票（如：Paxos、Raft  [效率高 但问题较多]） 

## ThreadLocal  
* 定义：提供线程局部变量；一个线程局部变量在多个线程中，分别由独立的值（副本）
* 特点：简单（开箱即用）、快速（无额外开销）、安全（线程安全）
* 实现：Java中用哈希表实现

## 使用API
```
protected T initialValue() 
返回此线程局部变量的当前线程的“初始值”。  

void set(T value) 
将当前线程的此线程局部变量的副本设置为指定的值。  

T get() 
返回当前线程的此线程局部变量的副本中的值。  

void remove() 
删除此线程局部变量的当前线程的值。  
```

## 使用场景（源码）
* Quartz（控制线程数量）
```
public class SimpleSemaphore implements Semaphore {
    ThreadLocal<HashSet<String>> lockOwners = new ThreadLocal();
    HashSet<String> locks = new HashSet();
    
    private HashSet<String> getThreadLocks() {
        HashSet<String> threadLocks = (HashSet)this.lockOwners.get();
        if (threadLocks == null) {
            threadLocks = new HashSet();
            this.lockOwners.set(threadLocks);
        }

        return threadLocks;
    }
    
    public synchronized boolean obtainLock(Connection conn, String lockName) {
        lockName = lockName.intern();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Lock '" + lockName + "' is desired by: " + Thread.currentThread().getName());
        }

        if (!this.isLockOwner(lockName)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Lock '" + lockName + "' is being obtained: " + Thread.currentThread().getName());
            }

            while(this.locks.contains(lockName)) {
                try {
                    this.wait();
                } catch (InterruptedException var4) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Lock '" + lockName + "' was not obtained by: " + Thread.currentThread().getName());
                    }
                }
            }

            if (this.log.isDebugEnabled()) {
                this.log.debug("Lock '" + lockName + "' given to: " + Thread.currentThread().getName());
            }

            this.getThreadLocks().add(lockName);
            this.locks.add(lockName);
        } else if (this.log.isDebugEnabled()) {
            this.log.debug("Lock '" + lockName + "' already owned by: " + Thread.currentThread().getName() + " -- but not owner!", new Exception("stack-trace of wrongful returner"));
        }

        return true;
    }
}
```


* Mybatis保持连接池一致（同一事务中使用的链接是同一个）
```
public class SqlSessionManager implements SqlSessionFactory, SqlSession {
    private final ThreadLocal<SqlSession> localSqlSession = new ThreadLocal();
    
    //初始化时：直接向ThreadLocal中存入connection
    public void startManagedSession() {
        this.localSqlSession.set(this.openSession());
    }
    public void startManagedSession(boolean autoCommit) {
        this.localSqlSession.set(this.openSession(autoCommit));
    }

    public void startManagedSession(Connection connection) {
        this.localSqlSession.set(this.openSession(connection));
    }
    //通过ThreadLocal中是否有值来判断ManagedSession已经启动
    public boolean isManagedSessionStarted() {
        return this.localSqlSession.get() != null;
    }
    //获取Connection时直接从ThreadLocal中获取
    public Connection getConnection() {
        SqlSession sqlSession = (SqlSession)this.localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot get connection.  No managed session is started.");
        } else {
            return sqlSession.getConnection();
        }
    }
}
```

* spring中对分布式事务的支持（分布式资源的持有）
```
final class TransactionContextHolder {
    private static final ThreadLocal<TransactionContext> currentTransactionContext = new NamedInheritableThreadLocal("Test Transaction Context");

    private TransactionContextHolder() {
    }

    //储存到ThreadLocal中
    static void setCurrentTransactionContext(TransactionContext transactionContext) {
        currentTransactionContext.set(transactionContext);
    }

    //从ThreadLocal中获取
    @Nullable
    static TransactionContext getCurrentTransactionContext() {
        return (TransactionContext)currentTransactionContext.get();
    }

    @Nullable
    static TransactionContext removeCurrentTransactionContext() {
        TransactionContext transactionContext = (TransactionContext)currentTransactionContext.get();
        currentTransactionContext.remove();
        return transactionContext;
    }
}
```

## 实现自己的TheadLocal
```
/**
 * 实现自己的ThreadLocal
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
     * @return
     */
    synchronized static HashMap<Integer, Object> getMap(){
        Thread thread = Thread.currentThread();
        if(!threadLocalMap.containsKey(thread)){
            threadLocalMap.put(thread, new HashMap<Integer, Object>());
        }
        return threadLocalMap.get(thread);
    }

    protected  T initialValue(){
        return null;
    }

    public T get(){
        HashMap<Integer, Object> map = getMap();
        if(!map.containsKey(this.threadLocalHash)){
            map.put(this.threadLocalHash, initialValue());
        }
        return (T) map.get(this.threadLocalHash);
    }

    public void set(T t){
        HashMap<Integer, Object> map = getMap();
        map.put(this.threadLocalHash, t);
    }
}
```
## MyThreadLocal问题
* HashMap无限增加
* 初始空间分配是否合理
* 性能是否ok
