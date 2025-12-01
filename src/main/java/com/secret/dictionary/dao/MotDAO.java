package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

import java.util.List;
import java.util.Map;

public interface MotDAO {

    List<String> findAllMot () throws DAOExeption ;
    boolean saveMot(Mot m ) throws DAOExeption ;
    Mot findByMot(Mot m ) throws DAOExeption ;
    List<String> getListMot ( String mot ) throws DAOExeption ;
    boolean updateMot ( Mot ancien , Mot nouveau ) throws DAOExeption ;


    int getIDByMot(String mot) throws DAOExeption ; /**
     *
     * Récupérer d’abord l’ID du mot via getID(mot) permet de n’effectuer qu’une seule recherche
     * textuelle (index-only scan très rapide), puis d’exécuter toutes les opérations du Synonymes / antonymes
     * uniquement à partir de l’ID entier. Cela optimise fortement les requêtes, surtout pour la table de relation
     * N–N (mots_synonymes) qui travaille naturellement avec mot_id et synonyme_id. Le léger coût de
     * getID() est négligeable, et cette approche rend l’architecture plus propre : le service gère
     * la logique métier et le DAO utilise toujours des recherches par ID, plus performantes.
     * @Return -1 si mot non trouvé dans la DB
     */

    boolean addSynonyme ( Mot mot1 , Mot mot2 ) throws DAOExeption ; // @ Mot doit avoir l'id valide
    boolean addAntonyme ( Mot mot1 , Mot mot2 ) throws DAOExeption ; // @ Mot doit avoir l'id valide
    List<String> getSynonymes ( Mot mot ) throws DAOExeption ; // @ Mot doit avoir l'id valide
    List<String> getAntonymes ( Mot mot ) throws DAOExeption ; // @ Mot doit avoir l'id valide

    List<Mot> findByCategorie ( Mot m ) throws DAOExeption ;
    List<Mot> findByEmojie ( Mot m ) throws DAOExeption ;

    Map<String,Integer> getMotCountParCategorie() throws DAOExeption ;

    void rafraichirMaterializedView () throws DAOExeption ; // @ On l'utilise pour rafraichir apres chaque insert ou update
}
