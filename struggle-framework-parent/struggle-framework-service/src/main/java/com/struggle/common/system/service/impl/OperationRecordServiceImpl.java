package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.OperationRecord;
import com.struggle.common.system.mapper.OperationRecordMapper;
import com.struggle.common.system.param.OperationRecordParam;
import com.struggle.common.system.service.OperationRecordService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 操作日志Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@Service
public class OperationRecordServiceImpl extends ServiceImpl<OperationRecordMapper, OperationRecord> implements OperationRecordService {

    @Override
    public PageResult<OperationRecord> pageRel(OperationRecordParam param) {
        PageParam<OperationRecord, OperationRecordParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return new PageResult<>(baseMapper.selectPageRel(page, param), page.getTotal());
    }

    @Override
    public List<OperationRecord> listRel(OperationRecordParam param) {
        PageParam<OperationRecord, OperationRecordParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(baseMapper.selectListRel(param));
    }

    @Override
    public void dataExport(HttpServletResponse response, OperationRecordParam param) {
        try {
            ExcelOperation entity = new ExcelOperation(ExcelConstant.EXCEL.COLLEGE_EXCEL_EXPORT_DATA.getCode());
            List<OperationRecord> operationRecords = this.listRel(param);
            List<Map<String, Object>>  xxList = new ArrayList<>(operationRecords.size());
            String[] statusArr = new String[]{"正常","异常"};
            if (!CollectionUtils.isEmpty(operationRecords)) {
                for (OperationRecord operationRecord : operationRecords) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("username",operationRecord.getUsername());
                    map.put("realname",operationRecord.getRealname());
                    map.put("module",operationRecord.getModule());
                    map.put("description",operationRecord.getDescription());
                    map.put("url",operationRecord.getUrl());
                    map.put("requestMethod",operationRecord.getRequestMethod());
                    map.put("status",statusArr[operationRecord.getStatus()]);
                    map.put("spendTime",operationRecord.getSpendTime() + "ms");
                    map.put("createTime",CommonUtil.dateToString(operationRecord.getCreateTime(),CommonUtil.ymdhms));
                    xxList.add(map);
                }
            }

            //excesl列表头部
            String[] colNamesStrArr = new String[]{"账号","姓名","操作模块","操作功能","请求地址","请求方式","状态","耗时","操作时间"};
            //数据库对应字段
            String[] colCodesStrArr = new String[]{"username","realname","module","description","url","requestMethod","status","spendTime","createTime"};
            //文件名
            String title = "操作日志信息_".concat(CommonUtil.dateToString(new Date(),CommonUtil.ymd)).concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode());
            entity.commExport(response, title, xxList, Arrays.asList(colNamesStrArr), Arrays.asList(colCodesStrArr));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导出数据失败");
        }
    }

    @Override
    public OperationRecord getByIdRel(Integer id) {
        OperationRecordParam param = new OperationRecordParam();
        param.setId(id);
        return param.getOne(baseMapper.selectListRel(param));
    }

    @Async("myAsyncExecutor")
    @Override
    public void saveAsync(OperationRecord operationRecord) {
        ThreadLocalContextHolder.setTenant(operationRecord.getTenantId());
        try {
            baseMapper.insert(operationRecord);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ThreadLocalContextHolder.remove();
        }
    }
}
