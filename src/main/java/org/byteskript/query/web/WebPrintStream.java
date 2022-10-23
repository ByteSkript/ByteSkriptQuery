package org.byteskript.query.web;

import org.byteskript.skript.runtime.threading.ScriptThread;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class WebPrintStream extends PrintStream {
    public WebPrintStream() {
        super(System.out);
    }
    
    @Override
    public void print(@Nullable String object) {
        if (Thread.currentThread() instanceof ScriptThread thread) {
            if (thread.event instanceof RequestEvent event) {
                if (object == null) return;
                event.request.response.append(object);
            } else super.print(object);
        } else super.print(object);
    }
    
    @Override
    public void println(@Nullable String object) {
        if (Thread.currentThread() instanceof ScriptThread thread) {
            if (thread.event instanceof RequestEvent event) {
                if (object == null) return;
                event.request.response.append(object);
            } else super.println(object);
        } else super.println(object);
    }
    
    @Override
    public void println(Object object) {
        if (Thread.currentThread() instanceof ScriptThread thread) {
            if (thread.event instanceof RequestEvent event) {
                if (object == null) return;
                event.request.response.append(object);
            } else super.println(object);
        } else super.println(object);
    }
    
}
