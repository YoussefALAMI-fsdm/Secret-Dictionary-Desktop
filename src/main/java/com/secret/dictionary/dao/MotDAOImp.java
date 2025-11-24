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

    private final Connection connexion ;

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
    public boolean saveMot(Mot m) throws DAOExeption {

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

    @Override
    public List<String> getListMot(String mot) throws DAOExeption {

        String sql = "SELECT mot FROM mots " +
                "WHERE mot % ? " +               // opérateur flou pg_trgm
                "ORDER BY similarity(mot, ?) DESC " +
                "LIMIT 10;" ;

        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, mot) ;
            ps.setString(2, mot) ;

            ResultSet rs = ps.executeQuery();

            List<String> mots = new LinkedList<>();
            while (rs.next()) {
                mots.add(rs.getString("mot"));
            }

            if (mots.isEmpty()) {
                return null ;
            }

            return mots;

        } catch (SQLException e) {
            throw new DAOExeption("Problème dans la recherche floue", e);
        }
    }

    public boolean updateMot ( Mot ancien , Mot nouveau ) throws DAOExeption {

        String sql = "UPDATE mots SET mot = ?, def = ? WHERE mot = ?;";

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1,ancien.getMot());
            ps.setString(2,nouveau.getMot());
            ps.setString(3,nouveau.getDefinition());

            int nbrligneAffecte = ps.executeUpdate() ;

            return nbrligneAffecte > 0 ;

        } catch (SQLException e) {
            throw new DAOExeption("Problème dans la modification du mot", e);
        }
    }
}
