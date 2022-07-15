package com.lxc.frankmall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.lxc.common.entity.MemberEntity;
import com.lxc.common.exception.BizCodeEnum;
import com.lxc.common.vo.UserLoginVo;
import com.lxc.common.vo.UserRegistVo;
import com.lxc.frankmall.member.exception.PhoneExistException;
import com.lxc.frankmall.member.exception.UsernameExistException;
import com.lxc.frankmall.member.feign.CouponFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxc.frankmall.member.service.MemberService;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.R;

import javax.servlet.http.HttpSession;


/**
 * 会员
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:46:09
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    @Autowired
    CouponFeignService couponFeignService;


    @GetMapping("/coupons")
    public R testCouponFeignService() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R r = couponFeignService.memberCoupons();

        return R.ok().put("member", memberEntity).put("coupons", r.get("coupons"));
    }


    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo vo, HttpSession session){
        MemberEntity entity = memberService.login(vo);
        if (entity==null){
            return R.error(BizCodeEnum.LOGIN_FAILED_EXCEPTION);
        }else {
            // session.setAttribute("loginUser",entity);
            return R.ok().put("data",entity);
        }
    }

    @PostMapping("/regist")
    public R regist(@RequestBody UserRegistVo vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION);
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnum.USERNAME_EXIST_EXCEPTION);
        }
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
