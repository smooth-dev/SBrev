package com.example.reversement_assurance.jobs.processors;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.DeclarationModel;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.example.reversement_assurance.jobs.batch_context.BatchConsts.*;

@Component("declaration-map-tasklet")
@StepScope
public class DeclarationMapProcessor implements Tasklet {

    Logger log = LoggerFactory.getLogger(DeclarationMapProcessor.class);

    String currentContractNumber = null;

    @Override
    public RepeatStatus execute(@NotNull StepContribution stepContribution, @NotNull ChunkContext chunkContext) throws Exception {

        Table<String, String, String> pdddos = BatchContext.getInstance().getPdddos();
        HashMap<String, String> cre = BatchContext.getInstance().getCre06();
        Table<String, String, String> pdevt = BatchContext.getInstance().getPdevt();
        log.info("cre size : {}, pdddos size : {}, pdevt size : {}", cre.size(), pdddos.size(), pdevt.size());
        List<String> contracts = new ArrayList<>();


        for (Map.Entry<String, String> entry : cre.entrySet()) {
            //check if key exists in pdddos
            if (pdddos.containsRow(entry.getKey()) && pdevt.containsRow(entry.getKey())) {
                contracts.add(entry.getKey());
            }
        }
        log.info("contracts found : {}", Arrays.toString(contracts.toArray()));

        for (Map.Entry<String, String> entry : cre.entrySet()) {
            //check if key exists in pdddos
            if (pdddos.containsRow(entry.getKey()) && pdevt.containsRow(entry.getKey())) {
                // make declarationModel

                try {
                    currentContractNumber = entry.getKey();
                    String cre06Value = entry.getValue();
                    DeclarationModel declarationModel = new DeclarationModel();
                    getCreData(declarationModel, cre06Value);
                    setCreBusinessLogic(declarationModel);
                    getDdosData(declarationModel, pdddos.row(entry.getKey()));
                    setPdddosBusinessLogic(declarationModel);
                    getPdevtData(declarationModel, pdevt.row(entry.getKey()));
                    getPdevtBusinessLogic(declarationModel);
                    setMiscBusinessLogic(declarationModel);
                    declarationModel.setContractNumber(entry.getKey());
                    BatchContext.getInstance().getDeclarationModels().add(declarationModel);
                } catch (NullPointerException | StringIndexOutOfBoundsException e) {
                    log.error("Error while processing contract number: {} \n \t Exception name: {}", currentContractNumber, e.getClass());
                }
            } else {
                log.warn("{} not found in PDDDOS OR/AND PDDEVT, ignoring .... ", entry.getKey());
            }
        }
        return RepeatStatus.FINISHED;
    }

    private void setMiscBusinessLogic(DeclarationModel declarationModel) {
        declarationModel.setCodePays("MA");
        declarationModel.setPeriodicite("M");
        declarationModel.setTypeConvention("X");
        declarationModel.setCodeRejet("  ");
        declarationModel.setCodeReseau("ABB");
        declarationModel.setFiler(StringUtils.leftPad("", 82, " "));
        //Placeholder for now until ABB decides what to do with this field
        declarationModel.setAdrClient2(" ");
        declarationModel.setCodePostal(" ");
    }


    /**
     * get data from PDDDOS
     * <li>numClient</li>
     * <li>nomClient</li>
     * <li>prenomClient</li>
     * <li>dateNaisClient</li>
     * <li>numCinClient</li>
     * <li>adrClient1</li>
     * <li>codeVille</li>
     * <li>codePhase</li>
     * <li>modePaiement</li>
     * <li>primeAssurance</li>
     * <li>tauxAssurance</li>
     * <li>montantCredit</li>
     * <li>tauxEmprunt</li>
     * <li>typeTauxEmprunt</li>
     * <li>pourcentageEmprunt</li>
     * <li>date1Ech</li>
     * <li>dateDerEch</li>
     * <li>CapitalRestantDu</li>
     * <li>tauxSurprime</li>
     *
     * @param declarationModel
     * @param row
     */
    private void getDdosData(DeclarationModel declarationModel, Map<String, String> row) throws NullPointerException, StringIndexOutOfBoundsException {
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 02)
        getPDDDOS05Bloc02(declarationModel, row);
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 03)
        getPDDDOS05Bloc03(declarationModel, row);
        //PDDOST-RES FONC-50
        getPDDDOS_RES_FONC_50(declarationModel, row);
        //PPDOS BLOC 12
        getPDDDOSBloc12(declarationModel, row);
        //PDDDOS BLOC 01
        getPDDDOSBloc01(declarationModel, row);
        //PDDDOS BLOC 50
        getPDDDOSBloc50(declarationModel, row);
    }


    private void getPDDDOS05Bloc02(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            declarationModel.setNumClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(263, 273));
            declarationModel.setNomClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(233, 243));
            declarationModel.setPrenomClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(243, 253));
            declarationModel.setDateNaisClient(new LocalDate(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(283, 293)));
            declarationModel.setNumCinClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(253, 263));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 02 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }

    private void getPDDDOS05Bloc03(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            declarationModel.setAdrClient1(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03).substring(240, 255));
            //TODO add city code
            declarationModel.setCodeVille(declarationModel.getAdrClient1().substring(0, 3));
            //
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 03 \n \t Exception name: {}", currentContractNumber, e.getMessage());
        } catch (NullPointerException e) {
            if (row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03) == null) {
                declarationModel.setAdrClient1("");
                declarationModel.setCodeVille("");
                log.error("Bloc 05 sub Bloc 03 is not refered for contract number: {} ,defaulted adrClient1 and CodeVille to EMPTY_STRING", currentContractNumber);
            }
        }
    }


    private void getPDDDOS_RES_FONC_50(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            declarationModel.setCodePhase(row.get(PDDDOS_BLOCK_50).substring(143, 144));
            declarationModel.setMontantCredit(new BigInteger(row.get(PDDDOS_BLOCK_50).substring(281, 298)));
            declarationModel.setTauxEmprunt(getFormatedTauxEmprunt(row));
            declarationModel.setCapitalRestantDu(new BigInteger(row.get(PDDDOS_BLOCK_50).substring(1624, 1642)));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on RES-FONC-50 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }

    private Integer getFormatedTauxEmprunt(Map<String, String> row) {
        String tauxEmprunt = new BigDecimal(row.get(PDDDOS_BLOCK_50).substring(1596, 1606)).movePointLeft(4).toString();
        tauxEmprunt = tauxEmprunt.substring(0, tauxEmprunt.indexOf("."));
        return Integer.valueOf(tauxEmprunt);
    }

    private void getPDDDOSBloc12(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            //    declarationModel.setPrimeAssurance(new BigInteger(row.get(PDDDOS_BLOCK_12).substring(418, 436).trim())); migrated to pdevt
            declarationModel.setModePaiement(row.get(PDDDOS_BLOCK_12).substring(354, 359).trim());
            declarationModel.setNatureAssurance(row.get(PDDDOS_BLOCK_12).substring(459, 461).trim());
            declarationModel.setTauxAssurance(new BigInteger(row.get(PDDDOS_BLOCK_12).substring(459, 461).trim()));
            declarationModel.setPourcentageEmprunt(Integer.parseInt(row.get(PDDDOS_BLOCK_12).substring(348, 351).trim()));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 12 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (NumberFormatException e) {
//            if (row.get(PDDDOS_BLOCK_12).substring(418, 436).trim().equals("")||
//            row.get(PDDDOS_BLOCK_12).substring(418, 436).trim().contains(" ")) {
//                declarationModel.setPrimeAssurance(BigInteger.ZERO);
//                log.error("Prime Assurance is empty or contains spaces for contract number: {} ,defaulted to ZERO", currentContractNumber);
//            }
            if (row.get(PDDDOS_BLOCK_12).substring(459, 464).trim().equals("") ||
                    row.get(PDDDOS_BLOCK_12).substring(459, 464).trim().contains(" ")) {
                declarationModel.setTauxAssurance(BigInteger.ZERO);
                log.error("Taux Assurance is empty or contains spaces for contract number: {} ,defaulted to ZERO", currentContractNumber);
            }
//            if(row.get(PDDDOS_BLOCK_12).substring(348, 351).trim().equals("")||
//                    row.get(PDDDOS_BLOCK_12).substring(348, 351).trim().contains(" ")){
//                declarationModel.setPourcentageEmprunt(0);
//                log.error("Pourcentage Emprunt is empty or contains spaces for contract number: {} ,defaulted to ZERO", currentContractNumber);
//            }

        }
    }

    private void getPDDDOSBloc01(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            declarationModel.setDateRealisation(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(265, 275)));
            declarationModel.setDate1Ech(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(275, 285)));
            declarationModel.setTypeTauxEmprunt(row.get(PDDDOS_BLOCK_101).substring(861, 862));
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 01 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (NullPointerException e) {
            if (row.get(PDDDOS_BLOCK_101) == null) {
                declarationModel.setDate1Ech(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
                log.error("Bloc 01 is not refered for contract number: {} ,defaulted date1Ech to UNIX_TIMESTAMP_ORIGIN ", currentContractNumber);
            } else if (row.get(PDDDOS_BLOCK_201) == null) {
                declarationModel.setTypeTauxEmprunt("");
                log.error("Bloc 02 is not refered for contract number: {} ,defaulted typeTauxEmprunt to EMPTY_STRING", currentContractNumber);
            }
        }
    }

    private void getPDDDOSBloc50(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            declarationModel.setDateDerEch(LocalDate.parse(row.get(PDDDOS_BLOCK_50).substring(163, 173)));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 50 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (IllegalFieldValueException e) {
            declarationModel.setDateDerEch(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
            log.error("Error while processing contract number: {} on Block 50 , defaulted dateDerEch to UNIX_TIMESTAMP_ORIGIN\n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }


    /**
     * Get data from cre06 and set it to declarationModel
     * <li>typeClient</li>
     * <li>numCompteClient</li>
     * <li>population</li>
     * <li>numContratFiliale</li>
     * <li>codeProduit</li>
     * <li>dateEffet</li>
     * <li>dureeSousc</li>
     *
     * @param declarationModel declaration model
     */
    private void getCreData(DeclarationModel declarationModel, String cre06Value) {
        try {
            declarationModel.setTypeClient(cre06Value.substring(999, 1004).trim());
            declarationModel.setNumCompteClient(cre06Value.substring(1541, 1575).trim());
            declarationModel.setPopulation(cre06Value.substring(1024, 1029).trim());
            declarationModel.setNumContratFiliale(cre06Value.substring(291, 325).trim());
            declarationModel.setCodeProduit("0000002");
            declarationModel.setDateEffet(new LocalDate(cre06Value.substring(1718, 1728).trim()));
            declarationModel.setDureeSousc(Integer.parseInt(cre06Value.substring(1460, 1463).trim()));
        } catch (IndexOutOfBoundsException e) {
            log.error("Error while getting data from cre06 for Dossier {} , stack {}", this.currentContractNumber, e);
        }

    }


    /**
     * Set business logic for cre06
     *
     * @param declarationModel declaration model
     */

    private void setCreBusinessLogic(DeclarationModel declarationModel) {
        if (Objects.equals(declarationModel.getTypeClient(), "001") || Objects.equals(declarationModel.getTypeClient(), "002") || Objects.equals(declarationModel.getTypeClient(), "003") || Objects.equals(declarationModel.getTypeClient(), "004") || Objects.equals(declarationModel.getTypeClient(), "005")) {
            declarationModel.setTypeClient("P");
        }

        if (Objects.equals(declarationModel.getPopulation(), "CS")) {
            declarationModel.setPopulation("3023");
        } else if (Objects.equals(declarationModel.getPopulation(), "IM")) {
            declarationModel.setPopulation("3022");
        }
    }

    /**
     * set business logic for pddos
     *
     * @param declarationModel declaration model
     */

    private void setPdddosBusinessLogic(DeclarationModel declarationModel) {
        if ("001".equals(declarationModel.getModePaiement())) declarationModel.setModePaiement("U");
        else declarationModel.setModePaiement("P");

        String taux = BatchContext.getInstance().getBaremeAssurance().get(declarationModel.getNatureAssurance()).replace(",", ".");
        if ("P".equals(declarationModel.getModePaiement()))
            //*1_000_000
            declarationModel.setTauxAssurance(new BigDecimal(taux).multiply(BigDecimal.valueOf(1_000_000)).toBigInteger());
        else if ("U".equals(declarationModel.getModePaiement()))
            declarationModel.setTauxAssurance(new BigDecimal(taux).multiply(BigDecimal.valueOf(10_000)).toBigInteger());

        declarationModel.setDureeDiffere(Math.abs(Months.monthsBetween(declarationModel.getDate1Ech(), new LocalDate()).getMonths()));

        switch (declarationModel.getCodePhase()) {
            case "1":
            case "2":
                declarationModel.setCodePhase("P300");//cf notion
                break;
            case "3":
            case "4":
                declarationModel.setCodePhase("P117");
                break;
            case "5":
                declarationModel.setCodePhase("P006");
                break;
            default:
                break;
        }
        declarationModel.setMontantCredit(declarationModel.getMontantCredit().multiply(BigInteger.valueOf(100)));
        declarationModel.setDureeReport(0);//TODO: to be implemented
    }

    private void getPdevtData(DeclarationModel declarationModel, Map<String, String> row) {
        getBloc12(declarationModel, row.get(PDEVT_BLOCK_12));
        //bloc 51
        try {
            declarationModel.setPrimeAssurance(new BigInteger(row.get(PDEVT_BLOCK_51).substring(188, 206)));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on Block 51 \n \t Exception name: {}", currentContractNumber, e.getClass());
            System.out.println("Prime(Row).."+row+"##");
            System.out.println("Prime.."+row.get(PDEVT_BLOCK_51));
            System.out.println("Prime..12"+row.get(PDEVT_BLOCK_12));

            declarationModel.setPrimeAssurance(BigInteger.valueOf(0));
        }
        //bloc 10
   /*     try {
            declarationModel.setTypeTauxEmprunt(row.get(PDEVT_BLOCK_10).substring(500, 500));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 10 \n \t Exception name: {}", currentContractNumber, e.getClass());
            declarationModel.setTypeTauxEmprunt(" ");
        }*/

    }

    private void getBloc12(DeclarationModel declarationModel, String line) {
    /*    try {
            declarationModel.setModePaiement(line.substring(321, 326).trim());
        } catch (NullPointerException e) {
            log.error("Error while processing contract number: {} missing Block 12 \n \t Exception name: {}", currentContractNumber, "NullPointerException");
            declarationModel.setModePaiement(" ");
        }*/
        declarationModel.setTauxSurprime(0);//TODO fix this with the right value
    }


    private void getPdevtBusinessLogic(DeclarationModel declarationModel) {

    }

}


