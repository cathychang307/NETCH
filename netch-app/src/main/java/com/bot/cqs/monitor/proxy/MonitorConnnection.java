package com.bot.cqs.monitor.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;


/**
 * @author AndyChen
 * @version <ul>
 *          <li>2019/4/9 AndyChen,new
 *          </ul>
 * @since 2019/4/9
 */
public class MonitorConnnection implements InvocationHandler {

    private Connection connection;

    public MonitorConnnection() {
    }

    public Connection bind(Connection connection) {
        this.connection = connection;
        Connection instance;
        try {
            instance = (Connection) Proxy.newProxyInstance(connection.getClass().getClassLoader(), connection.getClass().getInterfaces(), this);
        } catch (ClassCastException e) {
            //應為container實作的class not implements Connection,而是繼承基類，改取父類interfaces
            instance = (Connection) Proxy.newProxyInstance(connection.getClass().getClassLoader(), connection.getClass().getSuperclass().getInterfaces(), this);
        }
//        P6SpyDriver.jdbcEventListenerFactory = JdbcEventListenerFactoryLoader.load();
        return instance;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String[] targetNames = {"prepareStatement"}; //目前執行SQL的method names
        if (Arrays.asList(targetNames).contains(methodName) && args != null) {
            Object[] executeArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                String sqlStr = args[i].toString().toLowerCase().trim();
                if(sqlStr.startsWith("select")){
                    MonitorThreadLocal.threadLocal.set(sqlStr);
                }
            }
            return method.invoke(connection, args);
        } else {
            return method.invoke(connection, args);
        }
    }

}
