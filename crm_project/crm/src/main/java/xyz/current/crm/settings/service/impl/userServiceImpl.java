package xyz.current.crm.settings.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.current.crm.settings.mapper.UserMapper;
import xyz.current.crm.settings.service.UserService;
import xyz.current.crm.settings.transaction.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class userServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User queryUserByLoginActAndPwd(Map<String, Object> map) {
        return userMapper.selectUserByLoginActAndPwd(map);
    }

    @Override
    public void queryAllUsers(HttpServletRequest request) {
        //调用mapper层的查询所有用户
        List<User> users = userMapper.selectAllUsers();
        //把数据存到request中
        request.setAttribute("userList",users);
        // return users;
    }

}
