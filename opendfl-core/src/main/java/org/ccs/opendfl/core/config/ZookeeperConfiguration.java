package org.ccs.opendfl.core.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.ccs.opendfl.core.utils.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;


/**
 * 支持zookeeper
 *
 * @author chenjh
 */
@Configuration
@Slf4j
@Data
@Component
public class ZookeeperConfiguration {
    public ZookeeperConfiguration(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    private final ZookeeperProperties zookeeperProperties;

    @Bean(name = "zkClient")
    public ZooKeeper zkClient() {
        ZooKeeper zooKeeper = null;
        try {
            if (StringUtils.isBlank(zookeeperProperties.getAddress())) {
                log.warn("----zkClient--zookeeper.address={} empty disabled", zookeeperProperties.getAddress());
                return zooKeeper;
            }
            log.info("----zkClient--zookeeper.address={}", zookeeperProperties.getAddress());
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            zooKeeper = new ZooKeeper(zookeeperProperties.getAddress(), zookeeperProperties.getConnectionTimeoutMs(), new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState()) {
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            log.info("【初始化ZooKeeper连接状态....】={}", zooKeeper.getState());
        } catch (Exception e) {
            log.error("初始化ZooKeeper连接异常....】={}", zookeeperProperties.getAddress(), e);
        }
        return zooKeeper;
    }


    /**
     * Zookeeper开源客户端框架Curator简介
     * 参考文档: https://www.iteye.com/blog/macrochen-1366136
     *
     * @return CuratorFramework
     */
    @Bean
    public CuratorFramework curatorFramework() {
        if (StringUtils.isBlank(zookeeperProperties.getAddress())) {
            log.warn("----curatorFramework-zk-disabled");
            return null;
        }
        //配置zookeeper连接的重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(), zookeeperProperties.getMaxRetries());

        //构建 CuratorFramework
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString(zookeeperProperties.getAddress())
                        .sessionTimeoutMs(zookeeperProperties.getSessionTimeoutMs())
                        .connectionTimeoutMs(zookeeperProperties.getConnectionTimeoutMs())
                        .retryPolicy(retryPolicy)
                        .build();
        //连接 zookeeper
        curatorFramework.start();
        return curatorFramework;
    }

}


