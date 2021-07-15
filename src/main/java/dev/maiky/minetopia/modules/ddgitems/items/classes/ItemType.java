package dev.maiky.minetopia.modules.ddgitems.items.classes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public enum ItemType {

    ITEM("Items"),
    REGULAR_ARMOR_SET("Armor Setjes"),
    CUSTOM_ARMOR_SET("Optifine Setjes"),
    SINGLE_ARMOR_PIECE("Losse Stukjes Armor");

    private final @Getter String label;

    ItemType(String label) {
        this.label = label;
    }

    public static List<String> list() {
        List<String> list = new ArrayList<>();
        Iterator<ItemType> bagTypeIterator = createIterator();
        while(bagTypeIterator.hasNext())
            list.add(bagTypeIterator.next().toString());
        return list;
    }

    public static Iterator<ItemType> createIterator() {
        return new ArrayList<>(Arrays.asList(values())).iterator();
    }

}
