package com.lxc.frankmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.product.entity.AttrEntity;
import com.lxc.frankmall.product.vo.AttrGroupRelationVo;
import com.lxc.frankmall.product.vo.AttrResponseVo;
import com.lxc.frankmall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 00:59:50
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrResponseVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    /**
     * 根据分组id查找关联的所有属性
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * 获取当前分组没有关联的所有属性
     * @param attrgroupId
     * @param params
     * @return
     */
    PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params);
}

