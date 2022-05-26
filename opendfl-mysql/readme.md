#OpenDFL console for mysql

## Quick Start
http://localhost:8080/index.html  
* admin/admin  
* user/user

#模块说明
核心功能与console一样，增加了mysql的支持，相关配置（权限，频率限制，分布式锁）的动态可修改，同时支持把超限信息写入mysql，以及超限doc日志统计。

##功能特性
支持用户管理，角色权限管理  
支持频率限制，分布式锁管理  
支持规则的即时增加与修改（缓存10秒，改后10秒生效）

支持超限日志  
以异步写数据库方式，在服务重启时，可能丢日志  
理论上超限日志没那么重要，允许部份丢失