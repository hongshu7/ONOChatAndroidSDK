package chat.ono.chatsdk.callback;

import chat.ono.chatsdk.proto.MessageProtos;

public class ErrorData {
    private int code;
    private String message;

    public ErrorData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ErrorData fromMessage(MessageProtos.ErrorResponse ep) {
        return new ErrorData(ep.getCode(), ep.getMessage());
    }
}
