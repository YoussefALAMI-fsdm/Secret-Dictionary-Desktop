package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

import java.util.List;

public interface MotDAO {

    List<String> findAllMot () throws DAOExeption ;
    boolean save ( Mot m ) throws DAOExeption ;
    Mot findWByMot( Mot m ) throws DAOExeption ;
}
