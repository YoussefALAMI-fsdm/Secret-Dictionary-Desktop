package com.secret.dictionary.model;

public class Mot { // C'est la class model qui permet de représenter les données et entités métier

    int id ;
    String mot ;
    String definition ;
    String categorie ;
    String emojie ;

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

   public Mot ( int id , String mot , String definition , String categorie , String emojie ) {
        this.id = id ;
        this.mot = mot ;
        this.definition = definition ;
        this.categorie = categorie ;
        this.emojie = emojie ;
   }

    public String getCategorie() {
        return categorie;
    }

    public String getEmojie() {
        return emojie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setEmojie(String emojie) {
        this.emojie = emojie;
    }
}
