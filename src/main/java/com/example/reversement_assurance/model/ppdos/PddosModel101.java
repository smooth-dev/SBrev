package com.example.reversement_assurance.model.ppdos;

import lombok.*;

/**
 * Class representing Bloc 01 of PDDOS:
 * @author ZIDANI El Mehdi
 */
@Data
@NoArgsConstructor
@ToString
@Builder
public class PddosModel101 {

    String typeTauxEmprunt;


    public PddosModel101(String zoneDonnees) {
        typeTauxEmprunt = zoneDonnees.substring(861,862);

    }

}

