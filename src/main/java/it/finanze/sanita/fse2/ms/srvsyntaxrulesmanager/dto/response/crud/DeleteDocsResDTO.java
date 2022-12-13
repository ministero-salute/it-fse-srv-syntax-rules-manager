package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.crud;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;


@Getter
public class DeleteDocsResDTO extends ResponseDTO {

    private final int deletedItems;

    public DeleteDocsResDTO(LogTraceInfoDTO info, int deletedItems) {
        super(info);
        this.deletedItems = deletedItems;
    }
}
