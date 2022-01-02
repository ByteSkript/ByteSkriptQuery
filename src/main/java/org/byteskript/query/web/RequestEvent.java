package org.byteskript.query.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.note.EventValue;

public class RequestEvent extends Event {
    
    public final HttpExchange exchange;
    public final HttpServer server;
    public final Request request;
    
    public RequestEvent(HttpExchange exchange, HttpServer server) {
        this.exchange = exchange;
        this.server = server;
        this.request = new Request(exchange, server);
    }
    
    @EventValue("request")
    public Request getRequest() {
        return request;
    }
    
    @EventValue("server")
    public HttpServer getServer() {
        return server;
    }
    
    @EventValue("exchange")
    public HttpExchange getExchange() {
        return exchange;
    }
    
}
