package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;
import com.secret.dictionary.util.DataBase;

import java.sql.*;
import java.util.*;

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

        String sql = "INSERT INTO mots(mot,def,categorie,emojie) VALUES(?,?,?,?);" ;

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1,m.getMot());
            ps.setString(2,m.getDefinition());
            ps.setString(3,m.getCategorie());
            ps.setString(4,m.getEmojie());

            int n = ps.executeUpdate() ;

            if ( n > 0 ) // le nbr de ligne affecté ( inserer )
                return true ;

        }catch ( SQLException e ) {
            throw new DAOExeption("Erreur lors d'ajout du mot ( save )",e) ;
        }
        return false ;
    }

    @Override
    public Mot findByMot(Mot m) throws DAOExeption {

        String sql = "SELECT * FROM mots WHERE mot = ?;" ;

        try( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ps.setString(1,m.getMot());
            ResultSet rs = ps.executeQuery() ;

            if ( rs.next() ) {
                m.setId(rs.getInt("id"));
                m.setDefinition(rs.getString("def"));
                m.setCategorie(rs.getString("categorie"));
                m.setEmojie(rs.getString("emojie"));
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

        String sql = "UPDATE mots SET mot = ?, def = ?, categorie = ?, emojie = ? WHERE mot = ?;";

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1,nouveau.getMot());
            ps.setString(2,nouveau.getDefinition());
            ps.setString(3,nouveau.getCategorie());
            ps.setString(4,nouveau.getEmojie());
            ps.setString(5,ancien.getMot());

            return ps.executeUpdate() > 0 ;

        } catch (SQLException e) {
            throw new DAOExeption("Problème dans la modification du mot", e);
        }
    }

    @Override
    public int getIDByMot(String mot) throws DAOExeption {

        String sql = "SELECT id FROM mots WHERE mot = ?;";

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1,mot);
            ResultSet rs = ps.executeQuery() ;

            if ( rs.next() )
                return rs.getInt("id");

            return -1 ; // Cas de aucun ligne n'est trouvé

        } catch ( SQLException e ) {
            throw new DAOExeption("Probleme dans la recherche d'index d'un mot",e) ;
        }

    }

    @Override
    public boolean addSynonyme(Mot mot1, Mot mot2) throws DAOExeption {

        String sql = "INSERT INTO mots_synonymes(mot_id,synonyme_id) VALUES(?,?);" ;

        try ( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ps.setInt(1,mot1.getId());
            ps.setInt(2,mot2.getId());

           return  ps.executeUpdate() > 0 ;

        } catch ( SQLException e ) {
            throw new DAOExeption("Probleme dans l'ajout du synonymes",e) ;
        }
    }

    @Override
    public boolean addAntonyme (Mot mot1, Mot mot2) throws DAOExeption {

        String sql = "INSERT INTO mots_antonymes(mot_id,antonyme_id) VALUES(?,?);" ;

        try ( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ps.setInt(1,mot1.getId());
            ps.setInt(2,mot2.getId());

            return  ps.executeUpdate() > 0 ;

        } catch ( SQLException e ) {
            throw new DAOExeption("Probleme dans l'ajout du antonyme",e) ;
        }
    }

    @Override
    public List<String> getSynonymes(Mot mot) throws DAOExeption {

        // Usage du TEXT Bloc
        String sql = """       
                   SELECT mot FROM mots
                   WHERE id IN (
                    SELECT synonyme_id FROM mots_synonymes
                    WHERE mot_id = ?
                    UNION -- Pour ne pas faire retourner deux id identique ( apparaitre a mot_id et synonyme_id ) 
                    SELECT mot_id FROM mots_synonymes
                    WHERE synonyme_id = ?
                    );
                """;

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1,mot.getId());
            ps.setInt(2,mot.getId());

            ResultSet rs = ps.executeQuery();

            List<String> synonymes = new LinkedList<>() ;

            while (rs.next())
                synonymes.add(rs.getString("mot"));


            return synonymes.isEmpty() ? null : synonymes ;

        }catch ( SQLException e ) {
            throw new DAOExeption("Probleme lorqs du recherches des synonymes",e) ;
        }
    }

    @Override
    public List<String> getAntonymes(Mot mot) throws DAOExeption {
        String sql = """       
                   SELECT mot FROM mots
                   WHERE id IN (
                    SELECT antonyme_id FROM mots_antonymes
                    WHERE mot_id = ?
                    UNION -- Pour ne pas faire retourner deux id identique ( apparaitre a mot_id et synonyme_id ) 
                    SELECT mot_id FROM mots_antonymes
                    WHERE antonyme_id = ?
                    );
                """;

        try ( PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1,mot.getId());
            ps.setInt(2,mot.getId());

            ResultSet rs = ps.executeQuery();

            List<String> antonymes = new LinkedList<>() ;

            while (rs.next())
                antonymes.add(rs.getString("mot"));

            return antonymes.isEmpty() ? null : antonymes ;

        }catch ( SQLException e ) {
            throw new DAOExeption("Probleme lorqs du recherches des antonymes",e) ;
        }
    }

    @Override
    public List<Mot> findByCategorie(Mot mot) throws DAOExeption {

        String sql = "SELECT * FROM mots WHERE categorie = ?;" ;

        try( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ps.setString(1,mot.getCategorie());
            ResultSet rs = ps.executeQuery() ;

            List<Mot> mots = new LinkedList<>();

            while ( rs.next() ) {

                Mot m = new Mot(rs.getInt("id"),
                        rs.getString("mot"),
                        rs.getString("def"),
                        rs.getString("categorie"),
                        rs.getString("emojie")) ;

                mots.add(m);
            }
           return mots.isEmpty() ? null : mots ;

        } catch (SQLException e) {
            throw new DAOExeption("Erreur lors du recherche de mot ( findByMot ) ",e) ;
        }
    }

    @Override
    public List<Mot> findByEmojie (Mot mot) throws DAOExeption {

        String sql = "SELECT * FROM mots WHERE emojie = ?;" ;

        try( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ps.setString(1,mot.getEmojie());
            ResultSet rs = ps.executeQuery() ;

            List<Mot> mots = new LinkedList<>();

            while ( rs.next() ) {

                Mot m = new Mot(rs.getInt("id"),
                        rs.getString("mot"),
                        rs.getString("def"),
                        rs.getString("categorie"),
                        rs.getString("emojie")) ;

                mots.add(m);
            }
            return mots.isEmpty() ? null : mots ;

        } catch (SQLException e) {
            throw new DAOExeption("Erreur lors du recherche de mot ( findByMot ) ",e) ;
        }
    }

    @Override
    public Map<String, Integer> getMotCountParCategorie( ) throws DAOExeption {

        String sql = "SELECT categorie,COUNT(*) FROM mots GROUP BY categorie ORDER BY categorie;";

        try ( PreparedStatement ps = connexion.prepareStatement(sql) ) {

            ResultSet rs = ps.executeQuery();

            Map<String,Integer> map = new LinkedHashMap<>();

            while ( rs.next() )
                map.put(rs.getString("categorie"),rs.getInt(1));

            if ( map.isEmpty() )
                return null ;

            return map ;

        } catch ( SQLException e )  {
            throw new DAOExeption("Erreur lors du getMotCountParCategorie() ",e) ;
        }
    }
}
