package net.glxn.slurpr;

import net.glxn.slurpr.exception.*;
import org.apache.commons.io.*;

import java.io.*;

public class Resources {
    static InputStream getStreamFromClasspath(String fileName) {
        InputStream stream = ClassLoader.getSystemResourceAsStream(fileName);
        if (stream == null) {
            throw new SlurpRException("failed to find file on classpath with name " + fileName);
        }
        return stream;
    }

    static String getClasspathFileContent(String fileName) {
        try {
            return IOUtils.toString(getStreamFromClasspath(fileName));
        } catch (IOException e) {
            throw new SlurpRException("failed to get file content for " + fileName, e);
        }
    }
}