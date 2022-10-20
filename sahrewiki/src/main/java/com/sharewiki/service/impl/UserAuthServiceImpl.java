package com.sharewiki.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sharewiki.dto.EmailDTO;
import com.sharewiki.entity.UserAuthEntity;
import com.sharewiki.exception.BizException;
import com.sharewiki.mapper.UserAuthMapper;
import com.sharewiki.service.RedisService;
import com.sharewiki.service.UserAuthService;
import com.sharewiki.vo.PasswordVO;
import com.sharewiki.vo.UserVO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.sharewiki.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.sharewiki.constant.RedisPrefixConst.CODE_EXPIRE_TIME;
import static com.sharewiki.constant.RedisPrefixConst.USER_CODE_KEY;
import static com.sharewiki.util.CommonUtils.checkEmail;
import static com.sharewiki.util.CommonUtils.getRandomCode;

/**
 * 账号授权
 * @author IKY
 * @date 2022/10/19
 */
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper,UserAuthEntity> implements UserAuthService {

    @Resource
    private RedisService redisService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendCode(String userEmail) {
        // 校验账号是否合法
        if (!checkEmail(userEmail)) {
            throw new BizException("请输入正确邮箱");
        }
        String code = getRandomCode();
        EmailDTO emailDTO = EmailDTO.builder()
                .email(userEmail)
                .subject("验证码")
                .content("您的验证码为 " + code + " 有效期15分钟，请不要告诉他人哦！")
                .build();
        // 向RabbitMQ发送消息
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        //验证码放入redis，15min到期
        redisService.set(USER_CODE_KEY+userEmail,code,CODE_EXPIRE_TIME);

    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserVO user) {

    }

    @Override
    public void updatePassword(UserVO user) {

    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {

    }


}
