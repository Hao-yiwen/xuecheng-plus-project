package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // 将传入的json串转化为对象
        AuthParamsDto authParamsDto = null;
        try{
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        }catch (Exception error) {
            throw new RuntimeException("请求认证参数不符合要求");
        }

        // 认证类型
        String authType = authParamsDto.getAuthType();

        //根据认证类型从容器取出认证的bean
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        // 调用统一execute方法完成认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        // 封装xcUserExt为UserDetails
        UserDetails userPrincipal = getUserPrincipal(xcUserExt);
        return userPrincipal;
    }

    public UserDetails getUserPrincipal(XcUserExt xcUser) {
        String password = xcUser.getPassword();
        // 根据用户id查询用户权限
        String[] authorities = {"test"};
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        if(xcMenus.size() > 0) {
            List<String> permissions = new ArrayList<>();
            xcMenus.forEach(m -> {
                // 用户拥有的权限标识符
                permissions.add(m.getCode());
            });
            authorities = permissions.toArray(new String[0]);
        }
        xcUser.setPassword(null);
        // 将用户信息转为json
        String jsonString = JSON.toJSONString(xcUser);
        UserDetails userDetails = User.withUsername(jsonString).password(password).authorities(authorities).build();
        return userDetails;
    }
}
