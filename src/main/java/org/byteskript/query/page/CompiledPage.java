package org.byteskript.query.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CompiledPage implements Runnable {
    
    private final Runnable runnable;
    private final String hash;
    
    public CompiledPage(Runnable runnable, File file) {
        this.hash = this.getHash(file);
        this.runnable = runnable;
    }
    
    private String getHash(File file) {
        try (final InputStream stream = new FileInputStream(file)) {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(stream.readAllBytes());
            return new String(digest.digest());
        } catch (IOException | NoSuchAlgorithmException ex) {
            return "";
        }
    }
    
    @Override
    public void run() {
        runnable.run();
    }
    
    public boolean matches(File file) {
        final String hash = this.getHash(file);
        return (this.hash.equals(hash));
    }
    
}
