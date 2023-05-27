package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.MemberEditDTO;
import com.enation.app.javashop.model.member.dto.MemberQueryParam;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.validation.annotation.Mobile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * 会员控制器
 *
 * @author zh
 * @version v2.0
 * @since v7.0.0
 * 2018-03-16 11:33:56
 */
@RestController
@RequestMapping("/admin/members")
@Validated
@Api(description = "会员后台管理API")
public class MemberManagerController {

    @Autowired
    private MemberManager memberManager;


    @ApiOperation(value = "查询会员列表", response = Member.class)
    @GetMapping
    public WebPage list(@Valid MemberQueryParam memberQueryParam, @ApiIgnore Long pageNo,
                        @ApiIgnore Long pageSize) {
        memberQueryParam.setPageNo(pageNo);
        memberQueryParam.setPageSize(pageSize);
        return this.memberManager.list(memberQueryParam);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改会员", response = Member.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "会员密码", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "会员备注", required = false, dataType = "String", paramType = "query")
    })
    public Member edit(@Valid MemberEditDTO memberEditDTO, @PathVariable Long id, String password, @Mobile String mobile, String remark) {
        Member member = memberManager.getModel(id);
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }
        //如果密码不为空的话 修改密码
        if (!StringUtil.isEmpty(password)) {
            //退出会员信息
            memberManager.memberLoginout(id);
            //组织会员的新密码
            member.setPassword(StringUtil.md5(password + member.getUname().toLowerCase()));
        }
        member.setRemark(remark);
        member.setUname(member.getUname());
        member.setMobile(mobile);
        member.setTel(memberEditDTO.getTel());
        BeanUtil.copyProperties(memberEditDTO, member);
        if (memberEditDTO.getRegion() != null) {
            BeanUtil.copyProperties(memberEditDTO.getRegion(), member);
        }
        this.memberManager.edit(member, id);
        return member;
    }


    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的会员主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {
        this.memberManager.disable(id);
        return "";
    }


    @PostMapping(value = "/{id}")
    @ApiOperation(value = "恢复会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要恢复的会员主键", required = true, dataType = "int", paramType = "path")
    })
    public Member recovery(@PathVariable Long id) {
        Member member = memberManager.getModel(id);
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }
        if (member.getDisabled().equals(-1)) {
            member.setDisabled(0);
            this.memberManager.edit(member, id);

            //发送会员解禁消息
            this.memberManager.recoverySendMsg(id);
        }
        return member;
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的会员主键", required = true, dataType = "int", paramType = "path")
    })
    public Member get(@PathVariable Long id) {
        return this.memberManager.getModel(id);
    }


    @PostMapping
    @ApiOperation(value = "平台添加会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "会员密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "uname", value = "会员用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "query")
    })
    public Member addMember(@Valid MemberEditDTO memberEditDTO, @NotEmpty(message = "会员密码不能为空") String password, @Length(min = 2, max = 20, message = "用户名长度必须在2到20位之间") String uname, @Mobile String mobile) {
        Member member = new Member();
        member.setUname(uname);
        member.setPassword(password);
        member.setNickname(memberEditDTO.getNickname());
        member.setMobile(mobile);
        member.setTel(memberEditDTO.getTel());
        BeanUtil.copyProperties(memberEditDTO, member);
        BeanUtil.copyProperties(memberEditDTO.getRegion(), member);
        memberManager.register(member);
        return member;

    }


    @GetMapping(value = "/{member_ids}/list")
    @ApiOperation(value = "查询多个会员的基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "member_ids", value = "要查询的会员的主键", required = true, dataType = "int", paramType = "path", allowMultiple = true)})
    public List<Member> getGoodsDetail(@PathVariable("member_ids") Long[] memberIds) {
        return this.memberManager.getMemberByIds(memberIds);

    }

}
