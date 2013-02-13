package net.glxn.slurpr;

import net.glxn.qbe.reflection.*;

import java.io.*;
import java.util.*;

public class SlurpMapper<T> {

    private final Scanner scanner;
    private final Class<T> clazz;

    SlurpMapper(InputStream stream, Class<T> clazz) {
        this.clazz = clazz;
        scanner = new Scanner(stream);
    }

    public List<T> list() {
        ArrayList<T> list = new ArrayList<T>();
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            list.add(Reflection.createInstance(clazz));
        }
        return list;
    }
}
