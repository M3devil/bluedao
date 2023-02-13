package com.bluedao.util;

import com.bluedao.bean.Constant;
import com.bluedao.bean.MappedStatement;
import com.bluedao.session.Configuration;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;

import java.io.File;
import java.util.Iterator;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/19 9:44
 * @类职责：解析.xml文件的标签信息
 */
public class XmlParseUtil {

    private final static Logger LOGGER = LogUtil.getLogger();

    /**
     * 解析mapper.xml标签西悉尼
     * @param fileName xml文件
     * @param configuration 配置信息
     */
    @SuppressWarnings("rawtypes")
    public static void mapperParser(File fileName, Configuration configuration) {
        try {
            //创建读取器
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding(Constant.CHARSET_UTF8);

            //读取文件内容
            Document document = saxReader.read(fileName);

            //获取xml中根元素
            Element rootElement = document.getRootElement();

            //判断根元素是否争取
            if (!Constant.XML_ROOT_LABEL.equals(rootElement.getName())){
                LOGGER.warn("mapper.xml文件的元素错误!");
                return;
            }

            //获取标签内容
            String namespace = rootElement.attributeValue(Constant.XML_NAMESPACE);
            LOGGER.debug("注册mapper代理工厂：{}",namespace);

            //遍历根元素内标签
            Iterator iterator = rootElement.elementIterator();
            while (iterator.hasNext()){
                //封装mappedStatement信息
                MappedStatement mappedStatement = new MappedStatement();
                //遍历的标签
                Element element = (Element) iterator.next();
                //标签名
                String elementName = element.getName();
                //标签id
                String elementId = element.attributeValue(Constant.XML_ELEMENT_ID);
                //标签返回值
                String returnType = element.attributeValue(Constant.XML_ELEMENT_RESULT_TYPE);
                //sql语句内容
                String sql = element.getStringValue();
                //设置sql的唯一Id
                String id = namespace + "." + elementId;
                //封装信息
                //判断标签名是否已定义，未定义则使用default
                if (!Constant.SqlType.SELECT.value().equals(elementName) &&
                        !Constant.SqlType.UPDATE.value().equals(elementName) &&
                        !Constant.SqlType.DELETE.value().equals(elementName) &&
                        !Constant.SqlType.INSERT.value().equals(elementName)) {
                    LOGGER.warn("mapper.xml中存在未定义标签:" + elementName);
                    mappedStatement.setSqlType(Constant.SqlType.DEFAULT.value());
                } else {
                    mappedStatement.setSqlType(elementName);
                }

                mappedStatement.setId(id);
                mappedStatement.setSql(ObjectValueUtil.stringTrim(sql));
                mappedStatement.setNamespace(namespace);
                mappedStatement.setReturnType(returnType);

                configuration.addMappedStatement(id, mappedStatement);
                configuration.addMapper(Class.forName(namespace));
                LOGGER.debug("addMapper : " + namespace + ", info: " + id);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
