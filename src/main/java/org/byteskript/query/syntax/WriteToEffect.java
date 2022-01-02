package org.byteskript.query.syntax;

import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.web.Request;
import org.byteskript.skript.api.syntax.Effect;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

public class WriteToEffect extends Effect {
    
    public WriteToEffect() {
        super(ByteSkriptQuery.LIBRARY, StandardElements.EFFECT, "write %String% to %Request%");
        handlers.put(StandardHandlers.RUN, findMethod(this.getClass(), "write", Object.class, Object.class));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("write ")) return null;
        if (!thing.contains(" to ")) return null;
        return super.match(thing, context);
    }
    
    public static void write(Object object, Object thing) {
        if (object == null) return;
        if (thing instanceof Request request) request.response.append(object);
    }
}
