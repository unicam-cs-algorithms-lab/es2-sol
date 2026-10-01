package it.unicam.cs.asdl.es2sol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test JUnit per {@link CombinationLock}.
 *
 * Ogni test verifica un comportamento osservabile attraverso l'API pubblica,
 * senza fare assunzioni su come lo stato interno sia rappresentato.
 */
class CombinationLockTest {

    @Test
    void testCostruttoreRifiutaCombinazioneNulla() {
        // Una combinazione nulla non identifica una cassaforte valida.
        assertThrows(NullPointerException.class,
                () -> new CombinationLock(null));
    }

    @Test
    void testCostruttoreRifiutaCombinazioniNonValide() {
        // La combinazione deve contenere esattamente tre lettere tra A e Z.
        assertThrows(IllegalArgumentException.class,
                () -> new CombinationLock(""));
        assertThrows(IllegalArgumentException.class,
                () -> new CombinationLock("AA"));
        assertThrows(IllegalArgumentException.class,
                () -> new CombinationLock("ABCD"));
        assertThrows(IllegalArgumentException.class,
                () -> new CombinationLock("ABa"));
        assertThrows(IllegalArgumentException.class,
                () -> new CombinationLock("A1C"));
        assertThrows(IllegalArgumentException.class,
                () -> new CombinationLock("A C"));
    }

    @Test
    void testCostruttoreCreaCassaforteAperta() {
        // Per contratto una nuova cassaforte nasce nello stato aperto.
        CombinationLock c = new CombinationLock("ABC");
        assertTrue(c.isOpen());
    }

    @Test
    void testSetPositionRifiutaCaratteriNonValidi() {
        CombinationLock c = new CombinationLock("ABC");

        // Sono ammesse soltanto le lettere maiuscole dell'alfabeto inglese.
        assertThrows(IllegalArgumentException.class,
                () -> c.setPosition('a'));
        assertThrows(IllegalArgumentException.class,
                () -> c.setPosition('1'));
        assertThrows(IllegalArgumentException.class,
                () -> c.setPosition('@'));
    }

    @Test
    void testAperturaConCombinazioneCorretta() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        // Le tre posizioni corrette, nello stesso ordine, devono aprire.
        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');
        c.open();

        assertTrue(c.isOpen());
    }

    @Test
    void testAperturaConCombinazioneErrata() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        // Una sola lettera errata e' sufficiente per lasciare la cassaforte chiusa.
        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('D');
        c.open();

        assertFalse(c.isOpen());
    }

    @Test
    void testOpenSenzaTreNuovePosizioniNonApre() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        // Con meno di tre posizioni impostate il tentativo deve fallire.
        c.setPosition('A');
        c.setPosition('B');
        c.open();

        assertFalse(c.isOpen());
    }

    @Test
    void testOpenConsideraSoloLeUltimeTrePosizioni() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        // Posizioni piu' vecchie non contano: le ultime tre sono A, B, C.
        c.setPosition('X');
        c.setPosition('Y');
        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');
        c.open();

        assertTrue(c.isOpen());
    }

    @Test
    void testOpenAzzeraLePosizioniDopoTentativoFallito() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        // Primo tentativo incompleto: deve fallire e azzerare le posizioni A e B.
        c.setPosition('A');
        c.setPosition('B');
        c.open();
        assertFalse(c.isOpen());

        // Se A e B fossero rimaste nello stato interno, aggiungendo C si aprirebbe.
        c.setPosition('C');
        c.open();
        assertFalse(c.isOpen());
    }

    @Test
    void testOpenAzzeraLePosizioniAncheDopoTentativoRiuscito() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');
        c.open();
        assertTrue(c.isOpen());

        // Richiudendo, le posizioni usate per aprire non possono essere riutilizzate.
        c.lock();
        c.open();
        assertFalse(c.isOpen());
    }

    @Test
    void testOpenSuCassaforteGiaApertaLaLasciaAperta() {
        CombinationLock c = new CombinationLock("ABC");

        // Un tentativo di apertura non deve trasformare una cassaforte aperta in chiusa.
        c.setPosition('X');
        c.setPosition('Y');
        c.setPosition('Z');
        c.open();

        assertTrue(c.isOpen());
    }

    @Test
    void testLockChiudeSenzaCambiareCombinazione() {
        CombinationLock c = new CombinationLock("ABC");

        c.lock();
        assertFalse(c.isOpen());

        // Dopo la chiusura la combinazione originaria deve essere ancora valida.
        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');
        c.open();
        assertTrue(c.isOpen());
    }

    @Test
    void testLockAzzeraLePosizioniGiaImpostate() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');

        // Una nuova chiamata a lock deve cancellare le posizioni gia' impostate.
        c.lock();
        c.open();

        assertFalse(c.isOpen());
    }

    @Test
    void testLockFunzionaAncheSeLaCassaforteEgiaChiusa() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();
        c.setPosition('A');
        c.setPosition('B');

        // Anche da chiusa, lock deve azzerare le posizioni A e B.
        c.lock();
        c.setPosition('C');
        c.open();

        assertFalse(c.isOpen());
    }

    @Test
    void testLockAndChangeCombinationRifiutaValoriNonValidi() {
        CombinationLock c = new CombinationLock("ABC");

        // La nuova combinazione ha gli stessi vincoli di quella del costruttore.
        assertThrows(NullPointerException.class,
                () -> c.lockAndChangeCombination(null));
        assertThrows(IllegalArgumentException.class,
                () -> c.lockAndChangeCombination("AA"));
        assertThrows(IllegalArgumentException.class,
                () -> c.lockAndChangeCombination("ABCD"));
        assertThrows(IllegalArgumentException.class,
                () -> c.lockAndChangeCombination("ABa"));
        assertThrows(IllegalArgumentException.class,
                () -> c.lockAndChangeCombination("A1C"));
    }

    @Test
    void testLockAndChangeCombinationDaApertaCambiaCombinazioneEChiude() {
        CombinationLock c = new CombinationLock("ABC");

        // Da aperta la nuova combinazione viene accettata e la cassaforte si chiude.
        c.lockAndChangeCombination("BCA");
        assertFalse(c.isOpen());

        // La vecchia combinazione non deve piu' funzionare.
        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');
        c.open();
        assertFalse(c.isOpen());

        // La nuova combinazione deve invece aprire la cassaforte.
        c.setPosition('B');
        c.setPosition('C');
        c.setPosition('A');
        c.open();
        assertTrue(c.isOpen());
    }

    @Test
    void testLockAndChangeCombinationDaChiusaNonCambiaCombinazione() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();

        // Da chiusa la richiesta di cambio combinazione deve essere ignorata.
        c.lockAndChangeCombination("XYZ");
        assertFalse(c.isOpen());

        // XYZ non deve aprire.
        c.setPosition('X');
        c.setPosition('Y');
        c.setPosition('Z');
        c.open();
        assertFalse(c.isOpen());

        // La combinazione originaria deve essere ancora quella valida.
        c.setPosition('A');
        c.setPosition('B');
        c.setPosition('C');
        c.open();
        assertTrue(c.isOpen());
    }

    @Test
    void testLockAndChangeCombinationDaChiusaAzzeraLePosizioni() {
        CombinationLock c = new CombinationLock("ABC");
        c.lock();
        c.setPosition('A');
        c.setPosition('B');

        // Anche se il cambio combinazione viene ignorato, le posizioni vanno azzerate.
        c.lockAndChangeCombination("XYZ");
        c.setPosition('C');
        c.open();

        assertFalse(c.isOpen());
    }
}
