package com.lxc.frankmall.coupon.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.coupon.dao.SeckillSkuRelationDao;
import com.lxc.frankmall.coupon.entity.SeckillSkuRelationEntity;
import com.lxc.frankmall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> seckillSkuRelationEntityQueryWrapper = new QueryWrapper<>();

        String promotionSessionId = (String) params.get("promotionSessionId");
        if (StringUtils.isNotEmpty(promotionSessionId)){
            seckillSkuRelationEntityQueryWrapper
                    .eq("promotion_session_id",promotionSessionId);
        }
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                seckillSkuRelationEntityQueryWrapper
        );

        return new PageUtils(page);
    }

}