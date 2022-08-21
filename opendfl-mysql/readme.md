#OpenDFL console for mysql

## Quick Start
http://localhost:8080/index.html  
* admin/admin  
* user/user

#模块说明
核心功能与console一样，增加了mysql的支持，相关配置（权限，频率限制，分布式锁）的动态可修改，同时支持把超限信息写入mysql，以及超限doc日志统计。

##功能特性
* 支持频率限制
* 分布式锁管理  
* 支持动态策略，动态规则，可随时新增或修改（缓存10秒，改后10秒生效）  
* 支持不仅限于Mysql，因为没用到mysql函数,存储过程，只是简单的增删改查，同时支持Oracle,PostgreSql等关系型数据库(只需改数据源及驱动即可)

##支持超限日志  
以异步写数据库方式，在服务重启时，可能丢日志  
理论上超限日志没那么重要，允许部份丢失