/*
 * Copyright 2017-2021 Dromara.org

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

package io.blueman.foreign.trade.accountb.mapper;

import io.blueman.foreign.trade.accountb.dto.AccountBDTO;
import io.blueman.foreign.trade.common.account.entity.AccountDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AccountMapper {


    @Update("update account set dollar_balance = dollar_balance - #{dollarAmount}," +
            " dollar_freeze_amount= dollar_freeze_amount + #{dollarAmount}, " +
            " rmb_balance = rmb_balance - #{rmbAmount}," +
            " rmb_freeze_amount= rmb_freeze_amount + #{rmbAmount}, " +
            " update_time = now()" +
            " where user_id =#{userId}  and  dollar_balance >= #{dollarAmount} " +
            " and  rmb_balance >= #{rmbAmount}  ")
    int update(AccountBDTO accountBDTO);



    @Update("update account set " +
            " dollar_freeze_amount= dollar_freeze_amount - #{dollarAmount}," +
            " rmb_freeze_amount= rmb_freeze_amount - #{rmbAmount}" +
            " where user_id =#{userId}  " +
            " and dollar_freeze_amount >= #{dollarAmount}  " +
            " and rmb_freeze_amount >= #{rmbAmount} ")
    int confirm(AccountBDTO accountBDTO);


    @Update("update account set " +
            " dollar_balance = dollar_balance + #{dollarAmount}," +
            " dollar_freeze_amount= dollar_freeze_amount - #{dollarAmount}, " +
            " rmb_balance = rmb_balance + #{rmbAmount}," +
            " rmb_freeze_amount= rmb_freeze_amount - #{rmbAmount} " +
            " where user_id =#{userId}  " +
            " and  dollar_freeze_amount >= #{dollarAmount} " +
            " and  rmb_freeze_amount >= #{rmbAmount}  ")
    int cancel(AccountBDTO accountBDTO);
    

    /**
     * 根据userId获取用户账户信息
     *
     * @param userId 用户id
     * @return AccountDO account do
     */
    @Select("select * from account where user_id =#{userId} limit 1")
    AccountDO findByUserId(String userId);
}
