package org.byteskript.query.syntax;

import com.sun.net.httpserver.HttpServer;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

public class StartServerEffect extends Effect {
    
    public StartServerEffect() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EFFECT, "start [(web|http)][ ]server %Server%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "start", Object.class));
    }
    
    public static void start(Object object) {
        if (object instanceof HttpServer server) server.start();
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("start ")) return null;
        return super.match(thing, context);
    }
}
