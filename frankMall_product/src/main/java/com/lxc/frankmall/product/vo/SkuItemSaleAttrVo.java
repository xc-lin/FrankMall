package com.lxc.frankmall.product.vo;

import lombok.Data;

import java.util.List;
/**
 * @author yaoxinjia
 */
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
