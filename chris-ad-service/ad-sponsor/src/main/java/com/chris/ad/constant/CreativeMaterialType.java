package com.chris.ad.constant;

import lombok.Getter;

@Getter
public enum CreativeMaterialType {
    JPG(1, "jpg"),
    BMP(2, "bmp"),
    MP4(3, "mp4"),
    TXT(4, "txt");

    private int type;
    private String desc;

    CreativeMaterialType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
