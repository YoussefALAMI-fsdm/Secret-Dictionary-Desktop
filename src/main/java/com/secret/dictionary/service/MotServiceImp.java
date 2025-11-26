package com.secret.dictionary.service;

import com.secret.dictionary.dao.DAOExeption;
import com.secret.dictionary.dao.MotDAOImp;
import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.model.Mot;

import java.util.Collections;
import java.util.List;

public class MotServiceImp implements MotService { // Le controlleur logique ( fait aussi DAO <=> DTO )

    private final MotDAOImp dao ;

    public MotServiceImp(MotDAOImp dao ) {
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
            return mots != null ? mots : Collections.emptyList();
        } catch (DAOExeption e) {
            System.err.println("Probleme DAO : "+e.getMessage());
            e.getStackTrace() ;
            return Collections.emptyList() ;
        }
    }

    @Override
    public int addMot(MotDTO dto) {
        Mot m = dtoToEntity(dto);

        try {
            boolean resultat = dao.saveMot(m);

            if (resultat)  // car .save(m) return true si bien
                return 1;
            else
                return -1; // .save() return false si probleme BD sans exeption ( non capturer ) ( mot existant leve une exeptions )

        } catch (DAOExeption e) { // Si l'exeption est levé alors on test les cas ( mot deja existant , ou probleme DB )

             if ( getInfoMot(dto) != null ) // Mot existant
                 return 0 ;
             else {
                 System.err.println("Probleme DAO : " + e.getMessage());
                 e.printStackTrace();
                 return -1;
             }
        }
    }

    @Override
    public MotDTO getInfoMot(MotDTO dto) {
        try {
            Mot m = dao.findWByMot(dtoToEntity(dto));
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
            List<String> mots = dao.getListMot(mot) ;
            return mots ;
        } catch (DAOExeption e) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateMot ( MotDTO ancien , MotDTO nouveau ) {

        try {
             Mot m1 = dtoToEntity(ancien) ;
            Mot m2 = dtoToEntity(nouveau) ;
            return dao.updateMot(m1,m2) ;

        } catch ( DAOExeption e ) {
            System.err.println("Probleme DAO : " + e.getMessage());
            e.printStackTrace();
            return false ;
        }
    }
}
