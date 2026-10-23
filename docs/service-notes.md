# Atelier 4 — Couche Service

## A. Modes d'injection comparés

Au cours de cet atelier, deux modes d'injection de dépendances Spring ont été comparés.

### 1. Injection par attribut

Exemple :

```java
@Autowired
private IContratRepository contratRepository;
```

Cette méthode fonctionne avec Spring, mais SonarQube for IDE a signalé l'anomalie suivante :

> Remove this field injection and use constructor injection instead.

L'injection par attribut rend la dépendance moins explicite et ne permet pas de déclarer le champ `final`.

### 2. Injection par constructeur

La version retenue utilise l'injection par constructeur avec Lombok :

```java
@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
}
```

L'annotation `@RequiredArgsConstructor` génère automatiquement un constructeur contenant les attributs `final`.

Spring utilise alors ce constructeur pour injecter `IContratRepository`.

Cette solution permet :

- de rendre les dépendances explicites ;
- d'utiliser des attributs `final` ;
- de faciliter les tests unitaires ;
- d'éviter l'injection directe dans les attributs ;
- d'éviter qu'un service soit créé sans ses dépendances obligatoires ;
- de respecter la recommandation de SonarQube for IDE.

Après remplacement de l'injection par attribut par l'injection par constructeur, l'analyse SonarQube for IDE affiche :

`No issues to display`

---

## B. Services

Une couche Service a été créée entre les futurs contrôleurs et les repositories.

Pour chaque entité, une interface Service et une implémentation ont été créées.

| Entité | Interface | Implémentation | Dépendance injectée | Mode |
|---|---|---|---|---|
| Agence | `IAgenceService` | `AgenceServiceImpl` | `IAgenceRepository` | Constructeur |
| Client | `IClientService` | `ClientServiceImpl` | `IClientRepository` | Constructeur |
| Contrat | `IContratService` | `ContratServiceImpl` | `IContratRepository` | Constructeur |
| Employe | `IEmployeService` | `EmployeServiceImpl` | `IEmployeRepository` | Constructeur |
| Equipement | `IEquipementService` | `EquipementServiceImpl` | `IEquipementRepository` | Constructeur |
| Maintenance | `IMaintenanceService` | `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur |
| Paiement | `IPaiementService` | `PaiementServiceImpl` | `IPaiementRepository` | Constructeur |
| Reservation | `IReservationService` | `ReservationServiceImpl` | `IReservationRepository` | Constructeur |
| Vehicule | `IVehiculeService` | `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur |

Les implémentations utilisent :

```java
@Service
@RequiredArgsConstructor
```

`@Service` permet à Spring de détecter la classe et de créer un bean correspondant à la couche métier.

`@RequiredArgsConstructor` permet de générer le constructeur utilisé pour l'injection des repositories déclarés `final`.

### Exemple : ContratServiceImpl

Le service `ContratServiceImpl` possède la dépendance suivante :

```java
private final IContratRepository contratRepository;
```

Il contient également les règles métier liées au contrat.

Lors de la création, un nouveau contrat ne doit pas déjà posséder un identifiant :

```java
if (contrat.getIdContrat() != null) {
    throw new IllegalArgumentException(
        "Un nouveau contrat ne doit pas avoir d'identifiant"
    );
}
```

Le montant total d'un contrat ne doit pas être négatif :

```java
if (contrat.getMontantTotal() != null
        && contrat.getMontantTotal().signum() < 0) {
    throw new IllegalArgumentException(
        "Le montant total ne peut pas être négatif"
    );
}
```

Pour une mise à jour, le service charge d'abord le contrat existant puis recopie les attributs modifiables :

```java
Contrat existant = findById(id);

existant.setDateSignature(contrat.getDateSignature());
existant.setMontantTotal(contrat.getMontantTotal());
existant.setValide(contrat.isValide());
existant.setReservation(contrat.getReservation());

return contratRepository.save(existant);
```

Cela permet notamment de conserver l'identifiant de l'entité existante au lieu de remplacer directement l'objet persistant.

### Règles métier appliquées aux autres services

Les autres services suivent le même principe avec des règles adaptées aux attributs réellement présents dans les entités :

- **Agence** : contrôle des informations obligatoires comme le nom et la ville.
- **Employe** : contrôle des informations obligatoires comme le nom.
- **Vehicule** : contrôle des valeurs numériques, notamment le tarif journalier qui ne doit pas être négatif.
- **Equipement** : contrôle du libellé.
- **Client** : contrôle des informations d'identification comme le nom et l'e-mail.
- **Reservation** : la date de fin ne doit pas être antérieure à la date de début.
- **Maintenance** : la date de fin ne doit pas être antérieure à la date de début.
- **Contrat** : le montant total ne doit pas être négatif.
- **Paiement** : service en lecture seule conformément à l'énoncé ; les créations, modifications et suppressions passent par le Contrat.

---

## C. Messages d'erreur du conteneur Spring

### Message 1 — Bean `IContratService` introuvable

Le message indique que Spring ne trouve aucun bean de type `IContratService`.

Deux causes probables côté `ContratServiceImpl` sont :

1. l'annotation `@Service` est absente ;
2. la classe est placée hors du package racine `tn.esprit.autoloc` et n'est donc pas détectée par le component scan.

La correction consiste à placer correctement `ContratServiceImpl` dans le package `tn.esprit.autoloc.service` et à annoter la classe :

```java
@Service
public class ContratServiceImpl implements IContratService {
}
```

### Message 2 — Plusieurs beans du même type

Le message indique que Spring trouve plusieurs beans compatibles avec la dépendance demandée.

Deux solutions sont possibles.

Première solution : utiliser `@Primary` sur l'implémentation qui doit être utilisée par défaut.

```java
@Primary
@Service
public class EmailNotificateur {
}
```

Deuxième solution : utiliser `@Qualifier` au niveau du point d'injection afin d'indiquer explicitement le bean à utiliser.

```java
public NotificationController(
        @Qualifier("emailNotificateur") Notificateur notificateur) {
    this.notificateur = notificateur;
}
```

`@Primary` définit donc un candidat par défaut, tandis que `@Qualifier` sélectionne explicitement un bean précis.

### Message 3 — Dépendance circulaire

Le message indique un cycle entre deux services :

```text
ClientServiceImpl
        ↑
        |
        ↓
ReservationServiceImpl
```

Chaque service a besoin de l'autre pour être construit.

Avec l'injection par constructeur, les dépendances obligatoires doivent être disponibles dès la création de l'objet. Spring ne peut donc construire aucun des deux services en premier.

La correction recommandée consiste à revoir la conception et à supprimer la dépendance circulaire, par exemple en déplaçant la responsabilité commune dans un autre service.

---

## D. Anomalies SonarQube for IDE

Une analyse du code a été effectuée avec SonarQube for IDE.

### Anomalie 1 — Injection par attribut

Avec :

```java
@Autowired
private IContratRepository contratRepository;
```

SonarQube for IDE a détecté :

> Remove this field injection and use constructor injection instead.

La correction consiste à utiliser l'injection par constructeur :

```java
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
}
```

Après correction, cette anomalie disparaît.

### Anomalie 2 — Exception générique

Dans `findById`, `ResourceNotFoundException` a été temporairement remplacée par `RuntimeException`.

L'objectif de cette expérience était d'observer le signalement d'une exception générique et de montrer l'intérêt d'utiliser une exception métier spécifique.

La version correcte a ensuite été restaurée :

```java
@Override
public Contrat findById(Long id) {
    return contratRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Contrat", id));
}
```

### Anomalie 3 — Qualité générale du code

Les packages `service` et `exception` ont été analysés avec SonarQube for IDE.

Après correction et restauration du code final, l'analyse affiche :

- `No issues to display`
- `No Security Hotspots to display`
- `No Taint Vulnerabilities to display`

L'analyse de `ResourceNotFoundException` n'a également détecté aucune anomalie.

---

## E. Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?

Il n'y a pas de `new ContratServiceImpl(...)` écrit manuellement dans notre code.

La classe étant annotée avec `@Service`, elle est détectée par le component scan de Spring.

Le conteneur Spring IoC crée le bean `ContratServiceImpl` et lui fournit son `IContratRepository` à travers l'injection par constructeur.

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?

Le contrôleur doit dépendre de l'abstraction, donc de l'interface `IContratService`, plutôt que d'une implémentation concrète.

Cela réduit le couplage entre les couches et permet de remplacer plus facilement l'implémentation du service sans modifier le contrôleur.

L'architecture devient donc :

```text
Controller
    |
    v
IContratService
    |
    v
ContratServiceImpl
    |
    v
IContratRepository
```

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.

Par défaut, un service Spring est un singleton : une seule instance est partagée entre plusieurs requêtes et plusieurs utilisateurs.

Il ne doit donc pas conserver dans ses attributs des données propres à une requête ou à un utilisateur.

Par exemple, ceci serait dangereux :

```java
private Contrat contratCourant;
```

Si deux utilisateurs utilisent simultanément le même service, l'un pourrait modifier `contratCourant` pendant que l'autre l'utilise.

Les services doivent donc rester sans état mutable propre à un appel.

### 4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` ?

Le service charge d'abord l'entité existante afin de vérifier qu'elle existe et de conserver son identité.

Ensuite, seuls les attributs modifiables sont recopiés.

Par exemple :

```java
Contrat existant = findById(id);

existant.setDateSignature(contrat.getDateSignature());
existant.setMontantTotal(contrat.getMontantTotal());
existant.setValide(contrat.isValide());
existant.setReservation(contrat.getReservation());

return contratRepository.save(existant);
```

Cela évite de remplacer directement l'entité par l'objet reçu et permet de mieux contrôler les modifications effectuées.

### 5. Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?

`@Component` est placé sur une classe afin qu'elle soit automatiquement détectée par le component scan de Spring.

`@Bean` est placé sur une méthode, généralement dans une classe `@Configuration`, afin de déclarer explicitement un objet géré par Spring. Il est notamment utile pour des classes que l'on ne peut pas annoter directement.

`@Primary` permet de désigner le bean utilisé par défaut lorsqu'il existe plusieurs beans du même type.

`@Qualifier` permet de sélectionner explicitement un bean particulier au niveau du point d'injection.

`IPaiementService` est volontairement en lecture seule dans cet atelier. Il expose uniquement `findById` et `findAll`. La création, la modification et la suppression des paiements passent par le Contrat afin de respecter la gestion de l'association entre `Contrat` et `Paiement`.

---

## Exception métier utilisée

Une exception personnalisée `ResourceNotFoundException` a été créée dans :

```text
tn.esprit.autoloc.exception
```

Son implémentation est :

```java
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable avec l'identifiant " + id);
    }
}
```

Elle permet de traduire l'absence d'une entité en erreur métier explicite.

Exemple :

```java
return contratRepository.findById(id)
        .orElseThrow(() ->
                new ResourceNotFoundException("Contrat", id));
```

Si le contrat n'existe pas, le message obtenu est par exemple :

```text
Contrat introuvable avec l'identifiant 15
```

---

## Validation finale

Après création des neuf interfaces Service et des neuf implémentations, l'application Spring Boot a été relancée.

Résultats observés :

- profil actif : `dev` ;
- 9 interfaces JPA Repository détectées ;
- connexion à la base de données réussie ;
- `EntityManagerFactory` initialisé ;
- serveur Tomcat démarré sur le port `8081` ;
- `AutolocApiApplication` démarrée avec succès ;
- aucune erreur bloquante liée à la couche Service.

Le log confirme notamment :

```text
Found 9 JPA repository interfaces.
Tomcat started on port 8081 (http)
Started AutolocApiApplication
```

Un avertissement concernant la version MySQL est présent, mais il n'empêche pas le démarrage de l'application et n'est pas lié à la couche Service.

## Conclusion

L'Atelier 4 a permis de mettre en place la couche Service de l'application AutoLoc.

Les neuf services utilisent l'injection de dépendances par constructeur, les implémentations sont déclarées avec `@Service`, les repositories sont injectés sous forme d'interfaces et les services restent sans état.

L'utilisation de `ResourceNotFoundException` permet également de traduire l'absence d'une ressource en erreur métier explicite.

Enfin, SonarQube for IDE a permis de comparer l'injection par attribut à l'injection par constructeur et de vérifier la qualité du code final.