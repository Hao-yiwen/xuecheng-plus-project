package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import org.apache.tomcat.Jar;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 微信扫码验证
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService, WxAuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WxAuthService wxAuthService;

    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        XcUser xcuser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(xcuser==null) {
            throw new RuntimeException("用户不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcuser, xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        // 申请令牌
        Map<String, String> acessToken = getAcess_token(code);
        String access_token = acessToken.get("access_token");
        String openid = acessToken.get("openid");
        // 保存用户信息到数据库
        Map<String, String> userInfo = getUserInfo(access_token, openid);
        XcUser xcUser = wxAuthService.addWxUser(userInfo);
        return xcUser;
    }

    @Transactional
    public XcUser addWxUser(Map<String, String> userInfo_map) {
        // 根据unionid查询用户信息
        String unionid = userInfo_map.get("unionid");
        String nickname = userInfo_map.get("nickname");
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if(xcUser!=null) {
            return xcUser;
        }
        // 向数据库新增记录
         xcUser = new XcUser();
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setWxUnionid(unionid);
        xcUser.setNickname(nickname);
        xcUser.setName(nickname);
        xcUser.setUtype("101001"); //  学生类型
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        int insert = xcUserMapper.insert(xcUser);

        // 向用户角色关系表新增记录
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);

        return xcUser;
    }

    /**
     * 携带授权码申请令牌
     * @url
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code
     * @response
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "UNIONID"
     * }
     * @param code
     * @return
     */
    private Map<String, String> getAcess_token(String code) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(wxUrl_template, appid, secret, code);
        // 远程调用
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        // 获取响应结果
        String result = exchange.getBody();
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }

    /**
     * 携带令牌查询用户信息
     *
     * @param access_token
     * @param openid
     * @return
     * @url https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
     * @response {
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * <p>
     * }
     */
    private Map<String, String> getUserInfo(String access_token, String openid) {
        String url_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(url_template, access_token, openid);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }
}
