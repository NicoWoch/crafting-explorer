package com.nicowoch.recipe_exporter.registers;

import javax.annotation.Nullable;
import java.util.*;

public abstract class RegisterBase<T> {

    protected final List<T> register = new ArrayList<>();

    protected final Map<String, List<AbstractMap.SimpleEntry<T, Integer>>> registerByHash = new HashMap<>();

    public List<T> getRegister() {
        return register;
    }

    protected int findOrAddItemToRegistry(T item) {
        Integer registryIndex = findItemInRegistry(item);

        if (registryIndex == null) {
            registryIndex = addItemToRegistry(item);
        }

        return registryIndex;
    }

    @Nullable
    protected Integer findItemInRegistry(T item) {
        String itemHash = getItemHash(item);

        if (!registerByHash.containsKey(itemHash)) {
            return null;
        }

        for (AbstractMap.SimpleEntry<T, Integer> entry : registerByHash.get(itemHash)) {
            if (compareItems(entry.getKey(), item)) {
                return entry.getValue();
            }
        }

        return null;
    }

    protected int addItemToRegistry(T item) {
        String itemHash = getItemHash(item);

        int registerIndex = register.size();
        register.add(item);

        if (!registerByHash.containsKey(itemHash)) {
            registerByHash.put(itemHash, new ArrayList<>());
        }

        registerByHash.get(itemHash).add(new AbstractMap.SimpleEntry<>(item, registerIndex));

        return registerIndex;
    }

    protected abstract String getItemHash(T item);
    protected abstract boolean compareItems(T a, T b);
}
