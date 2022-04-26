package org.ccs.opendfl.core;

import com.google.common.base.Charsets;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lock.LockResponse;
import org.ccs.opendfl.core.utils.EtcdUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class EtcdUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(EtcdUtilTest.class);
    @Autowired
    private Client etcdClient = null;
    private long grantLease(long ttl) throws Exception {
        CompletableFuture<LeaseGrantResponse> feature = etcdClient.getLeaseClient().grant(ttl);
        LeaseGrantResponse response = feature.get();
        return response.getID();
    }
    @Test
    public void testLock() throws Exception  {
        long time=System.currentTimeMillis();
        ByteSequence SAMPLE_NAME= ByteSequence.from("sample_name", Charsets.UTF_8);
        Lock lockClient = etcdClient.getLockClient();
        long lease = grantLease(5);
        CompletableFuture<LockResponse> feature = lockClient.lock(SAMPLE_NAME, lease);
        LockResponse response = feature.get();

        long startMillis = System.currentTimeMillis();

        CompletableFuture<LockResponse> feature2 = lockClient.lock(SAMPLE_NAME, 0);
        LockResponse response2 = feature2.get();

        long runTime = System.currentTimeMillis() - startMillis;


        assertThat(response2.getKey()).isNotEqualTo(response.getKey());
        assertThat(time >= 4500 && time <= 6000)
                .withFailMessage(String.format("Lease not runned out after 5000ms, was %dms", runTime)).isTrue();
        Set<ByteSequence> locksToRelease=new HashSet<>();
        locksToRelease.add(response.getKey());
        locksToRelease.add(response2.getKey());
        for (ByteSequence lockKey : locksToRelease) {
            lockClient.unlock(lockKey).get();
        }
        logger.info("----runTime={} time={}", runTime, System.currentTimeMillis()-time);

    }

    @Test
    public void testLockAndUnlock() throws Exception {
        ByteSequence SAMPLE_NAME= ByteSequence.from("sample_name2", Charsets.UTF_8);
        Lock lockClient = etcdClient.getLockClient();
        long time=System.currentTimeMillis();
        long lease = grantLease(20);
        logger.info("----time0={}", System.currentTimeMillis()-time);
        CompletableFuture<LockResponse> feature = lockClient.lock(SAMPLE_NAME, lease);
        LockResponse response = feature.get();
        logger.info("----time1={}", System.currentTimeMillis()-time);
        System.out.println(response.getKey());

        lockClient.unlock(response.getKey()).get();
        logger.info("----time2={}", System.currentTimeMillis()-time);

        long startTime = System.currentTimeMillis();

        CompletableFuture<LockResponse> feature2 = lockClient.lock(SAMPLE_NAME, 0);
        logger.info("----time3={}", System.currentTimeMillis()-time);
        LockResponse response2 = feature2.get();
        System.out.println(response2.getKey());
        long runTime = System.currentTimeMillis() - startTime;

        Set<ByteSequence> locksToRelease=new HashSet<>();
        locksToRelease.add(response2.getKey());
        for (ByteSequence lockKey : locksToRelease) {
//            lockClient.unlock(lockKey).get();
        }

        assertThat(response2.getKey()).isNotEqualTo(response.getKey());
        assertThat(runTime <= 500).withFailMessage(String.format("Lease not unlocked, wait time was too long (%dms)", runTime))
                .isTrue();
        logger.info("----time={}", System.currentTimeMillis()-time);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testEtcd() throws Exception {
        boolean success = true;

        String key = "zyh";
        String value = "zyh-value";
        String newValue = "zyh-value-new";

        System.out.println("**** 测试方法开始 ****");
        EtcdUtil.putEtcdValueByKey(key, value);
        String retValue = EtcdUtil.getEtcdValueByKey(key);
        // System.out.println("查询key " + key + " 对应的值是 " + retValue);
        if (value.equals(retValue)) {
            System.out.println("数据插入成功。");
            System.out.println("数据查询成功。");
        } else {
            success = false;
            System.out.println("数据插入或查询失败！");
        }

        EtcdUtil.putEtcdValueByKey(key, newValue);
        retValue = EtcdUtil.getEtcdValueByKey(key);
        // System.out.println("查询key " + key + " 对应的值是 " + retValue);
        if (newValue.equals(retValue)) {
            System.out.println("数据更新成功。");
        } else {
            success = false;
            System.out.println("数据更新失败！");
        }

        EtcdUtil.deleteEtcdValueByKey(key);
        retValue = EtcdUtil.getEtcdValueByKey(key);
        // System.out.println("查询key " + key + " 对应的值是 " + retValue);
        if (retValue == null) {
            System.out.println("数据删除成功。");
        } else {
            success = false;
            System.out.println("数据删除失败！");
        }

        // EtcdUtil.watchEtcdKey(key);

        if (success) {
            System.out.println("**** 测试方法全部通过。 ****");
        } else {
            System.out.println("**** 测试失败！ ****");
        }
    }
}
