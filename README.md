# shacl
## Utilizzo
Per poter utilizzare ShaclGenerator è necessaria la presenza del file prefixes/prefixes.txt nella directory del progetto.
I percorsi dei file di summary (.txt), di shapes (.ttl), del dataset da validare (.ttl) e del validation report (.ttl) vengono invece specificati ai metodi dall'utente.
### Generazione shape
La generazione di shape viene effettuata tramite i metodi statici della classe ShaclGenerator.
È possibile sia generare un file shape per ogni pattern presente nel summary che generare un unico file contenente tutte le shapes in modo compatto.
In entrambi i casi è necessario specificare il percorso in cui si trovano i file del summary e il percorso in cui salvare le shapes prodotte.
### Validazione dati
La validazione dei dati secondo le shapes viene effettuata tramite i metodi statici della classe ShaclValidator.
La validazione avviene prendendo come input un dataset e uno schema (shapes) e producendo in output il file del validation report al percorso specificato.
Il processo di validazione viene eseguito utilizzando il validatore shaclex.jar .
### Test
Per effettuare test di generazione e validazione è possibile eseguire la classe Main.
