package vn.hoangshitposting.gapgapticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyingQueueService {

    private static final String QUEUE_KEY = "buying:queue";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void enqueueUser(String sessionId) {
        stringRedisTemplate.opsForList().rightPush(QUEUE_KEY, sessionId);
    }

    public String peekFirstInQueue() {
        return stringRedisTemplate.opsForList().index(QUEUE_KEY, 0);
    }

    public void dequeueUser() {
        stringRedisTemplate.opsForList().leftPop(QUEUE_KEY);
    }

    public long getPosition(String sessionId) {
        List<String> all = stringRedisTemplate.opsForList().range(QUEUE_KEY, 0, -1);
        return all != null ? all.indexOf(sessionId) + 1 : -1;
    }
}
