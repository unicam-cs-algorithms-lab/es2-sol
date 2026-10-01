package it.unicam.cs.asdl.es2sol;

/**
 * Modella una cassaforte dotata di una serratura a combinazione di tre lettere
 * maiuscole dell'alfabeto inglese.
 * <p>
 * Un oggetto di questa classe possiede uno <em>stato interno</em>: in
 * particolare deve ricordare la combinazione segreta, se la cassaforte è
 * aperta oppure chiusa e le posizioni della manopola rilevanti per il prossimo
 * tentativo di apertura. Tali informazioni fanno parte dell'implementazione e
 * sono incapsulate nelle variabili istanza della classe: chi usa la cassaforte
 * interagisce con essa esclusivamente attraverso i metodi pubblici definiti da
 * questa API.
 * </p>
 * <p>
 * Ogni chiamata a un metodo può modificare lo stato dell'oggetto. In
 * particolare, un tentativo di apertura utilizza le ultime tre posizioni
 * impostate dopo l'ultimo azzeramento delle posizioni. Dopo ogni tentativo di
 * apertura, riuscito o meno, le posizioni precedentemente impostate non devono
 * essere riutilizzate in un tentativo successivo. Anche la chiusura della
 * cassaforte azzera le posizioni precedentemente impostate.
 * </p>
 *
 * @author Luca Tesei
 */
public class CombinationLock {

    /*
     * Lo stato interno della cassaforte è completamente incapsulato: dall'esterno
     * non si accede direttamente a questi campi, ma solo attraverso l'API pubblica.
     */
    private String combinazioneAttuale;

    private boolean chiusa;

    private char ultimaPosizioneImpostata;

    private char penultimaPosizioneImpostata;

    private char terzultimaPosizioneImpostata;

    /**
     * Costruisce una cassaforte inizialmente <strong>aperta</strong> con la
     * combinazione indicata. Al termine della costruzione non ci sono posizioni
     * della manopola già impostate da considerare per un futuro tentativo di
     * apertura.
     *
     * @param aCombination la combinazione segreta, costituita esattamente da tre
     *                     lettere comprese tra {@code 'A'} e {@code 'Z'}
     * @throws NullPointerException     se {@code aCombination} è {@code null}
     * @throws IllegalArgumentException se {@code aCombination} non è una stringa
     *                                  di esattamente tre lettere maiuscole
     *                                  dell'alfabeto inglese
     */
    public CombinationLock(String aCombination) {
        checkCombination(aCombination);
        this.combinazioneAttuale = aCombination;
        this.chiusa = false;
        resetPositions();
    }

    /**
     * Imposta la manopola sulla posizione indicata. La nuova posizione diventa
     * l'ultima posizione impostata e deve essere ricordata nello stato interno
     * della cassaforte in vista di un successivo tentativo di apertura.
     * <p>
     * Se vengono impostate più di tre posizioni prima di una chiamata a
     * {@link #open()}, ai fini del tentativo di apertura contano solo le ultime
     * tre.
     * </p>
     *
     * @param aPosition la posizione della manopola, compresa tra {@code 'A'} e
     *                  {@code 'Z'}
     * @throws IllegalArgumentException se {@code aPosition} non è una lettera
     *                                  maiuscola dell'alfabeto inglese
     */
    public void setPosition(char aPosition) {
        if (aPosition < 'A' || aPosition > 'Z')
            throw new IllegalArgumentException(
                    "Posizionamento di carattere non consentito: " + aPosition);

        /*
         * Manteniamo nello stato solo le ultime tre posizioni. Ogni nuova posizione
         * fa quindi "scorrere" le precedenti di una posizione verso il passato.
         */
        this.terzultimaPosizioneImpostata = this.penultimaPosizioneImpostata;
        this.penultimaPosizioneImpostata = this.ultimaPosizioneImpostata;
        this.ultimaPosizioneImpostata = aPosition;
    }

    /**
     * Effettua un tentativo di apertura utilizzando le ultime tre posizioni
     * impostate dopo l'ultimo azzeramento delle posizioni.
     * <p>
     * Se sono state impostate almeno tre posizioni e le ultime tre coincidono,
     * nello stesso ordine, con la combinazione segreta, la cassaforte risulta
     * aperta. In caso contrario una cassaforte chiusa rimane chiusa. Se la
     * cassaforte è già aperta, rimane aperta.
     * </p>
     * <p>
     * In ogni caso, al termine del tentativo tutte le posizioni impostate fino a
     * quel momento vengono dimenticate: un successivo tentativo deve utilizzare
     * soltanto nuove chiamate a {@link #setPosition(char)}.
     * </p>
     */
    public void open() {
        /*
         * Il carattere '\0' usato nel reset non può coincidere con una lettera A-Z.
         * Di conseguenza, se dopo l'ultimo reset sono state impostate meno di tre
         * posizioni, questo confronto non potrà avere successo.
         */
        boolean combinazioneCorretta = this.combinazioneAttuale.charAt(0) == this.terzultimaPosizioneImpostata
                && this.combinazioneAttuale.charAt(1) == this.penultimaPosizioneImpostata
                && this.combinazioneAttuale.charAt(2) == this.ultimaPosizioneImpostata;

        if (combinazioneCorretta)
            this.chiusa = false;

        /*
         * Il tentativo, riuscito o meno, è concluso: le posizioni utilizzate non
         * devono poter contribuire al tentativo successivo.
         */
        resetPositions();
    }

    /**
     * Determina lo stato corrente della cassaforte senza modificarlo.
     *
     * @return {@code true} se la cassaforte è attualmente aperta,
     *         {@code false} se è chiusa
     */
    public boolean isOpen() {
        return !this.chiusa;
    }

    /**
     * Chiude la cassaforte senza modificare la combinazione segreta.
     * <p>
     * La chiamata azzera inoltre tutte le posizioni della manopola impostate in
     * precedenza. Di conseguenza, dopo una chiamata a questo metodo non è
     * possibile riaprire la cassaforte chiamando immediatamente {@link #open()}:
     * occorre prima impostare nuovamente la combinazione mediante
     * {@link #setPosition(char)}.
     * </p>
     * <p>
     * Se la cassaforte è già chiusa, rimane chiusa e le posizioni eventualmente
     * impostate vengono comunque azzerate.
     * </p>
     */
    public void lock() {
        this.chiusa = true;

        // La chiusura inizia una nuova sequenza di posizionamenti della manopola.
        resetPositions();
    }

    /**
     * Chiude la cassaforte e, soltanto se essa è attualmente aperta, sostituisce
     * la combinazione segreta con quella indicata.
     * <p>
     * Se la cassaforte è chiusa, la combinazione segreta non viene modificata.
     * In entrambi i casi, al termine della chiamata la cassaforte è chiusa e le
     * posizioni della manopola precedentemente impostate vengono azzerate.
     * </p>
     * <p>
     * Il parametro viene comunque validato: una combinazione nulla o non valida
     * provoca l'eccezione indicata e non deve produrre una normale modifica dello
     * stato della cassaforte.
     * </p>
     *
     * @param aCombination la nuova combinazione, costituita esattamente da tre
     *                     lettere comprese tra {@code 'A'} e {@code 'Z'}
     * @throws NullPointerException     se {@code aCombination} è {@code null}
     * @throws IllegalArgumentException se {@code aCombination} non è una stringa
     *                                  di esattamente tre lettere maiuscole
     *                                  dell'alfabeto inglese
     */
    public void lockAndChangeCombination(String aCombination) {
        /*
         * La validazione riguarda il contratto del metodo e viene quindi effettuata
         * indipendentemente dallo stato corrente della cassaforte.
         */
        checkCombination(aCombination);

        // Solo una cassaforte aperta consente realmente di cambiare la combinazione.
        if (!this.chiusa)
            this.combinazioneAttuale = aCombination;

        /*
         * In ogni caso il metodo termina con la cassaforte chiusa e senza posizioni
         * precedenti riutilizzabili.
         */
        this.chiusa = true;
        resetPositions();
    }

    /**
     * Verifica che una stringa sia una combinazione valida secondo il contratto
     * della classe.
     */
    private static void checkCombination(String aCombination) {
        if (aCombination == null)
            throw new NullPointerException(
                    "Tentativo di usare una combinazione nulla");

        if (aCombination.length() != 3)
            throw new IllegalArgumentException(
                    "La combinazione deve contenere esattamente 3 lettere");

        /*
         * Non usiamo Character.isLetter/isUpperCase perché questi metodi
         * riconoscono anche lettere Unicode diverse da quelle dell'alfabeto
         * inglese. Ad esempio, 'È' e 'Ω' sono entrambe considerate lettere
         * maiuscole. Qui il contratto richiede invece esclusivamente le lettere
         * maiuscole dell'alfabeto inglese, quindi ogni carattere deve essere
         * compreso tra 'A' e 'Z'.
         */
        for (int i = 0; i < aCombination.length(); i++)
            if (aCombination.charAt(i) < 'A' || aCombination.charAt(i) > 'Z')
                throw new IllegalArgumentException(
                        "Simbolo non valido nella combinazione: "
                                + aCombination.charAt(i));
    }

    /**
     * Dimentica tutte le posizioni della manopola impostate fino a questo momento.
     */
    private void resetPositions() {
        this.ultimaPosizioneImpostata = '\0';
        this.penultimaPosizioneImpostata = '\0';
        this.terzultimaPosizioneImpostata = '\0';
    }
}
