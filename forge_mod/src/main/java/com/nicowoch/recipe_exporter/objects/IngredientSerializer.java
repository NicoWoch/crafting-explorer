package com.nicowoch.recipe_exporter.objects;

import com.google.gson.*;

import java.lang.reflect.Type;

public class IngredientSerializer implements JsonSerializer<Ingredient> {

    @Override
    public JsonElement serialize(Ingredient src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray obj = new JsonArray();

        obj.add(src.item_index);
        obj.add(src.count);

        return obj;
    }
}
