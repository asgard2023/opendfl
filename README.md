# OpenDFL Open Distributed frequency limitation 
* Springboot,Springcloud分布式访问频率限制
* Distributed locks分布式锁
* 基于Redis
* Spring boot, spring cloud，springmvc
* 支持基于uri的限制，配置允许动态修改
* 核心代码采用责任链模式，功能扩展性好
* 支持可功能可配置，不需要的功能可以关闭

Example示例 http://175.178.252.112:8080/index.html

##简单使用：

1，分布式交易锁，@RequestLock注解
 ```java
@GetMapping("/waitLockTest")
@ResponseBody
@RequestLock(name = "waitLockTest", time=5, errMsg = "任务%s正在执行")
 ```

2，分布式频率限制，@Frequency注解模式，并支持通过yml修改频率限制次数
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

3，分布式频频率限制，配置模式，见application-frequency.yml
```yaml
limit:
  uriConfigs:
      - uri: /frequencyTest/serverTimeUri
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

4,有控制台能方便查看接口信息，以及对应的限制

示例见：http://175.178.252.112:8080/index.html
