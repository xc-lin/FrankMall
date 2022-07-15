package com.lxc.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/1
 * 购物项
 */
@Data
public class CartItem {
    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttrValues;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer count;

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(this.count));
    }
}
