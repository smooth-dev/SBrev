package com.example.reversement_assurance.model.ppdos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Builder
public class PddosModel201 {
    String date1Ech;

    public PddosModel201(String zoneDonnees) {
        date1Ech = zoneDonnees.substring(275,285);
    }
}
