package domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KnockoutRoundTest {

    @Test
    void getRoundNameReturnsCorrectRound() {

        assertEquals("FINAL", KnockoutRound.getRoundName(2));
        assertEquals("SEMI FINALS", KnockoutRound.getRoundName(4));
        assertEquals("QUARTER FINALS", KnockoutRound.getRoundName(8));
        assertEquals("ROUND OF 16", KnockoutRound.getRoundName(16));
        assertEquals("ROUND OF 32", KnockoutRound.getRoundName(32));
    }
    @Test
    void getRoundNameReturnsGenericNameForUnknownTeamCount() {

        assertEquals("KNOCKOUT ROUND", KnockoutRound.getRoundName(6));
    }
}