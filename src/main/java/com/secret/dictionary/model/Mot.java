package com.secret.dictionary.model;

public class Mot { // C'est la class model qui permet de représenter les données et entités métier

    int id ;
    String mot ;
    String definition ;

    public int getId() {
        return id;
    }

    public String getDefinition() {
        return definition;
    }

    public String getMot() {
        return mot;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

   public Mot ( int id , String mot , String definition ) {
        this.id = id ;
        this.mot = mot ;
        this.definition = definition ;
   }
}
