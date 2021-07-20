package io.imwj.kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * 带回调的生产者
 * @author langao_q
 * @since 2021-07-14 17:16
 */
public class CallBackProducer {

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

}
