package io.blueman.foreign.trade.accounta.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountADTO implements Serializable {
    private static final long serialVersionUID = 7223470850578998427L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 美元消费金额 可为负数
     */
    private BigDecimal dollarAmount;

    /**
     * 人民币消费金额 可为负数
     */
    private BigDecimal rmbAmount;
}
