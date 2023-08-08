package enigma;

import java.util.Collection;
import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author NikkiTrueblood
 */
class Machine {
    /** Integer to represent the number of rotors in the machine. */
    private int _numRotors;
    /** Integer to represent the number of pawls in the machine. */
    private int _numPawls;
    /** ArrayList of rotors to represent all
     * the available rotors of the machine. */
    private ArrayList<Rotor> _allRotors = new ArrayList<Rotor>();
    /** Permutation that represents the plugboard. */
    private Permutation _plugboard;
    /** Arraylist of Rotors to represent all the
     * rotors being used in this particular machine. */
    private ArrayList<Rotor> _myRotors = new ArrayList<Rotor>(_numRotors);
    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _plugboard = new Permutation("", _alphabet);
        for (Rotor r: allRotors) {
            _allRotors.add(r);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        int i = 0;
        for (Rotor r:_myRotors) {
            if (i == k) {
                return r;
            }
            i++;
        }
        return _myRotors.get(0);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (Rotor r: _allRotors) {
            if (rotors[0].equals(r.name())) {
                if (!r.reflecting()) {
                    throw new EnigmaException("First rotor isn't a reflector.");
                } else {
                    _myRotors.add(r);
                }
            }
        }
        for (int i = 1; i < rotors.length; i++) {
            for (Rotor r: _allRotors) {
                if (rotors[i].equals(r.name())) {
                    _myRotors.add(r);
                }
            }
        }
        int movingCounter = 0;
        for (Rotor r: _myRotors) {
            if (r.rotates()) {
                movingCounter++;
            }
        }
        if (movingCounter != _numPawls) {
            throw new EnigmaException("Wrong number of pawls.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Incorrect setting length.");
        }
        for (int i = 0; i < setting.length(); i++) {
            if (_alphabet.contains(setting.charAt(i))) {
                _myRotors.get(i + 1).set(setting.charAt(i));
            } else {
                throw new EnigmaException("Setting not in alphabet.");
            }
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        for (int i = 0; i < _numRotors; i++) {
            if ((i == _numRotors - 1) || (_myRotors.get(i).rotates()
                    && _myRotors.get(i + 1).atNotch())) {
                _myRotors.get(i).advance();
                if (i < _numRotors - 1) {
                    _myRotors.get(i + 1).advance();
                    i++;
                }
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int updated = c;
        for (int i = _myRotors.size() - 1; i >= 0; i--) {
            updated = _myRotors.get(i).convertForward(updated);
        }
        for (int i = 1; i < _myRotors.size(); i++) {
            updated = _myRotors.get(i).convertBackward(updated);
        }
        return updated;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String updated = "";
        for (int i = 0; i < msg.length(); i++) {
            int c = _alphabet.toInt(msg.charAt(i));
            updated += _alphabet.toChar(convert(c));
        }
        return updated;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    public boolean inAllRotors(String n) {
        for (Rotor r: _allRotors) {
            if (r.name().equals(n)) {
                return true;
            }
        }
        return false;
    }
    public void resetRotors() {
        _myRotors = new ArrayList<Rotor>(_numRotors);
    }
}
