package com.nicowoch.recipe_exporter.objects;

import java.util.Objects;

public class Ingredient {

    public int item_index;
    public int count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;

        Ingredient other = (Ingredient) o;

        return item_index == other.item_index && count == other.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item_index, count);
    }
}
