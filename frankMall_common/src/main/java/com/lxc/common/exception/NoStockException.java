package com.lxc.common.exception;

/**
 * @author Frank_lin
 * @date 2022/7/4
 */
public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException() {
        super();
    }

    public NoStockException(Long skuId) {
        super("商品Id: " + skuId + " 没有足够的库存了");
        this.skuId = skuId;
    }

    public NoStockException(String msg) {
        super(msg);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
