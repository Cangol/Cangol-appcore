#Cangol-appcore

>这是一个Android 应用核心库，将应用的常用功能模块化，以便提高开发效率。
>[详细文档JavaDoc](http://cangol.github.io/Cangol-appcore)

##CoreApplication
>核心Application 提供整个哭的初始化和一些方法

##Session
>一个可以用来做缓存的集合，生命周期随Application

##AppService
>应用服务:实现方式为依赖注入，这是整个框架的核心内容

	AnalyticsService统计服务
	CacheService 缓存服务
	CrashService 异常处理服务
	ConfigService 应用配置服务
	DownloadService 下载服务
	GlobalService 全局变量服务
	LocationService 地理位置服务
	StatusService 状态服务
	UpgradeService 更新服务

>使用方式
	
	//获取方式类似系统SysService
	ConfigService configService = (ConfigService) getAppService(AppService.CONFIG_SERVICE);
	//可修改属性
	ServiceProperty p=configService.getServiceProperty();
	p.putString(ConfigService.APP_DIR, Constants.APP_DIR);
	p.putString(ConfigService.SHARED_NAME, Constants.SHARED);
			
##ORM数据库模块

1. 实现数据库的orm
2. 数据表的创建删除在无需SQL
3. 对数据对象的CRUD均无需SQL语句，并支持复杂条件租车查询。

##日志Log
>封装并重写部分方法，是的日志输出更格式化，并提供可控制“开发”和”发布“模式的开关。

##数据解析
>利用注解和反射实现对XML和JSON对象化数据解析
 轻量级解析库，无需引入fastjson或gson，比这些更轻量级，而且同样支持xml的解析。

##安全模块
>提供AES Base64 RSA 三种方式的工具类

###网络Http请求

>修正AsyncHttpClient的部分bug，优化请求参数配置，扩展超时自动重置机制，增加Gzip的支持。并扩展：

	PollingHttpClient 可轮询请求的httpclient
	RouteHttpClient 可设置不同host的httpclient
###WebService请求
>使用此类需要ksoap2-android-assembly-3.0.0-jar-with-dependencies.jar 对Soap进行异步封装。



###Utils工具类

	AppUtils 应用管理工具类
	BitmapUtils 图像工具类
	ClassUtils Class工具类
	DeviceInfo 设备信息工具类
	HanziToPinyin 汉子转拼音
	LocationUtils 位置工具类
	Object2FileUtils 对象文件互转工具类
	StorageUtils 存储工具类
	StringUtils 字符串工具类
	TimeUtils 时间工具类
	UrlUtils URL工具类
	ValidateUtils 验证工具类

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

