package com.hyw.gym.realm;
// @author 黄元威

import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserDao userDao;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("已授权");
        Subject subject = SecurityUtils.getSubject();
        User user = (User) principalCollection.getPrimaryPrincipal() ;
        //
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo() ;
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken ;
        User user = userDao.findByLoginNameAndPassword(token.getUsername(),String.copyValueOf(token.getPassword()));

        if(null == user){
            return null ;
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getLoginName(),user.getPassword(),user.getRealName()) ;
        System.out.println("完成认证！");
        return info;
    }
}
