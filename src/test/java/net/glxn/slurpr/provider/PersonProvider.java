package net.glxn.slurpr.provider;

import net.glxn.slurpr.model.*;

public class PersonProvider implements LookupProvider<Person> {

    @Override
    public Person lookup(String key) {
        return new Person(key);
    }
}
