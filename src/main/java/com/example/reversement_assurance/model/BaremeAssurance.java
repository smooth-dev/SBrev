package com.example.reversement_assurance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaremeAssurance {
String numeroAssurance;
String codeTableParametre;
String codeBaremeAssurance;
String filler;
String dateDePriseEffet;
String libelleDuBareme;
String tauxDePerception;
String indComplementTauxSelonAge;
String ageDeReference;
String sensDuComplementDeTaux;
String complementDeTaux;
String modeSaisieDuTaux;
String codeDateReference;
String indicateurDeGestionDeTranche;
String codeBaseExpressionDesTranches;
String indicateurDeValidite;
}
