package dev.maiky.minetopia.modules.colors.fonts;

public enum CustomFont {

    A("Ａ"), B("Ｂ"), C("Ｃ"), D("Ｄ"), E("Ｅ"),
    F("Ｆ"), G("Ｇ"), H("Ｈ"), I("Ｉ"), J("Ｊ"), K("Ｋ"), L("Ｌ"),
    M("Ｍ"), N("Ｎ"), O("Ｏ"), P("Ｐ"), Q("Ｑ"), R("Ｒ"), S("Ｓ"),
    T("Ｔ"), U("Ｕ"), V("Ｖ"), W("Ｗ"), X("Ｘ"), Y("Ｙ"), Z("Ｚ"),

    a("ａ"), b("ｂ"), c("ｃ"), d("ｄ"), e("ｅ"),
    f("ｆ"), g("ｇ"), h("ｈ"), i("ｉ"), j("ｊ"), k("ｋ"), l("ｌ"),
    m("ｍ"), n("ｎ"), o("ｏ"), p("\uFF50"), q("\uFF51"), r("\uFF52"), s("\uFF53"),
    t("\uFF54"), u("\uFF55"), v("\uFF56"), w("\uFF57"), x("\uFF58"), y("\uFF59"), z("\uFF5A"),

    uitroepteken("！", "!"), hash("＂", "\""), hashtag("＃", "#"), dollar("＄", "$"),
    procent("％", "%"), and("＆", "&"), komma("＇", "'"), haakje1("（", "("), haakje2("）", ")"),
    star("＊", "*"), plus("＋", "+"), comma("，", ","), stripe("－", "-"), dot("．", "."),
    slash_right("／", "/"), zero("０", "0"), one("１", "1"), two("２", "2"), three("３", "3"),
    four("４", "4"), five("５", "5"), six("６", "6"), seven("７", "7"), eight("８", "8"),
    nine("９", "9"), di("：", ":"), di2("；", ";"), lowerThan("＜", "<"), equals("＝", "="),
    higherThan("＞", ">"), question("？", "?"), ap("＠", "@"), bracketLeft("［", "["), bracketRight("］", "]"),
    dakje("＾", "^"), underline("＿", "_"), highComma("｀", "`"), bracketRibbelLeft("｛", "{"), bracketRibbelRight("｝", "}"),
    pipe("｜", "|"), sea("～", "~");

    private String string;
    private String string2 = null;

    CustomFont(String aaa){
        this.string = aaa;
    }
    CustomFont(String aaa, String string2){
        this(aaa);

        boolean isGetal;
        try {
            Integer.parseInt(string2);
            isGetal = true;
        } catch(NumberFormatException exception){
            isGetal = false;
        }

        if (!isGetal) {
            this.string2 = "\\" + string2;
        }else{
            this.string2 = string2;
        }
    }

    public String replacer() {
        return string2;
    }

    public String a() {
        return this.string;
    }
}
