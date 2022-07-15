package com.lxc.frankmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxc.common.entity.MemberEntity;
import com.lxc.common.vo.UserLoginVo;
import com.lxc.common.vo.UserRegistVo;
import com.lxc.frankmall.member.entity.MemberLevelEntity;
import com.lxc.frankmall.member.exception.PhoneExistException;
import com.lxc.frankmall.member.exception.UsernameExistException;
import com.lxc.frankmall.member.service.MemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.member.dao.MemberDao;
import com.lxc.frankmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();

        MemberLevelEntity one = memberLevelService.getOne(new LambdaQueryWrapper<MemberLevelEntity>()
                .eq(MemberLevelEntity::getDefaultStatus, 1));

        // 设置默认等级
        memberEntity.setLevelId(one.getId());
        // 检查用户名和手机号是否唯一
        checkPhoneUnique(vo.getPhone());
        checkUsernaemUnique(vo.getUserName());

        memberEntity.setUsername(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(bCryptPasswordEncoder.encode(vo.getPassWord()));

        this.baseMapper.insert(memberEntity);


    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {

        int count = this.count(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getMobile, phone));
        if (count > 0) {
            throw new PhoneExistException();
        }

    }

    @Override
    public void checkUsernaemUnique(String username) throws UsernameExistException {
        int count = this.count(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, username));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(UserLoginVo vo) {
        MemberEntity memberEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getMobile, vo.getLoginacct())
                .or()
                .eq(MemberEntity::getUsername, vo.getLoginacct()));
        if (memberEntity == null) {
            return null;
        } else {
            String password = memberEntity.getPassword();
            return new BCryptPasswordEncoder().matches(vo.getPassword(), password) ? memberEntity : null;

        }
    }

}