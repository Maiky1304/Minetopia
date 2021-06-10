package dev.maiky.minetopia.modules.ddgitems.items.classes;

import dev.maiky.minetopia.modules.bags.bag.BagType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public enum ItemType {

    ITEM,REGULAR_ARMOR_SET,CUSTOM_ARMOR_SET,SINGLE_ARMOR_PIECE;

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
