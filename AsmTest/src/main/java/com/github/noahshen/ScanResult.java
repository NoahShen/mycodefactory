package com.github.noahshen;

/**
 * Created with IntelliJ IDEA.
 * User: noahshen
 * Date: 14-3-30
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 */
public class ScanResult {

    private String clazz;

    private int line;

    private String logType;

    private String logMethodName;

    public ScanResult() {
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogMethodName() {
        return logMethodName;
    }

    public void setLogMethodName(String logMethodName) {
        this.logMethodName = logMethodName;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "clazz='" + clazz + '\'' +
                ", line=" + line +
                ", logType='" + logType + '\'' +
                ", logMethodName='" + logMethodName + '\'' +
                '}';
    }
}
