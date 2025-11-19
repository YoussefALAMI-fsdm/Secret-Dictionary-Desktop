package com.secret.dictionary.service;

import com.secret.dictionary.dto.MotDTO;

import java.util.List;

public interface MotService {

    List<String> getAllMots () ; /**
                                    * @return une LinkedList<String> contenant tous les mots du dictionnaire..
                                    *  ⚠️ Si aucun mot n'est trouvé , une liste vide (jamais null) est retournée
     *                              *                                              => a verifier avec .isEmpty()
                                 */
}
