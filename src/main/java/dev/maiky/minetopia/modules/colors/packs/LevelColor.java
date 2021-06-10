package dev.maiky.minetopia.modules.colors.packs;

public enum LevelColor {

    CHATCOLOR_NORMAL_GRAY("7", false, "Grijs"),
    CHATCOLOR_NORMAL_WHITE("f", false, "Wit"),
    CHATCOLOR_NORMAL_DARKAQUA("3", false, "Cyaan"),
    CHATCOLOR_NORMAL_BLUE("9", false, "Blauw"),
    CHATCOLOR_NORMAL_DARKBLUE("1", false, "Donker Blauw"),
    CHATCOLOR_NORMAL_AQUA("b", false, "Licht Blauw"),
    CHATCOLOR_NORMAL_DARKRED("4", false, "Donker Rood"),
    CHATCOLOR_NORMAL_RED("c", false, "Rood"),
    CHATCOLOR_NORMAL_YELLOW("e", false, "Geel"),
    CHATCOLOR_NORMAL_GOLD("6", false, "Goud"),
    CHATCOLOR_NORMAL_DARKGRAY("8", false, "Donker Grijs"),
    CHATCOLOR_NORMAL_GREEN("a", false, "Licht Groen"),
    CHATCOLOR_NORMAL_DARKGREEN("2", false, "Donker Groen"),
    CHATCOLOR_NORMAL_DARKPURPLE("5", false, "Paars"),
    CHATCOLOR_NORMAL_LIGHTPURPLE("d", false, "Roze"),
    CHATCOLOR_NORMAL_BLACK("0", false, "Zwart"),

    CHATCOLOR_BOLD_GRAY("7&l", false, "Grijs"),
    CHATCOLOR_BOLD_WHITE("f&l", false, "Wit"),
    CHATCOLOR_BOLD_DARKAQUA("3&l", false, "Cyaan"),
    CHATCOLOR_BOLD_BLUE("9&l", false, "Blauw"),
    CHATCOLOR_BOLD_DARKBLUE("1&l", false, "Donker Blauw"),
    CHATCOLOR_BOLD_AQUA("b&l", false, "Licht Blauw"),
    CHATCOLOR_BOLD_DARKRED("4&l", false, "Donker Rood"),
    CHATCOLOR_BOLD_RED("c&l", false, "Rood"),
    CHATCOLOR_BOLD_YELLOW("e&l", false, "Geel"),
    CHATCOLOR_BOLD_GOLD("6&l", false, "Goud"),
    CHATCOLOR_BOLD_DARKGRAY("8&l", false, "Donker Grijs"),
    CHATCOLOR_BOLD_GREEN("a&l", false, "Licht Groen"),
    CHATCOLOR_BOLD_DARKGREEN("2&l", false, "Donker Groen"),
    CHATCOLOR_BOLD_DARKPURPLE("5&l", false, "Paars"),
    CHATCOLOR_BOLD_LIGHTPURPLE("d&l", false, "Roze"),
    CHATCOLOR_BOLD_BLACK("0&l", false, "Zwart"),
    // ---------- Fonted colors ------------- //
    CHATCOLOR_NORMAL_PAASROZE("f", true, "Paas Roze"),
    CHATCOLOR_NORMAL_DONKERGEEL("e", true, "Donker Geel"),
    CHATCOLOR_NORMAL_SUIKERSPIN("d", true, "Suikerspin"),
    CHATCOLOR_NORMAL_VUURROOD("c", true, "Vuur Rood"),
    CHATCOLOR_NORMAL_SKYBLUE("b", true, "Sky Blue"),
    CHATCOLOR_NORMAL_LIMEGROEN("a", true, "Lime Groen"),
    CHATCOLOR_NORMAL_VIOLET("9", true, "Violet"),
    CHATCOLOR_NORMAL_ROMIG("8", true, "Romig"),
    CHATCOLOR_NORMAL_ZALM("7", true, "Zalm"),
    CHATCOLOR_NORMAL_ORANJE("6", true, "Oranje"),
    CHATCOLOR_NORMAL_BARBIEPINK("5", true, "Rolandia"),
    CHATCOLOR_NORMAL_FERRARIROOD("4", true, "Ferrari Rood"),
    CHATCOLOR_NORMAL_WINDOWS("3", true, "Windows"),
    CHATCOLOR_NORMAL_DONKERGROEN("2", true, "Donker Groen"),
    CHATCOLOR_NORMAL_DEEPBLUE("1", true, "Deep Blue"),
    // -------- BOLD FONTED -------------- //
    CHATCOLOR_BOLD_PAASROZE("f&l", true, "Paas Roze"),
    CHATCOLOR_BOLD_DONKERGEEL("e&l", true, "Donker Geel"),
    CHATCOLOR_BOLD_SUIKERSPIN("d&l", true, "Suikerspin"),
    CHATCOLOR_BOLD_VUURROOD("c&l", true, "Vuur Rood"),
    CHATCOLOR_BOLD_SKYBLUE("b&l", true, "Sky Blue"),
    CHATCOLOR_BOLD_LIMEGROEN("a&l", true, "Lime Groen"),
    CHATCOLOR_BOLD_VIOLET("9&l", true, "Violet"),
    CHATCOLOR_BOLD_ROMIG("8&l", true, "Romig"),
    CHATCOLOR_BOLD_ZALM("7&l", true, "Zalm"),
    CHATCOLOR_BOLD_ORANJE("6&l", true, "Oranje"),
    CHATCOLOR_BOLD_BARBIEPINK("5&l", true, "Rolandia"),
    CHATCOLOR_BOLD_FERRARIROOD("4&l", true, "Ferrari Rood"),
    CHATCOLOR_BOLD_WINDOWS("3&l", true, "Windows"),
    CHATCOLOR_BOLD_DONKERGROEN("2&l", true, "Donker Groen"),
    CHATCOLOR_BOLD_DEEPBLUE("1&l", true, "Deep Blue");

    private String color;
    public boolean font;
    public String itemName;

    LevelColor(String color, boolean font, String name){
        this.color = color;
        this.font = font;
        this.itemName = name;
    }

    public String getColor() {
        return color;
    }
}
