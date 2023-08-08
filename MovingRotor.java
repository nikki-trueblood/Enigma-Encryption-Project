package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author NikkiTrueblood
 */
class MovingRotor extends Rotor {
    /** String to represent all the notches in the Rotor. */
    private String _notches;
    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    void advance() {
        int s = setting();
        set(s + 1);
    }

    @Override
    String notches() {
        return _notches;
    }

    boolean rotates() {
        return true;
    }


}
