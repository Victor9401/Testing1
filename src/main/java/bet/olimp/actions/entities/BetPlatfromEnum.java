package bet.olimp.actions.entities;

public enum BetPlatfromEnum {
    CRUNCH(0),
    IOS(1),
    ANDRIOD(2),
    CUPIS_IOS(3),
    CUPIS_ANDROID(4),
    CUPIS_ANDROID2(5),
    TERMINAL(6),
    TELEGRAM(7),
    MOBILESITE_RU(8),
    SITE_GE(9),
    SITE_RU(10),
    SITE_INFO(11),
    SITE_OPS(12),
    WOOPAY(13),
    TEST(14),
    DHC(15),
    SITE_OPS_CUPIS(16),
    OLIMP_BET(17),
    BOP(18),
    KZ(19);
    BetPlatfromEnum(Integer code){
        this.code = code;
    }
    private Integer code;
    public Integer getCode(){ return code;}
    }



