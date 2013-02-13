package net.glxn.slurpr;

import net.glxn.slurpr.exception.*;

import java.io.*;

public class SlurpR {
    private final InputStream stream;

    private SlurpR(InputStream stream) {
        this.stream = stream;
    }

    public static SlurpR csv(String fileName) {
        InputStream stream = ClassLoader.getSystemResourceAsStream(fileName);
        if(stream == null) {
            throw new SlurpRException("failed to find file on classpath with name " + fileName);
        }
        return new SlurpR(stream);
    }

    public <T> SlurpMapper<T> to(Class<T> clazz) {
        return new SlurpMapper<T>(stream, clazz);
    }
}
