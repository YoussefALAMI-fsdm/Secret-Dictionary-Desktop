package com.secret.dictionary.service;

import com.secret.dictionary.dao.DAOExeption;
import com.secret.dictionary.dao.MotDAO;
import com.secret.dictionary.dao.MotDAOImp;
import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.model.Mot;

import java.util.*;

public class MotServiceImp implements MotService { // Le controlleur logique ( fait aussi DAO <=> DTO )

    private final MotDAO dao ; // Rendre le Service accepte tt class qui implement la class MotDAO ( pour que couvre aussi les class de test )

    public MotServiceImp(MotDAO dao ) {
        this.dao = dao ;
    }

    public MotDTO entityToDTO ( Mot m ) {
        return new MotDTO( m.getMot(),
                           m.getDefinition(),
                           m.getCategorie(),
                           m.getEmojie()
        ) ;
    }

    public Mot dtoToEntity ( MotDTO dto ) {
        return new Mot(-1,            // -1 car on connu pas leur id ( vient d'UI )
                           dto.mot(),
                           dto.definition(),
                           dto.categorie(),
                           dto.emojie()
        ) ;
    }

    public List<String> getAllMots () {

        try {
            List<String> mots = dao.findAllMot();

            return Optional.ofNullable(mots)  // On creer un Optional<List<String>> qui est vide si mots == null
                    .orElse(Collections.emptyList())// si Optional est vide ( on a retourner Optional<List<String>> ) ,alors on la convertie en List ( vide )
                    .stream() // Convertie la list obtenue du Optional a un stream
                    .map(s -> s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1))
                    .toList();

        } catch (DAOExeption e) {
            System.err.println("Probleme DAO : "+e.getMessage());
            e.getStackTrace() ;
            return Collections.emptyList() ;
        }
    }

    @Override
    public int addMot(MotDTO dto) {
        Mot m = dtoToEntity(dto);

        var estAddSuccess = -1 ; // int

        try {
            var resultat = dao.saveMot(m); // boolean

            if (resultat)  // car .save(m) return true si bien
                estAddSuccess = 1;
            else
                estAddSuccess = -1; // .save() return false si probleme BD sans exeption ( non capturer ) ( mot existant leve une exeptions )

        } catch (DAOExeption e) { // Si l'exeption est levé alors on test les cas ( mot deja existant , ou probleme DB )

             if ( getInfoMot(dto) != null ) // Mot existant
                 estAddSuccess = 0 ;
             else {
                 System.err.println("Probleme DAO : " + e.getMessage());
                 e.printStackTrace();
                 estAddSuccess = -1;
             }
        }
        finally {
            if ( estAddSuccess == 1 ) { // rafraichir seulement c'est l'insert a bien passé
                try {
                    dao.rafraichirMaterializedView();
                } catch (DAOExeption e) {
                    System.err.println("Problème rafraîchissement MV : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return estAddSuccess ;
    }

    @Override
    public MotDTO getInfoMot(MotDTO dto) {
        try {
            Mot m = dao.findByMot(dtoToEntity(dto));
            if (m != null)
                return entityToDTO(m);
            return null;

        } catch (DAOExeption e) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getListMot(String mot) {

        try {
            List<Mot> mots = dao.getListMot(mot) ;

            List<String> listMots = new LinkedList<>();

            Iterator<Mot> it = mots.iterator() ;

            while ( it.hasNext() )
                listMots.add(it.next().getMot());

            return listMots ;
        } catch (DAOExeption e) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateMot ( MotDTO ancien , MotDTO nouveau ) {

        boolean estUpdateSuccess = false ;

        try {
             Mot m1 = dtoToEntity(ancien) ;
            Mot m2 = dtoToEntity(nouveau) ;
            estUpdateSuccess = dao.updateMot(m1,m2) ;

        } catch ( DAOExeption e ) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            if ( estUpdateSuccess ) { // rafraichir seulement c'est l'update a bien passé
                try {
                    dao.rafraichirMaterializedView();
                } catch (DAOExeption e) {
                    System.err.println("Problème rafraîchissement MV : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return estUpdateSuccess ;
    }

    @Override
    public int addSynonyme(MotDTO mot1, MotDTO mot2) {

        Mot m1 = dtoToEntity(mot1);
        Mot m2 = dtoToEntity(mot2);

        try {
            int idMot1 = dao.getIDByMot(m1.getMot());
            int idMot2 = dao.getIDByMot(m2.getMot());

            if ( idMot1 == -1 || idMot2 == -1 )
                return 0 ; // Un des mots est non trouvé pour l'utiliser en association

            m1.setId(idMot1); // Ajouter les ID dans l'entity Mot
            m2.setId(idMot2);

            try {
                if ( dao.addSynonyme(m1,m2) ) // si true alors bien ajouter
                    return 1 ;
                else // Sinon ( les mots exist mais probleme de creation de synonyme )
                    return -1 ;
            } catch ( DAOExeption e )  {
                System.err.println("Probleme DAO : " + e.getMessage());
                e.printStackTrace();
                return -1 ;
            }

        }catch ( DAOExeption e ) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return -1 ;
        }
    }

    @Override
    public int addAntonyme (MotDTO mot1, MotDTO mot2) {

        Mot m1 = dtoToEntity(mot1);
        Mot m2 = dtoToEntity(mot2);

        try {
            int idMot1 = dao.getIDByMot(m1.getMot());
            int idMot2 = dao.getIDByMot(m2.getMot());

            if ( idMot1 == -1 || idMot2 == -1 )
                return 0 ; // Un des mots est non trouvé pour l'utiliser en association

            m1.setId(idMot1); // Ajouter les ID dans l'entity Mot
            m2.setId(idMot2);

            try {
                if ( dao.addAntonyme(m1,m2) ) // si true alors bien ajouter
                    return 1 ;
                else // Sinon ( les mots exist mais probleme de creation de synonyme )
                    return -1 ;
            } catch ( DAOExeption e )  {
                System.err.println("Probleme DAO : " + e.getMessage());
                e.printStackTrace();
                return -1 ;
            }

        }catch ( DAOExeption e ) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return -1 ;
        }
    }

    @Override
    public List<String> getListSynonymes(MotDTO mot) {

        Mot m = dtoToEntity(mot);

       try {
           int idMot = dao.getIDByMot(m.getMot());

           if ( idMot == -1 )
               return null ;

           m.setId(idMot);

           List<String> mots ;

           try {
               mots = dao.getSynonymes(m);
               return mots == null ? Collections.emptyList() : mots ;

           } catch (DAOExeption e ) {
               System.err.println("Probleme DAO : " + e.getMessage());
               e.printStackTrace();
               return Collections.emptyList();
           }

       } catch ( DAOExeption e ) {
           System.err.println("Probleme DAO : " + e.getMessage());
           e.printStackTrace();
           return Collections.emptyList();
       }
    }

    @Override
    public List<String> getListAntonymes(MotDTO mot) {

        Mot m = dtoToEntity(mot);

        try {
            int idMot = dao.getIDByMot(m.getMot());

            if ( idMot == -1 )
                return null ;

            m.setId(idMot);

            List<String> mots ;

            try {
                mots = dao.getAntonymes(m);
                return mots == null ? Collections.emptyList() : mots ;

            } catch ( DAOExeption e ) {
                System.err.println("Probleme DAO : " + e.getMessage());
                e.printStackTrace();
                return Collections.emptyList();
            }

        } catch ( DAOExeption e ) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Integer> getMotCountParCategorie() {

        try {
            Map<String,Integer> map = dao.getMotCountParCategorie();

            return map == null ? Collections.EMPTY_MAP : map ;

        } catch ( DAOExeption e ) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return Collections.EMPTY_MAP ;
        }

    }
}
