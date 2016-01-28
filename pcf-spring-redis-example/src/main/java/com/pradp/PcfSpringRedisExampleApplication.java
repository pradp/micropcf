package com.pradp;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PcfSpringRedisExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcfSpringRedisExampleApplication.class, args);
    }
}

@RestController
class RedisController {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, byte[]> template;

    @RequestMapping(path = "/cache", method = RequestMethod.POST)
    public void addToCache(@RequestParam String transactionId, @RequestParam String field,
            @RequestParam String value) {
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.opsForHash().put(transactionId, field, value);
        template.expire(transactionId, 30, TimeUnit.MINUTES);
    }
    
    @RequestMapping(path = "/cache", method = RequestMethod.GET)
    public Object getFromCache(@RequestParam String transactionId, @RequestParam String field) {
        template.expire(transactionId, 30, TimeUnit.MINUTES);
        return template.opsForHash().get(transactionId, field);
    }
    
    @RequestMapping(path = "/cache/exists", method = RequestMethod.GET)
    public boolean isKeyExists(@RequestParam String key, @RequestParam String field) {
        return template.opsForHash().hasKey(key, field);
    }

}
