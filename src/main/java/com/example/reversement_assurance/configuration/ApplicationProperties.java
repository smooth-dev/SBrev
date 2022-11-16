package com.example.reversement_assurance.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * Properites specific to Reversement Assurance Batch
 * these properties shall be configured in the application.yml file
 * <ul>
 * <li>pddosPath: the path to the PDDOS file</li>
 * <li>revassPath: the path to the REVASS file</li>
 * <li>rejectPddosPath: the path to the reject file for PDDDOS</li>
 * <li>rejectRevassPath: the path to the reject file for REVASS</li>
 * </ul>
 * @author EL Mehdi ZIDANI el-mehdi.zidani@soprabanking.com
 */
@ConfigurationProperties(prefix = "application")
@Validated
@ConstructorBinding
@Getter
@AllArgsConstructor
public class ApplicationProperties  {
    @NotBlank
    private final String pdddosPath;
    @NotBlank
    private final String revassPath;
    @NotBlank
    private final String rejectPdddosPath;
    @NotBlank
    private final String rejectRevassPath;

}
