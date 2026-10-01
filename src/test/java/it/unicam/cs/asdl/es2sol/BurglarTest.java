package it.unicam.cs.asdl.es2sol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test JUnit per {@link Burglar}.
 *
 * I test controllano sia il risultato della ricerca sia lo stato mantenuto
 * dallo scassinatore, in particolare il numero di tentativi effettuati.
 */
class BurglarTest {

    @Test
    void testCostruttoreRifiutaCassaforteNulla() {
        // Uno scassinatore deve essere sempre associato a una cassaforte reale.
        assertThrows(NullPointerException.class,
                () -> new Burglar(null));
    }

    @Test
    void testTentativiInizialmenteMenoUno() {
        CombinationLock c = new CombinationLock("ABC");
        Burglar b = new Burglar(c);

        // Prima di una ricerca conclusa getAttempts() deve segnalare -1.
        assertEquals(-1, b.getAttempts());
    }

    @Test
    void testTrovaLaPrimaCombinazione() {
        CombinationLock c = new CombinationLock("AAA");
        Burglar b = new Burglar(c);

        // AAA e' il primo tentativo dell'ordine lessicografico.
        assertEquals("AAA", b.findCombination());
        assertEquals(1, b.getAttempts());
        assertTrue(c.isOpen());
    }

    @Test
    void testTrovaUnaCombinazioneIntermedia() {
        CombinationLock c = new CombinationLock("ABA");
        Burglar b = new Burglar(c);

        // Dopo AAA...AAZ, ABA e' il ventisettesimo tentativo.
        assertEquals("ABA", b.findCombination());
        assertEquals(27, b.getAttempts());
        assertTrue(c.isOpen());
    }

    @Test
    void testTrovaCombinazioneGenerica() {
        CombinationLock c = new CombinationLock("XHS");
        Burglar b = new Burglar(c);

        // Verifica una combinazione lontana dagli estremi della ricerca.
        assertEquals("XHS", b.findCombination());
        assertEquals(15749, b.getAttempts());
        assertTrue(c.isOpen());
    }

    @Test
    void testTrovaUltimaCombinazione() {
        CombinationLock c = new CombinationLock("ZZZ");
        Burglar b = new Burglar(c);

        // Con tre lettere ci sono 26^3 = 17576 combinazioni possibili.
        assertEquals("ZZZ", b.findCombination());
        assertEquals(17576, b.getAttempts());
        assertTrue(c.isOpen());
    }

    @Test
    void testRicercaParteAncheDaCassaforteAperta() {
        CombinationLock c = new CombinationLock("AAB");
        assertTrue(c.isOpen());
        Burglar b = new Burglar(c);

        // findCombination() deve gestire lo stato iniziale della cassaforte
        // tramite la sua API e non assumere che sia gia' chiusa.
        assertEquals("AAB", b.findCombination());
        assertEquals(2, b.getAttempts());
        assertTrue(c.isOpen());
    }
}
