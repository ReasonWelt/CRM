package xyz.current.crm.settings.service;

import xyz.current.crm.settings.transaction.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserService {

    User queryUserByLoginActAndPwd(Map<String,Object> map);

    void queryAllUsers(HttpServletRequest request);
}
