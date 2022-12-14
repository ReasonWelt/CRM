package xyz.current.crm.workbench.web.controller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import xyz.current.crm.comments.constant.Constants;
import xyz.current.crm.comments.domain.ReturnObject;
import xyz.current.crm.comments.utils.DateUtils;
import xyz.current.crm.comments.utils.HSSFUtils;
import xyz.current.crm.comments.utils.UUIDUtils;
import xyz.current.crm.settings.service.UserService;
import xyz.current.crm.settings.transaction.model.User;
import xyz.current.crm.workbench.service.ActivityRemarkService;
import xyz.current.crm.workbench.service.ActivityService;
import xyz.current.crm.workbench.transaction.model.Activity;
import xyz.current.crm.workbench.transaction.model.ActivityRemark;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;


@Controller
public class ActivityController {
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        userService.queryAllUsers(request);
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session){
        return activityService.saveCreateActivity(activity,session);
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name,String owner,String startDate,String endDate,Integer pageNo,Integer pageSize){
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);

        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalCount = activityService.getQueryCount(map);

        Map<String, Object> remap = new HashMap<>();
        remap.put("activityList",activityList);
        remap.put("totalCount",totalCount);

        return remap;

    }

    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public Object deleteActivityByIds(String[] id){
        ReturnObject returnObject = new ReturnObject();

        try{
            int ret = activityService.deleteActivityByIds(id);
            if (ret>0){
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("???????????????????????????????????????");
            }

        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("???????????????????????????????????????");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id){
        Activity activity = activityService.queryActivityById(id);
        return activity;
    }

    @RequestMapping("/workbench/activity/updateActivity.do")
    @ResponseBody
    public Object updateActivity(HttpSession session,Activity activity){
        ReturnObject returnObject = new ReturnObject();
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        activity.setEditTime(DateUtils.formatDateTime(new Date()));
        activity.setEditBy(user.getId());
        try {
            int ret = activityService.updateActivity(activity);
            if (ret >= 1){
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("?????????????????????????????????????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("?????????????????????????????????????????????");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryAllActivitys.do")
    public void queryAllActivitys(HttpServletResponse response) throws Exception {
        //??????service???????????????????????????????????????
        List<Activity> activityList = activityService.queryAllActivitys();
        //??????excel??????????????????activityList?????????????????????
        HSSFWorkbook wb = new HSSFWorkbook();
        //?????????
        HSSFSheet sheet = wb.createSheet("???????????????");
        //?????????
        HSSFRow row = sheet.createRow(0);
        //?????????
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("?????????");
        cell = row.createCell(2);
        cell.setCellValue("??????");
        cell = row.createCell(3);
        cell.setCellValue("????????????");
        cell = row.createCell(4);
        cell.setCellValue("????????????");
        cell = row.createCell(5);
        cell.setCellValue("??????");
        cell = row.createCell(6);
        cell.setCellValue("??????");
        cell = row.createCell(7);
        cell.setCellValue("????????????");
        cell = row.createCell(8);
        cell.setCellValue("?????????");
        cell = row.createCell(9);
        cell.setCellValue("????????????");
        cell = row.createCell(10);
        cell.setCellValue("?????????");


        if (activityList!=null && activityList.size()>0){
            Activity act = null;
            for (int i = 0;i<activityList.size();i++){
                act = activityList.get(i);
                //?????????
                row = sheet.createRow(i+1);
                //?????????
                cell = row.createCell(0);
                cell.setCellValue(act.getId());
                cell = row.createCell(1);
                cell.setCellValue(act.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(act.getName());
                cell = row.createCell(3);
                cell.setCellValue(act.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(act.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(act.getCost());
                cell = row.createCell(6);
                cell.setCellValue(act.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(act.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(act.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(act.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(act.getEditBy());
            }
        }

        //??????excel??????
        /*OutputStream os = new FileOutputStream("D:\\SSM\\CRM\\CRM_code\\crm_project\\crm\\src\\temporary\\ActivityList.xls");
        wb.write(os);//??????????????????????????????*/
        //?????????
        // os.close();
        // wb.close();

        //???????????????excel????????????????????????
        response.setContentType("application/octet-stream;charset=UTF-8");
        //???????????????
        response.addHeader("Content-Disposition","attachment;filename=ActivityList.xls");
        ServletOutputStream out = response.getOutputStream();
        /*FileInputStream is = new FileInputStream("D:\\SSM\\CRM\\CRM_code\\crm_project\\crm\\src\\temporary\\ActivityList.xls");
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=is.read(buff))!=-1){//???????????????????????????????????????
            out.write(buff,0,len);
        }
        is.close();*/
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        wb.write(out);
        wb.close();
        out.flush();

    }

    @RequestMapping("/workbench/activity/queryAllActivitysById.do")
    public void queryAllActivitysById(HttpServletResponse response,String[] id) throws Exception {
        //??????service???????????????????????????????????????
        List<Activity> activityList = activityService.selectAllActivitysById(id);
        //??????excel??????????????????activityList?????????????????????
        HSSFWorkbook wb = new HSSFWorkbook();
        //?????????
        HSSFSheet sheet = wb.createSheet("???????????????");
        //?????????
        HSSFRow row = sheet.createRow(0);
        //?????????
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("?????????");
        cell = row.createCell(2);
        cell.setCellValue("??????");
        cell = row.createCell(3);
        cell.setCellValue("????????????");
        cell = row.createCell(4);
        cell.setCellValue("????????????");
        cell = row.createCell(5);
        cell.setCellValue("??????");
        cell = row.createCell(6);
        cell.setCellValue("??????");
        cell = row.createCell(7);
        cell.setCellValue("????????????");
        cell = row.createCell(8);
        cell.setCellValue("?????????");
        cell = row.createCell(9);
        cell.setCellValue("????????????");
        cell = row.createCell(10);
        cell.setCellValue("?????????");


        if (activityList!=null && activityList.size()>0){
            Activity act = null;
            for (int i = 0;i<activityList.size();i++){
                act = activityList.get(i);
                //?????????
                row = sheet.createRow(i+1);
                //?????????
                cell = row.createCell(0);
                cell.setCellValue(act.getId());
                cell = row.createCell(1);
                cell.setCellValue(act.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(act.getName());
                cell = row.createCell(3);
                cell.setCellValue(act.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(act.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(act.getCost());
                cell = row.createCell(6);
                cell.setCellValue(act.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(act.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(act.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(act.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(act.getEditBy());
            }
        }
        //???????????????excel????????????????????????
        response.setContentType("application/octet-stream;charset=UTF-8");
        //???????????????
        response.addHeader("Content-Disposition","attachment;filename=ActivityList.xls");
        ServletOutputStream out = response.getOutputStream();
        /*FileInputStream is = new FileInputStream("D:\\SSM\\CRM\\CRM_code\\crm_project\\crm\\src\\temporary\\ActivityList.xls");
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=is.read(buff))!=-1){//???????????????????????????????????????
            out.write(buff,0,len);
        }
        is.close();*/
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        wb.write(out);
        wb.close();
        out.flush();
    }

    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile,HttpSession session){
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        ReturnObject returnObject = new ReturnObject();
        try {
            //???excel???????????????????????????
            /*String originalFilename = activityFile.getOriginalFilename();//??????????????????
            File file = new File("D:\\SSM\\CRM\\CRM_code\\crm_project\\crm\\src\\temporary\\demo"+originalFilename);//??????????????????????????????originalFilename?????????????????????????????????
            activityFile.transferTo(file);

            //??????excel???????????????????????????????????????????????????activityList
            InputStream is = new FileInputStream("D:\\SSM\\CRM\\CRM_code\\crm_project\\crm\\src\\temporary\\" + originalFilename);*/

            InputStream is = activityFile.getInputStream();
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);

            List<Activity> activityList = new ArrayList<>();
            Activity activity = null;
            HSSFRow row = null;
            HSSFCell cell = null;
            for (int i = 1; i <=sheet.getLastRowNum(); i++) {//??????rowNum???????????????????????????????????????
                activity = new Activity();
                row = sheet.getRow(i);
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                activity.setCreateBy(user.getId());

                for (int j = 0; j < row.getLastCellNum(); j++) {//?????????CellNum????????????????????????+1????????????????????????????????????
                    cell = row.getCell(j);
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    if (j==0){
                        activity.setName(cellValue);
                    }else if (j==1){
                        activity.setStartDate(cellValue);
                    }else if (j==2){
                        activity.setEndDate(cellValue);
                    }else if (j==3){
                        activity.setCost(cellValue);
                    }else if (j==4){
                        activity.setDescription(cellValue);
                    }
                }
                activityList.add(activity);
            }

            //??????Service?????????
            int ret = activityService.saveActivityByList(activityList);

            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(ret);
            return  returnObject;

        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("?????????????????????????????????????????????");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id,HttpServletRequest request){
        //??????Service????????????????????????
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarks = activityRemarkService.queryActivityRemarkDetailByActivityId(id);
        //??????????????????request???
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarks);
        //????????????
        return "workbench/activity/detail";
    }
}
