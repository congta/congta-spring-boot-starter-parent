这里的项目大多都有官方的实现，不过个人不喜欢太过封装，
这里都浅浅包装一层，基本三四层调用就到原生 API 了。

## release

    mvn versions:set -DnewVersion=0.0.1-SNAPSHOT
    mvn clean deploy
    
## congta-spring-boot-xxx-starter

### redis

Use cache: 

    @Bean
    public CacheService redisCacheService(RedisFacade redisFacade) {
        return new RedisCacheService(redisFacade);
    }

Use redis:

    @Autowired
    private RedisFacade redisFacade;
    ...
    String value = redisFacade.get(key);

## mongodb

Use mongodb:

    @Service
    public class MyMapper extends MongoMapper<My> {
    
        public void doSth() {
            getTable(tableName).xxx();
        }
    }

## nacos

nacos 本身是有 spring-boot 版本的，但 springboot 2.4 以上有兼容性问题，
过去一年了到 0.2.10 仍未修复，详见 [issue-4554](https://github.com/alibaba/nacos/issues/4554)

本项目除了解决官方的问题，加上和个人开发环境息息相关的配置。用法：

1、 在 yml 中声明使用的环境：

    spring:
      nacos:
        name: mtus

2、 代码中使用：

    NacosClients.getClient().getConfig(dataId, group);

同时支持 spring-boot-nacos 的所有用法。如果使用其他 nacos 服务，需要在 yml 里配置全：

    spring:
      nacos:
        name: aaa  # 名字随便起
        namespace: 7bfbd3ad-b98c-44a9-a4ac-eb46b7ac9bc1  # nacos 的 namespaceId
        uri: 127.0.0.1:8848   # server addr
        username: nacos
        password: nacos

## 版本

* `0.0.1` - 第一版，支持 mongodb / nacos / redis / web
* `0.0.2` - 修复版，处理 mongo pool properties 为 null 的情况
