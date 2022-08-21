#分布式锁模块
##支持RequestLock注解
* 查看所有请求过的uri统计，见http://localhost:8080/frequency/locks?pwd=xxx
* 对应的数据attrName属性具有全局唯一，如果本ID有请求未完成，后续同样的ID请求全部拒绝
* 支持Redis,Zookeeper,ETCD