#OpenDFL demo nacos

# nacos文档见：<a href="https://spring.io/projects/spring-cloud-alibaba">spring-alibaba</a> <a href="https://nacos.io/zh-cn/docs/what-is-nacos.html">nacos</a>

##NACOS配置中心集成作用
支持频率限制配置参数统一下发。  
频率限制参数通过nacos动态修改，立即生效，灵活管理各项配置。  
比如：测试时把测试的IP加到黑名单，马上就不能访问了。

##NACOS配置
可以把/resources/的配置内容
* application-frequency.yml上传到nacos的demo-frequency.yml
* application-opendfl.yml上传到nacos的demo-opendfl.yml
* application-requestlock.yml上传到nacos的demo-requestlock.yml(locks有关的配置代码注释掉了，需要可以取消注释即可)
效果，如图：  
<img src="https://opendfl-1259373829.file.myqcloud.com/doc/nacos-config.png" width="80%" syt height="80%" />
