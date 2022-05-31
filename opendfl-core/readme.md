# 基于Redis的频率限制
 opendfl-core可以单独使用，缺点是没有console不方便查状态，查配置
 集成opendfl-console可以查配置中心的配置，并通过配置中心修改  
 集成Opendfl-mysql可以动态修改，直接在功能上修改
 推荐集成方式，一般服务用opendfl-core，查询服务用opendfl-console或opendfl-mysql
##1，支持RequestLock注解
* 查看所有请求过的uri统计，见http://localhost:8080/frequency/locks?pwd=xxx
* 对应的数据attrName属性具有全局唯一，如果本ID有请求未完成，后续同样的ID请求全部拒绝
* 支持Redis,Zookeeper,ETCD
##2，支持基于Frequency注解的频率限制
* 支持3级频率限制@Frequency,@Frequency2,@Frequency3
* 查看所有请求过的uri统计，见http://localhost:8080/frequency/limits?pwd=xxx
* 基于本注解的频率限制后就不支持基于uri的频率限制
##3，支持基于uri的频率限制
* 基于uri的频率限制
* 查看所有请求过的uri统计，见http://localhost:8080/frequency/requests?pwd=xxx
* 通过config的配置文件

#限制类型
###用户访问限制
开启的功能时段内(各功能各自算)，该功能单用户访问次数限制
###IP访问限制
开启的功能时段内(各功能各自算)，该功能单IP访问次数限制
###用户IP数限制
* 开启的所有功能共享（默认），一个用户最多允许的IP个数(不看访问次数，只看IP数)，超出后报频率限制。
* 可开启区分IP功能，则每个功能的用户IP数各自算
###IP用户数限制
* 开启的所有功能共享（默认），一个IP最多允许的用户个数(不看访问次数，只看用户数)，超出后报频率限制。
* 可开启区分IP功能，则每个功能的IP用户数各自算