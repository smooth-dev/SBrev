package com.example.reversement_assurance.model.ppdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Struct;

/**
 * This class Model for PDDDOS bloc 50
 * start index | end index | length
 * 137 141 5 * 3 F5-22 X(00005) * Identifiant de l'enregistrement *
 * 142 143 2 * 3 F2-22 X(00002) * Numéro d'occurrence du bloc *
 * 144 144 1 * 3 CDSITDOS-22 X(00001) * Code situation du dossier (voir ci-après) *
 * 145 147 3 * 3 DURCFD-D22 9(00003) * Durée restant à courir *
 * 148 150 3 * 3 NBECHE-D22 9(00003) * Nombre d'échéances échues *
 * 151 153 3 * 3 DURCDP-D22 9(00003) * Durée écoulée depuis le début du prêt *
 * 154 163 10 * 3 DTFDP-I22 X(00010) * Date de fin de dossier prévisionnelle *
 * 164 173 10 * 3 DTFDR-I22 X(00010) * Date de fin de dossier réelle
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PddosModel50 extends PddosModel {

    String codePhase;//CDSITDOS-22
    String tauxEmprunt;//TXINTC-I22
    String dateFinDossierReelle; //DTFDR-I22

    String montantCredit;//MTTDBL-I22

    String capitalRestantDu;//CRDU-I22



    public PddosModel50(String zoneGroupe) {
        this.codePhase = zoneGroupe.substring(143, 144);
        this.dateFinDossierReelle = zoneGroupe.substring(163, 173);
        this.tauxEmprunt = zoneGroupe.substring(1596,1606);
        this.montantCredit = zoneGroupe.substring(281,299);
        this.capitalRestantDu = zoneGroupe.substring(192,210);
    }
}
