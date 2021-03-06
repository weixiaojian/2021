# 1.kafka相关
> Kafka 是一个分布式的基于发布/订阅模式的消息队列  
> 异步、解耦、削峰

* 消息队列的两种模式  
1.点对点：一对一，消费者主动拉取数据消息收到后消息清除  
2.发布订阅：一对多，消费者消费数据后不会清除数据

* 架构  
![image](https://raw.githubusercontent.com/weixiaojian/2021/master/Kafka/img/001.png)

* 工作流程  
![image](https://raw.githubusercontent.com/weixiaojian/2021/master/Kafka/img/002.png)

## 安装
* 解压安装包
```
unzip kafka_2.11-2.1.0.zip 
```
* 修改kafka配置：/kafka/config/server.properties
```
broker.id=0  #id要唯一
delete.topic.enable=true  #允许删除主题
log.dirs=/data/kafka/logs   #运行日志地址
zookeeper.connect=192.168.153.128:2181   #zookeeper集群地址用逗号隔开（注意：zookeeper也要配置好集群模式）
```
* 修改zookeeper配置：/kafka/config/zookeeper.properties（kafka自带）
```
dataDir=/data/kafka/zookeeper/data  #运行日志地址
clientPort=2181     #端口
maxClientCnxns=0    
```

# 2.kafka命令
* 查看启动状态（jdk自带）
```
jps
```
* 启动zookeeper（kafka自带）
```
./bin/zookeeper-server-start.sh -daemon config/zookeeper.properties 
```
* 启动kafka
```
./bin/kafka-server-start.sh -daemon config/server.properties 
```

* 创建主题（主题名称first）
```
partitions：分区数
replication-factor：副本数

bin/kafka-topics.sh --create --zookeeper 192.168.31.129:2181 --topic first --partitions 1 --replication-factor 3
```
* 删除主题（first）
```
bin/kafka-topics.sh --delete --zookeeper 192.168.31.129:2181 --topic first
```
* 查看所有主题
```
bin/kafka-topics.sh --list --zookeeper 192.168.31.129:2181 
```
* 查看主题详情（first）
```
bin/kafka-topics.sh --describe --zookeeper 192.168.31.129:2181 --topic first
```
* 连接生产者控制台
```
bin/kafka-console-producer.sh --topic first --broker-list 192.168.31.129:9092
```
* 连接消费者控制台
```
bin/kafka-console-consumer.sh --topic first --bootstrap-server 192.168.31.129:9092
```

# 3.1Kafka生产者
## 文件存储机制
* Kafka 中消息是以 topic 进行分类的， 生产者生产消息，消费者消费消息，都是面向 topic的。  
* topic 是逻辑上的概念，而 partition 是物理上的概念，每个 partition 对应于一个 log 文件，该 log 文件中存储的就是 producer 生产的数据。  
* Producer 生产的数据会被不断追加到该log 文件末端，且每条数据都有自己的 offset。   
* Kafka 采取了分片和索引机制，将每个 partition 分为多个 segment。 每个 segment对应两个文件——“.index”文件和“.log”文件。 这些文件位于一个文件夹下， 该文件夹的命名规则为： topic 名称+分区序号。
* “.index”文件存储大量的索引信息，“.log”文件存储大量的数据，索引文件中的元数据指向对应数据文件中 message 的物理偏移地址。

## 分区架构
* 生产者分区
1.指明partition的情况下，直接将指明的值直接作为partiton值；  
2.没有指明partition值但有key的情况下，将key的hash值与topic的partition数进行取余得到partition值；  
3.既没有partition值又没有key值的情况下，第一次调用时随机生成一个整数（后面每次调用在这个整数上自增），将这个值与topic可用的partition总数取余得到partition值，也就是常说的round-robin算法  

##数据可靠性保证（ISR）
* topic的每个partition收到producer发送的数据后，都需要向producer发送ack（acknowledgement确认收到），如果producer收到ack，就会进行下一轮的发送，否则重新发送数据  
* Leader维护了一个动态的in-syncreplicaset(ISR)，意为和leader保持同步的follower集合。当ISR中的follower完成数据的同步之后，leader就会给follower发送ack。如果follower长时间未向leader同步数据，则该follower将被踢出ISR，该时间阈值由replica.lag.time.max.ms参数设定。Leader发生故障之后，就会从ISR中选举新的leader  

##ack应答机制（acks参数配置）
* 0：producer不等待broker的ack，这一操作提供了一个最低的延迟，broker一接收到还没有写入磁盘就已经返回，当broker故障时有可能丢失数据；  
* 1：producer等待broker的ack，partition的leader落盘成功后返回ack，如果在follower同步成功之前leader故障，那么将会丢失数据；  
* -1（all）：producer等待broker的ack，partition的leader和follower全部落盘成功后才返回ack。但是如果在follower同步完成后，broker发送ack之前，leader发生故障，那么会造成数据重复。  

## HW和LEO
* LEO：每个副本的最大offset  
* HW：所有副本中最小的LEO    
* follower故障：故障后会被踢出ISR,待恢复后follower会读取本地磁盘记录的上次HW，并将log文件中高于HW的部分截取掉，从HW开始向leader同步，待follower的LEO大于等于改Partition的HW（追上leader）就可以重新加入ISR  
* leader故障：故障后会从ISR重新选出一个leader，为保证多个的数据一致性会将其余副本高于当前leader的LEO部分截取掉，再把leader高于HW的部分同步给其他低于LEO的副本  
* 注意： 这只能保证副本之间的数据一致性，并不能保证数据不丢失或者不重复。  

## Exactly Once
1. At Least Once：至少一次（保证数据不丢失，不保证数据重复）  
2. At Most Once：至多一次（保证数据不重复，不保证数据不丢失）  
3. At Least Once + 幂等性 = Exactly Once  
4. 幂等性：enable.idompotence=true（设置幂等性）  
    * 在初始化的时候会被分配一个PID，发往同一Partition的消息会附带SequenceNumber。而Broker端会对<PID,Partition,SeqNumber>做缓存，当具有相同主键的消息提交时，Broker只会持久化一条。  
    * 但是PID重启就会变化，同时不同的Partition也具有不同主键，所以幂等性无法保证跨分区跨会话的ExactlyOnce。  


# 3.2Kafka消费者
* 消费者采用pull（拉）模式从broker中读取数据。Kafka的消费者在消费数据时会传入一个时长参数timeout，如果当前没有数据可供消费，consumer会等待一段时间之后再返回，这段时长即为timeout。
## 分区分配策略（消费者数量改变就会触发）
1. RoundRobin：轮询（根据组来划分）
    
2. Range：范围（根据主题来划分）

# 4.Kafka开发api
> Kafka 的 Producer 发送消息采用的是异步发送的方式。在消息发送的过程中，涉及到了两个线程——main 线程和 Sender 线程，以及一个线程共享变量——RecordAccumulator。   

1.发送消息到服务器
```
    public static void main(String[] args) {

        //1.创建kafka生产者的配置信息
        Properties props = new Properties();
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.129:9092");
        //应答级别
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //重试次数
        props.put("retries", 1);
        //批次大小
        props.put("batch.size", 16384);
        //等待时间（毫秒）
        props.put("linger.ms", 1);
        //RecordAccumulator 缓冲区大小（32m）
        props.put("buffer.memory", 33554432);
        //key、value所使用的序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //2.创建生产者对象
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        //3.发送数据
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<String, String>("first","hello: " + i));
        }

        //4.关闭连接(必须要关闭 否则消息不会发送出去)
        producer.close();
    }
```
2.带回调的生产者API
```
    public static void main(String[] args) {
        //1.创建kafka生产者的配置信息
        Properties props = new Properties();
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.129:9092");
        //key、value所使用的序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //2.创建生产者对象
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        //3.发送数据
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<String, String>("first", 0, "at", "hello: " + i), (metadata, e) -> {
                System.out.println(metadata.partition() + "----" + metadata.offset());
                System.out.println(metadata.topic() + "----" + metadata.toString());
            });
        }

        //4.关闭连接(必须要关闭 否则消息不会发送出去)
        producer.close();
    }
```

3.消费者demo
```
    public static void main(String[] args) {
        //1.创建kafka生产者的配置信息
        Properties props = new Properties();
        //主机地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.129:9092");
        //自动提交开关
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //自动提交延时
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        //key、value所使用的序列化器
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //消费者组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bigdata1");
        //重置消费者的offset（配置earliest + 切换消费者组 或 数据过期 = 可消费生产者最初的未消费数据）
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //2.创建消费者
        KafkaConsumer consumer = new KafkaConsumer<String, String>(props);

        //3.订阅主题
        consumer.subscribe(Arrays.asList("first"));

        while (true){
            //4.获取数据
            ConsumerRecords<String,String> records = consumer.poll(100);

            //5.遍历数据
            for(ConsumerRecord<String, String> consumerRecord : records){
                System.out.println(consumerRecord.key() + " --- " + consumerRecord.value());;
                System.out.println(consumerRecord.partition() + " --- " + consumerRecord.offset());;
            }
            //同步提交，当前线程会阻塞直到 offset 提交成功
            consumer.commitSync();
        }
    }
```

## 消费者自动提交与手动提交（同步/异步）
* 自动提交
```
    //自动提交开关
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
```

* 手动提交（同步/异步）
```
    //关闭自动提交开关
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    
    //同步提交，当前线程会阻塞直到 offset 提交成功
    consumer.commitSync();
    
    //异步提交
    consumer.commitAsync(new OffsetCommitCallback() {
        @Override
        public void onComplete(Map<TopicPartition,
                                OffsetAndMetadata> offsets, Exception exception) {
            if (exception != null) {
                System.err.println("Commit failed for" +
                        offsets);
            }
        }
    });
```

## 自定义分区器
* kafka中每个topic被划分为多个分区，那么生产者将消息发送到topic时，具体追加到哪个分区呢？
* 其路由机制为：若发送消息时指定了分区（即自定义分区策略），则直接将消息append到指定分区；
* 若发送消息时未指定 patition，但指定了 key（kafka允许为每条消息设置一个key），则对key值进行hash计算，根据计算结果路由到指定分区，这种情况下可以保证同一个 Key 的所有消息都进入到相同的分区；
* patition 和 key 都未指定，则使用kafka默认的分区策略，轮询选出一个 patition； 
```
/**
 * 自定义生产者分区
 * @author langao_q
 * @since 2021-07-20 15:39
 */
public class MyPartitioner implements Partitioner{
    /**
     * 自定义分区器：返回分区号
     * @param topic
     * @param key
     * @param keyBytes
     * @param value
     * @param valueBytes
     * @param cluster
     * @return
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //获取所有可用分区
        Integer integer = cluster.partitionCountForTopic(topic);
        //增加逻辑处理....
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
```
* 使用
```
        props.put("partitioner.class", "io.imwj.kafka.partitioner.MyPartitioner");
```

## 自定义拦截器(org.apache.kafka.clients.producer.ProducerInterceptor)
* configure(configs)
获取配置信息和初始化数据时调用。

* onSend(ProducerRecord)：消息发送前
该方法封装进 KafkaProducer.send 方法中，即它运行在用户主线程中。Producer确保在消息被序列化以及计算分区前调用该方法。用户可以在该方法中对消息做任何操作。

* onAcknowledgement(RecordMetadata, Exception)：消息发送后（成功/失败）
该方法会在消息从 RecordAccumulator 成功发送到 Kafka Broker 之后，或者在发送过程中失败时调用。

* close：
关闭 interceptor，主要用于执行一些资源清理工作

* 创建拦截器
```
public class CountIntercept implements ProducerInterceptor<String, String> {

    Integer successCount = 0;
    Integer errorCount = 0;

    @Override
    public void configure(Map<String, ?> configs) {

    }

    /**
     * 增加时间戳
     * @param record
     * @return
     */
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        //1.取出value数据
        String value = record.value();
        //2.增加时间戳
        value = System.currentTimeMillis() + "," + value;
        //3.创建一个新的对象返回
        return new ProducerRecord<String, String>(record.topic(), record.partition(), record.timestamp(), record.key(), value);
    }

    /**
     * 统计成功和失败条数
     * @param metadata
     * @param exception
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if(metadata != null){
            successCount ++;
        }else{
            errorCount ++;
        }
    }

    @Override
    public void close() {
        System.out.println("successCount:" + successCount);
        System.out.println("errorCount:" + errorCount);
    }
}
```

* 使用拦截器
```
    //1.创建kafka生产者的配置信息
    Properties props = new Properties();
    
    //2.配置拦截器拦截器
    ArrayList<String> interceptorList = new ArrayList<>();
    interceptorList.add("io.imwj.kafka.intercept.CountIntercept");
    props.put("interceptor.classes", interceptorList);
```

# Kafka监控(Eagle)
* 修改kafka-server-start.sh：if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then里增加下面这段代码
```
    export KAFKA_HEAP_OPTS="-server -Xms2G -Xmx2G -XX:PermSize=128m 
    -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=8 -
    XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70"
    export JMX_PORT="9999"
```
* 上传/解压/改名kafka-eagle-bin-1.3.7.tar.gz包
* 修改配置文件
```
cluster1.zk.list=127.0.0.1:2181
cluster1.kafka.eagle.offset.storage=kafka
kafka.eagle.metrics.charts=true

#数据库配置
kafka.eagle.driver=com.mysql.jdbc.Driver
kafka.eagle.url=jdbc:mysql://127.0.0.1:3306/ke?useUnicode=true&ch
aracterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull
kafka.eagle.username=root
kafka.eagle.password=123456
```
* 添加环境变量：注意source /etc/profile
```
export KE_HOME=/data/kafka/eagle
export PATH=$PATH:$KE_HOME/bin
```
* 启动Eagle
```
bin/ke.sh start
```
* 访问网页：[http://192.168.9.102:8048/ke](http://192.168.9.102:8048/ke)，账号admin 密码123456

## kafka与springboot可以参考
一个博主的文章：[https://blog.csdn.net/yuanlong122716/article/details/105160545/](https://blog.csdn.net/yuanlong122716/article/details/105160545/)

## 记一次Java远程连接kafka生产者的问题
* 报错：找不到Host配置错误，或者直接连接不上
* 原因：kafka没有开启允许外网请求访问，
```
18:55:46.237 [kafka-producer-network-thread | producer-1] WARN org.apache.kafka.clients.NetworkClient - [Producer clientId=producer-1] Error connecting to node iZuf688uiv7i1onjv82rf8Z:8318 (id: 10 rack: null)
java.net.UnknownHostException: iZuf688uiv7i1onjv82rf8Z
	at java.net.InetAddress.getAllByName0(InetAddress.java:1280)
	at java.net.InetAddress.getAllByName(InetAddress.java:1192)
	at java.net.InetAddress.getAllByName(InetAddress.java:1126)
```
* 解决方案1：在本地电脑上的host文件中添加（不推荐）
```
服务器ip iZuf688uiv7i1onjv82rf8Z
```

* 解决方案2：修改kafka配置（推荐）,advertised.listeners相当于一个nginx进行了转发
```
listeners=PLAINTEXT://:8318
advertised.listeners=PLAINTEXT://[主机的外网ip]:对外端口
```

## 查看kafka当前消费情况
* 查看所有topic分组
```
bin/kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:8318 --list
```

* 查看指定分组的消费情况
```
./bin/kafka-consumer-groups.sh --describe --bootstrap-server 127.0.0.1:8318 --group [分组名称]
```
* 参数解析  
GROUP：分组id  
TOPIC：TOPIC名称  
PARTITION：分区id  
CURRENT-OFFSET：当前已消费的条数  
LOG-END-OFFSET：总条数   
LAG：未消费的条数

# kafka鉴权验证
## 开启验证以及一些配置文件
* kafka的config/server.properties开启鉴权验证
```
#安全认证监控服务
advertised.listeners=SASL_PLAINTEXT://121.37.175.249:8318
security.inter.broker.protocol=SASL_PLAINTEXT  
sasl.enabled.mechanisms=SCRAM-SHA-512
sasl.mechanism.inter.broker.protocol=SCRAM-SHA-512
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
allow.everyone.if.no.acl.found=true
```

* kafka_server_jaas.conf配置(配置超级用户、Client用户等)
```
KafkaServer {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="admin"
    password="123123"
    user_admin="123123"
    user_producer="proD#pW2120";
};

Client {
org.apache.kafka.common.security.plain.PlainLoginModule required
    username="kafka"
    password="kafka#pW2120";
};
```

* admin.config配置(操作kafka相关命令、创建普通用户的admin超级用户)
```
security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="admin" password="123123";
```

* test.config配置（普通用户，连接生产者、消费者用）
```
security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="test" password="test123";
```

## 用户及权限操作相关
* 注意：在kafka开启用户验证后，之前的比如查看、创建topic等命令都需要在后面加上`--command-config /data/app/kafka/config/admin.conf`
* 连接生产者
```
bin/kafka-console-producer.sh --broker-list 127.0.0.1:9002 --topic zto-data1 --producer.config /data/app/kafka/test.conf
```
* 连接生产者
```
bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9002  --topic zto-data1 --consumer.config /data/app/kafka/test.conf
```

* 新增用户
```
bin/kafka-configs.sh  --zookeeper 127.0.0.1:2181 --alter --add-config 'SCRAM-SHA-512=[password=test123]' --entity-type users --entity-name test
```

* 更新用户（更新test用户密码为123456）
```
bin/kafka-configs.sh --zookeeper 127.0.0.1:2181 --alter --add-config 'SCRAM-SHA-512=[password=123456]' --entity-type users --entity-name test
```

* 创建topic
```
bin/kafka-topics.sh --create  --bootstrap-server  127.0.0.1:9002 --replication-factor 1 --partitions 12 --topic first --command-config /data/app/kafka/config/admin.conf
```

* 读取权限,设置用户test的消费者权限
```
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=127.0.0.1:2181 --add --allow-principal User:"test" --consumer --topic 'first' --group '*'
```

* 写入权限,设置用户test的生产者权限
```
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=127.0.0.1:2181 --add --allow-principal User:"test" --producer --topic 'first' --group '*'
```

* 查看所有用户权限情况
```
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=127.0.0.1:2181 --list
```

* 查看所有分组
```
bin/kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9002 --list --command-config /data/app/kafka/config/admin.conf
```

* 查看指定分组的消费情况
```
./bin/kafka-consumer-groups.sh --describe --bootstrap-server 127.0.0.1:9002 --group bigdata1  --command-config /data/app/kafka/config/admin.conf
```

## Java的生产者和消费者
* 生产者
```
public class MyProducer2 {

    public static void main(String[] args) {
        //1.创建kafka生产者的配置信息
        Properties props = new Properties();
        //连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.129:9092");
        //应答级别
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //重试次数
        props.put("retries", 1);
        //批次大小
        props.put("batch.size", 16384);
        //等待时间（毫秒）
        props.put("linger.ms", 1);
        //RecordAccumulator 缓冲区大小（32m）
        props.put("buffer.memory", 33554432);
        //key、value所使用的序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //鉴权、验证
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"test\" password=\"test123\";");

        //2.创建生产者对象
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        //3.发送数据
        producer.send(new ProducerRecord<String, String>("first", "hello: test"));

        //4.关闭连接(必须要关闭 否则消息不会发送出去)
        producer.close();
    }
}
```

* 消费者
```
public class MyConsumer2 {
    public static void main(String[] args) {
        //1.创建kafka生产者的配置信息
        Properties props = new Properties();
        //主机地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.129:9092");
        //自动提交开关
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //自动提交延时
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        //key、value所使用的序列化器
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //消费者组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bigdata1");
        //重置消费者的offset（配置earliest + 切换消费者组 或 数据过期 = 可消费生产者最初的未消费数据）
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //鉴权、验证
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"test\" password=\"test123\";");

        //2.创建消费者
        KafkaConsumer consumer = new KafkaConsumer<String, String>(props);

        //3.订阅主题
        consumer.subscribe(Arrays.asList("first"));

        while (true){
            //4.获取数据
            ConsumerRecords<String,String> records = consumer.poll(1);
            //5.遍历数据
            for(ConsumerRecord<String, String> consumerRecord : records){
                System.out.println(" 拉取数据--- " + consumerRecord.value());;
            }
            //同步提交，当前线程会阻塞直到 offset 提交成功
            consumer.commitSync();
        }
    }
}
```

## Offset Explorer可视化工具
* 下载地址：[https://www.kafkatool.com/](https://www.kafkatool.com/)

* 正常直连：直接配置Properties下的参数即可  
Cluster name：服务名称  
Kafka Cluster Version：kafka版本（需要和安装的kafka版本一致）  
Zookeeper Host：zookeeper的ip  
Zookeeper port：zookeeper的端口  
chroot path：默认/即可  
注意：如果还连接不上可以在Advanced中配置kafka地址和端口

* kafka开启用户验证一下连接：需要额外配置一下选项
Security下配置加密类型如：SASL_PLAINTEXT
JAAS Config中配置连接的用户名和密码
```
org.apache.kafka.common.security.scram.ScramLoginModule required username="test" password="test123";
```





