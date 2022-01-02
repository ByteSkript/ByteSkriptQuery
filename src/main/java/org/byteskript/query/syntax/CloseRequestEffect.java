package org.byteskript.query.syntax;

import com.sun.net.httpserver.HttpExchange;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.web.Request;
import org.byteskript.query.web.RequestEvent;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.threading.ScriptThread;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CloseRequestEffect extends Effect {
    
    public CloseRequestEffect() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EFFECT, "close [the] request %Request%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "close", Object.class));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("close ")) return null;
        return super.match(thing, context);
    }
    
    public static void close(Object object) throws IOException {
        if (object instanceof Request request) {
            final String response = request.response.toString();
            close(request.exchange, response, request.code);
        } else if (object instanceof RequestEvent event) {
            close(event.request);
        } else if (object instanceof HttpExchange) {
            if (!(Thread.currentThread() instanceof ScriptThread thread))
                throw new ScriptRuntimeError("The 'close' effect can only be used in the thread from a web request event.");
            if (!(thread.event instanceof RequestEvent event))
                throw new ScriptRuntimeError("The 'close' effect can only be used in the thread from a web request event.");
            close(event.request);
        }
    }
    
    public static void close(HttpExchange exchange, String response, int code) throws IOException {
        exchange.sendResponseHeaders(code, response.length());
        try (final OutputStream stream = exchange.getResponseBody()) {
            stream.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
