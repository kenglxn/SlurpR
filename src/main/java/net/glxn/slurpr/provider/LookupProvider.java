package net.glxn.slurpr.provider;

public interface LookupProvider<T> {
    T lookup(String key);
}
