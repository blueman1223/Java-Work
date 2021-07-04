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

package io.blueman.foreign.trade.common.account.api;

import io.blueman.foreign.trade.common.account.dto.ExchangeDTO;
import io.blueman.foreign.trade.common.account.entity.AccountDO;

/**
 * The interface Account service.
 *
 * @author xiaoyu
 */
public interface AccountAService {

    /**
     * 使用人名币兑换A账户的美元
     */
    boolean exchangeDollar(ExchangeDTO exchangeDTO);

    /**
     * 获取用户账户信息
     *
     * @param userId 用户id
     * @return AccountDO account do
     */
    AccountDO findByUserId(String userId);
}
