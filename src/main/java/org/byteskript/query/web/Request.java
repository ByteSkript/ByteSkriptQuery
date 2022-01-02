package org.byteskript.query.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class Request {
    
    public final HttpExchange exchange;
    public final HttpServer server;
    public final StringBuilder response;
    public int code = 200;
    
    public Request(HttpExchange exchange, HttpServer server) {
        this.exchange = exchange;
        this.server = server;
        this.response = new StringBuilder();
    }
    
}
