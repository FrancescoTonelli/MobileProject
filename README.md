# HitWaves

# Argomento

Lo scopo di HitWaves è quello di fornire a case discografiche la possibilità di pubblicare concerti e/o eventi relativi agli artisti che curano, mettendo a disposizione una serie di locali convenzionati. La casa discografica potrà pubblicizzare i suoi artisti e vendere i relativi biglietti.

Gli utenti fruiranno dell’applicativo per acquistare i biglietti e lasciare recensioni ad eventi e luoghi. Sarà data la possibilità di sfruttare un sistema di mappe per individuare i concerti nella zona di interesse.

Case discografiche e utenti avranno anche a disposizione un sistema di notifiche in base a biglietti venduti, artisti preferiti, nuovi eventi in zona…

# Funzionalità minime

## Case Discografiche

- visualizzazione, modifica e inserimento di artisti
    - visualizzare recensioni
- visualizzazione dei palazzetti convenzionati (ricerca e disponibilità) e relative recensioni
- visualizzazione, modifica e inserimento di nuovi eventi o tour (collezioni di eventi)
    - in caso di aggiunta di un evento, arriverà una notifica agli utenti con almeno un artista interessato tra i preferiti, e comparirà nel carosello degli utenti ad una distanza in km (specificata dall’utente) dal luogo in questione.
    - in caso di cancellazione di un evento, agli utenti che avevano acquistato un biglietto arriva il rimborso sul portafoglio in-app
- visualizzazione e cancellazione di notifiche relative a sold out eventi.
- visualizzare pagina account con infografiche e modifica dettagli.
- possibilità di aprire un ticket all’admin per problematiche.

## Utenti

- visualizzare lista di eventi e tour, con possibilità di filtro in base a tipologia e ricerca su nome e artista.
    - visualizzare mappa con icone di eventi.
    - visualizzare dettagli di evento (recensioni palazzetto) e artista (con recensioni)
    - visualizzare eventuale planimetria del palazzetto dell’evento per selezione posto
- acquistare biglietti (segnando la data nel calendario del device) tramite portafoglio ricaricabile e visualizzare carrello.
- visualizzare, aggiungere e rimuovere artisti preferiti.
- lasciare recensioni su evento e/o palazzetto in cui è già stato (biglietto acquistato e datetime evento passato), deciso tramite combo box
- visualizzazione e modifiche pagina utente: foto profilo, bio, dati personali (in scuro), ricarica portafoglio
- visualizzazione elenco dei biglietti acquistati
- visualizzazione e eliminazione notifiche (reminder evento)
- visualizzazione e modifica proprie recensioni
- visualizzazione del carosello per gli eventi in [specificare distanza] km

## Amministratori

- visualizzare il dettaglio di ogni profilo, casa discografica o utente, con possibilità di eliminare qualsiasi cosa.
    - Partendo dalla casa discografica, possono avere accesso ai concerti, tour e artisti
- visualizzazione, aggiunta e rimozione di luoghi convenzionati, con relativi dettagli
    - statistiche delle recensioni
    - planimetria posti
- visualizzazione dei ticket delle case discografiche (pannello simile alle notifiche)
- possibilità di inviare notifiche ad hoc agli utenti (per ragioni puramente di avviso)

# Funzionalità aggiuntive

- effettiva funzionalità dei qr

# Istruzioni per l'uso

## Montare il Database (Solo primo avvio)
1. Tramite XAMPP, avviare Apache e MySQL (quest'ultimo sulla porta 3306, che dovrebbe essere di default)
2. Cliccare sul pulsante "Admin" sulla riga di MySQL: si aprirà phpmyadmin
3. Caricare il database utilizzando il codice SQL contenuto in "generator.sql"


