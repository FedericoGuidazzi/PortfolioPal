# PortfolioPal

PortfolioPal è un'applicazione progettata per aiutarti a gestire e monitorare il tuo portafoglio di investimenti. Con PortfolioPal, puoi tenere traccia delle tue azioni, obbligazioni, fondi comuni e altri investimenti in un unico posto.

## Overview dei Microservizi
- API Gateway, è un componente software che funge da punto di ingresso centrale per gestire e orchestrare tutte le richieste verso una serie di microservizi. In pratica, riceve richieste dal client, le instrada ai servizi appropriati, e ritorna la risposta al client.
I suoi principali compiti sono:
Centralizzazione delle richieste, gestisce tutte le chiamate API provenienti dall'esterno, riducendo la complessità dei client nel comunicare con più servizi.
Sicurezza, applica meccanismi di autenticazione e autorizzazione per proteggere l'accesso ai servizi.
Load balancing, distribuisce le richieste in modo bilanciato tra i vari servizi per migliorare le prestazioni.

- User è un microservizio che si occupa in maniera indipendente di tutto ciò che riguarda lo User, come il nome, cognome e la volontà di rendere il portfolio condivisibile o meno, andando a salvare e gestire i dati salvati all'interno di un database proprio hostato su firebase.
Le responsabilità del servizio sono:
Gestione dell'inserimento e aggiornamento dei dati riguardanti l'Utente.
Fornire informazioni che riguardano l'utente al FE.
Comunicazione attraverso RabbitMQ della modifica delle impostazioni di privacy dell'utente.

- Asset è un microservizio che si occupa in maniera indipendente di tutto ciò che riguarda i dati degli asset e delle currency, questi vengono resi disponibili da delle chiamate verso le API di Yahoo Finance.
Le responsabilità del servizio sono:
Gestione del recupero delle informazioni riguardanti gli Asset.
Logiche di mapping e customizzazione dell'oggetto recuperato rispetto a quello passato come risposta.
Comunicazione attraverso chiamate sincrone dei dati agli altri microservizi.

- Storico è un microservizio che si occupa in maniera indipendente di tutto ciò che riguarda lo storico del portafoglio nel tempo.
Le responsabilità del servizio sono:
Gestione e aggiornamento dello storico.
Comunicazione attraverso chiamate sincrone e asincrone dei dati agli altri microservizi.

- Transazioni è un microservizio che si occupa in maniera indipendente di tutto ciò che riguarda i le transazioni che un portfolio ha effettuato durante il suo periodo di attività.
Le responsabilità del servizio sono:
Gestione dell'inserimento e modifica delle transazioni.
Comunicazione attraverso chiamate sincrone e asincrone dei dati agli altri microservizi.

- Service Discovery è un microservizio che si occupa di fornire un indirizzo ai microservizi e renderli accessibili.
Le responsabilità del servizio sono:
Gestione degli indirizzi dei microservizi.

## Installazione

### Kubernetes
Per poter far partire Kubernetes in locale, è necessario avere installati [Minikube](https://minikube.sigs.k8s.io/docs/start/?arch=%2Flinux%2Fx86-64%2Fstable%2Fbinary+download) e [Kompose](https://kompose.io/installation/).
Kubernetes scaricherà automaticamente le immagini dei microservizi da docker hub.

Spostarsi nella cartella ```kubernetes/kube-services``` Successivamente eseguire il comando per convertire il file docker
```bash
kompose convert -f ../docker-compose.yml
```

Fai partire un kubernetes cluster
```bash
minikube start
```
Crea deployments, services, volumes e networks
```bash
kubectl apply -f kube-services/
```
Assicurati che tutti i pods stiano funzionando senza errori del tipo CrashLoopBackOff
```bash
kubectl get pods
```
Se non tutti i pods stanno funzionando correttamente lancia questo comando e riparti dal punto 1.
``` bash
kubectl delete -f
```
Espone le porte dei microservizi richiesti:
```bash
kubectl port-forward svc/api-gateway 8080:8080
kubectl port-forward svc/webapp 4201:4201
```

### Docker
Il deploy degli ambienti docker può essere fatto separatamente o utilizzando il docker-compose.

Docker-compose
Il file docker-compose generale è presente nella root del progetto:

docker-compose.yml
Esegui il comando, per lanciare lo script che crea gli ambienti docker
```bash
docker-compose up
```
## Contribuire

Se desideri contribuire al progetto, per favore segui questi passaggi:

1. Fai un fork del repository.
2. Crea un nuovo branch:
    ```bash
    git checkout -b feature/nome-feature
    ```
    
3. Fai le tue modifiche e committale:
    ``` bash
    git commit -m "Aggiunta una nuova feature"
    ```
    
4. Pusha il branch:
    ``` bash
    git push origin feature/nome-feature
    ```
    
5. Apri una Pull Request.

