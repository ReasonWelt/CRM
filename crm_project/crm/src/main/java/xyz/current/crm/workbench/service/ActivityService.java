package xyz.current.crm.workbench.service;

import xyz.current.crm.workbench.transaction.model.Activity;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface ActivityService {
    Object saveCreateActivity(Activity activity, HttpSession session);

    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    int getQueryCount(Map<String,Object> map);

    int deleteActivityByIds(String[] ids);

    Activity queryActivityById(String id);

    int updateActivity(Activity activity);

    List<Activity> queryAllActivitys();

    List<Activity> selectAllActivitysById(String[] ids);

    int saveActivityByList(List<Activity> activityList);

    Activity queryActivityForDetailById(String id);
}
