package com.hyw.gym.controller;
// @author 黄元威



import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 登录Controller控制层
 */
@Controller
@RequestMapping("/")
public class LoginController {
    @Autowired
    private UserDao userDao;

    /**
     * @Description: 输入端口号直接跳转登录界面
     */
    @RequestMapping("/")
    public String beforeLogin(){
        return "login";
    }

    /**
     * @Description: 我的桌面
     */
    @RequestMapping("/myDesktop")
    public String myDesktop(){

        return "WEB-INF/jsp/MyDesktop";
    }

    /**
     * @Description: 管理员登录验证方法
     */
    @RequestMapping("/dl/yz")
    public String login(String username, String password,HttpSession httpSession,Model model){

        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken userToken=new UsernamePasswordToken(username,DigestUtils.md5Hex(password));
        try{
            subject.login(userToken);
            User user= userDao.findByLoginNameAndPassword(username,DigestUtils.md5Hex(password));
            httpSession.setAttribute("user",user);
            // 根据type控制菜单权限
            if(UserType.ADMIN.getType().equals(user.getUserType())){
                return "WEB-INF/jsp/index";
            } else if(UserType.COACH.getType().equals(user.getUserType())){
                return "WEB-INF/jsp/indexCoach";
            } else if(UserType.MEMBER.getType().equals(user.getUserType())){
                return "WEB-INF/jsp/indexMember";
            }
        }catch (UnknownAccountException e){
            model.addAttribute("msg","用户名或密码错误,请重新输入");
        }
        /* User user = userDao.findByLoginNameAndPassword(username,password);
        if(a!=null){
            httpSession.setAttribute("user",user);
            return "WEB-INF/jsp/index" ;
        }
        model.addAttribute("mag","账号或密码错误");
        return "login";*/
        return "login";
    }

    /**
     * @Description: 退出登录后清楚session
     */
    @RequestMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login";

    }

    /**
     * @Description: 跳转到修改密码界面
     */
    @RequestMapping("/updPassword")
    public String updPassword(){
        return "WEB-INF/jsp/updPassword";
    }


    /**
     * @Description: 修改密码
     */
    @RequestMapping("/upd/updPassword")
    public String updPasswordConfirm(String oldPassword,String newPassword,String newPasswordAgain,HttpSession httpSession,Model model){
        Pattern p = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!.%*#?&])[A-Za-z\\d$@$!.%*#?&]{8,}$");
        Matcher m = p.matcher(newPassword);
        if(!m.matches()){
            model.addAttribute("msg","新密码最少为8位并为字母+数字+特殊字符");
            return "WEB-INF/jsp/updPassword";
        }
        if(!newPassword.equals(newPasswordAgain)){
            model.addAttribute("msg","两次输入新密码不一致,请重新输入");
            return "WEB-INF/jsp/updPassword";
        }
        User user=(User) httpSession.getAttribute("user");
        if(null != user){
            if(!user.getPassword().equals(DigestUtils.md5Hex(oldPassword))){
                model.addAttribute("msg","原密码不正确,请重新输入");
                return "WEB-INF/jsp/updPassword";
            }
            userDao.updPassword(user.getId(), DigestUtils.md5Hex(newPassword));
        }
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
       return "redirect:/login.jsp";
    }


}
