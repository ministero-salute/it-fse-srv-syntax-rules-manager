package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.crud;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;


@Getter
public class PostDocsResDTO extends ResponseDTO {

    private final int insertedItems;

    public PostDocsResDTO(LogTraceInfoDTO info, int insertedItems) {
        super(info);
        this.insertedItems = insertedItems;
    }
}
