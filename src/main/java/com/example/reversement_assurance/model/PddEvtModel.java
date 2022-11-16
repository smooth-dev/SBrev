package com.example.reversement_assurance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PddEvtModel {

    String typeTauxEmprunt;
    String pdd0502;
    public PddEvtModel(String zoneDonnees) {
        typeTauxEmprunt = "DEBUG";
        pdd0502 ="DEBUG";

    }
}
