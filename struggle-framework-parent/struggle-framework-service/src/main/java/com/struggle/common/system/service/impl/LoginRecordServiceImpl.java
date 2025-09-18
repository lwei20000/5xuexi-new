package com.struggle.common.system.service.impl;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.LoginRecord;
import com.struggle.common.system.mapper.LoginRecordMapper;
import com.struggle.common.system.param.LoginRecordParam;
import com.struggle.common.system.service.LoginRecordService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 登录日志Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:14
 */
@Service
public class LoginRecordServiceImpl extends ServiceImpl<LoginRecordMapper, LoginRecord>
        implements LoginRecordService {

    @Override
    public PageResult<LoginRecord> pageRel(LoginRecordParam param) {
        PageParam<LoginRecord, LoginRecordParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return new PageResult<>(baseMapper.selectPageRel(page, param), page.getTotal());
    }

    @Override
    public List<LoginRecord> listRel(LoginRecordParam param) {
        PageParam<LoginRecord, LoginRecordParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(baseMapper.selectListRel(param));
    }

    @Override
    public LoginRecord getByIdRel(Integer id) {
        LoginRecordParam param = new LoginRecordParam();
        param.setId(id);
        return param.getOne(baseMapper.selectListRel(param));
    }

    @Override
    public void dataExport(HttpServletResponse response, LoginRecordParam param) {
        try {
            ExcelOperation entity = new ExcelOperation(ExcelConstant.EXCEL.COLLEGE_EXCEL_EXPORT_DATA.getCode());
            List<LoginRecord> loginRecords = this.listRel(param);
            List<Map<String, Object>>  xxList = new ArrayList<>(loginRecords.size());
            String[] loginTypeArr = new String[]{"登录成功","登录失败","退出登录","刷新TOKEN"};
            if (!CollectionUtils.isEmpty(loginRecords)) {
                for (LoginRecord loginRecord : loginRecords) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("username",loginRecord.getUsername());
                    map.put("realname",loginRecord.getRealname());
                    map.put("ip",loginRecord.getIp());
                    map.put("device",loginRecord.getDevice());
                    map.put("os",loginRecord.getOs());
                    map.put("browser",loginRecord.getBrowser());
                    map.put("status",loginTypeArr[loginRecord.getLoginType()]);
                    map.put("comments",loginRecord.getComments());
                    map.put("createTime", CommonUtil.dateToString(loginRecord.getCreateTime(),CommonUtil.ymdhms));
                    xxList.add(map);
                }
            }

            //excesl列表头部
            String[] colNamesStrArr = new String[]{"账号","姓名","IP地址","设备型号","操作系统","浏览器","操作类型","备注","登录时间"};
            //数据库对应字段
            String[] colCodesStrArr = new String[]{"username","realname","ip","device","os","browser","status","comments","createTime"};
            //文件名
            String title = "登录日志信息_".concat(CommonUtil.dateToString(new Date(),CommonUtil.ymd)).concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode());
            entity.commExport(response, title, xxList, Arrays.asList(colNamesStrArr), Arrays.asList(colCodesStrArr));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导出数据失败");
        }
    }

    @Override
    public void saveAsync(String username, Integer type, String comments, Integer tenantId, HttpServletRequest request) {
        if (username == null) {
            return;
        }
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setUsername(username);
        loginRecord.setLoginType(type);
        loginRecord.setComments(comments);
        loginRecord.setTenantId(tenantId);
        UserAgent ua = UserAgentUtil.parse(ServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
        if (ua != null) {
            if (ua.getPlatform() != null) {
                loginRecord.setOs(ua.getPlatform().toString());
            }
            if (ua.getOs() != null) {
                loginRecord.setDevice(ua.getOs().toString());
            }
            if (ua.getBrowser() != null) {
                loginRecord.setBrowser(ua.getBrowser().toString());
            }
        }
        loginRecord.setIp(ServletUtil.getClientIP(request));
        this._saveAsync(loginRecord);
    }

    @Async("myAsyncExecutor")
    public void _saveAsync(LoginRecord loginRecord) {
        ThreadLocalContextHolder.setTenant(loginRecord.getTenantId());
        try {
            baseMapper.insert(loginRecord);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ThreadLocalContextHolder.remove();
        }
    }
}
