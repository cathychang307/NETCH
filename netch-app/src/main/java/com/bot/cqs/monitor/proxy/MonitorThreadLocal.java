package com.bot.cqs.monitor.proxy;

public class MonitorThreadLocal {

    public static ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
}
