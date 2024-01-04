package pers.xuchen.module.core.model.ex;

public class BusiException extends RuntimeException {
    int code;

    public BusiException(String message) {
        super(message);
    }

    public BusiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
