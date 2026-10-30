package in.cper.url_shortener_service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTest {

    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void test() {
        redisTemplate.opsForValue().set("test:key", "Redis Connected");

        System.out.println(
                redisTemplate.opsForValue().get("test:key")
        );
    }
}
