package net.glxn.slurpr;

import net.glxn.qbe.reflection.*;
import net.glxn.slurpr.exception.*;
import net.glxn.slurpr.provider.*;
import net.sf.json.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static net.glxn.qbe.reflection.Reflection.*;
import static net.glxn.slurpr.Resources.*;

public class SlurpMapper<T> {

    private final Class<T> clazz;
    private final Scanner scanner;
    private final Map<String, Integer> headers = new HashMap<String, Integer>();
    private HashMap<String, String> fieldMapping = new HashMap<String, String>();
    private HashMap<String,String> providers = new HashMap<String, String>();

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
            for (Field field : fields(hierarchy(clazz))) {
                String fieldName = field.getName();
                if (headers.containsKey(fieldName) || fieldMapping.containsKey(fieldName)) {
                    field.setAccessible(true);
                    try {
                        String key = headers.containsKey(fieldName) ? fieldName : fieldMapping.get(fieldName);
                        field.set(instance, findValue(lineValues, key));
                    } catch (Exception e) {
                        throw new SlurpRException("unable to set value on field", e);
                    }
                }
            }
            list.add(instance);
        }
        return list;
    }

    private Object findValue(String[] lineValues, String key) {
        Object value = lineValues[headers.get(key)];
        if (providers.containsKey(key)) {
            value = Reflection.<LookupProvider>createInstance(providers.get(key)).lookup((String) value);
        }
        return value;
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
            String value = mappingJSON.getString((String) field);
            JSONObject jsonObject = mappingJSON.optJSONObject((String) field);
            if (jsonObject != null) {
                value = jsonObject.getString("key");
                providers.put(value, jsonObject.getString("provider"));
            }
            fieldMapping.put((String) field, value);
        }
        return this;
    }
}