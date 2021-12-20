package io.imwj.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author wj
 * @since 2021-09-17 18:50
 */
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
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"admin\" password=\"123123\";");

        //2.创建消费者
        KafkaConsumer consumer = new KafkaConsumer<String, String>(props);

        //3.订阅主题
        consumer.subscribe(Arrays.asList("first"));

        while (true) {
            //4.获取数据
            ConsumerRecords<String, String> records = consumer.poll(1);
            //5.遍历数据
            for (ConsumerRecord<String, String> consumerRecord : records) {
                System.out.println(" 拉取数据--- " + consumerRecord.value());
            }
            //同步提交，当前线程会阻塞直到 offset 提交成功
            consumer.commitSync();
        }
    }
}
