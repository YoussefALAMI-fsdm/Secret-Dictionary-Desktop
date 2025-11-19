package com.secret.dictionary.dao;

import com.secret.dictionary.model.Mot;

import java.util.List;

public interface MotDAO {

    List<String> findAllMot () throws DAOExeption ;
}
