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

所以继续我的风格，非常浅的包装一层。