package com.example.reversement_assurance.model.output_files;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="client")
public class DeclarationModelOutput {
    private String pdd0502;
    private String cre;
    private String pdd0503;
    private String pdd50;
    private String pdd12;
    private String pdd101;
    private String pdd201;
    private String pddevt;
}
