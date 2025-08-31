package ru.ya.vsz.terricon.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MutableString {
    private final boolean noSpaces;
    private final char[] chars;
    private int length = 0;

    public MutableString() {
        this(1000, false);
    }

    public MutableString(int maxLength, boolean noSpaces) {
        this.noSpaces = noSpaces;
        chars = new char[maxLength];
    }

    public MutableString readln(InputStream in) throws IOException {
        length = 0;
        char ch;
        do {
            int read = in.read();
            if (read < 0) {
                break;
            }
            ch = (char) read;
            if (noSpaces && Character.isWhitespace(ch)) {
                continue;
            }
            chars[length] = ch;
            length++;
        } while (ch != '\n');
        return this;
    }

    public void println(OutputStream out) throws IOException {
        for (int i = 0; i < length; i++) {
            out.write(chars[i]);
        }
        out.write('\n');
    }

    public void set(MutableString s) {
        this.length = s.length;
        System.arraycopy(s.chars, 0, this.chars, 0, s.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutableString that = (MutableString) o;
        if (length != that.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (chars[i] != that.chars[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (chars == null) {
            return 0;
        }
        int result = 1;
        for (int i = 0; i < length; i++) {
            char element = chars[i];
            result = 31 * result + element;
        }
        return result;
    }

    @Override
    public String toString() {
        return new String(chars, 0, length);
    }
}
