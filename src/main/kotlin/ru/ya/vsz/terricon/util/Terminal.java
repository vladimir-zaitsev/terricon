package ru.ya.vsz.terricon.util;
/**
 * Форматирование текста в терминале.
 *
 * @author vzay
 */
public class Terminal {
    private static final char ESC = '\u001b';       // начало ESC-последовательности
    private static final char CSI = '[';            // Control sequence introducer
    private static final char SGR = 'm';            // "set attributes" action

    public static final int RESET = 0;              // reset all attributes to their defaults    // нормальный
    public static final int BOLD = 1;               // set bold                                  // жирный
    public static final int HALF_BRIGHT = 2;        // set half-bright (simulated with color on a color display)
    public static final int UNDERLINE = 4;          // set underscore (simulated with color ...) // подчеркнутый
                                                    // (the  colors  used  to  simulate dim or underline are set
                                                    //     using ESC ] ...)
    public static final int BLINK = 5;              // set blink                                 // мигающий
    public static final int REVERSE_COLOR = 7;      // set reverse video                         // инвертировать цвета
    // public static final int ? = 8;               //                                           // невидимый
    // public static final int ?= 10;               // reset selected mapping, display control flag, and  toggle
                                                    // meta flag (ECMA-48 says "primary font").
    // public static final int ? = 11;              // select null mapping, set display control flag, reset tog‐
                                                    // gle meta flag (ECMA-48 says "first alternate font").
    // public static final int ? = 12;              // select null mapping, set display control flag, set toggle
                                                    // meta  flag  (ECMA-48  says "second alternate font").  The
                                                    // toggle meta flag causes the high bit of a byte to be tog‐
                                                    // gled before the mapping table translation is done.
    // public static final int ? = 21;              // set normal intensity (ECMA-48 says "doubly underlined")
    // public static final int ? = 22;              // set normal intensity
    public static final int UNDERLINE_OFF = 24;     // underline off
    public static final int BLINK_OFF = 25;         // blink off
    public static final int REVERSE_COLOR_OFF = 27; // reverse video off
    public static final int FOREGROUND_COLOR = 30;  // префикс цвета текста, надо сложить с нужным цветом

    public enum COLOR {
        WHITE,   // = 0; //set black foreground/background    30/40  // черный  (на самом деле белый)  цвет текста/фона
        RED,     // = 1; //set red foreground/background      31/41  // красный    цвет текста/фона
        GREEN,   // = 2; //set green foreground/background    32/42  // зеленый    цвет текста/фона
        BROWN,   // = 3; //set brown foreground/background    33/43  // желтый     цвет текста/фона
        BLUE,    // = 4; //set blue foreground/background     34/44  // синий      цвет текста/фона
        MAGENTA, // = 5; //set magenta foreground/background  35/45  // фиолетовый цвет текста/фона
        CYAN,    // = 6; //set cyan foreground/background     36/46  // голубой    цвет текста/фона
        GRAY,    // = 7; //set white foreground/background    37/47  // белый   (на самом деле серый)  цвет текста/фона
    }

    // public static final int ? = 38;             // set underscore on, set default foreground color
    public static final int RESET_COLOR = 39;      // set underscore off, set default foreground color
    public static final int BACKGROUND_COLOR = 40; // префикс цвета фона, надо сложить с нужным цветом
    public static final int RESET_BACKGROUND = 49; // set default background color

    private Terminal() {
    }

    /**
     * Форматировать строку одним атрибутом.
     */
    public static String attribute(String s, int attribute, int reset) {
        return ""
            + ESC + CSI + attribute + SGR
            + s
            + ESC + CSI + reset + SGR;
    }

    public static String color(String s, COLOR color) {
        return attribute(s, FOREGROUND_COLOR + color.ordinal(), RESET_COLOR);
    }

    public static String background(String s, COLOR color) {
        return attribute(s, BACKGROUND_COLOR + color.ordinal(), RESET_COLOR);
    }

    public static String inverse(String s) {
        return attribute(s, REVERSE_COLOR, REVERSE_COLOR_OFF);
    }

    public static String red(String s) {
        return color(s, COLOR.RED);
    }

    public static String green(String s) {
        return color(s, COLOR.GREEN);
    }

    public static String brown(String s) {
        return color(s, COLOR.BROWN);
    }

    public static String blue(String s) {
        return color(s, COLOR.BLUE);
    }

    public static String magenta(String s) {
        return color(s, COLOR.MAGENTA);
    }

    public static String cyan(String s) {
        return color(s, COLOR.CYAN);
    }

// public static String unicode_underline(String s){
//  StringBuilder res = new StringBuilder(2*s.length());
//  for (char ch: s.toCharArray()){
//   res.append((char)0x0332).append(ch);
//  }
//  return res.toString();
// }

//Управление положением курсора
//
//\033[#A передвинуть курсор вверх на # строк
//\033[#B передвинуть курсор вниз на # строк
//\033[#С передвинуть курсор вправо на # столбцов
//\033[#D передвинуть курсор влево на # столбцов
//\033[#E передвинуть курсор вниз на # строк и поставить в начало строки
//\033[#F передвинуть курсор вверх на # строк и поставить в начало строки
//\033[#G переместить курсор в указанный столбец текущей строки
//\033[#;#H задает абсолютные координаты курсора (строка, столбец) (origin at 1,1).
//J   ED        Erase display (default: from cursor to end of display).
//  ESC [ 1 J: erase from start to cursor.
//  ESC [ 2 J: erase whole display.
//  ESC [ 3 J: erase whole display including scroll-back
// buffer (since Linux 3.0).
//K   EL        Erase line (default: from cursor to end of line).
//  ESC [ 1 K: erase from start of line to cursor.
//  ESC [ 2 K: erase whole line.
//L   IL        Insert the indicated # of blank lines.
//M   DL        Delete the indicated # of lines.
//P   DCH       Delete the indicated # of characters on current line.
//X   ECH       Erase the indicated # of characters on current line.

}
