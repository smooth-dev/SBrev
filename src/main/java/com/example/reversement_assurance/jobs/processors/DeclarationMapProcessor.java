package com.example.reversement_assurance.jobs.processors;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.ReverssementModel;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
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
import java.math.RoundingMode;
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
            Table<String, String, String> pddta = BatchContext.getInstance().getPddTa();

        log.info("cre size : {}, pdddos size : {}, pdevt size : {}, pddta sizeee : {}", cre.size(), pdddos.size(), pdevt.size(), pddta.size());
        //    


         

        List<String> contracts = new ArrayList<>();


        for (Map.Entry<String, String> entry : cre.entrySet()) {
            //check if key exists in pdddos
            if (pdddos.containsRow(entry.getKey()) && pdevt.containsRow(entry.getKey())) {
                contracts.add(entry.getKey());
            }
        }
        log.info("contracts found : {}", Arrays.toString(contracts.toArray()));



        // ###### this first loop reads "deblocage" events (06)
         for (Map.Entry<String, String> entry : cre.entrySet()) {

             
            //check if key exists in pdddos
             //entry.getKey () = NUM DOSSIER
            if (pdddos.containsRow(entry.getKey()) && pdevt.containsRow(entry.getKey())) {
                // make declarationModel

                 


                try {
                    currentContractNumber = entry.getKey();
                  if(currentContractNumber.contains("E")) continue;
                    String cre06Value = entry.getValue();
                    DeclarationModel declarationModel = new DeclarationModel();
                    if(cre06Value!=null) getCreData(declarationModel, cre06Value);

                    setCreBusinessLogic(declarationModel);

                    getDdosData(declarationModel, pdddos.row(entry.getKey()));
                    setPdddosBusinessLogic(declarationModel,currentContractNumber);
                    boolean isEvtModif117= getPdevtData(declarationModel, pdevt.row(entry.getKey()));
                    System.out.println("DEBUG3105CHECK"+isEvtModif117);
                    if(isEvtModif117)  continue;
                    getPrimeForDeblocageData(declarationModel, pdevt.row(entry.getKey()));
                    if(pddta.containsRow(currentContractNumber)){
                        getPddTaData(declarationModel, pddta.row(currentContractNumber));
                    }

                    getPdevtBusinessLogic(declarationModel);
                    setMiscBusinessLogic(declarationModel);
                    declarationModel.setContractNumber(entry.getKey());
                    BatchContext.getInstance().getDeclarationModels().add(declarationModel);
                } catch (NullPointerException | StringIndexOutOfBoundsException e) {
                    log.error("credebug{} Error while processing contract number: {}  \n \t Exception name: {}", currentContractNumber,currentContractNumber, e.getClass());
                }
            } else {
                log.warn("{} not found in PDDDOS OR/AND PDDEVT, ignoring .... ", entry.getKey());
            }
        }


        // ###### this first loop reads "deblocage" events (015,019,016...)

        Map<String, Map<String, String>> map = pdevt.rowMap();


        for (String row : map.keySet()) {

            // check if row is already inserted
            Map<String, String> tmp = map.get(row);

           if(row.contains("E")) continue;

             


            for (Map.Entry<String, String> pair : tmp.entrySet()) {
                 

                if(pair.getKey().startsWith(PDEVT_BLOCK_EVT) )

                {

                    try {

// permet de prendre la dernierer mise a jour des montant du pddevt pour un dossier donnée
//                        BatchContext.getInstance().getDeclarationModels().removeIf(d -> d.getContractNumber().equals(row));

//                        if (exists) {
//                             

                        currentContractNumber = row;


                        DeclarationModel declarationModel = new DeclarationModel();
                        String cre06Value = cre.get(row);

                        if (cre06Value != null) getCreData(declarationModel, cre06Value);


                        setCreBusinessLogicEvt(declarationModel, String.valueOf(currentContractNumber.charAt(3)));

                        getDdosDataEvt(declarationModel, pdddos.row(currentContractNumber));

                        getPDDDOS03And05Bloc03Evt(declarationModel, pdddos.row(currentContractNumber));
                          setPdddosBusinessLogic(declarationModel,currentContractNumber);


                        if (pdevt.containsRow(currentContractNumber)) {
                            boolean isEvtModif117= getPdevtDataEVT(declarationModel, pdevt.row(currentContractNumber),pair.getKey());
                            System.out.println("DEBUG3105CHECK"+isEvtModif117);
                            if(isEvtModif117)  continue;

            //               getPddTaData(declarationModel, pddta.row(currentContractNumber));

                        }

                        if(pddta.containsRow(currentContractNumber)){

                       getPddTaData(declarationModel, pddta.row(currentContractNumber));
                    }

                        getPdevtBusinessLogic(declarationModel);
                         

                        setMiscBusinessLogic(declarationModel);
                        declarationModel.setContractNumber(currentContractNumber);

                         
                         

                        BatchContext.getInstance().getDeclarationModels().add(declarationModel);
//                        declarationModel.setContractNumber("eede5465");
//                        BatchContext.getInstance().getDeclarationModels().add(declarationModel);


                         




                        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
                        log.error("Error while processing contract number: {} \n \t Exception name: {}", currentContractNumber, e.getClass());
                    }
                }  else {
                    log.warn("{} not11 found in PDDDOS OR/AND PDDEVT, ignoring .... ", currentContractNumber);
                }




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
//        declarationModel.setAdrClient2(" ");
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
        getPDDDOS03And05Bloc03(declarationModel, row);
        //PDDOST-RES FONC-50
        getPDDDOS_RES_FONC_50(declarationModel, row);
        //PPDOS BLOC 12
        getPDDDOSBloc12(declarationModel, row);
        //PDDDOS BLOC 01
        getPDDDOSBloc01(declarationModel, row);
        //PDDDOS BLOC 50
        getPDDDOSBloc50(declarationModel, row);


        getPDDDOS10(declarationModel, row);
        getPDDDOSBloc101(declarationModel, row);

    }


    private void getPDDDOS05Bloc02(DeclarationModel declarationModel, Map<String, String> row) {
        try {

             
            declarationModel.setNumClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(263, 271).trim());
            declarationModel.setNomClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(233, 243));
            declarationModel.setPrenomClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(243, 253));
            declarationModel.setNumCinClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(253, 263));
        } catch (NullPointerException | StringIndexOutOfBoundsException  e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 02 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }

        try {
            declarationModel.setDateNaisClient(new LocalDate(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(283, 293)));
        }catch (IllegalArgumentException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 02 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }


    private void getDdosDataEvt(DeclarationModel declarationModel, Map<String, String> row) throws NullPointerException, StringIndexOutOfBoundsException {
        //PDDDOS ( Donnée Complémentaire Code Enr =04 / Bloc 01)
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 02)
        getPDDDOS05Bloc02(declarationModel, row);
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 03)
        getPDDDOS03And05Bloc03(declarationModel, row);
        //PDDOST-RES FONC-50
        getPDDDOS_RES_FONC_50(declarationModel, row);

        System.out.println("123DEBdUG"+row.get((PDDDOS_BLOCK_50)));
        System.out.println("123DEBssdUG"+row.get((PDDDOS_BLOCK_50)).substring(27,37));
        //PPDOS BLOC 12
        getPDDDOSBloc12(declarationModel, row);
        //PDDDOS BLOC 01
        getPDDDOSBloc01Evt(declarationModel, row);
        //PDDDOS BLOC 50
        getPDDDOSBloc50(declarationModel, row);


        getPDDDOS10(declarationModel, row);
        getPDDDOSBloc101(declarationModel, row);

        getPDDDOSBloc0401(declarationModel, row);




    }


    private void getPDDDOSBloc0401(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            if(row.get(PDDDOS_BLOCK_04).substring(131,138).equals(PDDDOS_BLOCK_04_01))  

            declarationModel.setNumCompteClient(row.get(PDDDOS_BLOCK_04).substring(244,253));

        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error Decl while processing contract number: {} on Block 101-1 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }


    private void getPDDDOS10(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            String typePalier= row.get(PDDDOS_BLOCK_10).substring(248, 249);
            String montantEch= row.get(PDDDOS_BLOCK_10).substring(273, 290);
            String indicSaisi= row.get(PDDDOS_BLOCK_10).substring(143, 144);


            if(typePalier.equals("S") && montantEch.equals("00000000000000000") && indicSaisi.equals("1"))
            {

                Integer periode = Integer.parseInt(row.get(PDDDOS_BLOCK_10).substring(253, 256));
                Integer nbrTerme = Integer.parseInt(row.get(PDDDOS_BLOCK_10).substring(256, 259));

                declarationModel.setDureeDiffere(periode*nbrTerme);

            }
            else {
                declarationModel.setDureeDiffere(0);//265
            }



        } catch (StringIndexOutOfBoundsException e) {
            declarationModel.setDureeDiffere(0);
            log.error("Error while processing contract number: {} on Bloc 10 \n \t Exception name: {}", currentContractNumber, e.getMessage());
        } catch (NullPointerException e) {
            declarationModel.setDureeDiffere(0);

                log.error("Bloc 10 is not refered for contract number: {} ,defaulted duree Differe  to EMPTY_STRING", currentContractNumber);

        }

    }


    private void getPDDDOS03And05Bloc03Evt(DeclarationModel declarationModel, Map<String, String> row) {
        try {

            declarationModel.setCodeVille(row.get(PDDDOS_BLOCK_03).substring(259, 262));
             
            //265
            //
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 03 \n \t Exception name: {}", currentContractNumber, e.getMessage());
        }
    }
    private void getPDDDOS03And05Bloc03(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            declarationModel.setAdrClient1(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03).substring(240, 270));
            declarationModel.setAdrClient2(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03).substring(271, 301));
            //TODO add city code
            declarationModel.setCodeVille(row.get(PDDDOS_BLOCK_03).substring(259, 262));//265
            //
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 03 \n \t Exception name: {}", currentContractNumber, e.getMessage());
        } catch (NullPointerException e) {
            if (row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03) == null) {
                declarationModel.setAdrClient1("");

                log.error("Bloc 05 sub Bloc 03 is not refered for contract number: {} ,defaulted adrClient1  to EMPTY_STRING", currentContractNumber);
            }
            if (row.get(PDDDOS_BLOCK_03) == null) {
                declarationModel.setCodeVille("");

                log.error("Bloc 03 is not refered for contract number: {} ,defaulted  CodeVille to EMPTY_STRING", currentContractNumber);
            }
        }
    }


    private void getPDDDOS_RES_FONC_50(DeclarationModel declarationModel, Map<String, String> row) {

       //     
        try {
//            declarationModel.setCodePhase(row.get(PDDDOS_BLOCK_50).substring(143, 144));

            switch (row.get(PDDDOS_BLOCK_50).substring(143, 144)) {
                case "1":
                case "2":
                    declarationModel.setCodePhase("P300");//cf notion
                    break;
                case "3":
                case "4":
                    declarationModel.setCodePhase("P777");
                    break;
                case "5":
                    declarationModel.setCodePhase("P006");
                    break;
                default:
                    break;
            }


             //    
            System.out.println("DEBUG3105MONTANTdoss"+row.get(PDDDOS_BLOCK_50).substring(27, 37)+"#");
            System.out.println("DEBUG3105MONTANT"+row.get(PDDDOS_BLOCK_50).substring(282, 298)+"#");
//            declarationModel.setMontantCredit(new BigInteger(row.get(PDDDOS_BLOCK_50).substring(282, 298)));
            declarationModel.setTauxEmprunt(getFormatedTauxEmprunt(row));
             


            declarationModel.setCapitalRestantDu(new BigInteger(row.get(PDDDOS_BLOCK_50).substring(193, 209)));
            System.out.println("123DEBUG"+row.get(PDDDOS_BLOCK_50).substring(193, 209)+"//"+row.get(PDDDOS_BLOCK_50).substring(27, 37));
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
               
            declarationModel.setModePaiement(row.get(PDDDOS_BLOCK_12).substring(354, 357).trim().equals("001")?"U":"P");
//            if ("001".equals(declarationModel.getModePaiement())) declarationModel.setModePaiement("U");
//            else declarationModel.setModePaiement("P");
            declarationModel.setNatureAssurance(row.get(PDDDOS_BLOCK_12_01).substring(459, 461).trim());
               
            declarationModel.setPourcentageEmprunt(Integer.parseInt(row.get(PDDDOS_BLOCK_12_01).substring(341, 344).trim()));

               declarationModel.setTauxSurprime(Integer.parseInt(row.get(PDDDOS_BLOCK_12_01).substring(465, 470).trim())); // 473 aulieu de 472 pour simuler le *100
               

        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 12 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (NumberFormatException e) {

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


        try{
            declarationModel.setTauxAssurance(new BigInteger(row.get(PDDDOS_BLOCK_12).substring(459, 461).trim()));
        }catch(Exception e){

            log.error("Error while processing contract number: {} on Block 12 Taux Assurance \n \t Exception name: {}", currentContractNumber, e.getClass());

        }
    }

    private void getPDDDOSBloc01(DeclarationModel declarationModel, Map<String, String> row) {
        String dateRealisation= row.get(PDDDOS_BLOCK_201).substring(265, 275);
        String date1Ech= row.get(PDDDOS_BLOCK_201).substring(265, 275);
        String dateEffet= row.get(PDDDOS_BLOCK_201).substring(265, 275);

        System.out.println("DEBUG3105MONTeANTdoss"+row.get(PDDDOS_BLOCK_01).substring(27, 37)+"#");
        System.out.println("DEBUG3105MONTeANT"+row.get(PDDDOS_BLOCK_01).substring(432, 448)+"#");

        System.out.println("checkMontant1DEBUG0106 XXX"+declarationModel.getDureeSousc());

        if(declarationModel.getDureeSousc()==null)
            declarationModel.setDureeSousc(Integer.parseInt(row.get(PDDDOS_BLOCK_01).substring(623, 626)));

        if(declarationModel.getMontantCredit()==null) declarationModel.setMontantCredit(new BigInteger(row.get(PDDDOS_BLOCK_01).substring(432, 448)));



        try {



            if(!dateRealisation.equals(0000-00-00))declarationModel.setDateRealisation(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(265, 275)));
            if(!dateEffet.equals(0000-00-00))declarationModel.setDateEffet(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(265, 275)));
            declarationModel.setDate1Ech(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(275, 285)));

        }catch (IllegalFieldValueException e){
            if(date1Ech.equals(0000-00-00))  declarationModel.setDate1Ech(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
            if(dateRealisation.equals(0000-00-00)) declarationModel.setDateRealisation(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));

        }
        catch (StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 01 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (NullPointerException e) {
            if (row.get(PDDDOS_BLOCK_01) == null) {
                declarationModel.setDate1Ech(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
                log.error("Bloc 01 is not refered for contract number: {} ,defaulted date1Ech to UNIX_TIMESTAMP_ORIGIN ", currentContractNumber);
            } else if (row.get(PDDDOS_BLOCK_201) == null) {
//                declarationModel.setTypeTauxEmprunt("");
                log.error("Bloc 02 is not refered for contract number: {} ,defaulted typeTauxEmprunt to EMPTY_STRING", currentContractNumber);
            }
        }
    }



    private void getPDDDOSBloc01Evt(DeclarationModel declarationModel, Map<String, String> row) {
        String dateRealisation= row.get(PDDDOS_BLOCK_201).substring(265, 275);
        String date1Ech= row.get(PDDDOS_BLOCK_201).substring(265, 275);
        String dateEffet= row.get(PDDDOS_BLOCK_201).substring(265, 275);
        System.out.println("checkMontant1DEBUG0106 XXX");

        if(declarationModel.getDureeSousc()==null)
            declarationModel.setDureeSousc(Integer.parseInt(row.get(PDDDOS_BLOCK_01).substring(623, 626)));

        if(declarationModel.getMontantCredit()==null) declarationModel.setMontantCredit(new BigInteger(row.get(PDDDOS_BLOCK_01).substring(432, 448)));


        try {



            if(!dateRealisation.equals(0000-00-00))declarationModel.setDateRealisation(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(265, 275)));
            if(!dateEffet.equals(0000-00-00))declarationModel.setDateEffet(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(265, 275)));
            declarationModel.setDate1Ech(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(275, 285)));

        }catch (IllegalFieldValueException e){
            if(date1Ech.equals(0000-00-00))  declarationModel.setDate1Ech(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
            if(dateRealisation.equals(0000-00-00)) declarationModel.setDateRealisation(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));

        }
        catch (StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 01 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (NullPointerException e) {
            if (row.get(PDDDOS_BLOCK_01) == null) {
                declarationModel.setDate1Ech(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
                log.error("Bloc 01 is not refered for contract number: {} ,defaulted date1Ech to UNIX_TIMESTAMP_ORIGIN ", currentContractNumber);
            } else if (row.get(PDDDOS_BLOCK_201) == null) {
//                declarationModel.setTypeTauxEmprunt("");
                log.error("Bloc 02 is not refered for contract number: {} ,defaulted typeTauxEmprunt to EMPTY_STRING", currentContractNumber);
            }
        }
    }

    private void getPDDDOSBloc101(DeclarationModel declarationModel, Map<String, String> row) {
        try {
            System.out.println("checkMontant1DEBUG0106 XXX");

            if(declarationModel.getDureeSousc()==null)
                declarationModel.setDureeSousc(Integer.parseInt(row.get(PDDDOS_BLOCK_01).substring(623, 626)));

            if(declarationModel.getMontantCredit()==null) declarationModel.setMontantCredit(new BigInteger(row.get(PDDDOS_BLOCK_01).substring(432, 448)));


            if(row.get(PDDDOS_BLOCK_01).substring(861,862).equals("F")) {
                declarationModel.setTypeTauxEmprunt("F");

            }            else {
                declarationModel.setTypeTauxEmprunt("V");

            }

        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error Decl while processing contract number: {} on Block 101-1 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }


    private void getPDDDOSBloc50(DeclarationModel declarationModel, Map<String, String> row) {
        try {


             
             
            if(row.get(PDDDOS_BLOCK_50).substring(143, 144).equals("5"))
            {
                declarationModel.setDateDerEch(LocalDate.parse(row.get(PDDDOS_BLOCK_50).substring(163, 173)));

            }
            else
            {
                declarationModel.setDateDerEch(LocalDate.parse(row.get(PDDDOS_BLOCK_50).substring(153, 163)));
            }



        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error Decl while processing contract number: {} on Block 50-1 \n \t Exception name: {}", currentContractNumber, e.getClass());
        } catch (IllegalFieldValueException e) {
            declarationModel.setDateDerEch(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
            log.error("Error Decl while processing contract number: {} on Block 50-2 , defaulted dateDerEch to UNIX_TIMESTAMP_ORIGIN\n \t Exception name: {}", currentContractNumber, e.getClass());
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
//            declarationModel.setCodeProduit("0000002");
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


    private void setCreBusinessLogicEvt(DeclarationModel declarationModel,String type) {
         
        if (Objects.equals(declarationModel.getTypeClient(), "001") || Objects.equals(declarationModel.getTypeClient(), "002") || Objects.equals(declarationModel.getTypeClient(), "003") || Objects.equals(declarationModel.getTypeClient(), "004") || Objects.equals(declarationModel.getTypeClient(), "005")) {
            declarationModel.setTypeClient("P");
        }

        if (Objects.equals(type, "C")) {
            declarationModel.setPopulation("3023");
        } else if (Objects.equals(type, "I")) {
            declarationModel.setPopulation("3022");
        }
    }

    private void setPdddosBusinessLogic(DeclarationModel declarationModel,String currentContractNumber) {
         
//        if ("001".equals(declarationModel.getModePaiement())) declarationModel.setModePaiement("U");
//        else declarationModel.setModePaiement("P");

        String tauxAssurance="0";
        String natureAssurance="";

        if(declarationModel.getNatureAssurance()!=null) {
            if (declarationModel.getNatureAssurance().length() > 0) {
                natureAssurance=declarationModel.getNatureAssurance();
                 
                tauxAssurance = BatchContext.getInstance().getBaremeAssurance().get(declarationModel.getNatureAssurance()).replace(",", ".");
                 

            }
        }

         
        BigDecimal  tauxAssBigAnnuel= new BigDecimal(tauxAssurance);
        BigDecimal tauxAssBigMensuel=tauxAssBigAnnuel;
        if(LIST_TAUX_TO_DIVIDE.contains(natureAssurance)){
            tauxAssBigMensuel= tauxAssBigAnnuel.divide(BigDecimal.valueOf(12));
        }

         

         

        if ("P".equals(declarationModel.getModePaiement()))
            //*1_000_000
        {
//            if(natureAssurance.equals("04"))
            declarationModel.setTauxAssurance(tauxAssBigMensuel.multiply(BigDecimal.valueOf(1_000_000)).toBigInteger());  //added x100 on top do avoid 0 on the right
        }else if ("U".equals(declarationModel.getModePaiement())) {
            declarationModel.setTauxAssurance(tauxAssBigMensuel.multiply(BigDecimal.valueOf(10_000)).toBigInteger());

        }

         
         






//        declarationModel.setDureeDiffere(Math.abs(Months.monthsBetween(declarationModel.getDate1Ech(), new LocalDate()).getMonths()));

         
        switch (declarationModel.getCodePhase()) {
            case "1":
            case "2":
                declarationModel.setCodePhase("P300");//cf notion
                break;
            case "3":
            case "4":
                declarationModel.setCodePhase("P777");
                break;
            case "5":
                declarationModel.setCodePhase("P006");
                break;
            default:
                break;
        }
         
//        declarationModel.setMontantCredit(declarationModel.getMontantCredit().multiply(BigInteger.valueOf(10)));
         
        declarationModel.setDureeReport(0);//TODO: to be implemented
    }

    public static BigDecimal montant12ToBigDecimal(String montant) {
        StringBuffer str = new StringBuffer(montant);
        str.insert(14, '.');
        return new BigDecimal(str.toString());
    }





    private void getPddTaData(DeclarationModel declarationModel, Map<String, String> row) {

           
        try {

//               

            if(row.get(PDDTA_BLOCK_CURRENTMONTH) == null)
            {
                   

                   
                declarationModel.setPrimeAssurance(row.get(PDDTA_BLOCK_FIRSTMONTH).substring(939, 950));
                   
                   

            }
            else {
                   
                declarationModel.setPrimeAssurance(row.get(PDDTA_BLOCK_CURRENTMONTH).substring(939, 950));
                   
                   

            }


            declarationModel.setPrimeAssurance(row.get(PDDTA_BLOCK_FIRSTMONTH).substring(939, 950));

//               
            declarationModel.setDate1Ech(new LocalDate(row.get(PDDTA_BLOCK_FIRSTMONTH).substring(136, 146)));


        } catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on PDDTA getter \n \t Exception name: {}", currentContractNumber, e.getClass());


            declarationModel.setPrimeAssurance("0");
        }
    }


    private void getPrimeForDeblocageData(DeclarationModel declarationModel, Map<String, String> row) {
        try{
            declarationModel.setPrimeAssurance(row.get(PDEVT_BLOCK_8001_AT).substring(194,205));

        }
        catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on Block prime Assurance Setter \n \t Exception name: {}", currentContractNumber, e.getClass());


            declarationModel.setPrimeAssurance("0");
        }
    }



    // retriever specifique pour les dossiers avec plusieurs evenements
    private boolean getPdevtDataEVT(DeclarationModel declarationModel, Map<String, String> row,String block) {

        String evenementSameMonth =(row.get(block));





        if(evenementSameMonth!=null) {
            String codeEvenementLS = evenementSameMonth.substring(178, 181);
            if(codeEvenementLS.equals("048"))
                declarationModel.setDureeReport(Integer.valueOf((evenementSameMonth.substring(798, 801))));
            String codevenementABB= mapCodephase(codeEvenementLS);
            if(codevenementABB=="P117") return true;
            declarationModel.setCodePhase(codevenementABB);


            declarationModel.setNumContratFiliale(evenementSameMonth.substring(27, 38).trim());

        }
        int primeAssurance=0;
        try {



            String montantPrime = row.get(PDEVT_BLOCK_00PDEBL).substring(570,581);



            String dateString=row.get(PDEVT_BLOCK_00P).substring(194,204);
//            declarationModel.setDate1Ech(LocalDate.parse(dateString));

            primeAssurance=Integer.parseInt(montantPrime);

            declarationModel.setPrimeAssurance(montantPrime);
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on Block 51 \n \t Exception name: {}", currentContractNumber, e.getClass());


            declarationModel.setPrimeAssurance("0");
        }
        return false;

//        int cumulDecl = BatchContext.getInstance().getCumulPrimeDecl();
//
//        cumulDecl+=primeAssurance;



    }



    private boolean getPdevtData(DeclarationModel declarationModel, Map<String, String> row) {

        System.out.println("DEBUG3105ROW:"+row);
        String evenementSameMonth =(row.get(PDEVT_BLOCK_EVT));






        if(evenementSameMonth!=null) {
            String codeEvenementLS = evenementSameMonth.substring(178, 181);
            if(codeEvenementLS.equals("048"))
                    declarationModel.setDureeReport(Integer.valueOf((evenementSameMonth.substring(798, 801))));

            String codevenementABB= mapCodephase(codeEvenementLS);
            if(codevenementABB=="P117") return true;
            declarationModel.setCodePhase(codevenementABB);


            declarationModel.setNumContratFiliale(evenementSameMonth.substring(27, 38).trim());

        }
        int primeAssurance=0;
        try {


             
            String montantPrime = row.get(PDEVT_BLOCK_00PDEBL).substring(570,581);
               

               
            String dateString=row.get(PDEVT_BLOCK_00P).substring(194,204);
//            declarationModel.setDate1Ech(LocalDate.parse(dateString));

            primeAssurance=Integer.parseInt(montantPrime);

            declarationModel.setPrimeAssurance(montantPrime);
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on Block 51 \n \t Exception name: {}", currentContractNumber, e.getClass());


            declarationModel.setPrimeAssurance("0");
        }
        return false;

//        int cumulDecl = BatchContext.getInstance().getCumulPrimeDecl();
//
//        cumulDecl+=primeAssurance;



    }

    private void getBloc12(DeclarationModel declarationModel, String line) {
    /*    try {
            declarationModel.setModePaiement(line.substring(321, 326).trim());
        } catch (NullPointerException e) {
            log.error("Error while processing contract number: {} missing Block 12 \n \t Exception name: {}", currentContractNumber, "NullPointerException");
            declarationModel.setModePaiement(" ");
        }*/
//        declarationModel.setTauxSurprime(0);//TODO fix this with the right value
    }


    private void getPdevtBusinessLogic(DeclarationModel declarationModel) {

    }


    private String mapCodephase (   String code )
    {
        switch (code) {
            case "019":
                return "P999";
            case "015":
            case "016":
                return "P006";
            case "017":
                return "PCTX";
            default:
                return "P117";
        }
    }

}


