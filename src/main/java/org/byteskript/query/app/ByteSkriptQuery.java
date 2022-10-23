package org.byteskript.query.app;

import com.sun.net.httpserver.HttpServer;
import mx.kenzie.foundation.Type;
import org.byteskript.query.page.CompiledPage;
import org.byteskript.query.page.PageCompiler;
import org.byteskript.query.syntax.*;
import org.byteskript.query.web.WebPrintStream;
import org.byteskript.skript.api.ModifiableLibrary;
import org.byteskript.skript.app.SkriptApp;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.runtime.Skript;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ByteSkriptQuery extends SkriptApp {
    public static final Library LIBRARY = new Library();
    public static final Type SERVER = new Type(HttpServer.class);
    public static final Map<String, CompiledPage> PAGE_CACHE = new HashMap<>();
    
    public static void main(String... args) {
        System.out.println("This goes in the ByteSkript libraries/ folder.");
    }
    
    public static void load(Skript skript) {
        skript.registerLibrary(LIBRARY);
        skript.setOutput(new WebPrintStream());
    }
    
    public static Runnable getPage(String path) {
        if (!path.endsWith(".bsq")) {
            final File file = new File(path);
            if (!file.exists()) return null;
            if (file.isDirectory()) return null;
            return () -> {
                try (final InputStream stream = new FileInputStream(file)) {
                    WriteEffect.write(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            };
        }
        final File file = new File(path);
        if (PAGE_CACHE.containsKey(path)) {
            final CompiledPage page = PAGE_CACHE.get(path);
            if (page.matches(file)) return page;
        }
        if (file.exists() && !file.isDirectory()) {
            final PageCompiler compiler = new PageCompiler();
            final Runnable runnable = compiler.compile(file);
            if (runnable != null) PAGE_CACHE.put(path, new CompiledPage(runnable, file));
            return runnable;
        }
        return null;
    }
    
    public static class Library extends ModifiableLibrary {
        public Library() {
            super("query");
            registerEvents(new WebRequestEvent());
            registerSyntax(CompileState.CODE_BODY,
                new CloseRequestEffect(),
                new StartServerEffect(),
                new StopServerEffect(),
                new WriteLineEffect(),
                new SendPageEffect(),
                new WriteToEffect(),
                new WriteEffect()
            );
            registerSyntax(CompileState.STATEMENT,
                new NewRootWebServer(),
                new NewWebServer(),
                new ContentTypeExpression(),
                new ResponseCodeExpression()
            );
        }
    }
    
}
