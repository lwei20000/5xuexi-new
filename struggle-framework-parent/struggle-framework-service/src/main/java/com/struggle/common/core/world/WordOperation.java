package com.struggle.common.core.world;

import com.struggle.common.core.exception.BusinessException;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

public class WordOperation {

    private XWPFDocument document;
    /**
     * 构造函数，选择需要处理的excel文件路径
     *
     * @throws Exception
     */
    public WordOperation(String strFilePath) {
        try {

            ResourceLoader resourceLoader = new DefaultResourceLoader();
            InputStream inputStream = resourceLoader.getResource(WordConstant.WORD.COLLEGE_WORD_PATH.getCode()  + strFilePath).getInputStream();
            document =  new XWPFDocument(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void print(HttpServletResponse response, String fileName) throws Exception {
        OutputStream ouputStream = null;
        try {
            response.setContentType("application/msword");
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + encodedFileName);

            response.setHeader("Access-Control-Allow-Origin","*");
            ouputStream = response.getOutputStream();
            document.write(ouputStream);
            ouputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器异常[请联系管理员]");
        }finally {
            if(ouputStream !=null){
                ouputStream.close();
            }
            if(document != null){
                document.close();
            }
        }
    }

    /**
     * 返回当前处理过的文件
     * @return 处理后的文件对象
     * @throws IOException 如果文件保存失败
     */
    public File getProcessedFile(String prefix,String suffix) throws IOException {
        // 手动创建文件，不包含时间戳
        File tempFile = new File(prefix + suffix);
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            // 将文档写入临时文件
            document.write(outputStream);
        }
        return tempFile;
    }

    /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     * @return 成功返回true,失败返回false
     */
    public void changWord(Map<String, Object> textMap, List<Object[]> tableList) {
        //解析替换文本段落对象
        WordOperation.changeText(document, textMap);
        //解析替换表格对象
        WordOperation.changeTable(document, textMap, tableList);
    }
    /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     * @param inputUrl 模板存放地址
     * @param outputUrl 新文档存放地址
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     * @return 成功返回true,失败返回false
     */
    public static boolean changWord(String inputUrl, String outputUrl,
                                    Map<String, Object> textMap, List<Object[]> tableList) {

        //模板转换默认成功
        boolean changeFlag = true;
        try {
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(inputUrl));
            //解析替换文本段落对象
            WordOperation.changeText(document, textMap);
            //解析替换表格对象
            WordOperation.changeTable(document, textMap, tableList);

            //生成新的word
            File file = new File(outputUrl);
            FileOutputStream stream = new FileOutputStream(file);
            document.write(stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
            changeFlag = false;
        }

        return changeFlag;

    }

    /**
     * 替换段落文本
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     */
    public static void changeText(XWPFDocument document, Map<String, Object> textMap){
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (XWPFParagraph paragraph : paragraphs) {
            //判断此段落时候需要进行替换
            String text = paragraph.getText();
            if(checkText(text)){
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    //替换模板原来位置
                    Object o = changeValue(run.toString(), textMap);
                    if (o instanceof String) {
                        run.setText(o.toString(),0);
                    } else if (o instanceof ImgObject) {
                        run.setText("",0);
                        // 对象是ImgObject类型
                        initImg(run,o);
                    }else{
                        run.setText("",0);
                    }
                }
            }
        }
    }

    /**
     * 替换表格对象方法
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     */
    public static void changeTable(XWPFDocument document, Map<String, Object> textMap,
                                   List<Object[]> tableList){
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            //只处理行数大于等于2的表格，且不循环表头
            XWPFTable table = tables.get(i);
            if(table.getRows().size()>1){
                //判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
                if(checkText(table.getText())){
                    List<XWPFTableRow> rows = table.getRows();
                    //遍历表格,并替换模板
                    eachTable(rows, textMap);
                }else{
//                  System.out.println("插入"+table.getText());
                    insertTable(table, tableList);
                }
            }
        }
    }

    /**
     * 遍历表格
     * @param rows 表格行对象
     * @param textMap 需要替换的信息集合
     */
    public static void eachTable(List<XWPFTableRow> rows ,Map<String, Object> textMap){
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                //判断单元格是否需要替换
                if(checkText(cell.getText())){
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (XWPFParagraph paragraph : paragraphs) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (XWPFRun run : runs) {
                            Object o = changeValue(run.toString(), textMap);
                            if (o instanceof String) {
                                run.setText(o.toString(),0);
                            } else if (o instanceof ImgObject) {
                                run.setText("",0);
                                // 对象是ImgObject类型
                                initImg(run,o);
                            }else{
                                run.setText("",0);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 替换图片
     */
    public static void initImg(XWPFRun run, Object o){
        try {
            if(o != null){
                ImgObject imgObject = (ImgObject)o;
                if(StringUtils.hasText(imgObject.getImgUrl())){
                    URL url = new URL(imgObject.getImgUrl());

                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();

                    String name = imgObject.getImgName();
                    String imgUrl = imgObject.getImgUrl();
                    if(!StringUtils.hasText(name)){
                        name = imgUrl.substring(imgUrl.lastIndexOf("/")+1,imgUrl.lastIndexOf("."));
                    }
                    int type = 0;
                    String suffix = imgUrl.substring(imgUrl.lastIndexOf(".")+1).toUpperCase();
                    switch (suffix){
                        case "JPG": type = XWPFDocument.PICTURE_TYPE_JPEG; break;
                        case "JPEG": type = XWPFDocument.PICTURE_TYPE_JPEG; break;
                        case "PNG": type = XWPFDocument.PICTURE_TYPE_PNG; break;
                        case "GIF": type = XWPFDocument.PICTURE_TYPE_GIF; break;
                    }

                    run.addPicture(inputStream, type, name, Units.toEMU(imgObject.getWidth()), Units.toEMU(imgObject.getHeight()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 为表格插入数据，行数不够添加新行
     * @param table 需要插入数据的表格
     * @param tableList 插入数据集合
     */
    public static void insertTable(XWPFTable table, List<Object[]> tableList){
        if(!CollectionUtils.isEmpty(tableList)){
            //创建行,根据需要插入的数据添加新行，不处理表头
            for(int i = 1; i < tableList.size(); i++){
                XWPFTableRow row =table.createRow();
            }
            //遍历表格插入数据
            List<XWPFTableRow> rows = table.getRows();
            for(int i = 1; i < rows.size(); i++){
                XWPFTableRow newRow = table.getRow(i);
                List<XWPFTableCell> cells = newRow.getTableCells();
                for(int j = 0; j < cells.size(); j++){
                    XWPFTableCell cell = cells.get(j);
                    Object o = tableList.get(i - 1)[j];
                    if (o instanceof String) {
                        cell.setText(o.toString());
                    } else if (o instanceof ImgObject) {
                        // 对象是ImgObject类型
                        XWPFParagraph paragraph =null;
                        if(cell.getParagraphs().isEmpty()){
                            paragraph = cell.addParagraph();
                        }else{
                            paragraph = cell.getParagraphs().get(0);// 获取单元格的第一个段落
                        }
                        initImg(paragraph.createRun(),o);
                    }else{
                        cell.setText("");
                    }
                }
            }
        }
    }

    /**
     * 判断文本中时候包含$
     * @param text 文本
     * @return 包含返回true,不包含返回false
     */
    public static boolean checkText(String text){
        boolean check  =  false;
        if(text.indexOf("$")!= -1){
            check = true;
        }
        return check;
    }

    /**
     * 匹配传入信息集合与模板
     * @param value 模板需要替换的区域
     * @param textMap 传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    public static Object changeValue(String value, Map<String, Object> textMap){
        Set<Map.Entry<String, Object>> textSets = textMap.entrySet();
        for (Map.Entry<String, Object> textSet : textSets) {
            //匹配模板与替换值 格式${key}
            String key = "${"+textSet.getKey()+"}";
            if(value.indexOf(key)!= -1){
                return textSet.getValue();
            }
        }
        //模板未匹配到区域替换为空
        if(checkText(value)){
            value = "";
        }
        return value;
    }

    public static void main(String[] args) {
//        //模板文件地址
//        String inputUrl = "E:\\_aliyun\\jxuxue\\5xuexi-new-api\\5xuexi-new-front\\src\\main\\resources\\template\\学籍表.doc";
//        //新生产的模板文件
//        String outputUrl = "C:\\Users\\Administrator\\Downloads\\学籍表1.docx";

        //模板文件地址
        String inputUrl = "E:\\_aliyun\\jxuxue\\5xuexi-new-api\\5xuexi-new-front\\src\\main\\resources\\template\\登记表.doc";
        //新生产的模板文件
        String outputUrl = "C:\\Users\\Administrator\\Downloads\\登记表1.docx";

        Map<String, Object> testMap = new HashMap<>();

        testMap.put("xm", "张三");
        testMap.put("xb", "男");
        testMap.put("mz", "汉族");
        testMap.put("zzmm", "群众");
        testMap.put("jtzz", "测试地址");
        testMap.put("gzdw", "测试单位");
        testMap.put("csrq", "20180802");
        testMap.put("tx", new ImgObject("头像","http://5xuexi-new-vod.5xuexi.com/PGSbyXotqkKoduKI_rBkAAWSrq1qAD1HaAAA42XRIxCQ814.jpg",100,120));

        testMap.put("sfzzm", new ImgObject("身份证正面","http://5xuexi-new-vod.5xuexi.com/L3k1DpmsePTigG4J_rBkAAWSrq8yAIxNHAAFjx5-gTa0042.jpg",200,140));

        testMap.put("sfzfm", new ImgObject("身份证反面","http://5xuexi-new-vod.5xuexi.com/L3k1DpmsePTigG4J_rBkAAWSrq8yAIxNHAAFjx5-gTa0042.jpg",200,140));

        testMap.put("nf", "2025");
        testMap.put("zy", "测试专业");
        testMap.put("cc", "专升本");
        testMap.put("xz", "5");
        testMap.put("xs", "函授");
        testMap.put("rxsj", "20200115");
        testMap.put("bysj", "20250715");
        testMap.put("xxmc", "长春大学");


        List<Object[]> testList = new ArrayList<Object[]>();
//        testList.add(new Object[]{"1","1AA","1BB","1CC"});
//        testList.add(new Object[]{"2","2AA","2BB","2CC"});
//        testList.add(new Object[]{"3","3AA","3BB","3CC"});
//        testList.add(new Object[]{"4","4AA","4BB","4CC"});

        WordOperation.changWord(inputUrl, outputUrl, testMap, testList);
    }
}
