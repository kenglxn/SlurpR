package net.glxn.slurpr;

import net.glxn.qbe.reflection.*;
import net.glxn.slurpr.exception.*;
import net.glxn.slurpr.provider.*;
import net.sf.json.*;
import org.springframework.context.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static net.glxn.qbe.reflection.Reflection.*;
import static net.glxn.slurpr.Resources.*;

public class SlurpMapper<T> {

    public static final String SPRING = "SPRING";
    private final Class<T> clazz;
    private final Scanner scanner;
    private final Map<String, Integer> headers = new HashMap<String, Integer>();
    private HashMap<String, String> fieldMapping = new HashMap<String, String>();
    private HashMap<String,String> providers = new HashMap<String, String>();
    private ApplicationContext applicationContext;

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
                    } catch(SlurpRException e) {
                        throw e;
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
            String providerName = providers.get(key);
            if (SPRING.equals(providerName)) {
                if(applicationContext == null) {
                    String message = "mapping file says to use spring, but no applicationContext is present. " +
                            "Assign context with usingContext(context)";
                    throw new SlurpRException(message);
                }
                Map<String, LookupProvider> providerMap = applicationContext.getBeansOfType(LookupProvider.class);
                for (LookupProvider lookupProvider : providerMap.values()) {
                    Method method;
                    try {
                        method = lookupProvider.getClass().getMethod("lookup", String.class);
                    } catch (NoSuchMethodException e) {
                        throw new SlurpRException("failed", e); // TODO fix better
                    }
                    if(clazz.equals(method.getReturnType())) {
                        value = lookupProvider.lookup((String) value);
                    }
                }
            } else {
                value = Reflection.<LookupProvider>createInstance(providerName).lookup((String) value);
            }
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

    public SlurpMapper<T> usingContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }
}