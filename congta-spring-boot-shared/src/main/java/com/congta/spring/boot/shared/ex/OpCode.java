package com.congta.spring.boot.shared.ex;

/**
 * operation status code
 * Created by zhangfucheng on 2021/01/23.
 */
public class OpCode {

    public static OpCode SUCCESS = create(0, "成功");
    public static OpCode SUCCESS_200 = create(200, "成功");
    public static OpCode UNKNOWN_ERROR = create(500, "未知错误");
    public static OpCode PERMISSION_DENIED = create(10005, "无访问权限");
    public static OpCode SYSTEM_ERROR = create(10006, "系统错误");
    public static OpCode STORAGE_ERROR = create(10007, "存储错误");
    public static OpCode DB_ERROR = create(10008, "数据库错误", 1000);
    public static OpCode PARAM_ERROR = create(20001, "参数错误");
    public static OpCode ITEM_NOT_EXIST = create(20002, "记录不存在");

    private int value;

    private String verbose;

    /**
     * 下次重试/轮询间隔：
     * -1： 不可重试
     * 0： 可立即重试
     * N： N秒后重试
     */
    private long next;

    protected static OpCode create(int value, String name) {
        return create(value, name, 0);
    }

    protected static OpCode create(int value, String name, long next) {
        OpCode code = new OpCode();
        code.value = value;
        code.verbose = name;
        code.next = next;
        return code;
    }

    public int getValue() {
        return value;
    }

    public String getVerbose() {
        return verbose;
    }

    public long getNext() {
        return next;
    }
}
