package com.youyi.seataconsumer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@SpringBootApplication
public class SeataConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataConsumerApplication.class, args);
    }
}

@Data
class Account {
    private Long id;
    private Double money;
}

@Mapper
interface AccountMapper extends BaseMapper<Account> {
}

@RestController
class ConsumerController {
    private final AccountMapper accountMapper;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    public ConsumerController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @SuppressWarnings({"NumericOverflow", "divzero", "unused"})
    @GetMapping("consume/{id}/{provider_server_port}")
    @Transactional
    public String consume(@PathVariable("id") Long id, @PathVariable("provider_server_port") Integer providerServerPort) {
        accountMapper.update(Wrappers.<Account>lambdaUpdate()
                .eq(Account::getId, id)
                .setIncrBy(Account::getMoney, 200));
        ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:" + providerServerPort + "/provide/" + id, String.class);
        // 模拟异常
        long i = 10 / 0;
        return "OK" + Objects.requireNonNullElse(result.getBody(), 0);
    }
}
