package com.youyi.seataprovider;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SeataProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataProviderApplication.class, args);
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

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection", "unused", "NumericOverflow", "divzero"})
@RestController
class ProviderController {

    private final AccountMapper accountMapper;

    ProviderController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Transactional
    @GetMapping("provide/{id}")
    public String provide(@PathVariable("id") Long id) {
        accountMapper.update(Wrappers.<Account>lambdaUpdate()
                .eq(Account::getId, id)
                .setIncrBy(Account::getMoney, 200.0));
        return "OK";
    }

    @Transactional
    @GetMapping("provide_with_error/{id}")
    public String provideWithError(@PathVariable("id") Long id) {
        accountMapper.update(Wrappers.<Account>lambdaUpdate()
                .eq(Account::getId, id)
                .setIncrBy(Account::getMoney, 200.0));
        long i = 10 / 0;
        return "OK";
    }
}