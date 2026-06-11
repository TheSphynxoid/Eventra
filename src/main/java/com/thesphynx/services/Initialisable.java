package com.thesphynx.services;

/**
 * Interface pour les objets qui nécessitent une initialisation explicite
 */
public interface Initialisable {
    /**
     * Initialise l'objet
     * @throws InitialisationException si l'initialisation échoue
     */
    void initialiser() throws InitialisationException;

    /**
     * Vérifie l'état d'initialisation
     * @return true si l'objet est correctement initialisé
     */
    boolean estInitialise();
}