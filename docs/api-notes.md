# Atelier 5 — API REST, Postman, SpringDoc et Swagger

## A. Endpoints REST

L'Atelier 5 consiste à exposer la couche Service de l'application AutoLoc sous forme d'une API REST.

Le préfixe commun `/api` est défini une seule fois dans `application.properties` :

```properties
server.servlet.context-path=/api
```

Les contrôleurs utilisent donc uniquement le nom de la ressource dans `@RequestMapping`, par exemple :

```java
@RequestMapping("/contrats")
```

L'URL complète devient alors :

```text
/api/contrats
```

Les contrôleurs dépendent uniquement des interfaces de la couche Service et n'accèdent jamais directement aux repositories.

### 1. AgenceController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/agences` | `agenceService.findAll()` | 200 | — |
| GET `/api/agences/{id}` | `agenceService.findById(id)` | 200 | 404 |
| POST `/api/agences` | `agenceService.create(agence)` | 201 | 400 |
| PUT `/api/agences/{id}` | `agenceService.update(id, agence)` | 200 | 400, 404 |
| DELETE `/api/agences/{id}` | `agenceService.deleteById(id)` | 204 | 404 |

Les tests Postman ont permis de vérifier le CRUD complet ainsi que les cas d'erreur.

---

### 2. EmployeController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/employes` | `employeService.findAll()` | 200 | — |
| GET `/api/employes/{id}` | `employeService.findById(id)` | 200 | 404 |
| POST `/api/employes` | `employeService.create(employe)` | 201 | 400 |
| PUT `/api/employes/{id}` | `employeService.update(id, employe)` | 200 | 400, 404 |
| DELETE `/api/employes/{id}` | `employeService.deleteById(id)` | 204 | 404 |

L'agence d'un employé peut être indiquée dans le corps JSON sous forme imbriquée :

```json
{
  "agence": {
    "idAgence": 1
  }
}
```

---

### 3. VehiculeController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/vehicules` | `vehiculeService.findAll()` | 200 | — |
| GET `/api/vehicules/{id}` | `vehiculeService.findById(id)` | 200 | 404 |
| POST `/api/vehicules` | `vehiculeService.create(vehicule)` | 201 | 400 |
| PUT `/api/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | 200 | 400, 404 |
| DELETE `/api/vehicules/{id}` | `vehiculeService.deleteById(id)` | 204 | 404 |

Un test avec un tarif journalier négatif a été effectué :

```json
{
  "immatriculation": "247-TUN-2026",
  "marque": "Renault",
  "modele": "Clio",
  "categorie": "CITADINE",
  "tarifJournalier": -50.00,
  "statut": "DISPONIBLE",
  "agence": {
    "idAgence": 1
  }
}
```

Le service rejette cette valeur et l'API retourne :

```text
400 Bad Request
```

avec le message :

```text
Le tarif journalier ne peut pas être négatif
```

---

### 4. EquipementController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/equipements` | `equipementService.findAll()` | 200 | — |
| GET `/api/equipements/{id}` | `equipementService.findById(id)` | 200 | 404 |
| POST `/api/equipements` | `equipementService.create(equipement)` | 201 | 400 |
| PUT `/api/equipements/{id}` | `equipementService.update(id, equipement)` | 200 | 400, 404 |
| DELETE `/api/equipements/{id}` | `equipementService.deleteById(id)` | 204 | 404 |

Le modèle actuel possède l'attribut `libelle`.

Le service vérifie que celui-ci n'est ni `null` ni vide. Dans le cas contraire :

```text
400 Bad Request
```

avec le message :

```text
Le libellé de l'équipement est obligatoire
```

---

### 5. ClientController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/clients` | `clientService.findAll()` | 200 | — |
| GET `/api/clients/{id}` | `clientService.findById(id)` | 200 | 404 |
| POST `/api/clients` | `clientService.create(client)` | 201 | 400 |
| PUT `/api/clients/{id}` | `clientService.update(id, client)` | 200 | 400, 404 |
| DELETE `/api/clients/{id}` | `clientService.deleteById(id)` | 204 | 404 |

Dans l'implémentation actuelle, le service vérifie notamment que `nom` et `email` ne sont pas vides.

Un client valide peut par exemple être créé avec :

```json
{
  "nom": "Ben Ali",
  "prenom": "Sami",
  "email": "sami.benali@example.com",
  "telephone": "22123456",
  "numPermis": "PERMIS-2026-001",
  "dateInscription": "2026-10-24"
}
```

---

### 6. ReservationController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/reservations` | `reservationService.findAll()` | 200 | — |
| GET `/api/reservations/{id}` | `reservationService.findById(id)` | 200 | 404 |
| POST `/api/reservations` | `reservationService.create(reservation)` | 201 | 400 |
| PUT `/api/reservations/{id}` | `reservationService.update(id, reservation)` | 200 | 400, 404 |
| DELETE `/api/reservations/{id}` | `reservationService.deleteById(id)` | 204 | 404 |

Une réservation référence un client et un véhicule :

```json
{
  "vehicule": {
    "idVehicule": 4
  },
  "client": {
    "idClient": 1
  },
  "dateDebut": "2026-11-01",
  "dateFin": "2026-11-05",
  "statut": "EN_ATTENTE"
}
```

Une règle métier vérifie les dates.

Si `dateFin` est antérieure à `dateDebut`, une `IllegalArgumentException` est déclenchée et transformée en :

```text
400 Bad Request
```

avec le message :

```text
La date de fin ne peut pas être antérieure à la date de début
```

---

### 7. ContratController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/contrats` | `contratService.findAll()` | 200 | — |
| GET `/api/contrats/{id}` | `contratService.findById(id)` | 200 | 404 |
| POST `/api/contrats` | `contratService.create(contrat)` | 201 | 400 |
| PUT `/api/contrats/{id}` | `contratService.update(id, contrat)` | 200 | 400, 404 |
| DELETE `/api/contrats/{id}` | `contratService.deleteById(id)` | 204 | 404 |

La relation Contrat → Paiement utilise une cascade. Cela permet notamment de créer un contrat accompagné de ses paiements.

La suppression du contrat peut également entraîner la suppression des paiements associés selon la configuration de la relation (`cascade` et `orphanRemoval`).

---

### 8. PaiementController

Paiement est volontairement exposé en lecture seule.

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/paiements` | `paiementService.findAll()` | 200 | — |
| GET `/api/paiements/{id}` | `paiementService.findById(id)` | 200 | 404 |

Aucun `POST`, `PUT` ou `DELETE` n'est exposé par `PaiementController`.

Un test Postman a été réalisé avec :

```text
POST /api/paiements
```

Le résultat obtenu est :

```text
405 Method Not Allowed
```

Ce résultat est attendu : la route `/paiements` existe, mais le contrôleur n'autorise pas la méthode HTTP POST.

---

### 9. MaintenanceController

| Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---:|---:|
| GET `/api/maintenances` | `maintenanceService.findAll()` | 200 | — |
| GET `/api/maintenances/{id}` | `maintenanceService.findById(id)` | 200 | 404 |
| POST `/api/maintenances` | `maintenanceService.create(maintenance)` | 201 | 400 |
| PUT `/api/maintenances/{id}` | `maintenanceService.update(id, maintenance)` | 200 | 400, 404 |
| DELETE `/api/maintenances/{id}` | `maintenanceService.deleteById(id)` | 204 | 404 |

Une maintenance référence un véhicule :

```json
{
  "vehicule": {
    "idVehicule": 4
  },
  "dateDebut": "2026-11-10",
  "dateFin": "2026-11-15",
  "description": "Révision générale du véhicule"
}
```

Dans le modèle actuel, `Maintenance` ne possède pas d'attribut de coût. La règle métier testée porte donc sur les dates.

Si la date de fin est antérieure à la date de début, l'API retourne :

```text
400 Bad Request
```

---

## B. Annotations Jackson et associations bidirectionnelles

### 1. Problème des associations bidirectionnelles

Les entités JPA de l'application possèdent plusieurs associations bidirectionnelles.

Par exemple :

```text
Vehicule → Reservation
Reservation → Vehicule
```

Sans traitement particulier, Jackson peut tenter de sérialiser :

```text
Reservation
→ Vehicule
→ reservations
→ Reservation
→ Vehicule
→ reservations
→ ...
```

Cela peut produire une récursion infinie et une erreur de sérialisation.

Ce problème a réellement été rencontré pendant les tests Postman de `Reservation`.

Le statut HTTP était bien `200`, mais Postman ne parvenait pas à analyser le JSON et retournait une erreur `JSONError`.

### 2. Utilisation de @JsonIgnore

Pour les relations simples où l'objet enfant doit pouvoir contenir son parent, mais où la collection inverse n'est pas nécessaire dans la réponse JSON, `@JsonIgnore` a été utilisé.

Exemple dans `Vehicule` :

```java
@JsonIgnore
@OneToMany(mappedBy = "vehicule")
private Set<Maintenance> maintenances = new HashSet<>();
```

et :

```java
@JsonIgnore
@OneToMany(mappedBy = "vehicule")
private Set<Reservation> reservations = new HashSet<>();
```

Ainsi, une `Reservation` peut toujours contenir son `Vehicule`, mais Jackson ne repart pas ensuite de `Vehicule` vers toutes ses réservations.

Pour `Equipement`, la collection inverse des véhicules est également ignorée :

```java
@JsonIgnore
@ManyToMany(mappedBy = "equipements")
private Set<Vehicule> vehicules = new HashSet<>();
```

Cette stratégie permet de casser les boucles JSON tout en conservant les informations nécessaires aux requêtes.

### 3. Contrat ↔ Paiement

La relation Contrat/Paiement représente le cas de composition utilisé dans l'atelier.

Le contrat porte la collection de paiements et la cascade :

```java
@OneToMany(
    mappedBy = "contrat",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
@JsonManagedReference("contrat-paiements")
private List<Paiement> paiements;
```

Le paiement possède la référence vers son contrat :

```java
@ManyToOne(fetch = FetchType.LAZY)
@JsonBackReference("contrat-paiements")
private Contrat contrat;
```

Les deux annotations utilisent le même nom :

```text
contrat-paiements
```

`@JsonManagedReference` représente le côté sérialisé normalement.

`@JsonBackReference` représente le retour vers le parent et empêche Jackson de repartir vers celui-ci pendant la sérialisation.

Cela évite donc :

```text
Contrat
→ Paiement
→ Contrat
→ Paiement
→ ...
```

### 4. Différence entre @JsonBackReference et @JsonIgnore

`@JsonIgnore` indique simplement à Jackson de ne pas inclure une propriété dans le JSON.

Exemple :

```java
@JsonIgnore
private Set<Reservation> reservations;
```

Cette propriété est complètement ignorée par Jackson.

`@JsonBackReference`, en revanche, fait partie d'une paire :

```text
@JsonManagedReference
        ↕
@JsonBackReference
```

Elle est particulièrement adaptée aux relations parent/enfant bidirectionnelles.

Dans AutoLoc :

```text
Contrat
   |
   | @JsonManagedReference
   ↓
Paiements
   |
   | @JsonBackReference
   ↓
Contrat
```

Il ne faut pas placer `@JsonBackReference` arbitrairement sur les propriétés utilisées pour recevoir des données lors d'un POST.

Par exemple, l'utiliser sur `Reservation.client` pourrait empêcher la lecture correcte du client lors de la désérialisation d'une réservation.

---

## C. Postman, SpringDoc et Swagger

### 1. Postman

Postman est utilisé comme client HTTP pour tester réellement l'API.

Une collection nommée `AutoLoc` a été créée avec des dossiers correspondant aux différentes ressources :

```text
AutoLoc
├── Contrats
├── Paiements
├── Agences
├── Employes
├── Vehicules
├── Equipements
├── Clients
├── Reservations
└── Maintenances
```

Un environnement `local` contient la variable :

```text
baseUrl = http://localhost:8081
```

Les requêtes utilisent ensuite :

```text
{{baseUrl}}/api/...
```

Des variables comme `contratId`, `clientId`, `vehiculeId`, `reservationId` ou `maintenanceId` permettent de mémoriser les identifiants générés.

Exemple :

```javascript
const response = pm.response.json();
pm.environment.set("maintenanceId", response.idMaintenance);
```

Les scripts Postman vérifient également les codes HTTP.

Exemple :

```javascript
pm.test("Statut 201", () => {
    pm.response.to.have.status(201);
});
```

### 2. Tests nominaux et cas d'erreur

Les tests ont couvert les réponses principales :

```text
200 OK
201 Created
204 No Content
400 Bad Request
404 Not Found
405 Method Not Allowed
```

Exemples :

- GET d'une ressource existante → `200`
- POST réussi → `201`
- DELETE réussi → `204`
- tarif négatif → `400`
- dates invalides → `400`
- identifiant `9999` inexistant → `404`
- POST sur `/api/paiements` → `405`

### 3. Swagger UI

SpringDoc analyse les contrôleurs Spring MVC et génère automatiquement une description OpenAPI de l'API.

Swagger UI permet ensuite d'explorer visuellement cette documentation et d'exécuter les endpoints avec `Try it out`.

Dans le projet AutoLoc, Swagger UI est accessible sous le context-path `/api`.

Les contrôleurs apparaissent avec leurs différentes opérations GET, POST, PUT et DELETE.

Les annotations utilisées sont notamment :

```java
@Tag
@Operation
@ApiResponse
@Parameter
```

`@Tag` documente un groupe d'endpoints.

`@Operation` fournit le résumé et la description d'une opération.

`@ApiResponse` documente les codes HTTP possibles.

`@Parameter` documente les paramètres, par exemple l'identifiant transmis dans le chemin.

### 4. Différence entre Postman et Swagger

Postman et Swagger sont complémentaires.

**Postman** sert principalement à construire, enregistrer, rejouer et vérifier les requêtes HTTP. Il permet notamment d'utiliser des variables et des scripts de test.

**Swagger UI** sert principalement à documenter et explorer le contrat de l'API à partir du code.

Postman répond donc surtout à la question :

> Est-ce que mon API fonctionne réellement comme prévu ?

Swagger répond surtout à la question :

> Quels endpoints mon API expose-t-elle et comment doit-on les utiliser ?

### 5. Problème concret rencontré

Lors du test :

```text
GET /api/reservations/{id}
```

le serveur retournait `200`, mais Postman affichait une erreur lors de l'analyse JSON.

La cause était une association bidirectionnelle :

```text
Reservation → Vehicule → reservations → Reservation → ...
```

La correction a consisté à ajouter `@JsonIgnore` sur la collection inverse :

```java
@JsonIgnore
@OneToMany(mappedBy = "vehicule")
private Set<Reservation> reservations = new HashSet<>();
```

Après redémarrage de l'application, la réponse JSON est devenue correctement sérialisable et les deux tests Postman sont passés.

---

## D. Analyse SonarQube for IDE

Une analyse du package :

```text
web.controller
```

a été réalisée avec SonarQube for IDE.

### 1. Anomalie java:S1710 — wrapper @ApiResponses inutile

SonarQube a détecté plusieurs occurrences du problème :

```text
Remove the 'ApiResponses' wrapper from this annotation group
```

Le code utilisait par exemple :

```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
    @ApiResponse(responseCode = "404", description = "Contrat introuvable")
})
```

`@ApiResponse` étant répétable, le wrapper `@ApiResponses` était inutile.

La correction appliquée est :

```java
@ApiResponse(
    responseCode = "200",
    description = "Contrat trouvé"
)
@ApiResponse(
    responseCode = "404",
    description = "Contrat introuvable"
)
```

L'import suivant a également été supprimé lorsqu'il n'était plus utilisé :

```java
import io.swagger.v3.oas.annotations.responses.ApiResponses;
```

Cette correction a notamment été appliquée dans `ContratController`, `PaiementController` et `AgenceController`.

Plusieurs anomalies `java:S1710` ont ainsi été corrigées.

Les analyses successives ont montré une diminution du nombre d'issues :

```text
25 issues dans 9 fichiers
        ↓
20 issues dans 8 fichiers
        ↓
16 issues dans 8 fichiers
```

Les anomalies `java:S1710` concernées ont disparu après correction.

### 2. Anomalie java:S4684 — exposition directe des entités persistantes

SonarQube signale également :

```text
Replace this persistent entity with a simple POJO or DTO object.
```

Règle :

```text
java:S4684
```

Cette anomalie apparaît parce que les contrôleurs utilisent directement des entités JPA dans leurs entrées ou leurs sorties.

Exemple :

```java
public Contrat create(@RequestBody Contrat contrat)
```

ou :

```java
public List<Client> findAll()
```

Dans une architecture plus découplée, les contrôleurs devraient utiliser des DTO.

Par exemple :

```text
ClientDTO
ContratDTO
VehiculeDTO
ReservationDTO
```

Cela évite d'exposer directement le modèle de persistance et permet de contrôler précisément les données échangées par l'API.

Cependant, dans cet atelier, l'exposition directe des entités JPA est volontaire et provisoire. L'énoncé indique que les DTO seront introduits ultérieurement, au Chapitre 10.

Cette anomalie a donc été identifiée et analysée, mais sa correction architecturale est reportée à l'étape pédagogique consacrée aux DTO.

Après les corrections réalisables dans le périmètre de l'Atelier 5, le dernier rapport SonarQube affiche :

```text
16 issues dans 8 fichiers
```

correspondant aux alertes restantes liées principalement à l'exposition directe des entités persistantes.

---

## E. Questions de compréhension

### Question 1 — Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?

Dans Spring MVC, le composant qui reçoit les requêtes HTTP est le `DispatcherServlet`.

Il constitue le point d'entrée central de Spring MVC.

Le parcours simplifié est :

```text
Client/Postman
      ↓
DispatcherServlet
      ↓
HandlerMapping
      ↓
Controller
      ↓
Service
      ↓
Repository
      ↓
Base de données
```

Le `DispatcherServlet` reçoit la requête puis utilise le mécanisme de `HandlerMapping` pour déterminer quelle méthode du contrôleur correspond à l'URL et au verbe HTTP reçus.

Par exemple :

```text
GET /api/contrats/1
```

correspond à :

```java
@GetMapping("/{id}")
public Contrat findById(@PathVariable Long id)
```

Le résultat remonte ensuite jusqu'au contrôleur et Jackson transforme l'objet Java retourné en JSON avant l'envoi de la réponse HTTP.

---

### Question 2 — Pourquoi ContratController dépend-il de IContratService et non de ContratRepository ni de ContratServiceImpl ?

`ContratController` dépend de :

```java
private final IContratService contratService;
```

Cela respecte l'organisation en couches :

```text
Controller
    ↓
Interface Service
    ↓
ServiceImpl
    ↓
Repository
    ↓
Base de données
```

Le contrôleur ne doit pas accéder directement au repository, car il contournerait alors la couche métier et ses validations.

Il ne doit pas non plus dépendre directement de `ContratServiceImpl`, car cela créerait un couplage fort avec une implémentation précise.

Dépendre de `IContratService` permet de programmer contre une abstraction et de conserver un meilleur découplage entre les couches.

---

### Question 3 — Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?

Lorsqu'un POST crée correctement une nouvelle ressource, le code approprié est :

```text
201 Created
```

Dans les contrôleurs, il est obtenu avec :

```java
@ResponseStatus(HttpStatus.CREATED)
```

Lorsqu'un DELETE réussit et qu'aucun corps de réponse n'est nécessaire, le code utilisé est :

```text
204 No Content
```

avec :

```java
@ResponseStatus(HttpStatus.NO_CONTENT)
```

Lorsqu'une ressource demandée n'existe pas, le service déclenche une `ResourceNotFoundException`.

Le `GlobalExceptionHandler` transforme cette exception en :

```text
404 Not Found
```

Cela signifie que la ressource demandée n'existe pas.

Un `500 Internal Server Error` indiquerait plutôt une erreur interne inattendue du serveur. Une ressource inexistante est une situation métier connue et doit donc produire `404`, pas `500`.

---

### Question 4 — Pourquoi PaiementController n'expose-t-il que des GET ? Quelle réponse reçoit un POST /api/paiements ?

Dans cet atelier, `PaiementController` est volontairement en lecture seule.

Il expose uniquement :

```text
GET /api/paiements
GET /api/paiements/{id}
```

Les paiements sont notamment gérés dans le scénario de cascade depuis `Contrat`.

Aucune méthode :

```java
@PostMapping
```

n'existe dans `PaiementController`.

Le test Postman :

```text
POST /api/paiements
```

a donc retourné :

```text
405 Method Not Allowed
```

Ce résultat a été vérifié avec :

```javascript
pm.test("Statut 405", () => {
    pm.response.to.have.status(405);
});
```

Le test est passé avec succès.

---

### Question 5 — Quels risques présente l'exposition directe des entités JPA ? Pourquoi Contrat.paiements utilise-t-il @JsonManagedReference alors que Agence.vehicules utilise @JsonIgnore ?

Exposer directement les entités JPA dans une API présente plusieurs risques.

Premièrement, cela crée un couplage entre le modèle de base de données et le contrat REST. Une modification d'une entité peut donc modifier involontairement le JSON de l'API.

Deuxièmement, certains attributs internes peuvent être exposés alors qu'ils ne devraient pas être accessibles aux clients de l'API.

Troisièmement, les associations JPA bidirectionnelles peuvent créer des boucles de sérialisation JSON.

Enfin, les relations `LAZY` peuvent produire des problèmes de proxy Hibernate lors de la sérialisation.

Dans le cas `Contrat ↔ Paiement`, la relation représente une composition importante pour le scénario métier de l'atelier. On souhaite qu'un contrat puisse exposer ses paiements dans son JSON.

On utilise donc :

```java
@JsonManagedReference("contrat-paiements")
```

sur la collection des paiements du contrat et :

```java
@JsonBackReference("contrat-paiements")
```

sur la référence du paiement vers son contrat.

Ainsi :

```text
Contrat
  ↓
Paiement
```

est sérialisé, mais Jackson ne repart pas de `Paiement` vers `Contrat`.

Pour certaines autres associations, lorsque la collection inverse n'est pas utile dans la réponse JSON, `@JsonIgnore` est plus simple.

Par exemple, ignorer une collection inverse évite les boucles sans introduire une paire managed/back reference.

Le choix dépend donc de ce que l'API doit réellement exposer.

---

# Conclusion

L'Atelier 5 a permis de construire et de valider la couche Web REST de l'application AutoLoc.

Les neuf entités disposent maintenant de contrôleurs adaptés au besoin de l'atelier, avec un CRUD complet pour les ressources concernées et un accès volontairement en lecture seule pour `Paiement`.

Les principales conventions REST ont été appliquées :

```text
GET     → consultation
POST    → création
PUT     → modification
DELETE  → suppression
```

avec les codes HTTP :

```text
200 → opération réussie
201 → ressource créée
204 → suppression réussie sans corps
400 → données invalides
404 → ressource inexistante
405 → méthode HTTP non autorisée
```

Les endpoints ont été testés avec Postman, documentés avec SpringDoc/OpenAPI et Swagger UI, et analysés avec SonarQube for IDE.

Les problèmes de sérialisation liés aux associations bidirectionnelles ont également été traités grâce aux annotations Jackson adaptées.

L'API AutoLoc est ainsi exposée, testée et documentée conformément aux objectifs de l'Atelier 5.