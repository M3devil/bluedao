package com.bluedao.cache;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/6 21:28
 * @created: 管理连接规范
 */
public interface ConnectionControl {

    /**
     * 开启连接
     */
    default boolean connect(Object... params) {
        return false;
    }

    /**
     * 检查连接
     * @return 返回检查连接消息
     */
    default Object ping() {
        return null;
    }

    /**
     * 关闭连接
     */
    boolean close();

}
