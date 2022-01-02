package org.byteskript.query.syntax;

import com.sun.net.httpserver.HttpServer;
import mx.kenzie.foundation.Type;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.web.RequestHandler;
import org.byteskript.skript.api.syntax.SimpleExpression;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.Skript;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NewRootWebServer extends SimpleExpression {
    
    public NewRootWebServer() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EXPRESSION, "[a] new (web|http)[ ]server");
        handlers.put(StandardHandlers.GET, findMethod(NewRootWebServer.class, "createServer"));
        handlers.put(StandardHandlers.FIND, findMethod(NewRootWebServer.class, "createServer"));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        return super.match(thing, context);
    }
    
    public static HttpServer createServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new RequestHandler(server));
        server.setExecutor(Skript.currentInstance().getScheduler());
        return server;
    }
    
    @Override
    public Type getReturnType() {
        return ByteSkriptQuery.SERVER;
    }
}
