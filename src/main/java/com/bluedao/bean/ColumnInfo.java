package com.bluedao.bean;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/15 22:58
 * @类职责：封装数据库表中某一个字段的信息
 */
public class ColumnInfo {

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段数据类型
     */
    private String dataType;

    /**
     * 字段类型
     */
    private Key key = Key.DEFAULT;

    public enum Key{
        DEFAULT(0),
        PRIMARY(1),
        FOREIGN(2);
        private final int value;

        Key(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public ColumnInfo() {
    }

    public ColumnInfo(String name, String dataType, Key key) {
        this.name = name;
        this.dataType = dataType;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
