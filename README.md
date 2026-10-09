# Esercitazione 2 - Cassaforte a combinazione e scassinatore

Questa esercitazione del corso di **Algoritmi e Strutture Dati - Laboratorio** e' organizzata come un progetto Maven, con una struttura analoga a quella utilizzata negli esami di codifica del laboratorio.

L'obiettivo non e' soltanto ottenere il risultato corretto di una singola chiamata di metodo, ma imparare a progettare e gestire correttamente **lo stato interno degli oggetti**. Un oggetto deve reagire in modo coerente a qualunque sequenza di chiamate effettuata attraverso la sua **API pubblica**.

## Obiettivi didattici

L'esercitazione permette di esercitarsi in particolare su:

- definizione e uso delle **variabili istanza** per rappresentare lo stato di un oggetto;
- **incapsulamento** dello stato: i dettagli interni non devono essere accessibili direttamente dall'esterno;
- progettazione del comportamento di un oggetto in termini di **transizioni di stato** causate dalle chiamate ai metodi;
- rispetto del contratto specificato dalla **Javadoc** delle API pubbliche;
- gestione e validazione dei parametri mediante eccezioni;
- uso di **JUnit 5** per verificare il comportamento delle classi, compresi i casi limite e le sequenze di operazioni.

La Javadoc delle classi fa parte a tutti gli effetti della specifica dell'esercizio: prima di implementare un metodo e' quindi importante leggerne con attenzione il contratto.

## Classi da completare

Devono essere completate le classi:

- `CombinationLock.java`
- `Burglar.java`

Nei due file sono presenti commenti `TODO` che indicano le parti da implementare. I `TODO` non specificano necessariamente quali variabili istanza usare: scegliere lo stato interno necessario fa parte dell'esercizio.

### `CombinationLock`

La classe modella una cassaforte con una combinazione di tre lettere maiuscole.

La cassaforte deve ricordare il proprio stato e reagire correttamente alle operazioni previste dall'API, tra cui:

- impostare una posizione della manopola;
- tentare l'apertura usando le ultime tre posizioni impostate;
- sapere se la cassaforte e' aperta o chiusa;
- chiudere la cassaforte senza cambiare combinazione;
- chiudere la cassaforte cambiando la combinazione, quando l'operazione e' consentita.

Un aspetto importante e' stabilire quali informazioni devono essere conservate tra una chiamata e la successiva e quando, invece, devono essere azzerate. Non e' sufficiente che i singoli metodi sembrino funzionare isolatamente: deve essere corretta anche l'evoluzione complessiva dello stato dell'oggetto.

### `Burglar`

La classe modella uno scassinatore associato a una `CombinationLock`.

Lo scassinatore deve trovare la combinazione usando la **forza bruta**, cioe' provando le combinazioni nell'ordine indicato dalla Javadoc fino all'apertura della cassaforte.

Lo scassinatore deve usare esclusivamente l'API pubblica di `CombinationLock`: non deve conoscere ne' accedere direttamente alla rappresentazione interna della cassaforte.

Deve inoltre mantenere il numero di tentativi necessari per trovare la combinazione.

## Test

Nel progetto sono presenti test JUnit 5 che descrivono diversi scenari d'uso delle classi.

Per eseguire tutti i test dalla cartella principale del progetto Maven:

```bash
mvn test
```

E' consigliabile usare i test come strumento di sviluppo:

1. leggere la Javadoc del metodo da implementare;
2. osservare i test relativi a quel comportamento;
3. implementare il metodo senza modificare la sua firma pubblica;
4. eseguire nuovamente i test;
5. quando un test fallisce, ricostruire lo stato dell'oggetto dopo ogni chiamata e verificare dove il comportamento si discosta dal contratto.

Il superamento dei test forniti e' necessario, ma non garantisce da solo la correttezza completa: l'implementazione deve rispettare tutta la specifica espressa dalla Javadoc.

## File di supporto

Il progetto puo' contenere anche classi di supporto, ad esempio una GUI o un programma di test manuale. Questi file servono per sperimentare con le classi, ma **non sono oggetto della consegna**.

Non modificare i package o le firme dei metodi pubblici richiesti, perche' i test Maven si aspettano la struttura fornita.

## Consegna

La consegna va effettuata nel **compito corrispondente su Moodle del corso**.

Per questa esercitazione devono essere consegnati **esattamente due file**:

1. `CombinationLock.java`, con i relativi `TODO` implementati;
2. `Burglar.java`, con i relativi `TODO` implementati.

Prima della consegna verificare che il progetto compili e che i test vengano eseguiti correttamente con Maven.
