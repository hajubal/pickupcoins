package me.synology.hajubal.coins.entity.type;

public enum POINT_URL_TYPE {
    NAVER,
    OFW_NAVER,
    UNSUPPORT;

    public static POINT_URL_TYPE classifyUrlType(String url) {
        if(url.contains("campaign2-api.naver.com")) {
            return POINT_URL_TYPE.NAVER;
        } else if(url.contains("ofw.adison.co/u/naverpay")) {
            return POINT_URL_TYPE.OFW_NAVER;
        }

        return POINT_URL_TYPE.UNSUPPORT;
    }
}
