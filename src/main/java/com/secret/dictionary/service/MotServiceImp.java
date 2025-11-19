package com.secret.dictionary.service;

import com.secret.dictionary.dao.DAOExeption;
import com.secret.dictionary.dao.MotDAOImp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MotServiceImp implements MotService { // Le controlleur logique ( fait aussi DAO <=> DTO )

    private final MotDAOImp dao ;

    public MotServiceImp(MotDAOImp dao ) {
        this.dao = dao ;
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
}
