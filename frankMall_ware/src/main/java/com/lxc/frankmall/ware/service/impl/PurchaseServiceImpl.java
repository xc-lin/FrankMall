package com.lxc.frankmall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
import com.lxc.common.constant.WareConstant;
import com.lxc.frankmall.ware.entity.PurchaseDetailEntity;
import com.lxc.frankmall.ware.service.PurchaseDetailService;
import com.lxc.frankmall.ware.service.WareSkuService;
import com.lxc.frankmall.ware.vo.MergeVo;
import com.lxc.frankmall.ware.vo.PurchaseDoneVo;
import com.lxc.frankmall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.ware.dao.PurchaseDao;
import com.lxc.frankmall.ware.entity.PurchaseEntity;
import com.lxc.frankmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;


    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceived(Map<String, Object> params) {


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();

        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();


        }
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> entities = items.stream()
                .map(i -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(i);
                    detailEntity.setPurchaseId(finalPurchaseId);
                    detailEntity.setStatus(WareConstant.PurchaseDetailEnum.ASSIGNED.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(entities);

    }

    @Override
    public void received(List<Long> ids) {
        // 确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> list = this.list(new LambdaQueryWrapper<PurchaseEntity>()
                .in(PurchaseEntity::getId, ids));
        List<PurchaseEntity> purchaseEntities = list.stream()
                .filter(item -> item.getStatus() == WareConstant.PurchaseEnum.CREATED.getCode()
                        || item.getStatus() == WareConstant.PurchaseEnum.ASSIGNED.getCode())
                .map(item -> {
                    item.setStatus(WareConstant.PurchaseEnum.RECEIVED.getCode());
                    item.setUpdateTime(new Date());
                    return item;
                })
                .collect(Collectors.toList());
        // 改变采购单状态
        this.updateBatchById(purchaseEntities);


        // 改变采购想的状态
        purchaseDetailService.update(new LambdaUpdateWrapper<PurchaseDetailEntity>()
                .in(PurchaseDetailEntity::getPurchaseId, ids)
                .set(PurchaseDetailEntity::getStatus, WareConstant.PurchaseDetailEnum.BUYING.getCode()));


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        // 改变采购单的状态
        Long id = purchaseDoneVo.getId();
        // 改变采购项状态
        Boolean success = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        // Boolean success = items.stream().allMatch(item -> item.getStatus() == WareConstant.PurchaseDetailEnum.FINISHED.getCode());
        ArrayList<PurchaseDetailEntity> entities = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailEnum.HAS_ERROR.getCode()) {
                success = false;
            }else{
                // 入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setStatus(item.getStatus());
            detailEntity.setId(item.getItemId());
            entities.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(entities);

        this.update(new LambdaUpdateWrapper<PurchaseEntity>()
                .eq(PurchaseEntity::getId, id)
                .set(PurchaseEntity::getStatus
                        , success ? WareConstant.PurchaseEnum.FINISHED.getCode() : WareConstant.PurchaseEnum.HAS_ERROR.getCode())
                .set(PurchaseEntity::getUpdateTime,new Date()));


    }

}