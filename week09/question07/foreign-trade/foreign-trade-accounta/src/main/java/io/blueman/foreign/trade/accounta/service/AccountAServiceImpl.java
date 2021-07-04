/*
 * Copyright 2017-2021 Dromara.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.blueman.foreign.trade.accounta.service;

import io.blueman.foreign.trade.accounta.dto.AccountADTO;
import io.blueman.foreign.trade.accounta.mapper.AccountMapper;
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

import java.math.BigDecimal;

@Service("accountAService")
@Slf4j
@RequiredArgsConstructor
public class AccountAServiceImpl implements AccountAService {

    private final AccountMapper accountMapper;
    private final AccountBService serviceB;


    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean exchangeDollar(ExchangeDTO exchangeDTO) {
        AccountADTO accountADTO = parse(exchangeDTO);
        int update = accountMapper.update(accountADTO);
        if (update > 0) {
            return true;
        } else {
            throw new HmilyRuntimeException("账户扣减异常！");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(ExchangeDTO exchangeDTO) {
        log.info("============dubbo tcc 执行确认兑换USD接口===============");
        AccountADTO accountADTO = parse(exchangeDTO);
        return confirm(accountADTO);
    }

    /**
     * Cancel boolean.
     *
     * @param exchangeDTO the account dto
     * @return the boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(ExchangeDTO exchangeDTO) {
        log.info("============ dubbo tcc 执行取消兑换USD接口===============");
        final AccountDO accountDO = accountMapper.findByUserId(exchangeDTO.getUserId());
        AccountADTO accountADTO = parse(exchangeDTO);
        return cancel(accountADTO);
    }

    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean exchangeRMB(AccountADTO accountADTO, String from) {
        ExchangeDTO exchangeDTO = parse(accountADTO, from);
        int update = accountMapper.update(accountADTO);
        if (update <= 0) {
            throw new HmilyRuntimeException("账户扣减异常！");
        } else {
            return serviceB.exchangeRMB(exchangeDTO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(AccountADTO accountADTO, String from) {
        log.info("============dubbo tcc 执行确认兑换RMB接口===============");
        return confirm(accountADTO);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(AccountADTO accountADTO, String from) {
        log.info("============ dubbo tcc 执行取消兑换RMB接口===============");
        return cancel(accountADTO);
    }



    @Override
    public AccountDO findByUserId(String userId) {
        return accountMapper.findByUserId(userId);
    }


    private boolean confirm(AccountADTO accountADTO) {
        accountMapper.confirm(accountADTO);
        return Boolean.TRUE;
    }


    private boolean cancel(AccountADTO accountADTO) {
        accountMapper.cancel(accountADTO);
        return Boolean.TRUE;
    }

    private AccountADTO parse(ExchangeDTO exchangeDTO) {
        AccountADTO accountADTO = new AccountADTO();
        accountADTO.setUserId(exchangeDTO.getUserId());

        BigDecimal dollarAmount = exchangeDTO.getAmount().divide(BigDecimal.valueOf(7));
        try {
            dollarAmount.toBigIntegerExact();
        } catch (ArithmeticException e) {
            throw new HmilyRuntimeException("数学不太好，不会换零钱");
        }
        accountADTO.setDollarAmount(dollarAmount);
        accountADTO.setRmbAmount(exchangeDTO.getAmount().negate());
        return accountADTO;
    }

    private ExchangeDTO parse(AccountADTO accountADTO, String from) {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setUserId(from);
        exchangeDTO.setAmount(accountADTO.getDollarAmount());
        return exchangeDTO;
    }

}
