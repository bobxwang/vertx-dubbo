> vert.x 发布Http接口,收到数据访问后面的基于dubbo的服务,下载后不能直接跑,需要修改 **Configs** 类中的相关 zk 连接信息,同时就算改了,你在请求时因为后面的dubbo服务并不存在,所以也不跑起来,得连带修改,在这只是一个简单的模拟
>
> 你需要做的是:
> - 下载 [dubbo-ext](https://github.com/bobxwang/dubbo-ext) 扩展包,编译并打包到你本机的maven#repository 目录中

# facade 模块
> dubbo待发布接口

# webapi 模块 
> http服务, 同时实现dubbo接口并进行发布, 目前利用vert.x, 可方便转成自己熟悉的spring mvc模式或其它

#### 如何调用对方的服务
* 不强制在CLASSPATH中存在对方的jar包, 但要知道对方的服务签名, 构造出 **UniqueServiceDef** 类进行服务调用
* 如果有对方jar包在CLASSPTH中, 则可以用使用 **DubboProxy** 类来获得一个代理类进行服务调用 

#### 已有功能 
* get /api/gateway 返回目前支持的dubbo调用
* post /api/gateway 进行具体的dubbo调用,只有 get 请求中返回的那些东西才是你post需要的 

#### 注意点
* vert.x有自己的全套东西, 包括db/httpclient/redis全是异步,对基于spring等同步习惯了的人有点要求
* 通过service-proxy包装对dubbo的请求调用,所有对dubbo的请求发出都出自于 DubboServiceImpl#invoke 这个方法

