package com.lxc.frankmall.product.vo;

import lombok.Data;

/**
 * @author Frank_lin
 * @date 2022/6/26
 */
@Data
public class AttrResponseVo extends AttrVo{
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;

}
