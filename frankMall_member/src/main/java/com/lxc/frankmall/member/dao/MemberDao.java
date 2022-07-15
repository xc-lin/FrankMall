package com.lxc.frankmall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxc.common.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:46:09
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
