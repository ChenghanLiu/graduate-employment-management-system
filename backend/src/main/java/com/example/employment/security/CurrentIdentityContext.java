package com.example.employment.security;

public final class CurrentIdentityContext {

    private static final ThreadLocal<CurrentIdentity> HOLDER = new ThreadLocal<>();

    private CurrentIdentityContext() {
    }

    public static void set(CurrentIdentity identity) {
        HOLDER.set(identity);
    }

    public static CurrentIdentity get() {
        CurrentIdentity identity = HOLDER.get();
        if (identity == null) {
            throw new IllegalStateException("当前请求身份未初始化");
        }
        return identity;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
