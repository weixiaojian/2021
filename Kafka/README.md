## kafka相关
> Kafka 是一个分布式的基于发布/订阅模式的消息队列  
> 异步、解耦、削峰

* 消息队列的两种模式  
1.点对点：一对一，消费者主动拉取数据消息收到后消息清除  
2.发布订阅：一对多，消费者消费数据后不会清除数据

* 架构  
![image](https://raw.githubusercontent.com/weixiaojian/2021/master/Kafka/img/001.png)

* 工作流程  
![image](https://raw.githubusercontent.com/weixiaojian/2021/master/Kafka/img/001.png)

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

## kafka命令
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

bin/kafka-topics.sh --create --zookeeper 192.168.153.128:2181 --topic first --partitions 1 --replication-factor 3
```
* 删除主题（first）
```
bin/kafka-topics.sh --delete --zookeeper 192.168.153.128:2181 --topic first
```
* 查看所有主题
```
bin/kafka-topics.sh --list --zookeeper 192.168.153.128:2181 
```
* 查看主题详情（first）
```
bin/kafka-topics.sh --describe --zookeeper 192.168.153.128:2181 --topic first
```
* 连接生产者控制台
```
bin/kafka-console-producer.sh --topic first --broker-list 192.168.153.128:9092
```
* 连接消费者控制台
```
bin/kafka-console-consumer.sh --topic first --bootstrap-server 192.168.153.128:9092
```

# Kafka生产者
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


# Kafka消费者
* 消费者采用pull（拉）模式从broker中读取数据。Kafka的消费者在消费数据时会传入一个时长参数timeout，如果当前没有数据可供消费，consumer会等待一段时间之后再返回，这段时长即为timeout。
## 分区分配策略（消费者数量改变就会触发）
1. RoundRobin：轮询（根据组来划分）
    
2. Range：范围（根据主题来划分）