package org.byteskript.query.syntax;

import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.io.IOException;

public class SendPageEffect extends Effect {
    
    public SendPageEffect() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EFFECT, "send [page] %Page%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "send", Object.class));
    }
    
    public static void send(Object object) throws IOException {
        if (object == null) return;
        send(object.toString());
    }
    
    public static void send(String page) throws IOException {
        final Runnable runnable = ByteSkriptQuery.getPage(page);
        if (runnable == null) return;
        runnable.run();
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("send ")) return null;
        return super.match(thing, context);
    }
}
