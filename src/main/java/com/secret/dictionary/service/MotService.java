package com.secret.dictionary.service;

import com.secret.dictionary.dto.MotDTO;
import com.secret.dictionary.model.Mot;

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

    List<String> getListMot ( String mot ) ;  /**
                                               * @return une LinkedList<String> contenant tous les mots du dictionnaire..
                                                *  ⚠️ Si aucun mot n'est trouvé , une liste vide (jamais null) est retournée
                                                * => a verifier avec .isEmpty()
                                                */
    boolean updateMot ( MotDTO ancien , MotDTO nouveau ) ; /** @Return True si bien update , False sinon
                                                            * C'est a vous de verifier l'existant du mot souhaité modifier
                                                            * # Pensez a utiliser getInfoMot pour la verification
                                                           */

    int addSynonyme (MotDTO mot1 , MotDTO mot2 ) ; // @return -1 si un probleme dans la DB ,
                                                     //@ 0 si mot1 ou mot2 n'existe pas , 1 si bien ajouter

    int addAntonyme (MotDTO mot1 , MotDTO mot2 ) ; // @return -1 si un probleme dans la DB ,
                                                  //@ 0 si mot1 ou mot2 n'existe pas , 1 si bien ajouter

    List<MotDTO> getListSynonymes ( MotDTO mot ) ; // @Return null : mot inexistant a de base , list vide : Synonymes not trouvé
                                                  // @Return List<MotDTO> ( list des synonymes avec Info détaillé de chaque synonymes )


    List<MotDTO> getMotAntonymes ( MotDTO mot ) ;  // @Return null : mot inexistant a de base , list vide : Synonymes not trouvé
                                                  // @Return List<MotDTO> ( list des synonymes avec Info détaillé de chaque synonymes )



}
