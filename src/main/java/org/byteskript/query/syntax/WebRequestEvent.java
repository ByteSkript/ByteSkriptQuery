package org.byteskript.query.syntax;

import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.web.Flags;
import org.byteskript.query.web.RequestEvent;
import org.byteskript.skript.api.Event;
import org.byteskript.skript.api.syntax.EventHolder;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.structure.SectionMeta;

public class WebRequestEvent extends EventHolder {
    
    public WebRequestEvent() {
        super(ByteSkriptQuery.LIBRARY, "on (web|http) request");
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) {
        super.compile(context, match);
        context.addFlag(Flags.IN_REQUEST);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        super.onSectionExit(context, meta);
        context.removeFlag(Flags.IN_REQUEST);
    }
    
    @Override
    public Class<? extends Event> eventClass() {
        return RequestEvent.class;
    }
    
}
