/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.query.syntax;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import org.byteskript.query.web.Request;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.lang.reflect.Method;

public class ResponseCodeExpression extends SimpleExpression implements Referent {
    
    public ResponseCodeExpression() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "[the] response code of %Request%",
            "%Request%'s response code");
        try {
            handlers.put(StandardHandlers.GET, ResponseCodeExpression.class.getMethod("getProperty", Object.class));
            handlers.put(StandardHandlers.FIND, ResponseCodeExpression.class.getMethod("getProperty", Object.class));
            handlers.put(StandardHandlers.SET, ResponseCodeExpression.class.getMethod("setProperty", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.contains("response code")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.INTEGER;
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return super.allowAsInputFor(type) || type.equals(CommonTypes.REFERENT);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.OBJECT;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final Method target = handlers.get(context.getHandlerMode());
        assert target != null;
        this.writeCall(method, target, context);
    }
    
    public static Integer getProperty(Object object) {
        if (object instanceof Request request)
            return request.code;
        return 0;
    }
    
    public static Void setProperty(Object object, Object code) {
        if (!(code instanceof Number number)) return null;
        if (object instanceof Request request)
            request.code = number.intValue();
        return null;
    }
    
}
