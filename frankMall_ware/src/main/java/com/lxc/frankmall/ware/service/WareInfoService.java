package com.lxc.frankmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.ware.entity.WareInfoEntity;
import com.lxc.frankmall.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 11:15:07
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

