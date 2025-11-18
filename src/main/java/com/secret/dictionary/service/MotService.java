package com.secret.dictionary.service;

import com.secret.dictionary.dao.MotDAO;

public class MotService { // Le controlleur logique ( fait aussi DAO <=> DTO )

    private MotDAO dao ;

    public MotService ( MotDAO dao ) {
        this.dao = dao ;
    }
}
