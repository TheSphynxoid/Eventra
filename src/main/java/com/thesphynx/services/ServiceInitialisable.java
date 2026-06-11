package com.thesphynx.services;

/**
 * Exemple d'implémentation de Initialisable
 */
public class ServiceInitialisable implements Initialisable {
    private boolean initialise = false;

    @Override
    public void initialiser() throws InitialisationException {
        try {
            // Logique d'initialisation ici
            System.out.println("Initialisation du service...");
            // Simulation d'une opération qui peut échouer
            if (Math.random() < 0.2) {
                throw new RuntimeException("Erreur aléatoire pendant l'initialisation");
            }

            initialise = true;
            System.out.println("Service initialisé avec succès");
        } catch (RuntimeException e) {
            throw new InitialisationException("Échec de l'initialisation du service", e);
        }
    }

    @Override
    public boolean estInitialise() {
        return initialise;
    }
}