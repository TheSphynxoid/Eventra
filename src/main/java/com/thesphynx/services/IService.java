package com.thesphynx.services;

import java.sql.SQLException;
import java.util.List;

public interface IService <T>{
    public void Ajouter(T t) throws SQLException;
    public void Modifier(T t) throws SQLException;
    public void Supprimer(int id) throws SQLException;
    public List<T> Afficher() throws SQLException;
}
