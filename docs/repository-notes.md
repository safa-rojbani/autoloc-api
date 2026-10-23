# Atelier 3 — Spring Data JPA : couche Repository

## 1. Choix des interfaces Repository

Les neuf repositories de l'application AutoLoc sont placés dans le package
`tn.esprit.autoloc.repository`.

Ils utilisent `JpaRepository<Entité, Long>` afin de disposer du CRUD complet,
des listes, du tri, de la pagination ainsi que des fonctionnalités spécifiques à JPA.

| Interface | Étend | Justification |
|---|---|---|
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IEmployeRepository | JpaRepository<Employe, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IEquipementRepository | JpaRepository<Equipement, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IClientRepository | JpaRepository<Client, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IReservationRepository | JpaRepository<Reservation, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, findAll retourne une List et saveAndFlush est disponible |
| IPaiementRepository | JpaRepository<Paiement, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, listes, tri, pagination et méthodes JPA |

Au démarrage de l'application, Spring Data JPA détecte correctement les neuf
interfaces Repository :

`Found 9 JPA repository interfaces.`

## 2. Analyse SonarQube for IDE

Une analyse du projet a été réalisée avec SonarQube for IDE.

### Anomalie détectée

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
|---|---|---|
| Utilisation de System.out.println dans DataInitializer | java:S106 — les sorties standard ne doivent pas être utilisées directement pour les logs | Remplacement de System.out.println(...) par log.info(...) avec un logger SLF4J |
-
Après correction, une nouvelle analyse SonarQube for IDE a été effectuée.

Résultat :

`No issues to display`

Aucune autre anomalie n'a été détectée par SonarQube for IDE avec les règles
actuellement actives sur le projet.