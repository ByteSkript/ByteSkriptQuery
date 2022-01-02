package org.byteskript.query.syntax;

import com.sun.net.httpserver.HttpServer;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

public class StopServerEffect extends Effect {
    
    public StopServerEffect() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EFFECT, "stop [(web|http)][ ]server %Server%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "stop", Object.class));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("stop ")) return null;
        return super.match(thing, context);
    }
    
    public static void stop(Object object) {
        if (object instanceof HttpServer server) server.stop(0);
    }
}
