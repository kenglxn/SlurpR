package net.glxn.slurpr;

import java.io.*;

import static net.glxn.slurpr.Resources.*;

public class SlurpR {
    private final InputStream stream;

    private SlurpR(InputStream stream) {
        this.stream = stream;
    }

    public static SlurpR csv(String fileName) {
        InputStream stream = getStreamFromClasspath(fileName);
        return new SlurpR(stream);
    }

    public <T> SlurpMapper<T> to(Class<T> clazz) {
        return new SlurpMapper<T>(stream, clazz);
    }
}
