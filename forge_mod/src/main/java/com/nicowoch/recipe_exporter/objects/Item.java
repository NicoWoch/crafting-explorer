package com.nicowoch.recipe_exporter.objects;

import java.util.Objects;

public class Item {
    public String type;
    public String id;

    public String display_name;
    public int metadata;

    public int icon_index;

    public ItemSpecs specific_data;

    // Remember to always update function below after adding/removing a field from class
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item other = (Item) o;

        return Objects.equals(type, other.type) &&
                Objects.equals(id, other.id) &&
                Objects.equals(display_name, other.display_name) &&
                Objects.equals(metadata, other.metadata) &&
                Objects.equals(specific_data, other.specific_data);
    }
}
