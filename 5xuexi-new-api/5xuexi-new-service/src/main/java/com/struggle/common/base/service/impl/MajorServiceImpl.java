package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.Major;
import com.struggle.common.base.mapper.MajorCourseMapper;
import com.struggle.common.base.mapper.MajorMapper;
import com.struggle.common.base.param.CopyMajorParam;
import com.struggle.common.base.param.MajorParam;
import com.struggle.common.base.service.MajorService;
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
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.service.DictionaryDataService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 招生专业Service实现
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    @Resource
    private DictionaryDataService dictionaryDataService;
    @Resource
    private FileDfsUtil fileDfsUtil;
    @Resource
    private MajorCourseMapper majorCourseMapper;

    @Override
    public PageResult<Major> pageRel(MajorParam param) {
        PageParam<Major, MajorParam> page = new PageParam<>(param);
        page.setDefaultOrder("majorYear desc");
        List<Major> list = baseMapper.selectPageRel(page, param);
        this.initLastSemester(list);
        return new PageResult<>(list, page.getTotal());
    }

    private void initLastSemester(List<Major> list){
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> majorIds = new ArrayList<>();
            for(Major major:list){
                majorIds.add(major.getMajorId());
            }

            List<Map<String,Object>> maps = majorCourseMapper.selectLastSemester(majorIds);
            if(!CollectionUtils.isEmpty(maps)){
                Map<Integer, Integer> majorMap=new HashMap<>();
                for(Map<String,Object> map:maps){
                    majorMap.put(Integer.valueOf(map.get("majorId").toString()),Integer.valueOf(map.get("lastSemester").toString()));
                }

                for(Major major:list){
                    major.setLastSemester(majorMap.get(major.getMajorId()));
                }
            }
        }
    }

    @Override
    public List<Major> listRel(MajorParam param) {
        List<Major> list = baseMapper.selectListRel(param);
        // 排序
        PageParam<Major, MajorParam> page = new PageParam<>();
        page.setDefaultOrder("majorYear desc");
        return page.sortRecords(list);
    }

    @Override
    public Major getByIdRel(Integer majorId) {
        MajorParam param = new MajorParam();
        param.setMajorId(majorId);
        return param.getOne(baseMapper.selectListRel(param));
    }

    @Override
    public boolean copy(CopyMajorParam major) {
        if(major == null){
            throw new BusinessException("COPY对象不能为空");
        }
        if(!StringUtils.hasText(major.getMajorYear()) && CollectionUtils.isEmpty(major.getMajorIds()) ){
            throw new BusinessException("来源年份和来源专业至少填一个");
        }
        if(!StringUtils.hasText(major.getTargetMajorYear())){
            throw new BusinessException("目标年份不能为空");
        }

        List<Major> majors = null;
        if(!CollectionUtils.isEmpty(major.getMajorIds())){
            majors = baseMapper.selectBatchIds(major.getMajorIds());
        }else{
            majors = baseMapper.selectList(Wrappers.<Major>lambdaQuery().eq(Major::getMajorYear,major.getMajorYear()));
        }

        if(CollectionUtils.isEmpty(majors)){
            throw new BusinessException("根据条件未找到来源专业");
        }

        Set<String> majorNameList = new HashSet<>();
        Set<String> majorCodeList = new HashSet<>();
        Set<String> majorTypeList = new HashSet<>();
        Set<String> majorGradationList = new HashSet<>();
        Set<String> majorFormsList = new HashSet<>();
        Set<String> majorLengthList = new HashSet<>();
        for(Major _major:majors){
            majorNameList.add(_major.getMajorName());
            majorCodeList.add(_major.getMajorCode());
            majorTypeList.add(_major.getMajorType());
            majorGradationList.add(_major.getMajorGradation());
            majorFormsList.add(_major.getMajorForms());
            majorLengthList.add(_major.getMajorLength());
        }

        List<Major> _majors = baseMapper.selectList(Wrappers.<Major>lambdaQuery().eq(Major::getMajorYear, major.getTargetMajorYear())
                .in(Major::getMajorName, majorNameList)
                .in(Major::getMajorCode, majorCodeList)
                .in(Major::getMajorType, majorTypeList)
                .in(Major::getMajorGradation, majorGradationList)
                .in(Major::getMajorForms, majorFormsList)
                .in(Major::getMajorLength, majorLengthList));
        Map<String,Major> _majorMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(_majors)){
            for(Major _major:_majors){
                String key  = _major.getMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                        "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                        "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
                _majorMap.put(key,_major);
            }
        }
        List<Major> addRow = new ArrayList<>();
        for(Major _major:majors){
            _major.setMajorYear(major.getTargetMajorYear());
            String key  = _major.getMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                    "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                    "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
            if(_majorMap.get(key) == null){
                _major.setMajorId(null);
                _major.setCreateTime(null);
                _major.setUpdateTime(null);
                addRow.add(_major);
                _majorMap.put(key,_major);
            }
        }

        if(!CollectionUtils.isEmpty(addRow)){
           this.saveBatch(addRow,3000);
        }

        return true;
    }


    @Override
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
            if(list.size()>5003) return new ApiResult<>(Constants.RESULT_ERROR_CODE, "一次只支持导入5000,请重新导入");


            Set<String> majorYearList = new HashSet<>();
            Set<String> majorCodeList = new HashSet<>();
            Set<String> majorNameList = new HashSet<>();
            Set<String> majorTypeList = new HashSet<>();
            Set<String> majorGradationList = new HashSet<>();
            Set<String> majorFormsList = new HashSet<>();
            Set<String> majorLengthList = new HashSet<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    majorYearList.add(item[0]);
                }
                if(StringUtils.hasText(item[1])) {
                    majorCodeList.add(item[1]);
                }
                if(StringUtils.hasText(item[2])){
                    majorNameList.add(item[2]);
                }
                if(StringUtils.hasText(item[3])){
                    majorTypeList.add(item[3]);
                }
                if(StringUtils.hasText(item[4])){
                    majorGradationList.add(item[4]);
                }
                if(StringUtils.hasText(item[5])){
                    majorFormsList.add(item[5]);
                }
                if(StringUtils.hasText(item[6])){
                    majorLengthList.add(item[6]);
                }
            }
            // KEY_专业
            Map<String, Major> majorMap = new HashMap<>();
            Map<String,Major> excelMajorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(majorYearList) && !CollectionUtils.isEmpty(majorCodeList) && !CollectionUtils.isEmpty(majorNameList)&& !CollectionUtils.isEmpty(majorTypeList)
            && !CollectionUtils.isEmpty(majorGradationList)&& !CollectionUtils.isEmpty(majorFormsList)&& !CollectionUtils.isEmpty(majorLengthList)){
                List<Major> majorExists = baseMapper.selectList(new LambdaQueryWrapper<Major>().in(Major::getMajorYear,majorYearList)
                        .in(Major::getMajorCode, majorCodeList)
                        .in(Major::getMajorName, majorNameList)
                        .in(Major::getMajorType, majorTypeList)
                        .in(Major::getMajorGradation, majorGradationList)
                        .in(Major::getMajorForms, majorFormsList)
                        .in(Major::getMajorLength, majorLengthList));
                if(!CollectionUtils.isEmpty(majorExists)){
                    for(Major _major:majorExists){
                        String key  = _major.getMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                                "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                                "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
                        majorMap.put(key,_major);
                    }
                }
            }

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("major_type");
            List<DictionaryData> majorTypeDatas = dictionaryDataService.listRel(param);
            Map<String,String> majorTypeMap = new HashMap<>();
            for (DictionaryData dictionaryData:majorTypeDatas){
                majorTypeMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }
            param.setDictCode("major_gradation");
            List<DictionaryData> majorGradationDatas = dictionaryDataService.listRel(param);
            Map<String,String> majorGradationMap = new HashMap<>();
            for (DictionaryData dictionaryData:majorGradationDatas){
                majorGradationMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }
            param.setDictCode("major_forms");
            List<DictionaryData> majorFormsDatas = dictionaryDataService.listRel(param);
            Map<String,String> majorFormsMap = new HashMap<>();
            for (DictionaryData dictionaryData:majorFormsDatas){
                majorFormsMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }
            param.setDictCode("major_length");
            List<DictionaryData> majorLengthDatas = dictionaryDataService.listRel(param);
            Map<String,String> majorLengthMap = new HashMap<>();
            for (DictionaryData dictionaryData:majorLengthDatas){
                majorLengthMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }

            int lastIndex = list.get(2).length;
            List<Major> rowList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                Major major = new Major();
                major.setMajorYear(arr[0]);
                major.setMajorCode(arr[1]);
                major.setMajorName(arr[2]);
                major.setMajorType(arr[3]);
                major.setMajorGradation(arr[4]);
                major.setMajorForms(arr[5]);
                major.setMajorLength(arr[6]);
                major.setComments(arr[7]);

                if(!StringUtils.hasText(major.getMajorYear())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "年份不能为空", style);
                    continue;
                }else{
                    if(!CommonUtil.isLegalDate(major.getMajorYear(),"yyyy")){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "年份格式错误：例：2023", style);
                        continue;
                    }
                }
                if(!StringUtils.hasText(major.getMajorCode())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "专业代码不能为空", style);
                    continue;
                }
                if(!StringUtils.hasText(major.getMajorName())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "专业名称不能为空", style);
                    continue;
                }
                if(!StringUtils.hasText(major.getMajorType())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "教育类别不能为空", style);
                    continue;
                }else{
                    String s = majorTypeMap.get(major.getMajorType());
                    if(!StringUtils.hasText(s)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "教育类别错误，未找到对应字典", style);
                        continue;
                    }
                }
                if(!StringUtils.hasText(major.getMajorGradation())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "专业层次不能为空", style);
                    continue;
                }else{
                    String s = majorGradationMap.get(major.getMajorGradation());
                    if(!StringUtils.hasText(s)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "专业层次错误，未找到对应字典", style);
                        continue;
                    }
                }
                if(!StringUtils.hasText(major.getMajorForms())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "学习形式不能为空", style);
                    continue;
                }else{
                    String s = majorFormsMap.get(major.getMajorForms());
                    if(!StringUtils.hasText(s)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "学习形式错误，未找到对应字典", style);
                        continue;
                    }
                }
                if(!StringUtils.hasText(major.getMajorLength())){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "学制不能为空", style);
                    continue;
                }else{
                    String s = majorLengthMap.get(major.getMajorLength());
                    if(!StringUtils.hasText(s)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "学制错误，未找到对应字典", style);
                        continue;
                    }
                }

                String key  = major.getMajorYear()+"_"+major.getMajorCode()+"_"+major.getMajorName()+
                        "_"+major.getMajorType()+"_"+major.getMajorGradation()+
                        "_"+major.getMajorForms()+"_"+major.getMajorLength();
                if(majorMap.get(key) != null){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "专业唯一重复", style);
                    continue;
                }else{
                    Major major1 = excelMajorMap.get(key);
                    if(major1 != null){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "专业唯一重复导入", style);
                        continue;
                    }else{
                        excelMajorMap.put(key,major1);
                    }
                }
                excellReader.removeRow(0,i);
                CommonUtil.changeToNull(major);
                rowList.add(major);
            }

            if (!rowList.isEmpty()) {
               this.saveBatch(rowList,5000);
            }

            int num = list.size() - 3 - rowList.size();
            if (num > 0) {
                excellReader.setCellValue(sheetIndex, 2, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("专业导入_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
                return new ApiResult<>(999, "导入成功:" + rowList.size() + "条，失败：" + num + "条，失败原因详情请查看Excel。", upload.getPath());
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
        return new ApiResult<>(Constants.RESULT_OK_CODE, Constants.RESULT_OK_MSG);
    }

    @Override
    public void templateExport(HttpServletResponse res) {
        try {
            ExcelOperation entity = new ExcelOperation("/major_template.xlsx");

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("major_type");
            List<DictionaryData> majorTypeDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(majorTypeDatas)){
                String[] majorTypeArr = new String[majorTypeDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : majorTypeDatas) {
                    majorTypeArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(3, majorTypeArr,3,null,false,0);
            }
            param.setDictCode("major_gradation");
            List<DictionaryData> majorGradationDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(majorGradationDatas)){
                String[] majorGradationArr = new String[majorGradationDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : majorGradationDatas) {
                    majorGradationArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(4, majorGradationArr,3,null,false,0);
            }
            param.setDictCode("major_forms");
            List<DictionaryData> majorFormsDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(majorFormsDatas)){
                String[] majorFormsArr = new String[majorFormsDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : majorFormsDatas) {
                    majorFormsArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(5, majorFormsArr,3,null,false,0);
            }
            param.setDictCode("major_length");
            List<DictionaryData> majorLengthDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(majorLengthDatas)){
                String[] majorLengthArr = new String[majorLengthDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : majorLengthDatas) {
                    majorLengthArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(6, majorLengthArr,3,null,false,0);
            }

            entity.print(res, "专业导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }
}
