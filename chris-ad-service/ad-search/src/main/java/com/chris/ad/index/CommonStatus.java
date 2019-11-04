package com.chris.ad.index;

public enum CommonStatus {
    VALID(1, "valid"),
    INVALID(0, "invalid");

    private Integer status;
    private String desc;

    CommonStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
