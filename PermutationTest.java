package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @NikkiTrueblood
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;
    private Alphabet a = new Alphabet(alpha);

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void checkPermObject() {
        Permutation p = new Permutation("(DCBA) (LZTRJ) "
                + "(IEOXYKPV) (NGHWQSMU) (F)", a);
        assertEquals("DCBAD", p.perm().get(0));
        assertEquals("LZTRJL", p.perm().get(1));
        assertEquals("IEOXYKPVI", p.perm().get(2));
        assertEquals("NGHWQSMUN", p.perm().get(3));
        assertEquals("FF", p.perm().get(4));

    }
    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void testInvertChar() {
        perm = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('B', perm.invert('A'));
        assertEquals('A', perm.invert('C'));
        assertEquals('D', perm.invert('B'));
    }
    @Test
    public void testDerangement() {
        perm = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('C', perm.permute('A'));
        assertEquals('D', perm.permute('C'));
        assertEquals('B', perm.permute('D'));
    }

    @Test
    public void test1() {
        perm = new Permutation("(AGHE) (BFJIDCK) (PL) "
                + "(NZYXTQS) (RMV) (U)", new Alphabet());
        checkPerm("1", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "GFKCAJHEDIBPVZOLSMNQURWTXY");
    }
    @Test (expected = EnigmaException.class)
    public void test3() {
        Alphabet b = new Alphabet();
        perm = new Permutation("(BCD), (AG)", b);
        checkPerm("3", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "GCDBEFAHIJKLMNOPQRSTUVWXYZ");
    }
    @Test (expected = EnigmaException.class)
    public void test4() {
        Alphabet d = new Alphabet();
        perm = new Permutation("(ZYXWVUT) (TLMNO) (ABT)", d);
        checkPerm("4", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "BTCDEFGHIJKLNOTPQRSZTUVWXY");
    }

}
