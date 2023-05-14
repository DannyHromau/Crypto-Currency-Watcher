package com.dannyhromau.watcher.exception;

public class MethodNotAllowedException extends Exception{
    private String url;

    public MethodNotAllowedException(String message, String url) {

        super(message);
        this.url = url;
    }
}
