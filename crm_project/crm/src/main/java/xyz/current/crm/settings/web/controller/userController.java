package xyz.current.crm.settings.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.current.crm.comments.constant.Constants;
import xyz.current.crm.comments.domain.ReturnObject;
import xyz.current.crm.comments.utils.DateUtils;
import xyz.current.crm.settings.service.UserService;
import xyz.current.crm.settings.transaction.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class userController {

    @Autowired
    UserService userService;

    /*
    * url写的时候要和Controller方法处理玩请求之后，响应信息返回的页面的资源目录保持一致
    * */
    @RequestMapping("/settings/qx/user/login.do")
    public String login(){
        //请求转发到登录页面
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/toLogin.do")
    @ResponseBody//返回对象需要使用这个注解
    public Object doLogin(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpSession session, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        map.put("isRemPwd",isRemPwd);
        ReturnObject re = new ReturnObject();
        User user = userService.queryUserByLoginActAndPwd(map);
        if (user==null){
            //登录失败，账号密码错误或为空
            re.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            re.setMessage("登录失败，账号密码错误或为空");
        }else{
            String date = DateUtils.formatDateTime(new Date());
            if (date.compareTo(user.getExpireTime())>0){
                //登录失败，用户已过期
                re.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                re.setMessage("登录失败，用户已过期");
            }else if (Constants.RETURN_OBJECT_CODE_FAIL.equals(user.getLockState())){
                //登录失败，用户已锁定
                re.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                re.setMessage("登录失败，用户已锁定");
            }else if (!user.getAllowIps().contains(request.getRemoteAddr())){
                //登录失败，ip地址受限
                re.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                re.setMessage("登录失败，ip地址受限");
            }else{
                //登录成功
                re.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                session.setAttribute(Constants.SESSION_USER,user);

                //如果需要记住密码，则往外写cookie
                if("true".equals(isRemPwd)){
                    Cookie c1 = new Cookie("loginAct", user.getLoginAct());
                    c1.setMaxAge(10*24*60);
                    response.addCookie(c1);

                    Cookie c2 = new Cookie("loginPwd", user.getLoginPwd());
                    c2.setMaxAge(10*24*60);
                    response.addCookie(c2);

                }else{
                    Cookie c1 = new Cookie("loginAct", "");
                    c1.setMaxAge(0);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginPwd", "");
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }
            }
        }

        return re;
    }

    @RequestMapping("/settings/qx/user/loginOut.do")
    public String Logout(HttpServletResponse response,HttpSession session){
        //清空cookie
        Cookie c1 = new Cookie("loginAct", "");
        c1.setMaxAge(0);
        response.addCookie(c1);
        Cookie c2 = new Cookie("loginPwd", "");
        c1.setMaxAge(0);
        response.addCookie(c2);

        //销毁session
        session.invalidate();

        //跳转到首页
        return "redirect:/";
    }
}
