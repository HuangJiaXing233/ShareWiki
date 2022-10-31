package com.sharewiki.controller;

import com.sharewiki.service.UserAuthService;
import com.sharewiki.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户授权Controller
 *
 * @author IKY
 *
 */
@Api("用户账号模块")
@RestController
public class UserAuthController {

    @Resource
    private UserAuthService userAuthService;

    /**
     * 发送邮箱验证码
     *
     * @param username 用户名
     * @return {@link Result<>}
     */
    //@AccessLimit(seconds = 60, maxCount = 1)
    @ApiOperation(value = "发送邮箱验证码")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @GetMapping("/users/code")
    public Result<?> sendCode(String username) {
        userAuthService.sendCode(username);
        return Result.ok();
    }
}
