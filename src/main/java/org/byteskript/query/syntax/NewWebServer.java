package org.byteskript.query.syntax;

import com.sun.net.httpserver.HttpServer;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.web.RequestHandler;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.Skript;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NewWebServer extends SimpleExpression {
    
    public NewWebServer() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EXPRESSION, "[a] new (web|http)[ ]server for [path] %String% (on|at) port %Integer%");
        handlers.put(StandardHandlers.GET, findMethod(NewWebServer.class, "createServer", Object.class, Object.class));
        handlers.put(StandardHandlers.FIND, findMethod(NewWebServer.class, "createServer", Object.class, Object.class));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    public static HttpServer createServer(Object path, Object port) throws IOException {
        if (!(port instanceof Number number))
            throw new ScriptRuntimeError("The provided port was not a number: " + port);
        final String url;
        if (path == null) url = "/";
        else url = path.toString();
        HttpServer server = HttpServer.create(new InetSocketAddress(number.intValue()), 0);
        server.createContext(url, new RequestHandler(server));
        server.setExecutor(Skript.getExecutor());
        return server;
    }
    
}
