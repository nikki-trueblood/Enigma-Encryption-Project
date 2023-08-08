package enigma;

import static enigma.EnigmaException.*;
import java.util.ArrayList;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author NikkiTrueblood
 */
class Permutation {
    /** ArrayList of Strings to represent
     * all the cycles in the permutation. */
    private ArrayList<String> _perm = new ArrayList<String>();
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        char c;
        for (int i = 0; i < cycles.length(); i++) {
            c = cycles.charAt(i);
            if (!(_alphabet.contains(c) || c == '(' || c == ')' || c == ' ')) {
                throw new EnigmaException("Wrong character input.");
            }
            if (_alphabet.contains(c)) {
                if (cycles.substring(i + 1).indexOf(c) != -1) {
                    throw new EnigmaException("Letter is "
                            + "repeated too many times.");
                }
            }

        }
        String adder = "";

        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) != '(') {
                if (cycles.charAt(i) == ')') {
                    addCycle(adder);
                    adder = "";
                } else if (cycles.charAt(i) == ' ') {
                    int something = 0;
                } else {
                    adder = adder + cycles.charAt(i);
                }
            }
        }
    }
    ArrayList<String> perm() {
        return _perm;
    }
    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _perm.add(cycle + cycle.charAt(0));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char pp = _alphabet.toChar(wrap(p));
        return _alphabet.toInt(permute(pp));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char cc = _alphabet.toChar(wrap(c));
        return _alphabet.toInt(invert(cc));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (int i = 0; i < _perm.size(); i++) {
            for (int j = 0; j < _perm.get(i).length(); j++) {
                if (_perm.get(i).charAt(j) == p) {
                    return _perm.get(i).charAt(j + 1);
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (int i = 0; i < _perm.size(); i++) {
            for (int j = 0; j < _perm.get(i).length(); j++) {
                if (_perm.get(i).charAt(j) == c && j != 0) {
                    return _perm.get(i).charAt(j - 1);
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
}
