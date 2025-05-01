package net.badlion.common.libraries.exceptions;

public class HTTPRequestFailException extends Exception {

    public enum REQUEST_FAIL_TYPE { RESPONSE, TIMEOUT }

    private REQUEST_FAIL_TYPE type;
    private int responseCode;
    private String response;

    public HTTPRequestFailException(int responseCode, String response) {
        super();

        this.type = REQUEST_FAIL_TYPE.RESPONSE;
        this.responseCode = responseCode;
        this.response = response;
    }

    public HTTPRequestFailException(REQUEST_FAIL_TYPE type) {
        this.type = type;
    }

    public REQUEST_FAIL_TYPE getType() {
        return type;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
