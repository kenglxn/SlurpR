package net.glxn.slurpr;

import net.glxn.slurpr.exception.*;
import net.sf.json.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static net.glxn.qbe.reflection.Reflection.*;
import static net.glxn.slurpr.Resources.*;

public class SlurpMapper<T> {

    private final Scanner scanner;
    private final Class<T> clazz;
    private final Map<String, Integer> headers = new HashMap<String, Integer>();
    private HashMap<String, String> fieldMapping = new HashMap<String, String>();

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
                boolean headerContainsField = headers.containsKey(field.getName());
                boolean mappingContainsField = fieldMapping.containsKey(field.getName());
                if (headerContainsField || mappingContainsField) {
                    field.setAccessible(true);
                    try {
                        String key = headerContainsField ? field.getName() : fieldMapping.get(field.getName());
                        field.set(instance, lineValues[headers.get(key)]);
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

    public SlurpMapper<T> usingMapping(String mappingFile) {
        JSONObject mappingJSON = (JSONObject) JSONSerializer.toJSON(getClasspathFileContent(mappingFile));
        for (Object field : mappingJSON.keySet()) {
            fieldMapping.put((String) field, mappingJSON.getString((String) field));
        }
        return this;
    }
}
