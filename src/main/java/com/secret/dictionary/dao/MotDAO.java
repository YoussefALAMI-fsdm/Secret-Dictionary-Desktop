package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;
import com.secret.dictionary.util.DataBase;

public class MotDAO { // Defenir le CRUD complet ( create, findAll, findById, update, delete ) c a d permet
                     // l'acces et la communication avec la DB
                     // le transfer entre DTO <=> DAO ce fait dans le service ( Controller logique )

    private DataBase db ;

    public MotDAO(DataBase db ) {
        this.db = db ;
    }
}
