package com.echonest.api.v4;



/**
 * Represents echo nest oriented exceptions
 * @author plamere
 */

@SuppressWarnings("serial")
public class EchoNestException extends Exception {

    /** success */
     public final static int SUCCESS = 0;
    /** Missing/Invalid API Key */
     public final static int ERR_MISSING_OR_INVALID_API_KEY = 1;
    /**  API Key is not allowed to call this method */
     public final static int ERR_ACCESS_DENIED = 2;
    /** Rate limit exceeded */
     public final static int ERR_RATE_LIMIT_EXCEEDED = 3;
    /** missing parameter */
     public final static int ERR_MISSING_PARAMETER = 4;
    /** access to an invalid field */
     public final static int ERR_INVALID_PARAMETER = 5;

     // some synthetic error codes
     /** the client API expected something different from the server */
     public final static int CLIENT_SERVER_INCONSISTENCY = -2;
     /** no api key was given */
     public final static int ERR_NO_KEY = -4;

     private int code = -1;
     private String message;

    /**
     * Creates an exception
     * @param code the error code
     * @param message a description of the exception
     */
    public EchoNestException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * Creates an exception
     * @param arg0 the wrapped throwable
     */
    public EchoNestException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Gets the error code
     * @return the error code
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the exception message
     * @return the message
     */
    @Override
    public String getMessage() {
        if (message != null) {
            return String.format("(%d) %s", code, message);
        } else {
            return super.getMessage();
        }
    }
}