package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.changeset.GetChangeSet;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.NoFutureDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_VAL_FUTURE_DATE;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsRoutes.*;

/**
 * ChangeSet retriever controller
 *
 * @author G. Baittiner
 */
@Tag(name = API_CHANGESET_TAG)
@Validated
public interface IChangeSetCTL {

    @GetChangeSet
    @GetMapping(API_CHANGESET_STATUS)
    ChangeSetResDTO changeSet(
        @RequestParam(value=API_QP_LAST_UPDATE, required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @NoFutureDate(message = ERR_VAL_FUTURE_DATE)
        Date lastUpdate
    ) throws OperationException;
}
