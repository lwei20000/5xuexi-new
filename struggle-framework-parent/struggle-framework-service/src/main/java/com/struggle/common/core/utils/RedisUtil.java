package com.struggle.common.core.utils;

import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Async("myAsyncExecutor")
    public void set(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    @Async("myAsyncExecutor")
    public void set(String key, String value, Long expireTime){
        stringRedisTemplate.opsForValue().set(key,value,expireTime, TimeUnit.SECONDS);
    }

    @Async("myAsyncExecutor")
    public void del(String key){
        stringRedisTemplate.delete(key);
    }

    /**
     * keys 如果很大，需要用scan
     * @param keys
     */
    @Async("myAsyncExecutor")
    public void dels(List<String> keys){
        stringRedisTemplate.delete(keys);
    }

    /**
     * count是每次扫描的key个数，并不是结果集个数。
     * count要根据扫描数据量大小而定，Scan虽然无锁，但是也不能保证在超过百万数据量级别搜索效率；
     * count不能太小，网络交互会变多，count要尽可能的大。
     * 在搜索结果集1万以内，建议直接设置为与所搜集大小相同
     *
     * @param key
     * @param prefix
     */
    @Async("myAsyncExecutor")
    public void dels(String key,boolean prefix){
        try {
            if(prefix){
                key = "*"+key;
            }else{
                key = key+"*";
            }

            Cursor<String> cursor = this.scan(key, 1000);
            Iterator<String> iterator = cursor.stream().iterator();
            Set<String> keys = new HashSet<>();
            while (iterator.hasNext()){
                keys.add(iterator.next());
                if(keys.size()==500){
                    stringRedisTemplate.delete(keys);
                    keys = new HashSet<>();
                }
            }
            if(!CollectionUtils.isEmpty(keys)){
                stringRedisTemplate.delete(keys);
            }
//            cursor.forEachRemaining(_key -> {
//                stringRedisTemplate.delete(_key);
//            });
            //关闭cursor
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key,Class<T> c){
        String s = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.hasText(s)){
            return JSONUtil.parseObject(s,c);
        }
        return null;
    }

    public <T> List<T> getList(String key,Class<T> c){
        String s = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.hasText(s)){
            return JSONUtil.stringToList(s,c);
        }
        return null;
    }

    private  Cursor<String> scan(String match, int count){
        ScanOptions scanOptions = ScanOptions.scanOptions().match(match).count(count).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) stringRedisTemplate.getKeySerializer();
        return (Cursor) stringRedisTemplate.executeWithStickyConnection((RedisCallback) redisConnection ->
                new ConvertingCursor<>(redisConnection.scan(scanOptions), redisSerializer::deserialize));
    }
}
