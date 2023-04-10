package com.example.reversement_assurance.jobs.processors;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.ReverssementModel;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.reversement_assurance.jobs.batch_context.BatchConsts.*;
import static com.example.reversement_assurance.jobs.batch_context.BatchConsts.PDDDOS_BLOCK_50;

@Component("revass-map-tasklet")
@StepScope
public class RevassMapProcessor implements Tasklet {

    Logger log = LoggerFactory.getLogger(RevassMapProcessor.class);
    String currentContractNumber = null;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        Table<String, String, String> pdddos = BatchContext.getInstance().getPdddos();
        HashMap<String, String> cre = BatchContext.getInstance().getCre06();
        HashMap<String, String> revass = BatchContext.getInstance().getRevass();
        Table<String, String, String> pdevt = BatchContext.getInstance().getPdevt();
        Table<String, String, String> pddta = BatchContext.getInstance().getPddTa();


        log.info("Revass : cre size : {}, pdddos size : {}, revass size : {}", cre.size(), pdddos.size(), revass.size());

        for (Map.Entry<String, String> entry : revass.entrySet()) {
            //check if key exists in pdddos
            if (pdddos.containsRow(entry.getKey()) ) {
                // make declarationModel
                try {
                    String montant =pdddos.row(entry.getKey()).get(PDDDOS_BLOCK_50).substring(282, 298);
                    if(montant.equals("0000000000000000"))continue;
                        currentContractNumber = entry.getKey();
                    String cre06Value = entry.getValue();

                    ReverssementModel reverssementModel = new ReverssementModel();
                  //  getCreData(reverssementModel, cre06Value);
                    setCreBusinessLogic(reverssementModel);
                    getDdosData(reverssementModel, pdddos.row(entry.getKey()));
                    getPDDDOS10(reverssementModel, pdddos.row(entry.getKey()));
                    setPdddosBusinessLogic(reverssementModel);
                    getRevassData(reverssementModel, revass.get(entry.getKey()));
                    getPDDDOSBloc101(reverssementModel, pdddos.row(entry.getKey()));
                    setMiscBusinessLogic(reverssementModel);

                    if(pdevt.containsRow(entry.getKey())){
                        getPdevtData(reverssementModel, pdevt.row(entry.getKey()));

                    }
                    if(pddta.containsRow(entry.getKey())){
                        getPddTaData(reverssementModel, pddta.row(entry.getKey()));
                    }


                    reverssementModel.setContractNumber(currentContractNumber);
                    BatchContext.getInstance().getReverssementModels().add(reverssementModel);
                } catch (NullPointerException | StringIndexOutOfBoundsException e) {
                    log.error("Error while processing contract number: {} \n \t Exception name: {}", currentContractNumber, e.getClass());
                }
            } else {
                log.warn("{} not found in PDDDOS OR/AND REVASS, ignoring .... ", entry.getKey());
            }
        }


        Map<String, Map<String, String>> map = pdevt.rowMap();


        for (String row : map.keySet()) {
            Map<String, String> tmp = map.get(row);
            for (Map.Entry<String, String> pair : tmp.entrySet()) {
                if(pair.getKey().equals(PDEVT_BLOCK_00) && pdddos.containsRow(row))

                {
                    try {
                        String montant =pdddos.row(row).get(PDDDOS_BLOCK_50).substring(282, 298);
                        if(montant.equals("0000000000000000"))continue;
                        currentContractNumber = row;
                        ReverssementModel reverssementModel = new ReverssementModel();
                        setCreBusinessLogic(reverssementModel);

                        String cre06Value = cre.get(row);

                      if(cre06Value!=null) getCreData(reverssementModel, cre06Value);

                        reverssementModel.setContractNumber(currentContractNumber);

                        setCreBusinessLogic(reverssementModel);
                        getDdosData(reverssementModel, pdddos.row(currentContractNumber));
                        getPDDDOS10(reverssementModel, pdddos.row(currentContractNumber));
                        setPdddosBusinessLogic(reverssementModel);
                        getRevassData(reverssementModel, revass.get(currentContractNumber));
                        getPDDDOSBloc101(reverssementModel, pdddos.row(currentContractNumber));
                        setMiscBusinessLogic(reverssementModel);

                        if(pdevt.containsRow(currentContractNumber)){
                            getPdevtData(reverssementModel, pdevt.row(currentContractNumber));
                        }
                        if(pddta.containsRow(currentContractNumber)){
                            getDdosDataEvt(reverssementModel, pddta.row(currentContractNumber));
                        }


                        BatchContext.getInstance().getReverssementModels().add(reverssementModel);



                    } catch (NullPointerException | StringIndexOutOfBoundsException e) {
                        log.error("Error while processing contract number: {} \n \t Exception name: {}", currentContractNumber, e.getClass());
                    }
                } else {
                    log.warn("{} not found in PDDDOS OR/AND PDDEVT, ignoring .... ", currentContractNumber);
                }


            }
        }

        
        return RepeatStatus.FINISHED;
    }

    private void getPdevtData(ReverssementModel reverssementModel, Map<String, String> row) {

        if(reverssementModel.getPopulation()==null){
            String typeDossier = reverssementModel.getNumContratFiliale().substring(0,1);

            if(typeDossier.equals("C"))  reverssementModel.setPopulation("3023");
            else if(typeDossier.equals("I"))  reverssementModel.setPopulation("3022");

        }

        String evenementSameMonth =(row.get(PDEVT_BLOCK_00));
        if(evenementSameMonth!=null) {
            String codeEvenement = evenementSameMonth.substring(178, 181);
            if(codeEvenement.equals("048"))
                reverssementModel.setDureeReport(Integer.valueOf((evenementSameMonth.substring(798, 801))));
            reverssementModel.setCodePhase(mapCodephase(codeEvenement));

            reverssementModel.setCodePhase(mapCodephase(codeEvenement));
        }
        int primeAssurance=0;

        try {

               
               
//
           String montantPrime = row.get(PDEVT_BLOCK_00P).substring(570,581);

            primeAssurance=Integer.parseInt(montantPrime);
             
            reverssementModel.setPrimeAssurance(montantPrime); // on lit pas le dernier decimal pour emuler unx100
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on Block 51 \n \t Exception name: {}", currentContractNumber, e.getClass());


            reverssementModel.setPrimeAssurance("0");
        }

//        try{
//
//            String dateString = row.get(PDEVT_BLOCK_00P).substring(194,204);
//
//            reverssementModel.setDate1Ech(new LocalDate(dateString));
////            reverssementModel.setTypeTauxEmprunt(row.get(PDDDOS_BLOCK_10).substring(402,403));
//
//        }
//        catch(IllegalFieldValueException e)
//        {
//            reverssementModel.setDate1Ech(LocalDate.parse("01-01-1970",new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
//            log.error("Error while processing contract number: {} on Block 00P \n \t Exception name(date:0000-00-00): {}", currentContractNumber, e.getClass());
//        }
        int cumulDecl = BatchContext.getInstance().getCumulPrimeRev();

        BatchContext.getInstance().setCumulPrimeRev(cumulDecl+primeAssurance);

    }


    private void getPddTaData(ReverssementModel reverssementModel, Map<String, String> row) {

        try {
            String date1Ech = row.get(PDDTA_BLOCK_FIRSTMONTH).substring(136, 146);

            if(row.get(PDDTA_BLOCK_CURRENTMONTH) == null)
            {

                reverssementModel.setPrimeAssurance(row.get(PDDTA_BLOCK_FIRSTMONTH).substring(939, 950));

            }
            else {
                reverssementModel.setPrimeAssurance(row.get(PDDTA_BLOCK_CURRENTMONTH).substring(939, 950));

            }

               
            reverssementModel.setDate1Ech(new LocalDate(row.get(PDDTA_BLOCK_FIRSTMONTH).substring(136, 146)));


        } catch (NullPointerException | StringIndexOutOfBoundsException e) {

            log.error("Error while processing contract number: {} on PDDTA getter \n \t Exception name: {}", currentContractNumber, e.getClass());


            reverssementModel.setPrimeAssurance("0");
        }
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
     * @param reverssementModel
     * @param row
     */
    private void getDdosData(ReverssementModel reverssementModel, Map<String, String> row) throws NullPointerException, StringIndexOutOfBoundsException {
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 02)
        getPDDDOS05Bloc02_04(reverssementModel, row);
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 03)
        getPDDDOS05Bloc03(reverssementModel, row);
        //PDDOST-RES FONC-50
        getPDDDOS_RES_FONC_50(reverssementModel, row);
        //PPDOS BLOC 12
        getPDDDOSBloc12(reverssementModel, row);
        //PDDDOS BLOC 01
        getPDDDOSBloc01(reverssementModel, row);
        //PDDDOS BLOC 50
        getPDDDOSBloc50(reverssementModel, row);


    }


    private void getDdosDataEvt(ReverssementModel reverssementModel, Map<String, String> row) throws NullPointerException, StringIndexOutOfBoundsException {
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 02)
        getPDDDOS05Bloc02_04(reverssementModel, row);
        //PDDDOS ( Donnée Complémentaire Code Enr =05 / Bloc 03)
        getPDDDOS05Bloc03(reverssementModel, row);
        //PDDOST-RES FONC-50
        getPDDDOS_RES_FONC_50(reverssementModel, row);
        //PPDOS BLOC 12
        getPDDDOSBloc12(reverssementModel, row);
        //PDDDOS BLOC 01
      //  getPDDDOSBloc01(reverssementModel, row);
        //PDDDOS BLOC 50
        getPDDDOSBloc50(reverssementModel, row);


    }



    private void getPDDDOS05Bloc02_04(ReverssementModel reverssementModel, Map<String, String> row) {
        try {
            reverssementModel.setNumClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(263, 271).trim());

            reverssementModel.setNumCompteClient(row.get(PDDDOS_BLOCK_04).substring(244, 263));
            reverssementModel.setNomClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(233, 243));
            reverssementModel.setPrenomClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(243, 253));
            reverssementModel.setDateNaisClient(new LocalDate(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(283, 293)));
            reverssementModel.setNumCinClient(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02).substring(253, 263));
        } catch (NullPointerException | IllegalArgumentException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 02 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }

    private void getPDDDOS05Bloc03(ReverssementModel reverssementModel, Map<String, String> row) {
        try {
            reverssementModel.setAdrClient1(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03).substring(240, 270));
            reverssementModel.setAdrClient2(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03).substring(271, 301));
            //TODO add city code
            reverssementModel.setCodeVille(row.get(PDDDOS_BLOCK_03).substring(259, 262));//265
//            reverssementModel.setAdrClient1(" ");
//            reverssementModel.setCodePostal(" ");
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 05 sub Block 03 \n \t Exception name: {}", currentContractNumber, e.getMessage());
        }
        catch (NullPointerException e) {
            if(row.get(PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03) == null){
                reverssementModel.setAdrClient1("");
                log.error("Bloc 05 sub Bloc 03 is not refered for contract number: {} ,defaulted adrClient1 and CodeVille to EMPTY_STRING", currentContractNumber);
            }
            if(row.get(PDDDOS_BLOCK_03) == null){
                reverssementModel.setCodeVille("");
                log.error("Bloc 03 is not refered for contract number: {} ,defaulted CodeVille to EMPTY_STRING", currentContractNumber);
            }
        }
    }


    private void getPDDDOS_RES_FONC_50(ReverssementModel reverssementModel, Map<String, String> row) {
        try {
            reverssementModel.setCodePhase(row.get(PDDDOS_BLOCK_50).substring(143, 144));
               
            reverssementModel.setMontantCredit(new BigInteger(row.get(PDDDOS_BLOCK_50).substring(282, 298)));
            reverssementModel.setTauxEmprunt(getFormatedTauxEmprunt(row));
//            reverssementModel.setCapitalRestantDu(new BigInteger(row.get(PDDDOS_BLOCK_50).substring(1624, 1642)));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on RES-FONC-50 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }
        private Integer getFormatedTauxEmprunt(Map<String, String> row) {
        String tauxEmprunt = new BigDecimal(row.get(PDDDOS_BLOCK_50).substring(1596, 1606)).movePointLeft(4).toString();
        tauxEmprunt = tauxEmprunt.substring(0, tauxEmprunt.indexOf("."));
        return Integer.valueOf(tauxEmprunt);
    }


    private void getPDDDOSBloc12(ReverssementModel reverssementModel, Map<String, String> row) {


        try {
            reverssementModel.setModePaiement(row.get(PDDDOS_BLOCK_12).substring(354, 359).trim());
//            reverssementModel.setPrimeAssurance(new BigInteger(row.get(PDDDOS_BLOCK_12).substring(418, 436).trim()));
              
            reverssementModel.setNatureAssurance(row.get(PDDDOS_BLOCK_12_01).substring(459, 461).trim());

            System.out.println("dossierNatureassurance"+row.get(PDDDOS_BLOCK_12_01).substring(27,37).trim()+row.get(PDDDOS_BLOCK_12_01).substring(459, 461).trim());
//            reverssementModel.setTauxAssurance(new BigInteger(row.get(PDDDOS_BLOCK_12).substring(464,474).trim()));
            reverssementModel.setPourcentageEmprunt(Integer.parseInt(row.get(PDDDOS_BLOCK_12).substring(348, 351).trim()));
            //TODO find substring for reverssementModel.setTauxSurprime(Integer.parseInt(row.get(PDDDOS_BLOCK_12)));
             

//            if(row.get(PDDDOS_BLOCK_12).substring(141,143).equals("01")) {
//
//                 
                reverssementModel.setTauxSurprime(Integer.parseInt(row.get(PDDDOS_BLOCK_12_01).substring(465, 470).trim())); // 473 aulieu de 472 pour simuler le *100

        }catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 12 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
        catch (NumberFormatException e) {
//            if (row.get(PDDDOS_BLOCK_12).substring(418, 436).trim().equals("")||
//                    row.get(PDDDOS_BLOCK_12).substring(418, 436).trim().contains(" ")) {
//                reverssementModel.setPrimeAssurance(BigInteger.ZERO);
//                log.error("Prime Assurance is empty or contains spaces for contract number: {} ,defaulted to ZERO", currentContractNumber);
//            }
            if(row.get(PDDDOS_BLOCK_12).substring(464, 474).trim().equals("")||
                    row.get(PDDDOS_BLOCK_12).substring(459, 464).trim().contains(" ")){
                reverssementModel.setTauxAssurance(BigInteger.ZERO);
                log.error("Taux Assurance is empty or contains spaces for contract number: {} ,defaulted to ZERO", currentContractNumber);
            }
            if(row.get(PDDDOS_BLOCK_12).substring(348, 351).trim().equals("")||
                    row.get(PDDDOS_BLOCK_12).substring(348, 351).trim().contains(" ")){
                reverssementModel.setPourcentageEmprunt(0);
                log.error("Pourcentage Emprunt is empty or contains spaces for contract number: {} ,defaulted to ZERO", currentContractNumber);
            }

        }
    }

    private void getPDDDOSBloc01(ReverssementModel reverssementModel, Map<String, String> row) {
        try{
            reverssementModel.setDate1Ech(new LocalDate(row.get(PDDDOS_BLOCK_201).substring(275, 285)));
//            reverssementModel.setTypeTauxEmprunt(row.get(PDDDOS_BLOCK_10).substring(402,403));

        }
        catch(IllegalFieldValueException e)
        {
            reverssementModel.setDate1Ech(LocalDate.parse("01-01-1970",new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
            log.error("Error while processing contract number: {} on Block 01 \n \t Exception name(date:0000-00-00): {}", currentContractNumber, e.getClass());
        }

        catch ( StringIndexOutOfBoundsException e) {
            log.error("Error while processing contract number: {} on Block 01 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }catch (NullPointerException e) {
            if(row.get(PDDDOS_BLOCK_201) == null){
                reverssementModel.setDate1Ech(LocalDate.parse("01-01-1970",new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
                log.error("Bloc 01 is not refered for contract number: {} ,defaulted date1Ech to UNIX_TIMESTAMP_ORIGIN ", currentContractNumber);
            }else if (row.get(PDDDOS_BLOCK_10) == null){
                reverssementModel.setTypeTauxEmprunt("");
                log.error("Bloc 02 is not refered for contract number: {} ,defaulted typeTauxEmprunt to EMPTY_STRING", currentContractNumber);
            }
        }
    }

    private void getPDDDOSBloc50(ReverssementModel reverssementModel, Map<String, String> row) {
        try {
//            String dateFromPddos = row.get(PDDDOS_BLOCK_50).substring(163, 173);
//            if(dateFromPddos.contains("-00-"))             reverssementModel.setDateDerEch(LocalDate.parse("01-01-1970", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));


                if(row.get(PDDDOS_BLOCK_50).substring(143, 144).equals("5"))  reverssementModel.setDateDerEch(LocalDate.parse(row.get(PDDDOS_BLOCK_50).substring(163, 173)));

                else    reverssementModel.setDateDerEch(LocalDate.parse(row.get(PDDDOS_BLOCK_50).substring(153, 163)));


        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error Revass while processing contract number: {} on Block 50-3 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }catch (IllegalFieldValueException e){
            reverssementModel.setDateDerEch(LocalDate.parse("01-01-1970",new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
            log.error("Error Revass while processing contract number: {} on Block 50-4 , defaulted dateDerEch to UNIX_TIMESTAMP_ORIGIN\n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }



    /**
     * Get data from cre06 and set it to reverssementModel
     * <li>typeClient</li>
     * <li>numCompteClient</li>
     * <li>population</li>
     * <li>codeProduit</li>
     * <li>dateEffet</li>
     * <li>dureeSousc</li>
     *
     * @param reverssementModel declaration model
     */
    private void getCreData(ReverssementModel reverssementModel, String cre06Value) {
        try {
//            log.info("cre06Value",cre06Value);
            reverssementModel.setTypeClient(cre06Value.substring(999, 1004).trim());
            if(reverssementModel.getNumCompteClient()==null)reverssementModel.setNumCompteClient(cre06Value.substring(1541, 1575).trim());
            reverssementModel.setPopulation(cre06Value.substring(1024, 1029).trim());
            reverssementModel.setCodeProduit("0000002");
          //  reverssementModel.setDateEffet(new LocalDate(cre06Value.substring(1718, 1728).trim()));
            reverssementModel.setDureeSousc(Integer.parseInt(cre06Value.substring(1460, 1463).trim()));
        } catch (IndexOutOfBoundsException e) {
            log.error("Error while getting data from cre06Q for Dossier {} , stack {}", this.currentContractNumber, e);
        }

    }


    /**
     * Set business logic for cre06
     *
     * @param reverssementModel declaration model
     */

    private void setCreBusinessLogic(ReverssementModel reverssementModel) {
        if (Objects.equals(reverssementModel.getTypeClient(), "001") || Objects.equals(reverssementModel.getTypeClient(), "002") || Objects.equals(reverssementModel.getTypeClient(), "003") || Objects.equals(reverssementModel.getTypeClient(), "004") || Objects.equals(reverssementModel.getTypeClient(), "005")) {
            reverssementModel.setTypeClient("P");
        }
        if(Objects.equals(reverssementModel.getPopulation(),"CS")){
            reverssementModel.setPopulation("3023");
        }else if(Objects.equals(reverssementModel.getPopulation(),"IM")) {
            reverssementModel.setPopulation("3022");
        }
    }




    private void getPDDDOS10(ReverssementModel reverssementModel, Map<String, String> row) {
        try {
            String typePalier= row.get(PDDDOS_BLOCK_10).substring(248, 249);
            String montantEch= row.get(PDDDOS_BLOCK_10).substring(273, 290);
            String indicSaisi= row.get(PDDDOS_BLOCK_10).substring(143, 144);


            if(typePalier.equals("S") && montantEch.equals("00000000000000000") && indicSaisi.equals("1"))
            {

                Integer periode = Integer.parseInt(row.get(PDDDOS_BLOCK_10).substring(253, 256));
                Integer nbrTerme = Integer.parseInt(row.get(PDDDOS_BLOCK_10).substring(256, 259));

                 
                reverssementModel.setDureeDiffere(periode*nbrTerme);

            }
            else {
                reverssementModel.setDureeDiffere(0);//265
            }



        } catch (StringIndexOutOfBoundsException e) {
            reverssementModel.setDureeDiffere(0);
            log.error("Error while processing contract number: {} on Bloc 10 \n \t Exception name: {}", currentContractNumber, e.getMessage());
        } catch (NullPointerException e) {
            reverssementModel.setDureeDiffere(0);

            log.error("Bloc 10 is not refered for contract number: {} ,defaulted duree Differe  to EMPTY_STRING", currentContractNumber);

        }

    }


    private void getPDDDOSBloc101(ReverssementModel reverssementModel, Map<String, String> row) {
        try {
            if(row.get(PDDDOS_BLOCK_01).substring(861,862).equals("F")) {
                reverssementModel.setTypeTauxEmprunt("F");
            }            else {
                reverssementModel.setTypeTauxEmprunt("V");
            }
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            log.error("Error Rev while processing contract number: {} on Block 101-1 \n \t Exception name: {}", currentContractNumber, e.getClass());
        }
    }

    /**
     * set business logic for pddos
     *
     * @param reverssementModel declaration model
     */

    private void setPdddosBusinessLogic(ReverssementModel reverssementModel) {


        if ("001".equals(reverssementModel.getModePaiement()))
            reverssementModel.setModePaiement("U");
        else
            reverssementModel.setModePaiement("P");

          
          
          
        String tauxAssurance="0";
        String natureAssurance="";

       if(reverssementModel.getNatureAssurance()!=null) {
           if (reverssementModel.getNatureAssurance().length() > 0) {
               natureAssurance=reverssementModel.getNatureAssurance();
                 
               tauxAssurance = BatchContext.getInstance().getBaremeAssurance().get(reverssementModel.getNatureAssurance()).replace(",", ".");
           }
       }
        BigDecimal  tauxAssBigAnnuel= new BigDecimal(tauxAssurance);
        BigDecimal tauxAssBigMensuel=tauxAssBigAnnuel;
        if(LIST_TAUX_TO_DIVIDE.contains(natureAssurance)){
             tauxAssBigMensuel= tauxAssBigAnnuel.divide(BigDecimal.valueOf(12));
        }

        if ("P".equals(reverssementModel.getModePaiement()))
        //*1_000_000
        {
            reverssementModel.setTauxAssurance(tauxAssBigMensuel.multiply(BigDecimal.valueOf(1_000_000)).toBigInteger());

        }else if ("U".equals(reverssementModel.getModePaiement())) {
            reverssementModel.setTauxAssurance(tauxAssBigMensuel.multiply(BigDecimal.valueOf(10_000)).toBigInteger());

        }






        if( reverssementModel.getCodePhase() != null)
        switch (reverssementModel.getCodePhase()) {
            case "1":
            case "2":
                reverssementModel.setCodePhase("P300");//cf notion
                break;
            case "3":
            case "4":
                reverssementModel.setCodePhase("P117");
                break;
            case "5":
                reverssementModel.setCodePhase("P006");
                break;
            default:
                            reverssementModel.setCodePhase("P006");
                break;
        }

//     reverssementModel.setMontantCredit(reverssementModel.getMontantCredit());
        reverssementModel.setDureeReport(0);//TODO: to be implemented




    }

    /**
     * get data from revass
     * <li>numContratFiliale</li>
     * <li>modePaiement</li>
     * <li>dateEffet</li>
     * <li>dureeSousc</li>
     * <li>primeAssurance</li>
     * <li>tauxAssurance</li>
     * <li>montantCredit</li>
     * <li>CapitalRestantDu</li>
     * <li>tauxSurprime</li>
     * @param reverssementModel
     * @param revassValue
     */


    private void getRevassData(ReverssementModel reverssementModel, String revassValue) {
      reverssementModel.setNumContratFiliale(revassValue.substring(166, 201).trim().substring(1));
//        reverssementModel.setModePaiement(revassValue.substring(400, 405).trim());
        reverssementModel.setDateEffet(new LocalDate(revassValue.substring(410, 420).trim()));
        reverssementModel.setDureeSousc(Integer.parseInt(revassValue.substring(313, 316).trim()));
//        reverssementModel.setPrimeAssurance(new BigInteger(revassValue.substring(493, 511).trim()).multiply(BigInteger.valueOf(100)));


//        reverssementModel.setTauxAssurance(new BigInteger(revassValue.substring(511, 521).trim()));
        reverssementModel.setMontantCredit(new BigInteger(revassValue.substring(317, 334).trim()));
        reverssementModel.setCapitalRestantDu(new BigInteger(revassValue.substring(542, 559).trim()));
//        reverssementModel.setTauxSurprime(Integer.parseInt(revassValue.substring(465, 475).trim()));
    }

    private void setMiscBusinessLogic(ReverssementModel reverssementModel) {
        reverssementModel.setCodePays("MA");
        reverssementModel.setPeriodicite("M");
        reverssementModel.setTypeConvention("X");
        reverssementModel.setCodeRejet("  ");
        reverssementModel.setCodeReseau("ABB");
        reverssementModel.setFiler(StringUtils.leftPad("",82," "));
    }


    private String mapCodephase (   String code )
    {
        switch (code) {
            case "019":
                return "P999";
            case "015":
                return "P006";
            case "017":
                return "PCTX";
            default:
                return "P117";
        }
    }

}
