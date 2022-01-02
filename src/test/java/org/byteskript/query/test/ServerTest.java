package org.byteskript.query.test;

import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.skript.runtime.Script;
import org.byteskript.skript.runtime.Skript;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ServerTest {
    
    private static final Skript skript = new Skript();
    private static Script script;
    
    @BeforeClass
    public static void start() throws Throwable {
        System.setProperty("debug_mode", "true");
        skript.registerLibrary(ByteSkriptQuery.LIBRARY);
        final PostCompileClass cls = skript.compileScript(ServerTest.class.getClassLoader()
            .getResourceAsStream("create_server.bsk"), "skript.create_server");
        script = skript.loadScript(cls);
    }
    
    public static void main(String[] args) throws Throwable {
        start();
        script.getFunction("make_server").run(skript);
    }
    
    @Test
    public void test() {
        script.getFunction("make_server").invoke();
    }
    
    protected static void debug(final PostCompileClass source) throws Throwable {
        try (final OutputStream stream =
                 new FileOutputStream(source.name() + ".class")) {
            stream.write(source.code());
        }
    }
    
}
