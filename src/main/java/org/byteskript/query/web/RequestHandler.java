package org.byteskript.query.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.byteskript.skript.runtime.Skript;

import java.io.IOException;

public record RequestHandler(HttpServer server) implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Skript.currentInstance().runEvent(new RequestEvent(exchange, server));
    }
    
}
