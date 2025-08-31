package ru.ya.vsz.terricon.util;

import java.util.UUID;

/**
 * Doc 9303.
 * https://ru.wikipedia.org/wiki/Транслитерация_русского_алфавита_латиницей
 */
@SuppressWarnings("WeakerAccess")
public class Transliteration {
    private static final char HI_BYTE_MASK = 0xFF00;
    private static final char LO_BYTE_MASK = 0x00FF;
    private static final char CYRILLIC_HI_BYTE = 0x0400;
    private static final int ALL_BYTE_COUNT = 256;
    private static final String[] T = new String[ALL_BYTE_COUNT];

    static {
        T['а' & LO_BYTE_MASK] = "a";
        T['б' & LO_BYTE_MASK] = "b";
        T['в' & LO_BYTE_MASK] = "v";
        T['г' & LO_BYTE_MASK] = "g";
        T['д' & LO_BYTE_MASK] = "d";
        T['е' & LO_BYTE_MASK] = "e";
        T['ё' & LO_BYTE_MASK] = "e";
        T['ж' & LO_BYTE_MASK] = "zh";
        T['з' & LO_BYTE_MASK] = "z";
        T['и' & LO_BYTE_MASK] = "i";
        T['й' & LO_BYTE_MASK] = "i";
        T['к' & LO_BYTE_MASK] = "k";
        T['л' & LO_BYTE_MASK] = "l";
        T['м' & LO_BYTE_MASK] = "m";
        T['н' & LO_BYTE_MASK] = "n";
        T['о' & LO_BYTE_MASK] = "o";
        T['п' & LO_BYTE_MASK] = "p";
        T['р' & LO_BYTE_MASK] = "r";
        T['с' & LO_BYTE_MASK] = "s";
        T['т' & LO_BYTE_MASK] = "t";
        T['у' & LO_BYTE_MASK] = "u";
        T['ф' & LO_BYTE_MASK] = "f";
        T['х' & LO_BYTE_MASK] = "kh";
        T['ц' & LO_BYTE_MASK] = "ts";
        T['ч' & LO_BYTE_MASK] = "ch";
        T['ш' & LO_BYTE_MASK] = "sh";
        T['щ' & LO_BYTE_MASK] = "shch";
        T['ъ' & LO_BYTE_MASK] = "";
        T['ы' & LO_BYTE_MASK] = "y";
        T['ь' & LO_BYTE_MASK] = "";
        T['э' & LO_BYTE_MASK] = "e";
        T['ю' & LO_BYTE_MASK] = "iu";
        T['я' & LO_BYTE_MASK] = "ia";
        T['ß' & LO_BYTE_MASK] = "ss";
        T['à' & LO_BYTE_MASK] = "a";
        T['á' & LO_BYTE_MASK] = "a";
        T['â' & LO_BYTE_MASK] = "a";
        T['ã' & LO_BYTE_MASK] = "a";
        T['ä' & LO_BYTE_MASK] = "a";
        T['å' & LO_BYTE_MASK] = "a";
        T['æ' & LO_BYTE_MASK] = "ae";
        T['ç' & LO_BYTE_MASK] = "c";
        T['è' & LO_BYTE_MASK] = "e";
        T['é' & LO_BYTE_MASK] = "e";
        T['ê' & LO_BYTE_MASK] = "e";
        T['ë' & LO_BYTE_MASK] = "e";
        T['ì' & LO_BYTE_MASK] = "i";
        T['í' & LO_BYTE_MASK] = "i";
        T['î' & LO_BYTE_MASK] = "i";
        T['ï' & LO_BYTE_MASK] = "i";
        T['ð' & LO_BYTE_MASK] = "d";
        T['ñ' & LO_BYTE_MASK] = "n";
        T['ò' & LO_BYTE_MASK] = "o";
        T['ó' & LO_BYTE_MASK] = "o";
        T['ô' & LO_BYTE_MASK] = "o";
        T['õ' & LO_BYTE_MASK] = "o";
        T['ö' & LO_BYTE_MASK] = "o";
        T['ø' & LO_BYTE_MASK] = "oe";
        T['ù' & LO_BYTE_MASK] = "u";
        T['ú' & LO_BYTE_MASK] = "u";
        T['û' & LO_BYTE_MASK] = "u";
        T['ü' & LO_BYTE_MASK] = "u";
        T['ý' & LO_BYTE_MASK] = "y";
        T['þ' & LO_BYTE_MASK] = "th";
        T['ÿ' & LO_BYTE_MASK] = "y";
        T['ā' & LO_BYTE_MASK] = "a";
        T['ą' & LO_BYTE_MASK] = "a";
        T['č' & LO_BYTE_MASK] = "c";
//        T['ł' & LO_BYTE_MASK] = "l"; // Overwritten array element with 'т'
        T['œ' & LO_BYTE_MASK] = "oe";
        T['ś' & LO_BYTE_MASK] = "s";
        T['ş' & LO_BYTE_MASK] = "s";
        T['š' & LO_BYTE_MASK] = "s";
//        T['ı' & LO_BYTE_MASK] = "i"; // Overwritten array element with 'б'
        T['ƒ' & LO_BYTE_MASK] = "f";
    }

    private Transliteration() {
    }

    public static String transliterate(String s) {
        StringBuilder res = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch & HI_BYTE_MASK) != CYRILLIC_HI_BYTE) {
                res.append(ch);
                continue;
            }
            String newCh = T[Character.toLowerCase(ch) & LO_BYTE_MASK];
            if (newCh == null) {
                res.append(ch);
                continue;
            }
            res.append(newCh);
        }
        return res.toString();
    }

    public static void main(String[] args) {
        System.out.println(transliterate("мама мыла раму"));
    }

}
