package com.thesphynx.services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void ajouter(T entity) throws SQLException;
    void supprimer(T entity) throws SQLException;
    void modifier(T entity) throws SQLException;
    List<T> afficher() throws SQLException;
    List<T> getAll() throws SQLException;
}
