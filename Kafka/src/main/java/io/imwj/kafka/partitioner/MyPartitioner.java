package io.imwj.kafka.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

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
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
