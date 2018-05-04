package org.zstack.zql;

import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.zql.ZQLExtensionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ZQLContext {
    private static ThreadLocal<Map<String, Object>> local = new ThreadLocal<>();

    private static void put(String k, Object v) {
        Map map = local.get();
        if (map == null) {
            map = new HashMap();
            local.set(map);
        }

        map.put(k, v);
    }

    private static Object get(String k) {
        Map map = local.get();
        return map == null ? null : map.get(k);
    }

    private static void remove(String k) {
        Map map = local.get();
        if (map != null) {
            map.remove(k);
        }
    }

    private static Object computeIfAbsent(String k, Object obj) {
        Object ob = get(k);
        if (ob == null) {
            ob = obj;
            put(k, ob);
        }

        return ob;
    }

    private static final String QUERY_TARGET_INVENTORY_NAME = "QUERY_TARGET_INVENTORY_NAME";
    private static final String QUERY_TARGET_INVENTORY_STACK = "QUERY_TARGET_INVENTORY_STACK";
    private static final String API_SESSION = "API_SESSION";

    public static String getQueryTargetInventoryName() {
        return (String) get(QUERY_TARGET_INVENTORY_NAME);
    }

    public static void setQueryTargetInventoryName(String name) {
        put(QUERY_TARGET_INVENTORY_NAME, name);
    }

    public static void cleanQueryTargetInventoryName() {
        remove(QUERY_TARGET_INVENTORY_NAME);
    }

    public static void putAPISession(SessionInventory session) {
        put(API_SESSION, session);
    }

    public static SessionInventory getAPISession() {
        return (SessionInventory) get(API_SESSION);
    }

    public static void cleanAPISession() {
        remove(API_SESSION);
    }

    public static void pushQueryTargetInventoryName(String targetInventoryName) {
        Stack<String> stack = (Stack<String>) computeIfAbsent(QUERY_TARGET_INVENTORY_STACK, new Stack());
        stack.push(targetInventoryName);
    }

    public static String popQueryTargetInventoryName() {
        Stack<String> stack = (Stack<String>) get(QUERY_TARGET_INVENTORY_STACK);
        if (stack == null) {
            throw new CloudRuntimeException("no query target inventory stack yet");
        }
        return stack.pop();
    }

    public static String peekQueryTargetInventoryName() {
        Stack<String> stack = (Stack<String>) get(QUERY_TARGET_INVENTORY_STACK);
        if (stack == null) {
            throw new CloudRuntimeException("no query target inventory stack yet");
        }
        return stack.peek();
    }

    static class ZQLExtensionContextImpl implements ZQLExtensionContext {
        @Override
        public String getQueryTargetInventoryName() {
            return ZQLContext.getQueryTargetInventoryName();
        }

        @Override
        public SessionInventory getAPISession() {
            return ZQLContext.getAPISession();
        }
    }

    public static ZQLExtensionContext createZQLExtensionContext() {
        return new ZQLExtensionContextImpl();
    }
}