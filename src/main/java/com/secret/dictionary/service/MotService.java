package com.secret.dictionary.service;

import com.secret.dictionary.dto.MotDTO;

import java.util.List;

public interface MotService {

    List<String> getAllMots () ; /**
                                    * @return une LinkedList<String> contenant tous les mots du dictionnaire..
                                    *  ⚠️ Si aucun mot n'est trouvé , une liste vide (jamais null) est retournée
                                   *                                              => a verifier avec .isEmpty()
                                 */

    int addMot ( MotDTO dto ) ;  /** il prend un MotDTO en argument ,
                                      * @return -1 ( probleme DB ) , 0 ( mot existant deja ) , 1 : ( bien )
                                     */

    MotDTO getInfoMot ( MotDTO dto ) ; /** Prend un MotDTO en arguement
                                         * @return motDTO si trouvé , sinon return null
                                        */
}
