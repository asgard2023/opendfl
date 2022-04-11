# OpenDFL Open Distributed frequency limitation 
* Springboot,Springcloud分布式访问频率限制
* Distributed locks分布式锁
* 基于Redis
* Spring boot, spring cloud，springmvc
* 支持基于uri的限制，配置允许动态修改
* 核心代码采用责任链模式，功能扩展性好
* 支持功能可配置，不需要的功能可以关闭

## 功能特性
* 支持分布式锁，同一个数据ID，同一时段内只能有一个访问
* 支持IP白名单，用户白名单
* 支持IP黑名单，用户黑名单
* 支持用户访问次数限制，取不到用户，自动按用户IP限制（跨IP都算）
* 支持单IP访问次数限制（跨用户都算）
* 支持单用户IP数限制
* 支持单IP用户数限制
* 支持限制配置可见性，方便查看所有已配置的限制
* 支持接口调用次数统计，30秒最大执行时长统计
* 支持设备号deviceId黑名单限制
* 支持通过验证码重置用户单个功能限制
* 支持通过验证码重置用户所有限制
* 支持性能监控接口，及查询(每30秒内记录执行时间最大的接口)


## 简单使用：

1，分布式交易锁，@RequestLock注解，并支持通过yml动态修改锁时长
 ```java
@GetMapping("/waitLockTest")
@ResponseBody
@RequestLock(name = "waitLockTest", time=5, errMsg = "任务%s正在执行")
 ```

2，分布式频率限制，@Frequency注解模式，并支持通过yml动态修改频率限制次数
```java
/**
 * 5秒限5次
 * 1小时限100次
 */
@GetMapping("/serverTimeFreq")
@ResponseBody
@Frequency(time = 5, limit = 5, name = "serverTimeFreq")
@Frequency2(time =3600, limit = 100, name = "serverTimeFreq")
public Long serverTimeFreq(HttpServletRequest request){
    return System.currentTimeMillis();
}
```
动态修改限制，见application-frequency.yml
```yaml
limit:
  frequencyConfigs:
    - name: serverTimeFreq
      time: 5
      limit: 10 #改成新值
    - name: serverTimeFreq
      time: 3600
      limit: 50 #改成新值
```

3，分布式频频率限制，配置模式，见application-frequency.yml
```yaml
limit:
  uriConfigs:
      - uri: /frequencyTest/serverTimeUri #依赖接口uri
        time: 5
        limit: 8
        attrName: account
      - uri: /frequencyTest/serverTimeUri
        time: 3600
        limit: 30
        userIp: 7
        ipUser: 7
        attrName: account
```

4，有控制台能方便查看接口信息，以及对应的限制

示例见：http://175.178.252.112:8080/index.html

默认账号：
* admin/admin
* user/user