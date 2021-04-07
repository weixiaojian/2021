## kafka相关
> Kafka 是一个分布式的基于发布/订阅模式的消息队列  
> 异步、解耦、削峰

* 消息队列的两种模式  
1.点对点：一对一，消费者主动拉取数据消息收到后消息清除  
2.发布订阅：一对多，消费者消费数据后不会清除数据


## 单机安装
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

