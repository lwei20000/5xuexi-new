package com.struggle.common.core.utils;

import cn.hutool.core.util.ObjectUtil;
import com.struggle.common.core.Constants;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.DataPermissionParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 常用工具方法
 *
 * @author EleAdmin
 * @since 2017-06-10 10:10:22
 */
public class CommonUtil {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

    // 生成uuid的字符
    private static final String[] chars = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    public static String ymdhms = "yyyy-MM-dd HH:mm:ss";

    public static String ymdhm = "yyyy-MM-dd HH:mm";

    public static String ymd = "yyyy-MM-dd";

    //拼接字符串
    public static String stringJoiner(String... strs) {
        StringJoiner _stringJoiner = new StringJoiner("_");
        for (String str:strs){
            _stringJoiner.add(str);
        }
        return _stringJoiner.toString();
    }
    //拼接字符串
    public static String stringJoiner(CharSequence delimiter,String... strs) {
        StringJoiner _stringJoiner = new StringJoiner(delimiter);
        for (String str:strs){
            _stringJoiner.add(str);
        }
        return _stringJoiner.toString();
    }
    //拼接字符串
    public static String stringJoiner(CharSequence delimiter,CharSequence prefix, CharSequence suffix,String... strs) {
        StringJoiner _stringJoiner = new StringJoiner(delimiter,prefix,suffix);
        for (String str:strs){
            _stringJoiner.add(str);
        }
        return _stringJoiner.toString();
    }

    /**
     * 递归CODE处理
     *
     * @param code
     */
    public static Set<String> parentCode(String code) {
        Set<String> list = new HashSet<>();
        list.add(code);
        if(code.indexOf("_")>-1){
            do {
                code = code.replace(code.substring(code.lastIndexOf("_")),"");
                list.add(code);
            }while (code.indexOf("_")>-1);
        }
        return list;
    }
    /**
     * 0或者大于0的整数
     * @param str
     */
    public static boolean isInt(String str) {
        if (!StringUtils.hasText(str)) {
            return false;
        } else {
            String reg = "^(\\+?[1-9][0-9]*$|0)";
            return str.matches(reg);
        }
    }

    /**
     * 校验数据权限
     * @param param
     */
    public static void verifyingPermission(DataPermissionParam param,Integer orgId,String type){
        if(param == null){
            throw new BusinessException(Constants.UNAUTHORIZED_DATA_CODE,Constants.UNAUTHORIZED_DATA_MSG);
        }
        if(StringUtils.hasText(type)){
            String[] splits = type.split(",");
            for(String t:splits){
                if(param.getPermissionType().equals(t)){
                    throw new BusinessException(Constants.UNAUTHORIZED_DATA_CODE,Constants.UNAUTHORIZED_DATA_MSG);
                }
            }
        }
        if(orgId !=null && param.getPermissionType().equals(DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_3.getCode())){
            if(!param.getOrgIds().contains(orgId)){
                throw new BusinessException(Constants.UNAUTHORIZED_DATA_CODE,Constants.UNAUTHORIZED_DATA_MSG);
            }
        }
    }
    /**
     * 比较用户密码
     *
     * @param dbPassword    数据库存储的密码
     * @param inputPassword 用户输入的密码
     * @return boolean
     */
    public static boolean comparePassword(String dbPassword, String inputPassword) {

        return bCryptPasswordEncoder.matches(inputPassword, dbPassword);
    }

    /**
     * md5加密用户密码
     *
     * @param password 密码明文
     * @return 密文
     */
    public static String encodePassword(String password) {

        return password == null ? null : bCryptPasswordEncoder.encode(password);
    }

    /**
     * 生成随机数字
     *
     * @return String
     */
    public static String random(int num) {
        long i1 = (long) ((Math.random() * 9 + 1) * Math.pow(10,num-1));
        return i1+"";
    }

    /**
     * 生成8位uuid
     *
     * @return String
     */
    public static String randomUUID8() {
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            sb.append(chars[x % 0x3E]);
        }
        return sb.toString();
    }


    /**
     * 生成16位uuid
     *
     * @return String
     */
    public static String randomUUID16() {
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 16; i++) {
            String str = uuid.substring(i * 2, i * 2 + 2);
            int x = Integer.parseInt(str, 16);
            sb.append(chars[x % 0x3E]);
        }
        return sb.toString();
    }

    /**
     * 检查List是否有重复元素
     *
     * @param list   List
     * @param mapper 获取需要检查的字段的Function
     * @param <T>    数据的类型
     * @param <R>    需要检查的字段的类型
     * @return boolean
     */
    public static <T, R> boolean checkRepeat(List<T> list, Function<? super T, ? extends R> mapper) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (i != j && mapper.apply(list.get(i)).equals(mapper.apply(list.get(j)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * List转为树形结构
     *
     * @param data           List
     * @param parentId       顶级的parentId
     * @param parentIdMapper 获取parentId的Function
     * @param idMapper       获取id的Function
     * @param consumer       赋值children的Consumer
     * @param <T>            数据的类型
     * @param <R>            parentId的类型
     * @return List<T>
     */
    public static <T, R> List<T> toTreeData(List<T> data, R parentId,
                                            Function<? super T, ? extends R> parentIdMapper,
                                            Function<? super T, ? extends R> idMapper,
                                            BiConsumer<T, List<T>> consumer) {
        List<T> result = new ArrayList<>();
        for (T d : data) {
            R dParentId = parentIdMapper.apply(d);
            if (ObjectUtil.equals(parentId, dParentId)) {
                R dId = idMapper.apply(d);
                List<T> children = toTreeData(data, dId, parentIdMapper, idMapper, consumer);
                consumer.accept(d, children);
                result.add(d);
            }
        }
        return result;
    }

    /**
     * 遍历树形结构数据
     *
     * @param data     List
     * @param consumer 回调
     * @param mapper   获取children的Function
     * @param <T>      数据的类型
     */
    public static <T> void eachTreeData(List<T> data, Consumer<T> consumer, Function<T, List<T>> mapper) {
        for (T d : data) {
            consumer.accept(d);
            List<T> children = mapper.apply(d);
            if (children != null && children.size() > 0) {
                eachTreeData(children, consumer, mapper);
            }
        }
    }

    /**
     * 获取集合中的第一条数据
     *
     * @param records 集合
     * @return 第一条数据
     */
    public static <T> T listGetOne(List<T> records) {
        return records == null || records.size() == 0 ? null : records.get(0);
    }

    /**
     * 支持跨域
     *
     * @param response HttpServletResponse
     */
    public static void addCrossHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Expose-Headers", Constants.TOKEN_HEADER_NAME);
    }

    /**
     * 输出错误信息
     *
     * @param response HttpServletResponse
     * @param code     错误码
     * @param message  提示信息
     * @param error    错误信息
     */
    public static void responseError(HttpServletResponse response, Integer code, String message, String error) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.write(JSONUtil.toJSONString(new ApiResult<>(code, message, null, error)));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有字段，自己的包括递归父级
     * @param object
     * @return
     */
    public static Field[] getAllFields(Object object){
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){ fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()))); clazz = clazz.getSuperclass(); } Field[] fields = new Field[fieldList.size()]; fieldList.toArray(fields); return fields;
    }

    /**
     * 将一个对象里所有的空值属性设置成null
     * @param o
     * @return
     */
    public static Object changeToNull(Object o) {
        try {
            Field[] fs = getAllFields(o);
            for (Field f : fs) {
                f.setAccessible(true);
                String st = f.get(o) + "";
                String str = st.replaceAll(" ", "");
                if (str.equals("") || str == null || str.equals("null")) {
                    f.set(o, null);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
    /*
     * @param list     数据集合
     * @param quantity 分组数量
     * @return 分组结果
     */
    public static <T> List<List<T>> groupListByQuantity(List<T> list, int quantity) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Wrong quantity.");
        }

        List<List<T>> wrapList = new ArrayList<List<T>>();
        int count = 0;
        while (count < list.size()) {
            wrapList.add(new ArrayList<T>(list.subList(count, Math.min((count + quantity), list.size()))));
            count += quantity;
        }

        return wrapList;
    }

    /**
     * 校验时间格式是否正确
     * @param sDate
     * @param format
     * @return
     */
    public static boolean isLegalDate(String sDate, String format) {
        if (sDate != null && sDate.length() == format.length()) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);

            try {
                Date date = formatter.parse(sDate);
                return sDate.equals(formatter.format(date));
            } catch (Exception var4) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 日期转字符串
     * @param data
     * @param formatType
     * @return
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }
    /**
     * 字符串转日期
     * @param dataStr
     * @param formatType
     * @return
     */
    public static Date StringTodate(String dataStr, String formatType) {
        try {
            return new SimpleDateFormat(formatType).parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
