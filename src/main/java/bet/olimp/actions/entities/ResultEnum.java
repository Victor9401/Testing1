package bet.olimp.actions.entities;

public enum ResultEnum {
    NO_COUNT(0),
    WIN(1),
    FAIL(2),
    RECOUNT(3),
    RECOUNT_WIN(4),
    RECOUNT_FAIL(5);

    ResultEnum(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}