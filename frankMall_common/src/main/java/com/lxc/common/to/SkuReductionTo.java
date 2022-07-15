package com.lxc.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/6/27
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;


}
