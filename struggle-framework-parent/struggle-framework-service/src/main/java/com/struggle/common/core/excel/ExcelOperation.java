package com.struggle.common.core.excel;

import com.struggle.common.core.exception.BusinessException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @func Excel操作类
 *
 * @author chenbingcun
 *
 * @date
 */
public class ExcelOperation {

    private XSSFWorkbook wb;

    /**
     * 构造函数，选择需要处理的excel文件路径
     *
     * @throws Exception
     */
    public ExcelOperation(InputStream inputStream) throws Exception {
        try {
            wb = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * getByteArray
     * @return
     */
    public byte[] getByteArray() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return os.toByteArray();
    }

    /**
     * 构造函数，选择需要处理的excel文件路径
     *
     * @param strFilePath ex D:/test.xlsx
     * @throws Exception
     */
    public ExcelOperation(String strFilePath) throws Exception {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            InputStream inputStream = resourceLoader.getResource(ExcelConstant.EXCEL.COLLEGE_EXCEL_PATH.getCode() + strFilePath).getInputStream();
            wb = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void removeRow(int sheetIndex,int rowIndex) {
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        int lastRowNum=sheet.getLastRowNum();
        if(rowIndex>=0&&rowIndex<lastRowNum)
            sheet.shiftRows(rowIndex+1,lastRowNum,-1);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
        if(rowIndex==lastRowNum){
            XSSFRow removingRow=sheet.getRow(rowIndex);
            if(removingRow!=null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    /**
     * 通过sheet指定坐标获取样式
     *
     * @return
     */
    public CellStyle getCellStyle(int sheetIndex, int rowNum, int columnNum) {
        if (wb.getNumberOfSheets() < sheetIndex) {
            return wb.createCellStyle();
        }
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        if (sheet == null || sheet.getRow(rowNum) == null || sheet.getRow(rowNum).getCell(columnNum) == null) {
            return wb.createCellStyle();
        }
        XSSFCell cell = sheet.getRow(rowNum).getCell(columnNum);
        return cell.getCellStyle();
    }


    /**
     * 通过sheet指定坐标设置值
     *
     * @param sheetIndex sheet索引
     * @param rowIndex
     * @param columnIndex
     * @param rawValue 可以为null
     * @param cellStyle 可以为null
     * @return
     * @throws Exception
     */
    public Boolean setCellValue(int sheetIndex, int rowIndex, int columnIndex, Object rawValue, CellStyle cellStyle)  {
        if (wb.getNumberOfSheets() < sheetIndex) {
            return false;
        }
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        if (sheet == null) {
            return false;
        }
        XSSFCell cell = null;
        // cell不存在创建
        if (sheet.getRow(rowIndex) == null) {
            XSSFRow row = sheet.createRow(rowIndex);
            row.setHeight((short) (30 * 20));
            cell = row.createCell(columnIndex);
        } else if (sheet.getRow(rowIndex).getCell(columnIndex) == null) {
            cell = sheet.getRow(rowIndex).createCell(columnIndex);
        } else {
            cell = sheet.getRow(rowIndex).getCell(columnIndex);
        }
        try {
            if (rawValue != null) {
                if (rawValue.getClass().getSimpleName().equals("Date")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cell.setCellValue(sdf.format(rawValue).replace("00:00:00",""));
                }else{
                    cell.setCellValue(String.valueOf(rawValue));
                }
                cell.setCellType(CellType.STRING);
            }
            if (cellStyle != null)
                cell.setCellStyle(cellStyle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void print(HttpServletResponse response, String fileName) throws Exception {
        OutputStream ouputStream = null;
        try {
            response.setContentType("application/vnd.ms-excel");
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + encodedFileName);

            response.setHeader("Access-Control-Allow-Origin","*");
            ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器异常[请联系管理员]");
        }finally {
            if(ouputStream !=null){
                ouputStream.close();
            }
            if(wb != null){
                wb.close();
            }
        }
    }

    /**
     * 设置sheet名
     *
     * @param sheetIndex sheet顺
     * @param sheetName sheet名
     */
    public boolean setSheetName(int sheetIndex, String sheetName) {
        if (wb.getNumberOfSheets() <= sheetIndex) {
            wb.createSheet();
        }
        wb.setSheetName(sheetIndex, sheetName);
        return true;
    }

    /**
     * 合并单元格
     *
     * @param sheetIndex
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     */
    public boolean cellRangeAddress(int sheetIndex, int firstRow, int lastRow, int firstCol, int lastCol) {
        if (wb.getNumberOfSheets() < sheetIndex) {
            return false;
        }
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        if (sheet == null) {
            return false;
        }
        CellRangeAddress cellRange = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cellRange);
        RegionUtil.setBorderRight(null, cellRange, sheet);
        return true;
    }

    /**
     *
     * @param sheetIndex
     * @param firstIndex
     * @param rows
     * @param maxRow 之前复制sheet的最大数据行
     * @return
     */
    public boolean addRows(int sheetIndex, int firstIndex, List<XSSFRow> rows, int maxRow) {
        if (wb.getNumberOfSheets() < sheetIndex) {
            return false;
        }
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        if (sheet == null) {
            return false;
        }

        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
        cellCopyPolicy.setCopyCellStyle(false);
        cellCopyPolicy.setCondenseRows(true);
        if(maxRow>0){
            // 解决同行号不能复制的bug
            sheet.copyRows(rows,firstIndex+maxRow,cellCopyPolicy);
            delRow(sheet,firstIndex,maxRow);
        }else{
            sheet.copyRows(rows,firstIndex,cellCopyPolicy);
        }
        return true;
    }

    /// <summary>
    /// 删除行
    /// </summary>
    /// <param name="sheet">处理的sheet</param>
    /// <param name="startRow">从第几行开始（0开始）</param>
    /// <param name="delCount">共删除N行</param>
    public static void delRow(XSSFSheet sheet, int startRow, int delCount) {
        //sheet.shiftRows(startRow + 1, startRow + delCount+1, -startRow);
        for (int i = 0; i < delCount; i++) {
            sheet.shiftRows(startRow + 1, startRow + delCount+delCount+1, -1);
        }
    }


    /**
     * 设置公式的值
     *
     * @param sheetIndex sheet
     * @param rowIndex 行
     * @param columnIndex 列
     * @param formulaValue 公式的值
     * @return 是否成功
     */
    public boolean setCellFormula(int sheetIndex, int rowIndex, int columnIndex, String formulaValue) {
        if (wb.getNumberOfSheets() < sheetIndex) {
            return false;
        }
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        if (sheet == null) {
            return false;
        }
        XSSFCell cell = null;
        // cell不存在创建
        if (sheet.getRow(rowIndex) == null) {
            XSSFRow row = sheet.createRow(rowIndex);
            cell = row.createCell(columnIndex);
        } else if (sheet.getRow(rowIndex).getCell(columnIndex) == null) {
            cell = sheet.getRow(rowIndex).createCell(columnIndex);
        } else {
            cell = sheet.getRow(rowIndex).getCell(columnIndex);
        }
        cell.setCellFormula(formulaValue);
        return true;
    }
    /**
     * 插入行
     * @param sheetIndex 插入sheet
     */
    public XSSFRow getRow(int sheetIndex, int rowInde) {
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        if(sheet != null){
            return sheet.getRow(rowInde);
        }else{
            return null;
        }
    }

    /**
     * 获取 Excel工作簿对象
     *
     * @return XSSFWorkbook Excel工作簿对象
     */
    public XSSFWorkbook getWb() {
        return wb;
    }

    /**
     * 读取Excel数据内容
     *
     * @return Map 包含单元格数据内容的Map对象
     */
    public List<String[]> readAllExcelContent(int sheetIndex) {
        List<String[]> list = new ArrayList<String[]>();
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        XSSFRow row = sheet.getRow(0);
        int cellNum = row.getLastCellNum();
        // 正文内容应该从第一行开始,全部内容
        for (int i = 0; i <= rowNum; i++) {
            row = sheet.getRow(i);
            if(row == null){
                 continue;
            }
            // 创建一个数组 用来存储每一列的值
            String[] str = new String[cellNum];
            // 列数
            for (int j = 0; j < cellNum; j++) {
                // 获取第i行，第j列的值
                if (row.getCell(j) == null) {
                    continue;
                }
                str[j] = getValue(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
            }
            // 把刚获取的列存入list
            list.add(str);
        }
        return list;
    }

    private static String getValue(Cell cell) {
        if(cell.getCellType().equals(CellType.FORMULA) //公式
                || cell.getCellType().equals(CellType.BLANK) //空值
                || cell.getCellType().equals(CellType.ERROR)) {//错误
            return "";
        }else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }else if (cell.getCellType() == CellType.NUMERIC) {
            //数值类型又具体区分日期类型，单独处理
            if (DateUtil.isCellDateFormatted(cell)) {
                return  null;
            } else {
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            }
        } else {
            return StringUtils.trimWhitespace(cell.getStringCellValue());
        }
    }



    /**
     * 通用导出
     * @param title 标题
     * @param data  数据
     * @param colNames 列名集合（全集合）
     * @param colCodes 列名对应的字段集合（全集合）
     */
    public void commExport(HttpServletResponse response, String title, List<Map<String,Object>> data, List<String> colNames, List<String> colCodes) throws Exception{
        OutputStream ouputStream = null;
        SXSSFWorkbook workbook = null;
        try{
            workbook = new SXSSFWorkbook(wb,1000);
            Sheet sheet = workbook.getSheetAt(0);
            //样式5
            CellStyle style_5 = workbook.createCellStyle();
            style_5.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中对齐
            style_5.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            style_5.setBorderBottom(BorderStyle.THIN); //下边框
            style_5.setBorderRight(BorderStyle.THIN);//右边框
            style_5.setFillForegroundColor(IndexedColors.WHITE.getIndex());// 设置背景色
            style_5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style_5.setWrapText(true);

            //字体5
            Font font_5 = workbook.createFont();
            font_5.setFontHeightInPoints((short) 12);
            font_5.setFontName("新宋体");
            style_5.setFont(font_5);

            //样式3
            CellStyle style_3 = workbook.createCellStyle();
            style_3.setAlignment(HorizontalAlignment.CENTER); // 创建一个左对齐
            style_3.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            style_3.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());// 设置背景色
            style_3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style_3.setBorderBottom(BorderStyle.THIN); //下边框
            style_3.setBorderRight(BorderStyle.THIN);//右边框

            //字体3
            Font font_3 = workbook.createFont();
            font_3.setFontHeightInPoints((short) 12);
            font_3.setBold(true);
            font_3.setFontName("新宋体");
            style_3.setFont(font_3);

            //样式1
            CellStyle style_1 = workbook.createCellStyle();
            style_1.setAlignment(HorizontalAlignment.CENTER);
            style_1.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            style_1.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());// 设置背景色
            style_1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            //字体1
            Font font_1 = workbook.createFont();
            font_1.setFontHeightInPoints((short) 24);
            font_1.setBold(true);
            font_1.setFontName("新宋体");
            style_1.setFont(font_1);

            Row row = sheet.createRow((short) 0);
            Cell cell = row.createCell((short) 0);
            row.setHeight((short) (50 * 20));
            cell.setCellValue(title.replace(".xlsx",""));
            cell.setCellStyle(style_1);

            CellStyle defaultStyle = workbook.createCellStyle();
            // 设置为文本格式，防止身份证号变成科学计数法
            DataFormat defaultStyleFormat = workbook.createDataFormat();
            defaultStyle.setDataFormat(defaultStyleFormat.getFormat("@"));
            defaultStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
            defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            defaultStyle.setWrapText(true);

            //固定row
            sheet.createFreezePane(0,2,0,2);

            //参数1：起始行号 参数2：终止行号 参数3：起始列号 参数4：终止列号
            CellRangeAddress cra =new CellRangeAddress(0, (short) 0, 0, colNames.size()-1);
            sheet.addMergedRegion(cra);
            RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);

            // 行、行
            int i = 2,j = 0;
            //动态表头
            Row hedRow = sheet.createRow(1);
            hedRow.setHeight((short) (30 * 20));
            for (String colName : colNames) {
                Cell cell1 = hedRow.createCell(j++);
                sheet.setColumnWidth(j,256*25+184);
                sheet.setDefaultColumnStyle(j, defaultStyle);
                //设置值和样式
                cell1.setCellValue(colName);
                cell1.setCellStyle(style_3);
            }
            //表头对应数据
            j = 0;
            for (Map <String, Object> entityMap : data) {
                Row leRow = sheet.createRow(i);
                leRow.setHeight((short) (30 * 20));
                for (String colCode : colCodes){
                    Cell cell1 = leRow.createCell(j++);
                    if(entityMap.get(colCode) != null){
                        cell1.setCellValue(entityMap.get(colCode).toString());
                    }
                    cell1.setCellStyle(style_5);
                }
                j = 0;i++;
            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-disposition", "attachment;filename=" +  new String(title.getBytes("gb2312"),"ISO8859-1"));
            response.setHeader("Access-Control-Allow-Origin","*");

            ouputStream = response.getOutputStream();
            workbook.write(ouputStream);
            ouputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器异常[请联系管理员]");
        }finally {
            if(ouputStream !=null){
                ouputStream.close();
            }
            if(workbook != null){
                workbook.close();
            }
            if(wb != null){
                wb.close();
            }
        }
    }

    private static XSSFCellStyle createXssfCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        XSSFDataFormat fmt = workbook.createDataFormat();
        style.setDataFormat(fmt.getFormat("m/d/yy h:mm"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 构造有效性数据约束
     */
    public void generateRangeList(int column, String[] options,Integer rownum1,Integer rownum2,boolean fuzzy,int sheetIndex) {

        if(rownum1 == null){
            rownum1 = 1;
        }
        if(rownum2 == null){
            rownum2 = 65535;
        }
        if (options.length == 0){
            return;
        }
        //获取所有sheet页个数
        int sheetTotal = wb.getNumberOfSheets();
        String hiddenSheetName = "hiddenSheet" + sheetTotal;
        XSSFSheet hiddenSheet = (XSSFSheet)wb.createSheet(hiddenSheetName);

        Row row;
        //写入下拉数据到新的sheet页中
        String columnName = this.getColumnName(column);
        for (int i = 0; i < options.length; i++) {
            row = hiddenSheet.createRow(i);
            Cell cell = row.createCell(column);
            cell.setCellValue(options[i]);
            if(fuzzy){
                hiddenSheet.setArrayFormula("INDEX("+columnName+":"+ columnName +",SMALL(IF(ISNUMBER(FIND(CELL(\"contents\"),$"+columnName+"$1:$"+columnName+"$"+options.length+")),ROW($"+columnName+"$1:$"+columnName+"$"+options.length+"),4^8),ROW("+columnName+(i+1)+")))&\"\"", new CellRangeAddress(i,i,column+1,column+1));
            }
        }
        //获取新sheet页内容
        String strFormula = hiddenSheetName + "!$"+this.getColumnName(column)+"$1:$"+this.getColumnName(column)+"$"+options.length;
        if(fuzzy){
            String strFormula_1 = hiddenSheetName + "!$"+this.getColumnName(column+1)+"$1:$"+this.getColumnName(column+1)+"$"+options.length;
            strFormula = "OFFSET("+hiddenSheetName+"!$"+this.getColumnName(column+1)+"$1,0,0,COUNTA("+strFormula_1+")-COUNTIF("+strFormula_1+",\"\"),1)";
        }

        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        Name namedCell = wb.createName();
        namedCell.setNameName(hiddenSheetName);
        namedCell.setRefersToFormula(strFormula);
        CellRangeAddressList regions = new CellRangeAddressList(rownum1,rownum2, column, column);
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = dvHelper.createFormulaListConstraint(hiddenSheetName);
        DataValidation validation = dvHelper.createValidation(constraint, regions);
        sheet.addValidationData(validation);

        wb.setSheetHidden(sheetTotal, true);
    }

    /**
     *  构造名称管理器和数据验证及公式
     *
     * @param dropDownDataSource 以父级id为key,子级名称列表为value的集合
     * @param columnStep 起始列的列号（以下表0为初始列）
     * @param totalLevel 总共的层级数量
     */
    public void cascade(Map<String, List<String>> dropDownDataSource, int columnStep, final int totalLevel,Integer rownum1,Integer rownum2,int sheetIndex) {
        if(rownum1 == null){
            rownum1 = 1;
        }
        if(rownum2 == null){
            rownum2 = 65535;
        }

        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        int sheetTotal = wb.getNumberOfSheets();
        String dataSourceSheetName = "hiddenSheet" + sheetTotal;

        Sheet dataSourceSheet = wb.createSheet(dataSourceSheetName);
        wb.setSheetHidden(wb.getSheetIndex(dataSourceSheet), true);

        Row headerRow = dataSourceSheet.createRow(0);
        boolean firstTime = true;
        int columnIndex = 0;

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        // 构造名称管理器数据源
        for (String key : dropDownDataSource.keySet()) {

            String columnName = this.getColumnName(columnIndex);

            Cell cell = headerRow.createCell(columnIndex);
            cell.setCellValue(key);
            if (dropDownDataSource.get(key) == null || dropDownDataSource.get(key).size() == 0) {
                continue;
            }
            ArrayList<String> values = (ArrayList) dropDownDataSource.get(key);
            int dataRowIndex = 1;
            for (int i=0;i<values.size();i++) {
                Row row = firstTime ? dataSourceSheet.createRow(dataRowIndex) : dataSourceSheet.getRow(dataRowIndex);
                if (row == null) {
                    row = dataSourceSheet.createRow(dataRowIndex);
                }
                row.createCell(columnIndex).setCellValue(values.get(i));
                dataSourceSheet.setArrayFormula("INDEX("+columnName+":"+ columnName +",SMALL(IF(ISNUMBER(FIND(CELL(\"contents\"),$"+columnName+"$2:$"+columnName+"$"+(values.size()+1)+")),ROW($"+columnName+"$2:$"+columnName+"$"+(values.size()+1)+"),4^8),ROW("+columnName+(i+1)+")))&\"\"", new CellRangeAddress(i+1,i+1,columnIndex+1,columnIndex+1));
                dataRowIndex++;
            }

            // 构造名称管理器
            String range = buildRange(columnIndex, 2, values.size());
            Name name = wb.createName();
            name.setNameName(key);
            String formula = dataSourceSheetName + "!" + range;
            name.setRefersToFormula(formula);

            if (firstTime) {
                CellRangeAddressList _regions = new CellRangeAddressList(rownum1,rownum2, columnStep, columnStep);
                DataValidationConstraint constraint = dvHelper.createFormulaListConstraint(key);
                DataValidation validation = dvHelper.createValidation(constraint, _regions);
                sheet.addValidationData(validation);
            }

            columnIndex = columnIndex+2;
            firstTime = false;
        }

        // 剩下的层级设置DataValidation
        for (int i = 1; i < totalLevel; i++) {
            char[] offset = new char[1];
            offset[0] = (char) ('A' + columnStep + i - 1);
            String formulaString = buildFormulaString(new String(offset), rownum1+1);
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(formulaString);
            CellRangeAddressList regions = new CellRangeAddressList(rownum1, rownum2, 0 + columnStep + i, 0 + columnStep + i);
            XSSFDataValidation dataValidationList = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
            sheet.addValidationData(dataValidationList);
        }
    }
    private String buildRange(int offset, int startRow, int rowCount) {
        String start = getColumnName(offset);
        return "$" + start + "$" + startRow + ":$" + start + "$" + (startRow + rowCount - 1);
    }

    private String buildFormulaString(String offset, int rowNum) {
        return "INDIRECT($" + offset + (rowNum) + ")";
    }

    /**
     * 该方法用来将具体的数据转换成Excel中的ABCD列
     * @return column:ABCD列名称
     * **/
    public  String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "A";
        }
        columnIndex+=1;
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }
}
