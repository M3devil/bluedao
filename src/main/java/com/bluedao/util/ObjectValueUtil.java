package com.bluedao.util;

import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/14 10:45
 * @类职责： 对数据进行常规的操作
 */
public class ObjectValueUtil {

    /**
     * 正则表达式 用于匹配下划线
     */
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 正则表达式 用于匹配大写字母
     */
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 日志信息
     */
    private static final Logger LOGGER = LogUtil.getLogger();

    /**
     * 字符串类型判断
     * @param str 字符串
     * @return 结果
     */
    public static boolean isEmpty(String str){
        return null == str || "".equals(str);
    }

    /**
     * 集合类型判断
     * @param collection 集合
     * @return 结果
     */
    public static boolean isEmpty(Collection<?> collection){
        return null == collection || collection.isEmpty();
    }

    /**
     * map类型判断
     * @param map map
     * @return 结果
     */
    public static boolean isEmpty(Map<?,?> map){
        return null == map || map.isEmpty();
    }

    /**
     * 数组类型判断
     */
    public static boolean isEmpty(Object[] objects){
        return null == objects || objects.length == 0;
    }

    /**
     * 空对象判断
     * @param obj 对象
     * @return 结果
     */
    public static boolean isEmpty(Object obj){
        return null == obj;
    }

    /**
     * 将字符串的首字符小写
     * @param original 传入的字符串
     * @return 修改后的字符串
     */
    public static String firstLowercase(String original){
//        return Introspector.decapitalize(original);
        char[] chars = original.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 字符串首字母大写
     * @param original 传入字符串
     * @return 修改后字符串
     */
    public static String firstUpperCase(String original){
        char[] chars = original.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    /**
     * 下划线转驼峰
     *
     * @param str 待转换字符串
     * @return 驼峰风格字符串
     */
    public static String lineToHump(String str) {
        //将小写转换
        String newStr = str.toLowerCase();
        Matcher matcher = linePattern.matcher(newStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param str 待转换字符串
     * @return 下划线风格字符串
     */
    public static String humpToLine(String str) {
        //将首字母先进行小写转换
        String newStr = str.substring(0, 1).toLowerCase() + str.substring(1);
        //比对字符串中的大写字符
        Matcher matcher = humpPattern.matcher(newStr);
        StringBuffer sb = new StringBuffer();
        //匹配替换
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将数据库字段名转化为类的命名规则，即下划线改驼峰+首字母大写，例如要获取if_freeze字段的方法，方法为getIfFreeze(),
     *
     * @param str 传入字段
     * @return 下划线改驼峰+首字母大写
     */
    public static String columnNameToMethodName(String str) {
        return firstUpperCase(lineToHump(str));
    }

    /**
     * 将传入的表名去除t_字符，转化为类名
     *
     * @param str 传入字段
     * @return 取出t_的下划线转驼峰+首字母大写字段
     */
    public static String tableNameToClassName(String str) {
        if ("t_".equals(str.substring(0, 2))) {
            return firstUpperCase(lineToHump(str.substring(2)));
        } else {
            return firstUpperCase(lineToHump(str));
        }
    }

    /**
     * 对字符串去空白符和换行符等
     *
     * @return 字符串
     */
    public static String stringTrim(String src) {
        return (null != src) ? src.trim() : null;
    }

    /**
     * 查找一个类上的某一个注解的一个配置属性信息
     * @param clazzAnnotation 目标注解类
     * @return 注解信息
     */
    @SuppressWarnings("all")
    public static Object getAnnotationValue(Annotation clazzAnnotation){
        String value = clazzAnnotation.toString().split("[(w+)]")[1];
        String[] methodAndValue = value.split("=");
        String method = value.split("=")[0];
        if (methodAndValue.length < 2){
            return null;
        }
        String methodValue = value.split("=")[1];

        LOGGER.debug(clazzAnnotation+" has "+method+" = "+methodValue);
        return methodValue;
    }

    /**
     * 获取某一个类上的某一个注解的所有配置属性信息
     * @param clazz 某一个类的Class
     * @param annotation 某一个注解的Class
     * @return 配置属性信息的Map集合
     */
    public static Map<String,Object> getAnnotationValues(Class<?> clazz, Class<? extends Annotation> annotation){
        Annotation clazzAnnotation = clazz.getAnnotation(annotation);
        return null;
    }

}
