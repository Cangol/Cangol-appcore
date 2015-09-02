#Cangol-appcore

>这是一个Android 应用核心库，将应用的常用功能模块化，以便提高开发效率。
>[详细文档](http://cangol.github.io/Cangol-appcore)

##CoreApplication
>核心Application 提供整个库的初始化和一些方法，
[详细文档](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/CoreApplication.html)

##Session
>一个可以用来做缓存的集合，生命周期随Application，
[详细文档](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/Session.html)

##AppService
应用服务:实现方式为依赖注入，这是整个框架的核心内容

*   [AnalyticsService](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/analytics/AnalyticsService.html) 统计服务
* 	[CacheManager](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/cache/CacheManager.html) 缓存服务
* 	[CrashService](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/crash/CrashService.html)  异常处理服务
* 	[ConfigService](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/conf/ConfigService.html)  应用配置服务
* 	[DownloadManager](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/download/DownloadManager.html)  下载服务
* 	[GlobalData](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/global/GlobalData.html) 全局变量服务
* 	[LocationService](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/location/LocationService.html) 位置服务
* 	[StatusService](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/status/StatusService.html) 状态监听服务
* 	[UpgradeService](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/service/upgrade/UpgradeService.html) 更新服务

>使用方式
	
	//获取方式类似系统SystemService
	ConfigService configService = (ConfigService) getAppService(AppService.CONFIG_SERVICE);
	//可修改属性
	ServiceProperty p=configService.getServiceProperty();
	p.putString(ConfigService.APP_DIR, Constants.APP_DIR);
	p.putString(ConfigService.SHARED_NAME, Constants.SHARED);
			
##数据库ORM

* 实现数据库的orm
* 数据表的创建删除在无需SQL
* 对数据对象的CRUD均无需SQL语句，并支持复杂条件租车查询。

##日志Log
>封装并重写部分方法，是的日志输出更格式化，并提供可控制“开发”和”发布“模式的开关。
>[详细文档](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/logging/Log.html)

##数据解析
>利用注解和反射实现对XML和JSON对象化数据解析
 轻量级解析库，无需引入fastjson或gson，比这些更轻量级，而且同样支持xml的解析。
 
* [JsonUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/parser/JsonUtils.html)
* [XmlUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/parser/XmlUtils.html)

##安全模块
>提供三种方式的工具类

* [AESUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/security/AESUtils.html)
* [Base64](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/security/Base64.html)
* [RSAUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/security/RSAUtils.html)

###网络Http请求

>修正AsyncHttpClient的部分bug，优化请求参数配置，扩展超时自动重试机制，增加gzip的支持。并扩展：

* [PollingHttpClient](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/http/extras/PollingHttpClient.html) 可轮询请求的httpclient
* [RouteHttpClient](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/http/extras/RouteHttpClient.html) 可设置不同host的httpclient

###WebService请求
>使用此类需要ksoap2-android-assembly-3.0.0-jar-with-dependencies.jar 对Soap进行异步封装。[详细文档](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/soap/SoapClient.html)



###Utils工具类

*   [AppUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/AppUtils.html) 应用管理工具类
* 	[BitmapUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/BitmapUtils.html) 图像工具类
* 	[ClassUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/ClassUtils.html) Class工具类
* 	[DeviceInfo](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/DeviceInfo.html) 设备信息工具类
* 	[HanziToPinyin](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/HanziToPinyin.html) 汉字转拼音
* 	[LocationUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/LocationUtils.html) 位置工具类
* 	[Object2FileUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/Object2FileUtils.html) 对象文件互转工具类
* 	[StorageUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/StorageUtils.html) 存储工具类
* 	[StringUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/StringUtils.html) 字符串工具类
* 	[TimeUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/TimeUtils.html) 时间工具类
* 	[UrlUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/UrlUtils.html) URL工具类
* 	[ValidatorUtils](http://cangol.github.io/Cangol-appcore/mobi/cangol/mobile/utils/ValidatorUtils.html) 验证工具类

License
-----------

    Copyright 2012 Cangol

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

