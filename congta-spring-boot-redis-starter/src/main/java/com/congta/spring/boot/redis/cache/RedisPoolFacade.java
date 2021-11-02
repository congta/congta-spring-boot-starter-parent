package com.congta.spring.boot.redis.cache;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.StreamConsumersInfo;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamGroupInfo;
import redis.clients.jedis.StreamInfo;
import redis.clients.jedis.StreamPendingEntry;
import redis.clients.jedis.StreamPendingSummary;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.LPosParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XClaimParams;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.params.XTrimParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;
import redis.clients.jedis.resps.KeyedListElement;

/**
 * 这个工具是为了给 Jedis 和 JedisCluster 以统一的外观，
 * 因为测试环境使用 redislabs 提供的服务，是给自己挖了个坑，不支持 slots 命令，
 * 只有封装一个外观，将来才有切换成 cluster 模式的可能，
 * 使用 spring-data-redis 也是可以的，它会统一成 RedisTemplate，
 * 不过目前还没领会到用这个框架的好处。
 * Created by zhangfucheng on 2021/9/7.
 */
public class RedisPoolFacade implements RedisFacade {

    private JedisPool pool;

    public RedisPoolFacade(JedisPool pool) {
        this.pool = pool;
    }

    private <T> T withJedis(Function<Jedis, T> function) {
        try (Jedis jedis = pool.getResource()) {
            return function.apply(jedis);
        }
    }

    @Override
    public String set(String key, String value) {
        return withJedis(jedis -> jedis.set(key, value));
    }

    @Override
    public String set(String key, String value, SetParams params) {
        return withJedis(jedis -> jedis.set(key, value, params));
    }

    @Override
    public String get(String key) {
        return withJedis(jedis -> jedis.get(key));
    }

    @Override
    public String getDel(String key) {
        return withJedis(jedis -> jedis.getDel(key));
    }

    @Override
    public String getEx(String key, GetExParams params) {
        return withJedis(jedis -> jedis.getEx(key, params));
    }

    @Override
    public Boolean exists(String key) {
        return withJedis(jedis -> jedis.exists(key));
    }

    @Override
    public Long persist(String key) {
        return withJedis(jedis -> jedis.persist(key));
    }

    @Override
    public String type(String key) {
        return withJedis(jedis -> jedis.type(key));
    }

    @Override
    public byte[] dump(String key) {
        return withJedis(jedis -> jedis.dump(key));
    }

    @Override
    public String restore(String key, long ttl, byte[] serializedValue) {
        return withJedis(jedis -> jedis.restore(key, ttl, serializedValue));
    }

    @Override
    public String restoreReplace(String key, long ttl, byte[] serializedValue) {
        return withJedis(jedis -> jedis.restoreReplace(key, ttl, serializedValue));
    }

    @Override
    public String restore(String key, long ttl, byte[] serializedValue, RestoreParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long expire(String key, long seconds) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long pexpireAt(String key, long millisecondsTimestamp) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long ttl(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long pttl(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long touch(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Boolean getbit(String key, long offset) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String getSet(String key, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long setnx(String key, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String setex(String key, long seconds, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long decrBy(String key, long decrement) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long decr(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long incrBy(String key, long increment) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double incrByFloat(String key, double increment) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long incr(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long append(String key, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String substr(String key, int start, int end) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long hset(String key, String field, String value) {
        return withJedis(jedis -> jedis.hset(key, field, value));
    }

    @Override
    public Long hset(String key, Map<String, String> hash) {
        return withJedis(jedis -> jedis.hset(key, hash));
    }

    @Override
    public String hget(String key, String field) {
        return withJedis(jedis -> jedis.hget(key, field));
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Boolean hexists(String key, String field) {
        return withJedis(jedis -> jedis.hexists(key, field));
    }

    @Override
    public Long hdel(String key, String... field) {
        return withJedis(jedis -> jedis.hdel(key, field));
    }

    @Override
    public Long hlen(String key) {
        return withJedis(jedis -> jedis.hlen(key));
    }

    @Override
    public Set<String> hkeys(String key) {
        return withJedis(jedis -> jedis.hkeys(key));
    }

    @Override
    public List<String> hvals(String key) {
        return withJedis(jedis -> jedis.hvals(key));
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return withJedis(jedis -> jedis.hgetAll(key));
    }

    @Override
    public String hrandfield(String key) {
        return withJedis(jedis -> jedis.hrandfield(key));
    }

    @Override
    public List<String> hrandfield(String key, long count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Map<String, String> hrandfieldWithValues(String key, long count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long rpush(String key, String... string) {
        return withJedis(jedis -> jedis.rpush(key, string));
    }

    @Override
    public Long lpush(String key, String... string) {
        return withJedis(jedis -> jedis.lpush(key, string));
    }

    @Override
    public Long llen(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String lindex(String key, long index) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String lset(String key, long index, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long lrem(String key, long count, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String lpop(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> lpop(String key, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long lpos(String key, String element) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long lpos(String key, String element, LPosParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<Long> lpos(String key, String element, LPosParams params, long count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String rpop(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> rpop(String key, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long sadd(String key, String... member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> smembers(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long srem(String key, String... member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String spop(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> spop(String key, long count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long scard(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Boolean sismember(String key, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<Boolean> smismember(String key, String... members) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String srandmember(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> srandmember(String key, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long strlen(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zadd(String key, double score, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zadd(String key, double score, String member, ZAddParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double zaddIncr(String key, double score, String member, ZAddParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zrem(String key, String... members) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double zincrby(String key, double increment, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double zincrby(String key, double increment, String member, ZIncrByParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zrank(String key, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zrevrank(String key, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String zrandmember(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrandmember(String key, long count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrandmemberWithScores(String key, long count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zcard(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double zscore(String key, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<Double> zmscore(String key, String... members) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Tuple zpopmax(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zpopmax(String key, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Tuple zpopmin(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zpopmin(String key, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> sort(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zcount(String key, double min, double max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zcount(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zremrangeByRank(String key, long start, long stop) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zremrangeByScore(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long lpushx(String key, String... string) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long rpushx(String key, String... string) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public KeyedListElement blpop(double timeout, String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public KeyedListElement brpop(double timeout, String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long del(String key) {
        return withJedis(jedis -> jedis.del(key));
    }

    @Override
    public Long unlink(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String echo(String string) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long move(String key, int dbIndex) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long bitcount(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long bitpos(String key, boolean value) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long bitpos(String key, boolean value, BitPosParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long pfadd(String key, String... elements) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public long pfcount(String key) {
        return 0;
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long geoadd(String key, GeoAddParams params, Map<String, GeoCoordinate> memberCoordinateMap) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<String> geohash(String key, String... members) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoCoordinate> geopos(String key, String... members) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<Long> bitfieldReadonly(String key, String... arguments) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long hstrlen(String key, String field) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash, long maxLen, boolean approximateLength) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public StreamEntryID xadd(String key, Map<String, String> hash, XAddParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public Long xlen(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamEntry> xrange(String key, StreamEntryID start, StreamEntryID end) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamEntry> xrange(String key, StreamEntryID start, StreamEntryID end, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamEntry> xrevrange(String key, StreamEntryID end, StreamEntryID start) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamEntry> xrevrange(String key, StreamEntryID end, StreamEntryID start, int count) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public long xack(String key, String group, StreamEntryID... ids) {
        return 0;
    }

    @Override
    public String xgroupCreate(String key, String groupname, StreamEntryID id, boolean makeStream) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public String xgroupSetID(String key, String groupname, StreamEntryID id) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public long xgroupDestroy(String key, String groupname) {
        return 0;
    }

    @Override
    public Long xgroupDelConsumer(String key, String groupname, String consumername) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public StreamPendingSummary xpending(String key, String groupname) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamPendingEntry> xpending(String key, String groupname, StreamEntryID start, StreamEntryID end, int count, String consumername) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamPendingEntry> xpending(String key, String groupname, XPendingParams params) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public long xdel(String key, StreamEntryID... ids) {
        return 0;
    }

    @Override
    public long xtrim(String key, long maxLen, boolean approximate) {
        return 0;
    }

    @Override
    public long xtrim(String key, XTrimParams params) {
        return 0;
    }

    @Override
    public List<StreamEntry> xclaim(String key, String group, String consumername, long minIdleTime, long newIdleTime, int retries, boolean force, StreamEntryID... ids) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamEntry> xclaim(String key, String group, String consumername, long minIdleTime, XClaimParams params, StreamEntryID... ids) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamEntryID> xclaimJustId(String key, String group, String consumername, long minIdleTime, XClaimParams params, StreamEntryID... ids) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public StreamInfo xinfoStream(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamGroupInfo> xinfoGroup(String key) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }

    @Override
    public List<StreamConsumersInfo> xinfoConsumers(String key, String group) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "uncompleted function");
    }
}
