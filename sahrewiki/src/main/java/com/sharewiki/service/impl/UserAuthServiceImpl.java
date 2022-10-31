package com.sharewiki.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.system.UserInfo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sharewiki.constant.CommonConst;
import com.sharewiki.dto.EmailDTO;
import com.sharewiki.entity.UserAuthEntity;
import com.sharewiki.entity.UserInfoEntity;
import com.sharewiki.entity.UserRoleEntity;
import com.sharewiki.enums.RoleEnum;
import com.sharewiki.exception.BizException;
import com.sharewiki.mapper.UserAuthMapper;
import com.sharewiki.mapper.UserInfoMapper;
import com.sharewiki.mapper.UserRoleMapper;
import com.sharewiki.service.RedisService;
import com.sharewiki.service.UserAuthService;
import com.sharewiki.vo.PasswordVO;
import com.sharewiki.vo.UserVO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.sharewiki.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.sharewiki.constant.RedisPrefixConst.CODE_EXPIRE_TIME;
import static com.sharewiki.constant.RedisPrefixConst.USER_CODE_KEY;
import static com.sharewiki.util.CommonUtils.*;

/**
 * 账号授权
 * @author IKY
 * @date 2022/10/19
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper,UserAuthEntity> implements UserAuthService {

    @Resource
    private RedisService redisService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserAuthMapper userAuthMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public void sendCode(String username) {
        // 校验账号是否合法
        if (!checkName(username)) {
            throw new BizException("请输入正确邮箱");
        }
        String code = getRandomCode();
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .content("您的验证码为 " + code + " 有效期15分钟，请不要告诉他人哦！")
                .build();
        // 向RabbitMQ发送消息
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        //验证码放入redis，15min到期
        redisService.set(USER_CODE_KEY+username,code,CODE_EXPIRE_TIME);

    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserVO user) {
        if (userCheck(user)){
            throw new BizException("邮箱已被注册！");
        }
        //增加用户信息
        UserInfoEntity userInfo = UserInfoEntity.builder()
                .email(user.getUsername())
                .nickname(CommonConst.DEFAULT_NICKNAME + IdWorker.getId())
                .build();
        userInfoMapper.insert(userInfo);
        //增加用户账号
        UserAuthEntity userAuth = UserAuthEntity.builder()
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .build();
        userAuthMapper.insert(userAuth);
        //绑定用户角色
        UserRoleEntity userRole = UserRoleEntity.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
    }

    @Override
    public void updatePassword(UserVO user) {
        //校验账号
        if(!userCheck(user)){
            throw new BizException("此账号尚未注册");
        }
        //修改密码
        userAuthMapper.update(new UserAuthEntity(),new LambdaUpdateWrapper<UserAuthEntity>()
                .set(UserAuthEntity::getPassword,BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .eq(UserAuthEntity::getUsername,user.getUsername()));
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {


    }

    /**
     * 校验用户
     * @param user
     *
     */
    public Boolean userCheck(UserVO user){
        if(!(user.getCode().equals(redisService.get(USER_CODE_KEY+user.getUsername())))){
            throw new BizException("验证码错误！");
        }
        //查询用户名是否存在
        LambdaQueryWrapper<UserAuthEntity> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuthEntity::getUsername, user.getUsername());
        return CollUtil.isNotEmpty(userAuthMapper.selectList(queryWrapper));
    }


}
