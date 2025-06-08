# HitWaves

## Autori
- Montalti Elena - 0001089638 - elena.montalti5@studio.unibo.it
- Tonelli Francesco - 0001071531 - francesco.tonelli9@studio.unibo.it

## Argomento

Lo scopo di HitWaves è quello di offrire una piattaforma per la promozione e la gestione di tour e concerti musicali legati a case discografiche.

Gli utenti possono utilizzare l’app per acquistare biglietti, lasciare recensioni agli eventi a cui hanno preso parte, e individuare i concerti di proprio interesse.

Un sistema di notifiche personalizzate consente di tenere aggiornati gli utenti in base ai nuovi eventi legati agli artisti di loro interesse.

Al momento, il progetto si compone delle seguenti parti:
- cartella `back`: Server Python Flask per la gestione backend
- cartella `front`: Dashboard React Node.js per la gestione delle funzioni admin
- cartella `Hitwaves`: Applicativo Kotlin per l'interazione utente
- cartella `seat_chart_editor`: Script Python interattivo per la creazione delle planimetrie dei palazzetti

## Funzionalità

### Utenti

- visualizzare la lista di eventi e tour, con possibilità di ricerca su titolo, artista e luogo, con la possibilità di visualizzare quelli vicini a sé e quelli dei migliori artisti.
- visualizzare una mappa con le icone degli eventi.
- visualizzare i dettagli degli eventi e degli artisti
- acquistare biglietti (appuntando l'evento nel calendario del dispositivo) tramite selezione rapida o scegliendo i posti sulla planimetria interagibile del palazzetto.
- visualizzare, aggiungere e rimuovere artisti preferiti, per i quali vengono ricevute delle notifiche se questi appaiono in nuovi eventi.
- lasciare recensioni su un evento a cui è stato
- visualizzare e modificare la propria pagina utente: foto profilo, dati personali, elenco delle recensioni lasciate e ammontare del portafoglio dei rimborsi
- visualizzare l'elenco dei biglietti acquistati
- visualizzare e eliminare le proprie notifiche (che verranno ricevute anche tramite push)

### Amministratori

- visualizzare l'elenco degli utenti e i loro dettagli, con possibilità di cancellare gli account e inviare notifiche personalizzate
- visualizzare l'elenco dei concerti e i loro dettagli, con la possibilità di cancellarli
- visualizzare l'elenco dei tour, dei concerti che li compongono e i loro dettagli, con la possibilità di cancellarli
- visualizzare l'elenco delle case discografiche e i loro dettagli, con possibilità di creare e cancellare gli account, e inviare notifiche personalizzate
- visualizzare l'elenco dei palazzetti, con possibilità di crearne di nuovi, cancellarli e modificarne la planimetria (i file per questa funzionalità devono essere creati con l'applicazione specifica `seat_chart_editor`)
- visualizzare l'elenco degli artisti, con possibilità di cancellarli

> la cancellazione di un concerto o di un tour aggiorna il portafoglio dei rimborsi di tutti gli utenti che hanno acquistato un biglietto, restituendo loro l'importo dello stesso

#### Funzioni in fase sperimentale

Per "fase sperimentale" si intende che le funzioni qui elencate dovrebbero trovarsi nel portale applicativo destinato alle case discografiche. Queste sono, però, già stabili e funzionanti. Vedi "Sviluppi futuri" per ulteriori chiarimenti.

- validare i biglietti degli utenti (per esempio, all'ingresso di un concerto) tramite un lettore di codici QR, utilizzando quelli generati automaticamente dall'applicazione per gli utenti
- creazione di nuovi concerti
- creazione di nuovi tour

## Sviluppi futuri

Le "Funzioni in fase sperimentale" descritte sopra dovrebbero trovarsi in un applicativo Kotlin dedicato alle case discografiche. Design e API sono già pronti e funzionanti.

## Istruzioni per l'uso

### Montare il Database (Solo primo avvio)
1. Tramite XAMPP, avviare Apache e MySQL (quest'ultimo sulla porta 3306, che dovrebbe essere di default)
2. Cliccare sul pulsante "Admin" sulla riga di MySQL: si aprirà phpmyadmin
3. Creare un database vuoto chiamandolo "hitwaves", e popolarlo utilizzando il codice SQL contenuto in "generator.sql"

### Avviare il backend
1. Entrare nella cartella "back"
2. Lanciare `start.bat`
3. Memorizzare per il frontend Kotlin l'indirizzo IP su cui il server è in esecuzione

### Avviare il pannello Admin
1. Entrare nella cartella "admin"
2. Se non presenti, installare Node.js (versione 22.x) e vite
3. Lanciare il comando `npm install` (solo al primo avvio)
4. Lanciare il comando `npm run dev`
5. Il terminale indicherà a che porta del localhost è esposto il pannello per l'admin
6. Una volta nel pannello, entrare con le seguenti credenziali: 
    - Email: `admin@example.com`
    - Password: `admin123`

### Avviare l'editor per la creazione delle piantine
1. Entrare nella cartella "seat_chart_editor"
2. Doppio click su "start.bat"
 
## Avviare l'applicazione Kotlin
1. Avviare Android Studio
2. Prima di lanciare l'applicazione su dispositivo/emulatore, nel file `api\ApiGenericCall.kt`, aggiornare l'indirizzo IP nella costante `serverIp` con l'indirizzo ottenuto all'avvio del back, al punto 3