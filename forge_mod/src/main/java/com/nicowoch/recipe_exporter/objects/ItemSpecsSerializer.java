package com.nicowoch.recipe_exporter.objects;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class ItemSpecsSerializer implements JsonSerializer<ItemSpecs> {

    @Override
    public JsonElement serialize(ItemSpecs src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        List<String> keys = src.getKeys();
        List<String> values = src.getValues();

        for (int i = 0; i < keys.size(); i++) {
            obj.addProperty(keys.get(i), values.get(i));
        }

        return obj;
    }
}
