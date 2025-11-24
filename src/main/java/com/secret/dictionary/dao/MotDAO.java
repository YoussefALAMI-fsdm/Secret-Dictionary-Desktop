package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

import java.util.List;

public interface MotDAO {

    List<String> findAllMot () throws DAOExeption ;
    boolean saveMot(Mot m ) throws DAOExeption ;
    Mot findWByMot( Mot m ) throws DAOExeption ;
    List<String> getListMot ( String mot ) throws DAOExeption ;
    boolean updateMot ( Mot ancien , Mot nouveau ) throws DAOExeption ;

}
