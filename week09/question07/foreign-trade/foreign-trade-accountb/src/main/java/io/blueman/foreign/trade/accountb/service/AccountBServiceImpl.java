package io.blueman.foreign.trade.accountb.service;

import com.alibaba.fastjson.JSON;
import io.blueman.foreign.trade.accountb.dto.AccountBDTO;
import io.blueman.foreign.trade.accountb.mapper.AccountMapper;
import io.blueman.foreign.trade.common.account.api.AccountAService;
import io.blueman.foreign.trade.common.account.api.AccountBService;
import io.blueman.foreign.trade.common.account.dto.ExchangeDTO;
import io.blueman.foreign.trade.common.account.entity.AccountDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service(value = "accountBService")
@Slf4j
@RequiredArgsConstructor
public class AccountBServiceImpl implements AccountBService {

    private final AccountMapper accountMapper;
    private final AccountAService serviceA;

    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean exchangeDollar(AccountBDTO accountBDTO, String from) {
        ExchangeDTO exchangeDTO = parse(accountBDTO, from);
        accountBDTO.setUserId("user_b");
        int update = accountMapper.update(accountBDTO);
        if (update <= 0) {
            throw new HmilyRuntimeException("账户扣减异常！");
        } else {
            return serviceA.exchangeDollar(exchangeDTO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(AccountBDTO accountBDTO, String from) {
        log.info("============dubbo tcc 执行确认兑换USD接口===============");
        return confirm(accountBDTO);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(AccountBDTO accountBDTO, String from) {
        log.info("============ dubbo tcc 执行取消兑换USD接口===============");
        return cancel(accountBDTO);
    }

    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean exchangeRMB(ExchangeDTO exchangeDTO) {
        AccountBDTO accountBDTO = parse(exchangeDTO);
        int update = accountMapper.update(accountBDTO);
        if (update <= 0) {
            throw new HmilyRuntimeException("账户扣减异常！");
        } else {
            return Boolean.TRUE;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(ExchangeDTO exchangeDTO) {
        log.info("============dubbo tcc 执行确认兑换RMB接口===============");
        AccountBDTO accountBDTO = parse(exchangeDTO);
        return confirm(accountBDTO);
    }

    /**
     * Cancel boolean.
     *
     * @param exchangeDTO the account dto
     * @return the boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(ExchangeDTO exchangeDTO) {
        log.info("============ dubbo tcc 执行取消兑换RMB接口===============");
        final AccountDO accountDO = accountMapper.findByUserId(exchangeDTO.getUserId());
        AccountBDTO accountBDTO = parse(exchangeDTO);
        return cancel(accountBDTO);
    }



    @Override
    public AccountDO findByUserId(String userId) {
        return accountMapper.findByUserId(userId);
    }


    private boolean confirm(AccountBDTO accountBDTO) {
        accountMapper.confirm(accountBDTO);
        return Boolean.TRUE;
    }


    private boolean cancel(AccountBDTO accountBDTO) {
        accountMapper.cancel(accountBDTO);
        return Boolean.TRUE;
    }

    private AccountBDTO parse(ExchangeDTO exchangeDTO) {
        AccountBDTO accountBDTO = new AccountBDTO();
        accountBDTO.setUserId(exchangeDTO.getUserId());

        BigDecimal rmbAmount = exchangeDTO.getAmount().multiply(BigDecimal.valueOf(7));
        try {
            rmbAmount.toBigIntegerExact();
        } catch (ArithmeticException e) {
            throw new HmilyRuntimeException("数学不太好，不会换零钱");
        }
        accountBDTO.setDollarAmount(exchangeDTO.getAmount().negate());
        accountBDTO.setRmbAmount(rmbAmount);
        return accountBDTO;
    }

    private ExchangeDTO parse(AccountBDTO accountBDTO, String from) {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setUserId(from);
        exchangeDTO.setAmount(accountBDTO.getRmbAmount());
        return exchangeDTO;
    }

}
