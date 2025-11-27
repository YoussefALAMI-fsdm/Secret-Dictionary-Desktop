package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

import java.util.List;

public interface MotDAO {

    List<String> findAllMot () throws DAOExeption ;
    boolean saveMot(Mot m ) throws DAOExeption ;
    Mot findWByMot( Mot m ) throws DAOExeption ;
    List<String> getListMot ( String mot ) throws DAOExeption ;
    boolean updateMot ( Mot ancien , Mot nouveau ) throws DAOExeption ;
    int getID ( String mot) throws DAOExeption ; // puiseque mot est indexé et id est une PK alors l'id est indexéé avec indx_mot => meuilleur performance ( index-only scan )
    boolean addSynonyme ( Mot mot1 , Mot mot2 ) throws DAOExeption ; // @ Mot doit avoir l'id valide
    boolean addAntonyme ( Mot mot1 , Mot mot2 ) throws DAOExeption ; // @ Mot doit avoir l'id valide
    List<Mot> getSynonymes ( Mot mot ) throws DAOExeption ; // @ Mot doit avoir l'id valide
    List<Mot> getAntonymes ( Mot mot ) throws DAOExeption ; // @ Mot doit avoir l'id valide

}
