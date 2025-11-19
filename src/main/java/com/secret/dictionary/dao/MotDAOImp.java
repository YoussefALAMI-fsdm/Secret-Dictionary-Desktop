package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;
import com.secret.dictionary.util.DataBase;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

// DAO : Couche backend qui interagir avec la DB

public class MotDAOImp implements MotDAO { // Defenir le CRUD complet ( create, findAll, findById, update, delete ) c a d permet
                     // l'acces et la communication avec la DB
                     // le transfer entre DTO <=> DAO ce fait dans le service ( Controller logique )

    private Connection connexion ;

    public MotDAOImp(DataBase db ) {

        this.connexion = db.getConnection() ;
    }

    @Override
    public List<String> findAllMot() throws DAOExeption { // Futur modification si besion : Choisir de retourner List<Mot> plutôt que List<String> dans ton DAO, même si tu ne récupères qu’une seule colonne aujourd’hui, c’est une décision orientée vers la robustesse et l’évolutivité.

        List<String> mots = new LinkedList<>() ;

        String sql = "SELECT mot FROM mots ORDER BY mot ASC;" ;

        try (PreparedStatement ps = connexion.prepareStatement(sql) ; ResultSet rs = ps.executeQuery() ) { // On prepare le statement a executer
                                                                         // puis on l'execute et on stocke le resultas dans un cursor sur la table retourné
            while (rs.next())
              mots.add(rs.getString("mot")) ;

        } catch ( SQLException e ) {
           throw new DAOExeption("Erreur lors du recuperation des mots ( findAllMot )",e) ;
        }
        return mots ; // retourne list vide si non trouvé
    }

    @Override
    public boolean save(Mot m) throws DAOExeption {

        String sql = "INSERT INTO mots(mot,def) VALUES(?,?);" ;

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1,m.getMot());
            ps.setString(2,m.getDefinition());

            int n = ps.executeUpdate() ;

            if ( n > 0 ) // le nbr de ligne affecté ( inserer )
                return true ;

        }catch ( SQLException e ) {
            throw new DAOExeption("Erreur lors d'ajout du mot ( save )",e) ;
        }
        return false ;
    }

    @Override
    public Mot findWByMot(Mot m) throws DAOExeption {

        String sql = "SELECT * FROM mots WHERE mot = ?;" ;

        try( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ps.setString(1,m.getMot());
            ResultSet rs = ps.executeQuery() ;
            if ( rs.next() ) {
                m.setId(rs.getInt("id"));
                m.setDefinition(rs.getString("def"));
                return m ;
            }
            else
                return null ;

        }catch (SQLException e) {
            throw new DAOExeption("Erreur lors du recherche de mot ( findByMot ) ",e) ;
        }
    }
}
