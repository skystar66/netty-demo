package com.netty.core.enums;

public enum EnvType {

    SERVER("server"),
    CLIENT("client");

    private String env;

    EnvType(String env){
        this.env=env;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
