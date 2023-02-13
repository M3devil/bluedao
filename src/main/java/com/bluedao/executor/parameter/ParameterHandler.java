package com.bluedao.executor.parameter;

import java.sql.PreparedStatement;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 14:50
 * @类职责：参数处理器
 */
public interface ParameterHandler {

    /**
     * 参数设置
     * @param preparedStatement 预处理sql对象
     */
    void setParameters(PreparedStatement preparedStatement);
}
