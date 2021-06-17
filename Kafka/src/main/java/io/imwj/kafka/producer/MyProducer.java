package io.imwj.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * kafka生产者
 * @author langao_q
 * @since 2021-06-16 15:59
 */
public class MyProducer {

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
            producer.send(new ProducerRecord<String, String>("first2","hello: " + i));
        }

        //4.关闭连接(必须要关闭 否则消息不会发送出去)
        producer.close();
    }

}
