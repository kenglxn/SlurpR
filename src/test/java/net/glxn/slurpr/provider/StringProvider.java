package net.glxn.slurpr.provider;

public class StringProvider implements LookupProvider<String> {

    @Override
    public String lookup(String key) {
        return key;
    }
}
