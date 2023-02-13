package com.bluedao.executor.parameter;

import com.bluedao.util.ObjectValueUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/19 15:18
 */
public class DefaultParameterHandler implements ParameterHandler {

    private final Object parameter;

    public DefaultParameterHandler(Object parameter) {
        this.parameter = parameter;
    }

    @Override
    public void setParameters(PreparedStatement preparedStatement) {
        try {
            if (!ObjectValueUtil.isEmpty(parameter)){
                if (parameter.getClass().isArray()){
                    Object[] params = (Object[]) parameter;
                    for (int i = 0; i < params.length; i++) {
                        preparedStatement.setObject(i + 1, params[i]);
                    }
                }else {
                    preparedStatement.setObject(1, parameter);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
