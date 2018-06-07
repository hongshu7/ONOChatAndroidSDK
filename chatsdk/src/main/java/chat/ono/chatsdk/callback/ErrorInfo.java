package chat.ono.chatsdk.callback;

import chat.ono.chatsdk.proto.MessageProtos;

public class ErrorInfo {
    private int code;
    private String message;

    public ErrorInfo(int code, String message) {
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

    public static ErrorInfo fromMessage(MessageProtos.ErrorResponse ep) {
        return new ErrorInfo(ep.getCode(), ep.getMessage());
    }
}
