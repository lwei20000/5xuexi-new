package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.Constants;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.entity.FileRecord;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.mapper.TenantMapper;
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.param.TenantParam;
import com.struggle.common.system.service.DefaultRoleService;
import com.struggle.common.system.service.DictionaryDataService;
import com.struggle.common.system.service.TenantService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 租户Service实现
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Resource
    private DefaultRoleService defaultRoleService;
    @Resource
    private DictionaryDataService dictionaryDataService;
    @Resource
    private FileDfsUtil fileDfsUtil;

    @Override
    public PageResult<Tenant> pageRel(TenantParam param) {
        PageParam<Tenant, TenantParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        List<Tenant> list = baseMapper.selectPageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<Tenant> listRel(TenantParam param) {
        List<Tenant> list = baseMapper.selectListRel(param);
        // 排序
        PageParam<Tenant, TenantParam> page = new PageParam<>();
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(list);
    }

    private void getTenantChildrenIds(Tenant tenant, List<Integer> childrenIds) {
        List<Tenant> tenants = baseMapper.selectList(Wrappers.<Tenant>lambdaQuery().select(Tenant::getTenantId).eq(Tenant::getParentId, tenant.getTenantId()));
        if(!CollectionUtils.isEmpty(tenants)){
            for(Tenant _tenant : tenants){
                childrenIds.add(_tenant.getTenantId());
                this.getTenantChildrenIds(_tenant,childrenIds);
            }
        }
    }

    @Override
    @Transactional
    public boolean saveTenant(Tenant tenant) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantName, tenant.getTenantName()).eq(Tenant::getParentId, tenant.getParentId())) > 0) {
            throw new BusinessException("租户名称已存在");
        }
        boolean result = baseMapper.insert(tenant)> 0;
        if(result){
            this._updateTenant(tenant);
            List<Tenant> tenantList = new ArrayList<>();
            tenantList.add(tenant);
            defaultRoleService.initTenant(tenantList);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean updateTenant(Tenant tenant) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantName, tenant.getTenantName()).eq(Tenant::getParentId, tenant.getParentId()).ne(Tenant::getTenantId, tenant.getTenantId())) > 0) {
            throw new BusinessException("租户名称已存在");
        }
        if(tenant.getParentId() !=null && tenant.getParentId() !=0){
            if(tenant.getTenantId().equals(tenant.getParentId())){
                throw new BusinessException("上级租户不能是当前当前租户");
            }
            List<Integer> childrenIds = new ArrayList<>();
            this.getTenantChildrenIds(tenant,childrenIds);
            if(childrenIds.contains(tenant.getParentId())){
                throw new BusinessException("上级租户不能是当前租户的子租户");
            }
        }
        Tenant _tenant = baseMapper.selectById(tenant.getTenantId());
        boolean result = baseMapper.update(tenant, Wrappers.<Tenant>lambdaUpdate()
                .set(Tenant::getParentId,tenant.getParentId())
                .set(Tenant::getTenantLogo,tenant.getTenantLogo())
                .set(Tenant::getComments,tenant.getComments())
                .eq(Tenant::getTenantId,tenant.getTenantId()))>0;
        //层级变化或者名称变化
        if(!_tenant.getParentId().equals(tenant.getParentId()) || !_tenant.getTenantName().equals(tenant.getTenantName())){
            this._updateTenant(tenant);
        }

        return result;
    }

    @Override
    @Transactional
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res) {
        try {
            int sheetIndex = 0;
            ExcelOperation excellReader = new ExcelOperation(file.getInputStream());
            XSSFWorkbook wb = excellReader.getWb();
            DataFormat dataFormat = wb.createDataFormat();// 设置可编辑列为文本格式,打开excel编辑不会自动科学计数法
            CellStyle style = wb.createCellStyle();
            style.setDataFormat(dataFormat.getFormat("@"));
            style.setAlignment(HorizontalAlignment.CENTER);//水平居中
            style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            style.setWrapText(true);

            CellStyle style_1 = wb.createCellStyle();
            style_1.cloneStyleFrom(style);
            style_1.setBorderBottom(BorderStyle.THIN);
            style_1.setBorderLeft(BorderStyle.THIN);
            style_1.setBorderRight(BorderStyle.THIN);
            style_1.setBorderTop(BorderStyle.THIN);

            List<String[]> list = excellReader.readAllExcelContent(sheetIndex);
            if (list.size() <= 3) return new ApiResult<>(Constants.RESULT_ERROR_CODE, "模板没有数据,请重新导入");
            if(list.size()>3003) return new ApiResult<>(Constants.RESULT_ERROR_CODE, "一次只支持导入3000,请重新导入");

            List<String> tenantNameList = new ArrayList<>();
            List<String> pTenantNameList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    tenantNameList.add(item[0]);
                }
                if(StringUtils.hasText(item[2])){
                    pTenantNameList.add(item[2]);
                }
            }

            // 租户名_租户
            Map<String, Tenant> tenantNameMap = new HashMap<>();
            Map<String,Tenant> excelTenantNameMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(tenantNameList)){
                List<Tenant> tenantNameExists = baseMapper.selectList(new LambdaQueryWrapper<Tenant>().in(Tenant::getTenantName,tenantNameList));
                if(!CollectionUtils.isEmpty(tenantNameExists)){
                    for(Tenant tenant:tenantNameExists){
                        tenantNameMap.put(tenant.getTenantName(),tenant);
                    }
                }
            }

            // 上级租户名_租户
            Map<String, Tenant> pTenantNameMap = new HashMap<>();
            Map<Integer, Tenant> pTenantIdMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(pTenantNameList)){
                List<Tenant> tenantNameExists = baseMapper.selectList(new LambdaQueryWrapper<Tenant>().in(Tenant::getTenantFullName,pTenantNameList));
                if(!CollectionUtils.isEmpty(tenantNameExists)){
                    for(Tenant tenant:tenantNameExists){
                        pTenantNameMap.put(tenant.getTenantFullName(),tenant);
                        pTenantIdMap.put(tenant.getTenantId(),tenant);
                    }
                }
            }

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("tenant_type");
            List<DictionaryData> dictionaryDatas = dictionaryDataService.listRel(param);
            Map<String,String> dictionaryDataMap = new HashMap<>();
            for (DictionaryData dictionaryData:dictionaryDatas){
                dictionaryDataMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }

            int lastIndex = list.get(2).length;
            List<Tenant> rowList = new ArrayList<>(list.size());
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                Tenant tenant = new Tenant();
                tenant.setTenantName(arr[0]);
                tenant.setTenantType(arr[1]);
                tenant.setComments(arr[3]);

                //租户名称重复校验
                if(StringUtils.hasText(tenant.getTenantName())){
                    //校验数据库里面是否唯一
                    Tenant _obj1 = tenantNameMap.get(tenant.getTenantName());
                    if(_obj1 != null){
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "租户名称唯一重复", style);
                        continue;
                    }

                    //校验excel里面是否唯一
                    Tenant _obj = excelTenantNameMap.get(tenant.getTenantName());
                    if (_obj != null) {
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "租户名称唯一重复导入", style);
                        continue;
                    } else {
                        excelTenantNameMap.put(tenant.getTenantName(), tenant);
                    }
                }else{
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "租户名称不能为空", style);
                    continue;
                }

                if(!StringUtils.hasText(tenant.getTenantType())){
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "租户类型不能为空", style);
                    continue;
                }else{
                    String s = dictionaryDataMap.get(tenant.getTenantType());
                    if(!StringUtils.hasText(s)){
                        //判断是否找到对应代码
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "租户类型错误，未找到对应字典", style);
                        continue;
                    }
                }

                if(StringUtils.hasText(arr[2])){
                    Tenant _tenant = pTenantNameMap.get(arr[2]);
                    if(_tenant == null){
                        //判断是否找到对应代码
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "上级租户未找到，请确认上级租户名称是否全称", style);
                        continue;
                    }
                    tenant.setParentId(_tenant.getTenantId());
                }

                excellReader.removeRow(0,i);
                CommonUtil.changeToNull(tenant);
                rowList.add(tenant);
            }

            if (!rowList.isEmpty()) {
                this.saveBatch(rowList,3000);
                for(Tenant tenant:rowList){
                    if(tenant.getParentId() != null){
                        Tenant _tenant = pTenantIdMap.get(tenant.getParentId());
                        tenant.setTenantCode(_tenant.getTenantCode()+"_<"+tenant.getTenantId()+">");
                        tenant.setTenantFullName(CommonUtil.stringJoiner(_tenant.getTenantFullName(),tenant.getTenantName()));
                    }else{
                        tenant.setTenantCode("<"+tenant.getTenantId()+">");
                        tenant.setTenantFullName(tenant.getTenantName());
                    }
                }
                this.updateBatchById(rowList,3000);
                defaultRoleService.initTenant(rowList);
            }

            int num = list.size() - 3 - rowList.size();
            if (num > 0) {
                excellReader.setCellValue(sheetIndex, 2, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("租户导入_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
                return new ApiResult<>(999, "导入成功:" + rowList.size() + "条，失败：" + num + "条，失败原因详情请查看Excel。", upload.getPath());
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
        return new ApiResult<>(Constants.RESULT_OK_CODE, Constants.RESULT_OK_MSG);
    }

    @Override
    public void templateExport(HttpServletResponse response) {
        try {
            ExcelOperation entity = new ExcelOperation("/tenant_template.xlsx");

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("tenant_type");
            List<DictionaryData> dictionaryDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(dictionaryDatas)){
                String[] tenantTypeArr = new String[dictionaryDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : dictionaryDatas) {
                    tenantTypeArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(1, tenantTypeArr,3,null,false,0);
            }

            entity.print(response, "租户导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }

    @Override
    public List<Integer> getChildrens(Integer tenantId) {
        List<Integer> _tenants = new ArrayList<>();
        List<Tenant> childrenTenants = this.getChildrenTenants(tenantId);
        if (!CollectionUtils.isEmpty(childrenTenants)) {
            _tenants = childrenTenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
        }
        return _tenants;
    }

    @Override
    public List<Tenant> getChildrenTenants(Integer tenantId) {
        List<Tenant> tenants = new ArrayList<>();
        Tenant tenant = baseMapper.selectOne(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId,Tenant::getParentId,Tenant::getTenantCode).eq(Tenant::getTenantId,tenantId));
        if (tenant != null) {
            tenants = baseMapper.selectList(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId, Tenant::getTenantCode, Tenant::getTenantName, Tenant::getTenantFullName, Tenant::getParentId).likeRight(Tenant::getTenantCode, tenant.getTenantCode()));
        }
        return tenants;
    }

    @Override
    public List<Integer> getParents(Integer tenantId) {
        List<Integer> _tenants = new ArrayList<>();
        List<Tenant> parentTenants = this.getParentTenants(tenantId);
        if (!CollectionUtils.isEmpty(parentTenants)) {
            _tenants = parentTenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
        }
        return _tenants;
    }

    @Override
    public List<Tenant> getParentTenants(Integer tenantId) {
        List<Tenant> tenants = new ArrayList<>();
        Tenant tenant =baseMapper.selectOne(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId,Tenant::getParentId,Tenant::getTenantCode).eq(Tenant::getTenantId,tenantId));
        if (tenant != null) {
            Set<String> list = CommonUtil.parentCode(tenant.getTenantCode());
            tenants = baseMapper.selectList(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId, Tenant::getTenantCode, Tenant::getTenantName, Tenant::getTenantFullName, Tenant::getParentId).in(Tenant::getTenantCode, list));
        }
        return tenants;
    }

    /**
     * 递归更新fullName,code
     * @param tenant
     */
    private void  _updateTenant(Tenant tenant){
        String tenantCodeStr = this.getTenantCodeStr(tenant);
        String tenantNameStr = this.getTenantNameStr(tenant);
        tenant.setTenantCode(tenantCodeStr);
        tenant.setTenantFullName(tenantNameStr);
        baseMapper.update(null, Wrappers.<Tenant>lambdaUpdate().eq(Tenant::getTenantId,tenant.getTenantId())
                .set(Tenant::getTenantCode,tenantCodeStr)
                .set(Tenant::getTenantFullName,tenantNameStr));
        List<Tenant> tenants = baseMapper.selectList(Wrappers.<Tenant>lambdaQuery().select(Tenant::getTenantId,Tenant::getTenantName,Tenant::getParentId).eq(Tenant::getParentId, tenant.getTenantId()));
        if(!CollectionUtils.isEmpty(tenants)){
            for(Tenant _tenant : tenants){
                this._updateTenant(_tenant);
            }
        }
    }

    private String getTenantCodeStr(Tenant tenant){
        String _tenant = "<"+tenant.getTenantId()+">";
        if(tenant.getParentId() !=null && tenant.getParentId()!=0){
            Tenant tenant1 = baseMapper.selectOne(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId,Tenant::getParentId).eq(Tenant::getTenantId, tenant.getParentId()));
            if(tenant1 != null){
                _tenant = CommonUtil.stringJoiner(this.getTenantCodeStr(tenant1),_tenant);
            }
        }
        return _tenant;
    }
    private String getTenantNameStr(Tenant tenant){
        String _tenant = tenant.getTenantName();
        if(tenant.getParentId() !=null && tenant.getParentId()!=0){
            Tenant tenant1 = baseMapper.selectOne(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId,Tenant::getParentId,Tenant::getTenantName).eq(Tenant::getTenantId, tenant.getParentId()));
            if(tenant1 != null) {
                _tenant = CommonUtil.stringJoiner(this.getTenantNameStr(tenant1),_tenant);
            }
        }
        return _tenant;
    }
}
