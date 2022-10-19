package com.sharewiki.service;

import com.sharewiki.dto.UserInfoDTO;
import com.sharewiki.vo.PasswordVO;
import com.sharewiki.vo.UserVO;

/**
 * 账号授权
 * @author IKY
 * @date 2022/10/19
 */
public interface UserAuthService {

    /**
     * 发送邮箱验证码
     *
     * @param userEmail 邮箱号
     */
    void sendCode(String userEmail);

    /**
     * 用户注册
     *
     * @param user 用户对象
     */
    void register(UserVO user);

    /**
     * 修改密码
     *
     * @param user 用户对象
     */
    void updatePassword(UserVO user);

    /**
     * 修改管理员密码
     *
     * @param passwordVO 密码对象
     */
    void updateAdminPassword(PasswordVO passwordVO);
}
