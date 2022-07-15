package com.lxc.frankmall.ware.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.utils.R;
import com.lxc.frankmall.ware.feign.MemberFeignService;
import com.lxc.frankmall.ware.vo.FareVo;
import com.lxc.frankmall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.ware.dao.WareInfoDao;
import com.lxc.frankmall.ware.entity.WareInfoEntity;
import com.lxc.frankmall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignServicel;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String  key = (String) params.get("key");
        wrapper.and(!StringUtils.isEmpty(key), (w) -> {
            w.eq("id", key)
                    .or()
                    .like("name", key)
                    .or()
                    .like("address", key)
                    .or()
                    .like("areacode", key);
        });


        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignServicel.addrInfo(addrId);
        Object data = r.get("memberReceiveAddress");
        ObjectMapper objectMapper = new ObjectMapper();
        MemberAddressVo memberAddressVo = objectMapper.convertValue(data, MemberAddressVo.class);
        String phone = memberAddressVo.getPhone();
        fareVo.setFare( new BigDecimal(phone.substring(phone.length()-1)));
        fareVo.setAddress(memberAddressVo);
        return fareVo;

    }

}