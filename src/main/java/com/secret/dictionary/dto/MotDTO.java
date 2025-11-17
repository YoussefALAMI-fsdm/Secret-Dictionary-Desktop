package com.secret.dictionary.dto;

public class MotDTO { // Data transfer object : transfert de données entre service ( Controller logique )  et UI ( ControllerFX ) .
                            // le transfer entre DTO <=> DAO ce fait dans le service ( Controller logique )
    String mot ;
    String definition ;

    public String getDefinition() {
        return definition;
    }

    public String getMot() {
        return mot;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public MotDTO( String mot , String definition ) {
        this.mot = mot ;
        this.definition = definition ;
    }
}
