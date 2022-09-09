# Openclassrooms - Projet 8 : Amélioration d'une application avec des systèmes distribués
## Contexte
<i>Tour Guide</i> est une application API de mise en relation entre des voyageurs avec des agences de voyages. 

Elle permet de proposer aux voyageurs :
- des séjours avec des réductions
- des centres d'intérêt en fonction de la géolocalisation de l'utilisateur
- des points de fidélité en fonction du nombres séjours cumulés

### Problèmes
- GpsUtil est trop lent. Il doit être plus rapide. Il doit pouvoir obtenir 100 000 emplacements d'utilisateurs dans un délai de 15 minutes.
- RewardsCentral est trop lent. Il doit être plus rapide. Il doit pouvoir obtenir des récompenses pour 100 000 utilisateurs dans un délai de 20 minutes.
- TripPricer doit fonctionner avec les préférences de voyage de l'utilisateur. 
- Corriger le bug des destinations recommandées aux utilisateurs. Ajouter les 5 attractions les plus proches par rapport au dernier emplacement de l'utilisateur peu importe leur distance.
- Corriger les bugs des tests unitaires qui échouent par intermittence. 
- Aligner les tests unitaires de performances existants avec les corrections pour prouver l'amélioration de la rapidité.

Architecture initiale : 

![schema technique initial](https://user-images.githubusercontent.com/41240871/189343349-87c6ef9e-177f-43f3-9ea4-e983d6ef3ecc.jpg)

Code legacy : [lien](https://github.com/OpenClassrooms-Student-Center/JavaPathENProject8)

## Objectifs
- Découpler les librairies tiers de l'application
- Debugger l'application
- Implémenter les nouvelles fonctionnalités
- Optimiser le temps de traitement des données de masse

## Processus
- Découplage des librairies tiers en utilisant l'inversion de dépendance
- Résolution des bugs existants dans l'application avec des tests unitaires
- Externaliser les librairies tiers en microservices
- Implémentation de l'architecture Microservices (Microservice-Proxy)
- Création des proxies connectés aux microservices <i>GpsUtil</i>, <i>RewardCentral</i> et <i>TripPricer</i> avec Spring Cloud OpenFeign
- Implémentation des nouvelles fonctionnalités
- Rédaction et validation des tests unitaires et d'intégration (TDD et Postman)
- Optimisation du traitements des données de masse avec la mise en place du multithreading
- Rédaction et validation des tests de performances
- Contenairisation de chaque microservice et de l'application <i>Tour Guide</i> avec Docker
- Centralisation de l'utilisation des images avec Docker Compose

Architecture finale :

![schema technique](https://user-images.githubusercontent.com/41240871/189347248-c91d381b-7dc8-436f-a026-788256b707d4.jpg)

