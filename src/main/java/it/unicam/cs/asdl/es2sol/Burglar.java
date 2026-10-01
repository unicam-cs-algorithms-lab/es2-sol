package it.unicam.cs.asdl.es2sol;

/**
 * Modella uno scassinatore che cerca la combinazione di una
 * {@link CombinationLock} mediante forza bruta.
 * <p>
 * Lo scassinatore lavora sulla stessa cassaforte ricevuta al momento della
 * costruzione e interagisce con essa esclusivamente attraverso la sua API
 * pubblica. La combinazione non è quindi disponibile direttamente: deve essere
 * scoperta producendo tentativi di apertura.
 * </p>
 * <p>
 * La ricerca considera, in ordine lessicografico, tutte le combinazioni da
 * {@code AAA} a {@code ZZZ}. Un tentativo corrisponde alla prova completa di una
 * singola combinazione. L'oggetto mantiene nel proprio stato anche il numero di
 * tentativi effettuati nell'ultima ricerca conclusa con successo.
 * </p>
 *
 * @author Luca Tesei
 */
public class Burglar {

    /*
     * Lo scassinatore conserva un riferimento alla stessa cassaforte ricevuta dal
     * costruttore: non ne crea una copia e non accede al suo stato interno.
     */
    private final CombinationLock cassaforte;

    private int tentativi;

    /**
     * Costruisce uno scassinatore associato alla cassaforte indicata. La
     * costruzione non effettua ancora alcun tentativo di apertura.
     *
     * @param aCombinationLock la cassaforte da scassinare
     * @throws NullPointerException se {@code aCombinationLock} è {@code null}
     */
    public Burglar(CombinationLock aCombinationLock) {
        if (aCombinationLock == null)
            throw new NullPointerException(
                    "Tentativo di costruire uno scassinatore per una cassaforte nulla");

        this.cassaforte = aCombinationLock;

        /*
         * -1 distingue lo stato "nessuna ricerca ancora eseguita" da una ricerca
         * iniziata, per la quale il conteggio parte invece da 0.
         */
        this.tentativi = -1;
    }

    /**
     * Cerca la combinazione della cassaforte mediante forza bruta, provando in
     * ordine lessicografico tutte le combinazioni da {@code AAA} a {@code ZZZ}.
     * <p>
     * Prima di iniziare la ricerca la cassaforte viene posta nello stato chiuso
     * tramite la sua API pubblica. Per ogni combinazione candidata lo
     * scassinatore imposta le tre posizioni e tenta l'apertura. La ricerca
     * termina non appena la cassaforte risulta aperta.
     * </p>
     * <p>
     * Al termine della ricerca la cassaforte è aperta e
     * {@link #getAttempts()} restituisce il numero di combinazioni provate nella
     * ricerca appena conclusa.
     * </p>
     *
     * @return la combinazione segreta trovata; non può essere {@code null}
     */
    public String findCombination() {
        /*
         * La ricerca deve partire sempre da una situazione nota. Anche se la
         * cassaforte fosse già aperta, la chiudiamo usando esclusivamente la sua API.
         */
        this.cassaforte.lock();
        this.tentativi = 0;

        /*
         * I tre cicli annidati enumerano in ordine lessicografico tutte le 26^3
         * combinazioni possibili: AAA, AAB, ..., AAZ, ABA, ..., ZZZ.
         */
        for (char c1 = 'A'; c1 <= 'Z'; c1++)
            for (char c2 = 'A'; c2 <= 'Z'; c2++)
                for (char c3 = 'A'; c3 <= 'Z'; c3++) {
                    this.tentativi++;

                    // Ogni candidato viene provato esclusivamente tramite l'API.
                    this.cassaforte.setPosition(c1);
                    this.cassaforte.setPosition(c2);
                    this.cassaforte.setPosition(c3);
                    this.cassaforte.open();

                    if (this.cassaforte.isOpen()) {
                        /*
                         * La combinazione trovata coincide con i tre caratteri del
                         * candidato che ha appena aperto la cassaforte.
                         */
                        StringBuffer s = new StringBuffer();
                        s.append(c1);
                        s.append(c2);
                        s.append(c3);
                        return s.toString();
                    }
                }

        /*
         * Per contratto una CombinationLock ha sempre una combinazione valida di tre
         * lettere A-Z, quindi questo punto non dovrebbe essere raggiungibile.
         */
        throw new IllegalStateException(
                "Nessuna combinazione valida trovata per la cassaforte");
    }

    /**
     * Restituisce il numero di tentativi effettuati dall'ultima chiamata a
     * {@link #findCombination()} conclusa con successo.
     *
     * @return il numero di combinazioni provate, oppure {@code -1} se questo
     *         scassinatore non ha ancora completato una ricerca
     */
    public long getAttempts() {
        return this.tentativi;
    }
}
