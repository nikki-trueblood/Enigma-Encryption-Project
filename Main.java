
package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Collection;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author NikkiTrueblood
 */
public final class Main {
    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                    new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
     *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }
        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        while (_input.hasNext()) {
            if (_input.hasNextLine()) {
                String i = _input.nextLine();
                while (i.length() == 0) {
                    i = _input.nextLine();
                    _output.print("\n");
                }
                i = i.substring(1).trim() + " ";
                int space;
                String[] myRotors = new String[m.numRotors()];
                String settings = "";
                for (int x = 0; x <= i.length(); x++) {
                    space = i.indexOf(" ");
                    String group = i.substring(0, space);
                    if (m.inAllRotors(group)) {
                        for (String check: myRotors) {
                            if (group.equals(check)) {
                                throw new EnigmaException("Duplicate "
                                        + "rotor name.");
                            }
                        }
                        myRotors[x] = group;
                    } else {
                        settings = group;
                        i = i.substring(space).trim();
                        break;
                    }
                    i = i.substring(space + 1);
                }
                if (myRotors[0] == null) {
                    throw new EnigmaException("No configuration for message.");
                }
                if (myRotors[myRotors.length - 1] == null) {
                    throw new EnigmaException("Not enough rotors given.");
                }
                if (settings.length() != m.numRotors() - 1) {
                    throw new EnigmaException("Wrong number "
                            + "of settings given.");
                }
                m.insertRotors(myRotors);
                if (i.length() > 0) {
                    String plug = i;
                    m.setPlugboard(new Permutation(plug, _alphabet));
                }
                setUp(m, settings);

            }
            while (_input.hasNext("[^\\*]+")) {
                printMessageLine(m.convert(
                        _input.nextLine().replaceAll("\\s", "")));
            }
            m.resetRotors();
        }
    }
    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numRotors = 0;
            int numPawls = 0;
            if (_config.hasNextLine()) {
                _alphabet = new Alphabet(_config.nextLine());
            }
            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw new EnigmaException("Number of rotors/pawls not given.");
            }
            if (_config.hasNextInt()) {
                numPawls = _config.nextInt();
            }
            Collection<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }


            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String type = _config.next();
            String cycle = "";
            while (_config.hasNext("\\(.+\\)")) {
                cycle += _config.next("\\(.+\\)");
            }
            if (_config.hasNext("\\(.+")) {
                throw new EnigmaException("No closing "
                        + "parentheses on permutation.");
            }
            Permutation p = new Permutation(cycle, _alphabet);
            if (type.charAt(0) == 'R') {
                return new Reflector(name, p);
            } else if (type.charAt(0) == 'N') {
                return new FixedRotor(name, p);
            } else {
                return new MovingRotor(name, p, type.substring(1));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        for (int i = 0; i < settings.length(); i++) {
            if (!(_alphabet.contains(settings.charAt(i)))
                    || settings.length() != M.numRotors() - 1) {
                throw new EnigmaException("Setting not in alphabet "
                        + "or incorrect number of settings given.");
            } else {
                M.setRotors(settings);
            }
        }

    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String message = msg;
        while (message.length() > 4) {
            _output.print(message.substring(0, 5) + " ");
            message = message.substring(5);
        }
        _output.print(message);
        _output.print("\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
