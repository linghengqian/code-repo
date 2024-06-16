package com.youyi.seataprovider;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {
    @Autowired
    AccountMapper accountMapper;

    @GetMapping("provide")
    @Transactional
    public String provide(Long id) {
        accountMapper.update(Wrappers.<Account>lambdaUpdate().eq(Account::getId, id).setIncrBy(Account::getMoney, 200));
        return "OK";
    }
}
