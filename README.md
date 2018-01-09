# facade 模块
> dubbo待发布接口

# webapi 模块 
> http服务, 同时实现dubbo接口并进行发布, 目前利用vert.x, 可方便转成自己熟悉的spring mvc模式或其它

#### 如何调用对方的服务
* 不强制在CLASSPATH中存在对方的jar包, 但要知道对方的服务签名, 构造出 **UniqueServiceDef** 类进行服务调用
* 如果有对方jar包在CLASSPTH中, 则可以用使用 **DubboProxy** 类来获得一个代理类进行服务调用 

#### 已有功能 
* get /api/gateway 返回目前支持的dubbo调用
* post /api/gateway 进行具体的dubbo调用

#### 注意点
* vert.x有自己的全套东西, 包括db/httpclient/redis全是异步,对基于spring等同步习惯了的人有点要求

