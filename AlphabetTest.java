package enigma;

import org.junit.Test;
import static org.junit.Assert.*;
/** The suite of all JUnit tests for the Alphabet class.
 *  @author NikkiTrueblood
 */
public class AlphabetTest {
    @Test
    public void test1() {
        Alphabet test = new Alphabet("ABCD");
        assertEquals(4, test.size());
        char fail = 'E';
        char success = 'B';
        assertFalse(test.contains(fail));
        assertTrue(test.contains(success));
        assertEquals(1, test.toInt(success));
        assertEquals(success, test.toChar(1));
    }

}
