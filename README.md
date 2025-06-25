# 📍 GPS Project – Backend (Spring Boot)

Ce projet est le backend d'une application mobile de géolocalisation dédiée aux restaurants, cafés, snacks et autres établissements de restauration.

---

## 🚀 Objectif

Créer une API REST performante permettant :

- 📌 Gérer les établissements (ajout, modification, suppression)
- 🧭 Localiser les lieux autour de l'utilisateur en temps réel
- 🧑‍💼 Gérer les utilisateurs et leur authentification (JWT)
- ⭐ Laisser des avis, notes et favoris sur les lieux
- 🔍 Rechercher par distance, catégorie, note, etc.

---

## 🛠️ Stack technique

- Langage : Java 17+
- Framework : Spring Boot
- Système de build : Maven
- Base de données : PostgreSQL
- Sécurité : Spring Security + JWT
- Mapping ORM : Spring Data JPA / Hibernate
- Tests : JUnit, Mockito
- Déploiement : Docker / GitHub Actions / Railway (optionnel)

---

## 📦 Modules principaux

- `UserController`, `AuthController` : gestion des comptes, login, JWT
- `EtablissementController` : endpoints REST pour la gestion des lieux
- `GeoService` : logique de géolocalisation (calcul distance, rayon, etc.)
- `AvisController` : création de commentaires et notes
- `FavorisController` : ajout/suppression des favoris

---

## 🔧 Lancement local

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/aymenelmhaoud/gps-project.git
   cd gps-project
