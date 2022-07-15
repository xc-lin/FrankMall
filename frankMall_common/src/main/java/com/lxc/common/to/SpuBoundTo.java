package com.lxc.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Frank_lin
 * @date 2022/6/27
 */
@Data
public class SpuBoundTo {

    private Long spuId;

    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
