
package com.bot.cqs.query;

public class NoSuchFunctionException extends QueryException {

    public NoSuchFunctionException() {

    }

    public NoSuchFunctionException(String message) {

        super(message);
    }

    public NoSuchFunctionException(Throwable cause) {

        super(cause);
    }

    public NoSuchFunctionException(String message, Throwable cause) {

        super(message, cause);
    }

}
