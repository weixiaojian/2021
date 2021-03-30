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