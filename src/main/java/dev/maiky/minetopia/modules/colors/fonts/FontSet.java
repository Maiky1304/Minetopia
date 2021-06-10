package dev.maiky.minetopia.modules.colors.fonts;

public class FontSet {

    private static Object getLetter(String s){
        CustomFont letter;
        try {
            letter = CustomFont.valueOf(s);
        } catch (IllegalArgumentException exception){
            return s;
        }
        return letter;
    }

    public static String process(String text) {
        String returnable = text;
        for (CustomFont letter : CustomFont.values()){
            if (letter.replacer() != null){
                returnable = returnable.replaceAll(letter.replacer(), letter.a());
            }else {
                returnable = returnable.replaceAll(letter.toString(), letter.a());
            }
        }
        return returnable;
    }

}
