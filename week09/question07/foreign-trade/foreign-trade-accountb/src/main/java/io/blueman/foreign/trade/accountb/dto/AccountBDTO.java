package io.blueman.foreign.trade.accountb.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountBDTO implements Serializable {
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
