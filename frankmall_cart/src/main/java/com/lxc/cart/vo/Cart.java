package com.lxc.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/1
 */
@Data
public class Cart {


    private List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal reduce = BigDecimal.ZERO;

    public BigDecimal getTotalAmount() {
        BigDecimal total = new BigDecimal("0");
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                if (item.getCheck()) {
                    BigDecimal totalPrice = item.getTotalPrice();
                    total = total.add(totalPrice);
                }
            }
        }

        BigDecimal subtract = total.subtract(getReduce());

        return subtract;
    }


    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }


    public Integer getCountType() {
        return items.size();
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
