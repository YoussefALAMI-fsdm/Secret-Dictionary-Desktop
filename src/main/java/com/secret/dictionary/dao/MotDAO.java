package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

public class MotDAO { // Defenir le CRUD complet ( create, findAll, findById, update, delete ) c a d permet
                     // l'acces et la communication avec la DB
    Mot mot ;
    public MotDAO( Mot mot ) {
        this.mot = mot ;
    }
}
