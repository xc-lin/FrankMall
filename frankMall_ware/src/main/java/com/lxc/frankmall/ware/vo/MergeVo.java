package com.lxc.frankmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yaoxinjia
 */
@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
