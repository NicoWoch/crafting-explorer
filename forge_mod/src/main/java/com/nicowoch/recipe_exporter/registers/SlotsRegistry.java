package com.nicowoch.recipe_exporter.registers;

import com.nicowoch.recipe_exporter.objects.Ingredient;

import java.util.List;

public class SlotsRegistry extends RegisterBase<List<Ingredient>> {
    @Override
    protected String getItemHash(List<Ingredient> slot) {
        return String.valueOf(slot.size()) + '@' + slot.hashCode();
    }

    @Override
    protected boolean compareItems(List<Ingredient> a, List<Ingredient> b) {
        return a.equals(b);
    }

    public int registerSlot(List<Ingredient> slot) {
        return findOrAddItemToRegistry(slot);
    }
}
