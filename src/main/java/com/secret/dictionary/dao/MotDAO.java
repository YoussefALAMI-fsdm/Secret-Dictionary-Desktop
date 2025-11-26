package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

import java.util.List;

public interface MotDAO {

    List<String> findAllMot () throws DAOExeption ;
    boolean saveMot(Mot m ) throws DAOExeption ;
    Mot findWByMot( Mot m ) throws DAOExeption ;
    List<String> getListMot ( String mot ) throws DAOExeption ;
    boolean updateMot ( Mot ancien , Mot nouveau ) throws DAOExeption ;
    boolean addSynonyme ( Mot mot1 , Mot mot2 ) throws DAOExeption ;
    boolean addAntonyme ( Mot mot1 , Mot mot2 ) throws DAOExeption ;
    List<Mot> getSynonymes ( Mot mot ) throws DAOExeption ;
    List<Mot> getAntonymes ( Mot mot ) throws DAOExeption ;

}
