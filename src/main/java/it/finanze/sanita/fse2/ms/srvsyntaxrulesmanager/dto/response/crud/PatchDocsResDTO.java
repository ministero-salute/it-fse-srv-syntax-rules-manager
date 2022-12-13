package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.crud;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;


@Getter
public class PatchDocsResDTO extends ResponseDTO {

    private final int updatedItems;

    public PatchDocsResDTO(LogTraceInfoDTO info, int updatedItems) {
        super(info);
        this.updatedItems = updatedItems;
    }
}
