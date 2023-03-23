package com.example.reversement_assurance.jobs.batch_context;

import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.ReverssementModel;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.LocalDate;




public class BatchContext {
    private static final Logger log = LoggerFactory.getLogger(BatchContext.class.getName());

@Setter
    @Getter
    int cumulPrimeRev = 0;

    @Getter
    int cumulPrimeDecl = 0;

    @Getter
    LocalDate dateTraitement = null;

    @Getter
    HashMap<String,String> cre06 = null;

    @Getter
    HashMap<String,String> revass = null;

    @Getter
    Table<String, String, String> pdddos = null;

    @Getter
    Table<String,String,String> pdevt = null;

    @Getter
    Table<String,String,String> pddTa = null;

    @Getter
    List<DeclarationModel> declarationModels = null;

    @Getter
    List<ReverssementModel> reverssementModels = null;

    @Getter
    HashMap<String,String> baremeAssurance = null;

    private static final BatchContext instance = new BatchContext();
    public static  BatchContext getInstance(){
        return instance;
    }
    private BatchContext() {
        init();
    }

    private void init() {
        try{
            log.info("Initializing BatchContext...");
            dateTraitement=new LocalDate();
            cre06 = new HashMap<>();
            pdddos =  HashBasedTable.create();
            revass = new HashMap<>();
            declarationModels = new ArrayList<>();
            pdevt = HashBasedTable.create();
            pddTa = HashBasedTable.create();
            reverssementModels = new ArrayList<>();
            baremeAssurance = new HashMap<>();

        }catch (Exception e){
            log.error("Error while initializing BatchContext",e);
            e.printStackTrace();
        }

    }

}
