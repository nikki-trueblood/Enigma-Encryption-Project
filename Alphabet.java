package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author NikkiTrueblood
 */
class Alphabet {
    /** Array to represent all the characters in the alphabet. */
    private char[] _alphabet;
    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        char[] characters = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            chars.getChars(i, i + 1, characters, i);
        }
        _alphabet = characters;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_alphabet[i] == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabet[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int returner = 0;
        for (int i = 0; i < size(); i++) {
            if (_alphabet[i] == ch) {
                returner = i;
            }
        }
        return returner;
    }

}
