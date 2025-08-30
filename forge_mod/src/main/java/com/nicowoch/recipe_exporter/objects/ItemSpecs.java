package com.nicowoch.recipe_exporter.objects;

import java.util.ArrayList;
import java.util.List;

public class ItemSpecs {

    private final List<String> keys = new ArrayList<>();
    private final List<String> values = new ArrayList<>();

    public void add(String key, String value) {
        keys.add(key);
        values.add(value);
    }

    public void add(String key, int value) {
        add(key, String.valueOf(value));
    }

    public void add(String key, boolean value) {
        add(key, String.valueOf(value));
    }

    public List<String> getKeys() {
        return keys;
    }
    public List<String> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemSpecs)) return false;

        ItemSpecs other = (ItemSpecs) o;

        return keys.equals(other.keys) && values.equals(other.values);
    }
}
