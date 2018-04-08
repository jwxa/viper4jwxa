package com.github.jwxa;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/7 ProjectName: viper4jwxa Version: 1.0
 */
public enum AppPackageNameEnum {
    CLOUD_MUSIC("com.netease.cloudmusic","网易云音乐"),
    HIBY_MUSIC("com.hiby.music","海贝音乐"),
    ;


    private String packageName;

    private String desc;

    public String getDesc() {
        return desc;
    }

    public String getPackageName() {
        return packageName;
    }

    AppPackageNameEnum(String packageName, String desc) {
        this.packageName = packageName;
        this.desc = desc;
    }
}
