package com.itsok.exception;


public class AgentNotLoadException extends Exception {

    public AgentNotLoadException() {
        super("agent代理未加载!");
    }
}
