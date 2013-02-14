package net.glxn.slurpr;

import net.glxn.slurpr.exception.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static net.glxn.qbe.reflection.Reflection.*;

public class SlurpMapper<T> {

    private final Scanner scanner;
    private final Class<T> clazz;
    private final Map<String,Integer> headers = new HashMap<String, Integer>();

    SlurpMapper(InputStream stream, Class<T> clazz) {
        this.clazz = clazz;
        scanner = new Scanner(stream);
        createHeaderMap();
    }

    public List<T> list() {
        ArrayList<T> list = new ArrayList<T>();
        while (scanner.hasNextLine()) {
            String[] lineValues = scanner.nextLine().split(",");

            T instance = createInstance(clazz);
            List<Field> fields = fields(hierarchy(clazz));
            for (Field field : fields) {
                if (headers.containsKey(field.getName())) {
                    field.setAccessible(true);
                    try {
                        field.set(instance, lineValues[headers.get(field.getName())]);
                    } catch (IllegalAccessException e) {
                        throw new SlurpRException("unable to set value on field", e);
                    }
                }
            }
            list.add(instance);
        }
        return list;
    }

    private void createHeaderMap() {
        String[] lineValues = scanner.nextLine().split(",");
        for (Integer i = 0; i < lineValues.length; i++) {
            headers.put(lineValues[i], i);
        }
        scanner.reset();
    }
}
