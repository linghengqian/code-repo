package com.youyi.seataconsumer;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@RestController
public class ConsumerController {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("consume")
    @Transactional
    public String consume(Long id) {
        accountMapper.update(Wrappers.<Account>lambdaUpdate().eq(Account::getId, id).setIncrBy(Account::getMoney, 200));
        ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:8081/provide?id={id}", String.class, Map.of("id", id + 1));
        // 模拟异常
        long i = 10 / 0;
        return "OK" + Objects.requireNonNullElse(result.getBody(), 0);
    }

}
