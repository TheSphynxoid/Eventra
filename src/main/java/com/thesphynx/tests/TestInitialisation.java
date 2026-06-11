package com.thesphynx.tests;

import com.thesphynx.services.InitialisationException;
import com.thesphynx.services.ServiceInitialisable;

public class TestInitialisation {
    public static void main(String[] args) {
        ServiceInitialisable service = new ServiceInitialisable();

        try {
            System.out.println("Avant initialisation - état: " + service.estInitialise());
            service.initialiser();
            System.out.println("Après initialisation - état: " + service.estInitialise());
        } catch (InitialisationException e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}