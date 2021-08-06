package io.imwj.kafka.intercept;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 增加时间戳拦截器
 * @author langao_q
 * @since 2021-08-06 16:35
 */
public class TimeIntercept implements ProducerInterceptor<String, String> {

    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        //1.取出value数据
        String value = record.value();
        //2.增加时间戳
        value = System.currentTimeMillis() + "," + value;
        //3.创建一个新的对象返回
        return new ProducerRecord<String, String>(record.topic(), record.partition(), record.timestamp(), record.key(), value);
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }
}
