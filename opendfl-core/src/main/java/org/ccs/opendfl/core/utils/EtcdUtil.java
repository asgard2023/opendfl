package org.ccs.opendfl.core.utils;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch.Watcher;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lock.LockResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * etcd 操作工具，包括启动监听和操作etcd v3 版本协议
 *
 * @version 1.0
 */
@Component
@Slf4j
public class EtcdUtil {
    // etcl客户端链接
    private static Client etcdClient = null;

    @Resource(name = "etcdClient")
    public void setEtcdClient(Client etcdClient) {
        EtcdUtil.etcdClient = etcdClient;
    }

    // 链接初始化
    public static Client getEtclClient() {
        return etcdClient;
    }

    /**
     * 新增或者修改指定的配置
     *
     * @param key
     * @param value
     * @throws Exception
     * @author zhangyanhua
     * @date 2019年10月29日 下午4:41:06
     */
    public static void putEtcdValueByKey(String key, String value) throws Exception {
        Client client = EtcdUtil.getEtclClient();
        client.getKVClient().put(ByteSequence.from(key, StandardCharsets.UTF_8), ByteSequence.from(value, StandardCharsets.UTF_8)).get();
        client.close();
        etcdClient = null;
    }

    /**
     * 查询指定的key名称对应的value
     *
     * @param key
     * @return value值
     * @throws Exception
     * @author zhangyanhua
     * @date 2019年10月29日 下午4:35:44
     */
    public static String getEtcdValueByKey(String key) throws Exception {
        Client client = EtcdUtil.getEtclClient();
        GetResponse getResponse = client.getKVClient()
                .get(ByteSequence.from(key, StandardCharsets.UTF_8), GetOption.newBuilder().build()).get();
        client.close();
        etcdClient = null;

        // key does not exist
        if (getResponse.getKvs().isEmpty()) {
            return null;
        }

        return getResponse.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
    }

    /**
     * 删除指定的配置
     *
     * @param key
     * @throws InterruptedException
     * @throws ExecutionException
     * @author zhangyanhua
     * @date 2019年10月29日 下午4:53:24
     */
    public static void deleteEtcdValueByKey(String key) throws InterruptedException, ExecutionException {
        Client client = EtcdUtil.getEtclClient();
        client.getKVClient().delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
        //System.out.println("delete etcd key \"" + key + "\" success");
        client.close();
        etcdClient = null;
    }

    /**
     * 持续监控某个key变化的方法，执行后如果key有变化会被监控到，输入结果如下
     *  watch type= "PUT", key= "zyh1", value= "zyh1-value"
     *  watch type= "PUT", key= "zyh1", value= "zyh1-value111"
     *  watch type= "DELETE", key= "zyh1", value= ""
     *
     * @param key
     * @throws Exception
     * @author zhangyanhua
     * @date 2019年10月29日 下午5:26:09
     */
    public static void watchEtcdKey(String key) throws Exception {
        Client client = EtcdUtil.getEtclClient();
        // 最大事件数量
        Integer maxEvents = Integer.MAX_VALUE;
        CountDownLatch latch = new CountDownLatch(maxEvents);
        Watcher watcher = null;
        try {
            ByteSequence watchKey = ByteSequence.from(key, StandardCharsets.UTF_8);
            WatchOption watchOpts = WatchOption.newBuilder().build();

            watcher = client.getWatchClient().watch(watchKey, watchOpts, response -> {
                for (WatchEvent event : response.getEvents()) {
                    log.debug("watch type= \"" + event.getEventType().toString() + "\",  key= \""
                            + Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(StandardCharsets.UTF_8)).orElse("")
                            + "\",  value= \"" + Optional.ofNullable(event.getKeyValue().getValue())
                            .map(bs -> bs.toString(StandardCharsets.UTF_8)).orElse("")
                            + "\"");
                }

                latch.countDown();
            });

            latch.await();
        } catch (Exception e) {
            if (watcher != null) {
                watcher.close();
                client.close();
                etcdClient = null;
            }
            throw e;
        }
    }

    public static String lock(String redisKey, Long leaseId) throws ExecutionException, InterruptedException {
        long curTime = System.currentTimeMillis();
        CompletableFuture<LockResponse> feature = etcdClient.getLockClient().lock(ByteSequence.from(redisKey, StandardCharsets.UTF_8), leaseId);
        LockResponse lockResponse = feature.get();
        ByteSequence lockKey = lockResponse.getKey();
        long runTime=System.currentTimeMillis()-curTime;
        if(runTime>500L){
            log.info("----lock--time={}", runTime);
        }
        return new String(lockKey.getBytes());
    }

}