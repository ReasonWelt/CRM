package xyz.current.crm.workbench.mapper;

import xyz.current.crm.workbench.transaction.model.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Mon Aug 22 17:52:38 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Mon Aug 22 17:52:38 CST 2022
     */
    int insert(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Mon Aug 22 17:52:38 CST 2022
     */
    int insertSelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Mon Aug 22 17:52:38 CST 2022
     */
    Activity selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Mon Aug 22 17:52:38 CST 2022
     */
    int updateByPrimaryKeySelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Mon Aug 22 17:52:38 CST 2022
     */
    int updateByPrimaryKey(Activity record);

    /**
     * 保存创建的市场活动
     */
    int insertActivity(Activity activity);

    /**
     * 分页查询
     */
    List<Activity> selectActivityByConditionForPage(Map<String,Object> map);

    /**
     * 查询总条数
     */
    int selectActivityCount(Map<String,Object> map);

    /**
     * 删除用户选择的市场活动
     */
    int deleteActivityByIds(String[] ids);

    /**
     * 根据id查询数据
     */
    Activity selectActivityById(String id);

    /**
     * 更新数据库中数据
     */
    int saveEditActivity(Activity activity);

    /**
     * 查询所有市场活动
     */
    List<Activity> selectAllActivitys();

    /**
     * 查询选择的市场活动
     */
    List<Activity> selectAllActivitysById(String[] ids);

    /**
     * 通过标点插入数据
     */
    int insertActivityByList(List<Activity> activityList);

    /**
     * 查询单个信息
     */
    Activity selectActivityForDetailById(String id);

}