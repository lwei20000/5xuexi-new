package com.struggle.common.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.Constants;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.exam.entity.TPaperQuestion;
import com.struggle.common.exam.mapper.TPaperQuestionMapper;
import com.struggle.common.exam.service.ITPaperQuestionService;
import com.struggle.common.system.entity.FileRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: TPaperQuestionService
 * @Description:  课程试卷题目-ServiceImpl层
 * @author xsk
 */
@Service
public class TPaperQuestionServiceImpl extends ServiceImpl<TPaperQuestionMapper, TPaperQuestion> implements  ITPaperQuestionService {

    @Resource
    private FileDfsUtil fileDfsUtil;

    @Override
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res,Integer paperId) {
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
            if (list.size() <= 1) return new ApiResult<>(Constants.RESULT_ERROR_CODE, "模板没有数据,请重新导入");


            int lastIndex = list.get(0).length;
            List<TPaperQuestion> paperQuestionList = new ArrayList<>();
            boolean errFlag = false;
//            Map<String,Integer> questionGroupMap = new HashMap<>();
            Map<Integer,Integer> questionTypeMap = new HashMap<>();
            for (int i = list.size() - 1; i >= 1; i--) {
                String[] item = list.get(i);
                if(!StringUtils.hasText(item[0]) && !StringUtils.hasText(item[1]) && !StringUtils.hasText(item[2])
                   && !StringUtils.hasText(item[3]) && !StringUtils.hasText(item[4]) && !StringUtils.hasText(item[5])  && !StringUtils.hasText(item[6])){
                    continue;
                }
                TPaperQuestion paperQuestion = new TPaperQuestion();
                paperQuestion.setPaperId(paperId);
                paperQuestion.setQuestionGroup(item[0]);
                if(StringUtils.hasText(item[1])){
                    Integer questionType = null;
                    switch (item[1]){
                        case "单选题":questionType=1;break;
                        case "多选题":questionType=2;break;
                        case "判断题":questionType=3;break;
                        case "填空题":questionType=4;break;
                        case "主观题":questionType=5;break;
                    }
                    paperQuestion.setQuestionType(questionType);
                }
                paperQuestion.setQuestionSort(i);
                paperQuestion.setQuestionTitle(item[2]);
                if(StringUtils.hasText(item[3])){
                    List<String> questionOptions = JSONUtil.stringToList(item[3], String.class);
                    if(!CollectionUtils.isEmpty(questionOptions)){
                        paperQuestion.setQuestionOptions(item[3]);
                    }
                }
                if(StringUtils.hasText(item[4])){
                    paperQuestion.setQuestionScore(Integer.valueOf(item[4]));
                }
                paperQuestion.setQuestionAnswer(item[5]);
                paperQuestion.setQuestionAnalysis(item[6]);
                CommonUtil.changeToNull(paperQuestion);

                String s = ValidatorUtil.validBean(paperQuestion);
                if(StringUtils.hasText(s)){
                   errFlag = true;
                   excellReader.setCellValue(sheetIndex, i, lastIndex, s, style);
                   continue;
                }

//                Integer score = questionGroupMap.get(paperQuestion.getQuestionGroup());
//                if(score == null){
//                    score = paperQuestion.getQuestionScore();
//                    questionGroupMap.put(paperQuestion.getQuestionGroup(),score);
//                }else{
//                    if(!score.equals(paperQuestion.getQuestionScore())){
//                        errFlag = true;
//                        excellReader.setCellValue(sheetIndex, i, lastIndex, "同一分组下，分数必需一致", style);
//                        continue;
//                    }
//                }

                Integer type = questionTypeMap.get(paperQuestion.getQuestionType());
                if(type == null){
                    type = paperQuestion.getQuestionType();
                    questionTypeMap.put(paperQuestion.getQuestionType(),type);
                }else{
                    if(!type.equals(paperQuestion.getQuestionType())){
                        errFlag = true;
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "同一分组下，题型必需一致", style);
                        continue;
                    }
                }

                paperQuestionList.add(paperQuestion);
            }

            if (errFlag) {
                excellReader.setCellValue(sheetIndex, 0, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("导入答题卡_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
                // TODO 上传错误文件
                return new ApiResult<>(999, "失败原因详情请查看Excel。", upload.getPath());
            }else{
                baseMapper.delete(new LambdaQueryWrapper<TPaperQuestion>().eq(TPaperQuestion::getPaperId,paperId));
                this.saveBatch(paperQuestionList,1000);
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
            ExcelOperation entity = new ExcelOperation("/exam_card.xlsx");
            entity.print(response, "题目导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }
}