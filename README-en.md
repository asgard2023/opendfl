# OpenDFL Open Distributed frequency limitation 
* Springboot, Springcloud distributed access frequency limit
* Distributed locks Distributed locks
* Based on Redis
* Spring boot, spring cloud, springmvc
* Support uri-based restrictions, dynamic configuration modification
* The core code adopts the responsibility chain mode, and the function has good scalability
* Support function can be configured, and unnecessary functions can be turned off

## Features
* Support distributed lock, the same data ID can only have one access in the same period
* Support IP whitelist, user whitelist
* Support IP blacklist, user blacklist
* Support user access limit, if can not get user, automatically limit by user IP (cross-IP count)
* Support single IP access limit (count across users)
* Support single-user IP count limit
* Support single IP user count limit
* Supports restriction configuration visibility for easy to view.
* Support statistics on the count of interface calls, and statistics on the maximum execution time of 30 seconds
* Support device ID blacklist restriction
* Support to reset user's single function limit through verification code
* Support reset all user restrictions by verification code
* Support performance monitoring interface and query

## Full road map
## Full road map(<img src="https://opendfl-1259373829.file.myqcloud.com/doc/ok.webp" width="6%" syt height="6%" /> items are completed, The items in red are planned)
<img src="https://opendfl-1259373829.file.myqcloud.com/doc/opendfl_roadmap2.png" width="80%" syt height="80%" />


## Simple to use:

1. Distributed transaction lock, @RequestLock annotation, and supports dynamic modification of lock duration through yml
 ```java
@GetMapping("/waitLockTest")
@ResponseBody
@RequestLock(name = "waitLockTest", time=5, errMsg = "Task %s is running")
 ```

2. Distributed frequency limit, @Frequency annotation mode, and supports dynamic modification of frequency limit times through yml
```java
/**
 * 5 seconds in 5 times
 * 100 times per hour
 */
@GetMapping("/serverTimeFreq")
@ResponseBody
@Frequency(time = 5, limit = 5, name = "serverTimeFreq")
@Frequency2(time =3600, limit = 100, name = "serverTimeFreq")
public Long serverTimeFreq(HttpServletRequest request){
    return System.currentTimeMillis();
}
```
Dynamic modification limit, see application-frequency.yml
```yaml
limit:
  frequencyConfigs:
    - name: serverTimeFreq
      time: 5
      limit: 10
    - name: serverTimeFreq
      time: 3600
      limit: 50
```

3. Distributed frequency limit, configuration mode, see application-frequency.yml
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

4. There is a console to easily view interface information and corresponding restrictions

For example see: http://175.178.252.112:8080/index.html

Default account:
* admin/admin
* user/user