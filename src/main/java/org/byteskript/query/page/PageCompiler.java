package org.byteskript.query.page;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.language.PostCompileClass;
import org.byteskript.query.app.ByteSkriptQuery;
import org.byteskript.query.syntax.WriteEffect;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.TriggerTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.syntax.entry.EntryTriggerSection;
import org.byteskript.skript.lang.syntax.function.MemberFunctionNoArgs;
import org.byteskript.skript.runtime.internal.CompiledScript;
import org.byteskript.skript.runtime.type.AtomicVariable;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageCompiler extends SimpleSkriptCompiler {
    
    private static final Random RANDOM = new Random();
    private static final Method METHOD;
    
    static {
        try {
            METHOD = WriteEffect.class.getMethod("write", Object.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    private final WriteInstruction wrap = WriteInstruction
        .invokeStatic(new Type(AtomicVariable.class), new Type(AtomicVariable.class), "wrap", CommonTypes.OBJECT);
    private final WriteInstruction unwrap = WriteInstruction
        .invokeStatic(new Type(AtomicVariable.class), CommonTypes.OBJECT, "unwrap", CommonTypes.OBJECT);
    private final Pattern pattern = Pattern.compile("^(?<space>\\s*+)(?=\\S)");
    private int anonymous = 0;
    
    public PageCompiler() {
        super(ByteSkriptQuery.LIBRARY);
    }
    
    public Runnable compile(File file) {
        try (final InputStream stream = new FileInputStream(file)) {
            final String string = this.unstream(stream);
            return compile(string);
        } catch (IOException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private String unstream(InputStream stream) {
        final StringBuilder builder = new StringBuilder();
        try (final Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private Runnable compile(final String content) throws InstantiationException, IllegalAccessException, IOException {
        if (!content.contains("<%")) return () -> WriteEffect.write(content);
        final String name = "Page$" + RANDOM.nextInt(100000, 999999) + RANDOM.nextInt(100000, 999999);
        final Type type = new Type("org.byteskript.query.page." + name);
        final FileContext context = this.assemble(content, type);
        final ClassBuilder builder = context.getBuilder();
        builder.addInterfaces(new Type(Runnable.class));
        builder.addConstructor().writeCode(WriteInstruction.loadThis())
            .writeCode(WriteInstruction.invokeSpecial(new Type(CompiledScript.class)))
            .writeCode(WriteInstruction.returnEmpty());
        final PostCompileClass[] classes = context.compile();
        return (Runnable) (load(classes[0].code(), type.dotPath())).newInstance();
    }
    
    private FileContext assemble(String string, Type owner) {
        final FileContext context = new FileContext(owner);
        for (final Library library : getLibraries()) {
            context.addLibrary(library);
            for (final Type type : library.getTypes()) {
                context.registerType(type);
            }
        }
        this.readFile(string, context);
        return context;
    }
    
    private void readFile(String string, FileContext context) {
        final ClassBuilder builder = context.getBuilder();
        final MethodBuilder method = builder.addMethod("run").setReturnType(void.class).setModifiers(0x0001);
        final List<String> original = string.lines().toList(); // stream of sadness :(
        StringBuilder writer = new StringBuilder();
        StringBuilder coder = new StringBuilder();
        boolean inCode = false;
        int i = 0;
        loop:
        for (final String old : original) {
            i++;
            String line = old;
            if (inCode) {
                if (old.contains("%>")) {
                    final int end = old.indexOf("%>");
                    if (end > 0) coder.append(old, 0, end);
                    line = old.substring(end + 2);
                    this.evaluate(method, coder.toString(), context, i);
                    coder = new StringBuilder();
                    inCode = false;
                } else {
                    coder.append(old).append(System.lineSeparator());
                    continue;
                }
            }
            while (line.contains("<%")) {
                final int start = line.indexOf("<%");
                if (start > 0) writer.append(line, 0, start);
                method.writeCode(WriteInstruction.loadConstant(writer.toString()));
                method.writeCode(WriteInstruction.invokeStatic(METHOD));
                writer = new StringBuilder();
                line = line.substring(start + 2);
                if (line.contains("%>")) {
                    final int end = line.indexOf("%>");
                    coder.append(line, 0, end);
                    this.evaluate(method, coder.toString(), context, i);
                    coder = new StringBuilder();
                    line = line.substring(end + 2);
                } else {
                    coder.append(line).append(System.lineSeparator());
                    inCode = true;
                    continue loop;
                }
            }
            writer.append(line).append(System.lineSeparator());
        }
        method.writeCode(WriteInstruction.loadConstant(writer.toString()));
        method.writeCode(WriteInstruction.invokeStatic(METHOD));
        method.writeCode(WriteInstruction.returnEmpty());
    }
    
    private void evaluate(final MethodBuilder run, String code, FileContext context, final int end) {
        final List<String> lines = this.adjustOffset(this.removeComments(code));
        final int start = end - lines.size() + 1;
        context.lineNumber = end - lines.size();
        context.addFlag(AreaFlag.IN_FUNCTION);
        context.createUnit(StandardElements.SECTION);
        context.addSection(new MemberFunctionNoArgs());
        context.addSection(new EntryTriggerSection());
        anonymous++;
        final MethodBuilder method = context.getBuilder()
            .addMethod("func$" + anonymous)
            .addModifiers(Modifier.STATIC)
            .setReturnType(Object.class);
        run.writeCode(WriteInstruction.invokeStatic(context.getType(), method.getErasure()));
        run.writeCode(WriteInstruction.pop());
        context.setMethod(method);
        final TriggerTree tree = new TriggerTree(context.getSection(1), context.getVariables());
        context.createTree(tree);
        method.writeCode(prepareVariables(tree));
        context.setState(CompileState.CODE_BODY);
        for (String line : lines) {
            context.lineNumber++;
            context.line = null;
            if (line.isBlank()) continue;
            if (context.getMethod() != null) {
                context.getMethod().writeCode(WriteInstruction.lineNumber(context.lineNumber));
            }
            try {
                this.compileLine(line, context);
            } catch (ScriptParseError | ScriptCompileError ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new ScriptCompileError(context.lineNumber, "Unknown error during compilation:", ex);
            }
        }
        context.removeFlag(AreaFlag.IN_FUNCTION);
        context.closeAllTrees();
        if (method.getErasure().returnType().equals(new Type(void.class))) {
            method.writeCode(WriteInstruction.returnEmpty());
        } else {
            method.writeCode(WriteInstruction.pushNull());
            method.writeCode(WriteInstruction.returnObject());
        }
        context.emptyVariables();
    }
    
    private List<String> adjustOffset(final List<String> lines) {
        final Matcher matcher = pattern.matcher(lines.get(0));
        final boolean found = matcher.find();
        final String indent;
        if (found) indent = matcher.group("space");
        else return lines;
        if (indent.length() == 0) return lines;
        final List<String> trimmed = new ArrayList<>();
        for (final String line : lines) {
            trimmed.add(line.substring(indent.length()));
        }
        return trimmed;
    }
    
    private WriteInstruction prepareVariables(TriggerTree context) {
        return (writer, visitor) -> {
            int i = 0;
            for (PreVariable variable : context.getVariables()) {
                if (!variable.skipPreset()) {
                    if (variable.atomic) {
                        visitor.visitInsn(1); // push null
                        wrap.accept(writer, visitor);
                        visitor.visitVarInsn(58, i); // astore
                    } else {
                        visitor.visitInsn(1); // push null
                        visitor.visitVarInsn(58, i); // astore
                    }
                }
                if (variable.parameter) {
                    // this is handled by the dynamic callsite now
                }
                i++;
            }
        };
    }
    
}
