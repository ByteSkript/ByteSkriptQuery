package org.byteskript.query.syntax;

import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.web.RequestEvent;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.error.ScriptRuntimeError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.threading.ScriptThread;

public class WriteEffect extends Effect {
    
    public WriteEffect() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EFFECT, "write %Object%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "write", Object.class));
    }
    
    public static void write(Object object) {
        if (object == null) return;
        if (!(Thread.currentThread() instanceof ScriptThread thread))
            throw new ScriptRuntimeError("The 'write' effect can only be used in the thread from a web request event.");
        if (!(thread.event instanceof RequestEvent event))
            throw new ScriptRuntimeError("The 'write' effect can only be used in the thread from a web request event.");
        event.request.response.append(object);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("write ")) return null;
        return super.match(thing, context);
    }
}
