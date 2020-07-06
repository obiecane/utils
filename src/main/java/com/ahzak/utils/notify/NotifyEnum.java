package com.ahzak.utils.notify;

public enum NotifyEnum {
    /**
     * 短信
     */
    SMS(1),
    /**
     * 邮箱
     */
    EMAIL(2),
    /**
     * 站内信
     */
    WEBSITE(3);

    private int value;

    NotifyEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static NotifyEnum valueOf(int type) {
        switch (type) {
            case 1:
                return SMS;
            case 2:
                return EMAIL;
            case 3:
                return WEBSITE;
            default:
                throw new IllegalArgumentException("不存在的枚举值变量: " + type);
        }
    }
}
