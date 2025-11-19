package com.secret.dictionary.dao;

public class DAOExeption extends Exception { // extends du class des Exeptions a verifier

    public DAOExeption ( String message , Throwable cause ) { // Throwable est la classe racine de toutes les erreurs et exceptions. ( on l'utilise car il peut pointer sur n'importe quel type d'erreur )
        super(message,cause);
    }
}
