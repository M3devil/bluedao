package com.bluedao.bean;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/15 23:16
 * @类职责：配置文件对应的名称常量
 */
public interface Constant {

    /**
     * 编码格式
     */
    String CHARSET_UTF8 = "UTF-8";

    /**
     * mapper.xml所在包
     */
    String MAPPER_LOCATION = "mapper.location";

    /**
     * 与数据库表对应的pojo类所在的包
     */
    String PO_LOCATION = "po.location";

    /**
     * 数据库表名
     */
    String CATALOG = "catalog";

    /**
     * 数据库驱动
     */
    String JDBC_DRIVER = "jdbc.driver";

    /**
     * 数据库url
     */
    String JDBC_URL = "jdbc.url";

    /**
     * 数据库用户
     */
    String JDBC_USERNAME = "jdbc.username";

    /**
     * 数据库密码
     */
    String JDBC_PASSWORD = "jdbc.password";

    /**
     * 初始化连接个数
     */
    String JDBC_INIT_COUNT = "jdbc.initCount";

    /**
     * 最小连接个数
     */
    String JDBC_MIN_COUNT = "jdbc.minCount";

    /**
     * 最大连接个数
     */
    String JDBC_MAX_COUNT = "jdbc.maxCount";

    /**
     * 连接增长步长
     */
    String JDBC_INCREASING_COUNT = "jdbc.increasingCount";

    /**
     * 获取连接最大等待时间
     */
    String JDBC_MAX_WAITING_TIME = "jdbc.maxWaitingTIme";

    /**
     * 空闲最大存活时间
     */
    String JDBC_MAX_IDLE_TIME = "jdbc.maxIdleTime";

    /**
     * mapper文件的后缀
     */
    String MAPPER_FILE_SUFFIX = ".xml";

    /**
     * xml文件的根标签
     */
    String XML_ROOT_LABEL = "mapper";

    /**
     * mapper.xml的命名空间
     */
    String XML_NAMESPACE = "namespace";

    /**
     * sql语句的id
     */
    String XML_ELEMENT_ID = "id";

    /**
     * sql语句的返回对象
     */
    String XML_ELEMENT_RESULT_TYPE = "resultType";

    /**
     * 是否开启二级缓存
     */
    String NET_CACHE = "netCache";

    String NET_CACHE_IP = "netCache.ip";

    String NET_CACHE_PORT = "netCache.port";

    String NET_CACHE_PASSWORD = "netCache.password";

    /**
     * 是否开启一级缓存
     */
    String LOCAL_CACHE = "localCache";

    /**
     * 枚举类型定义mapper.xml中的标签
     */
     enum SqlType{
        SELECT("select"),
        INSERT("insert"),
        UPDATE("update"),
        DELETE("delete"),
        DEFAULT("default");

        private final String value;

        SqlType(String value) {
            this.value = value;
        }

        public String value(){
            return value;
        }
    }
}
