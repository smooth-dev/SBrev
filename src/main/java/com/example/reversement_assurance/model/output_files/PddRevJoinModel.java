package com.example.reversement_assurance.model.output_files;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="client")
public class PddRevJoinModel {
    private String pdd101;
    private String pdd201;
    private String pdd0502;
    private String pdd0503;
    private String pdd12;
    private String pdd50;
    private String rev;
    private String cre;

}
