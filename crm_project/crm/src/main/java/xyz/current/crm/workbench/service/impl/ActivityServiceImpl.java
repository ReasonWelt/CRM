package xyz.current.crm.workbench.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.current.crm.comments.constant.Constants;
import xyz.current.crm.comments.domain.ReturnObject;
import xyz.current.crm.comments.utils.DateUtils;
import xyz.current.crm.comments.utils.UUIDUtils;
import xyz.current.crm.settings.transaction.model.User;
import xyz.current.crm.workbench.mapper.ActivityMapper;
import xyz.current.crm.workbench.service.ActivityService;
import xyz.current.crm.workbench.transaction.model.Activity;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public Object saveCreateActivity(Activity activity, HttpSession session) {
        User user=(User)session.getAttribute(Constants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());
        ReturnObject returnObject = new ReturnObject();

        try {
            //调用mapper层的方法
            int ret = activityMapper.insertActivity(activity);

            if (ret>0){
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统繁忙，请稍候重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统繁忙，请稍候重试");
        }

        return returnObject;
    }

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String,Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    public int getQueryCount(Map<String, Object> map) {
        return activityMapper.selectActivityCount(map);
    }

    @Override
    public int deleteActivityByIds(String[] ids) {
        return activityMapper.deleteActivityByIds(ids);
    }

    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public int updateActivity(Activity activity) {
        return activityMapper.saveEditActivity(activity);
    }

    @Override
    public List<Activity> queryAllActivitys() {
        return activityMapper.selectAllActivitys();
    }

    @Override
    public List<Activity> selectAllActivitysById(String[] ids) {
        return activityMapper.selectAllActivitysById(ids);
    }

    @Override
    public int saveActivityByList(List<Activity> activityList) {
        return activityMapper.insertActivityByList(activityList);
    }

    @Override
    public Activity queryActivityForDetailById(String id) {
        return activityMapper.selectActivityForDetailById(id);
    }


}
