package edu.kalinin.acc.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.configuration.IntegrationConfiguration;
import edu.kalinin.acc.dto.external.output.ResponseDTO;
import edu.kalinin.acc.helper.FormatHelper;
import edu.kalinin.acc.helper.ParseHelper;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.service.ResponseCodeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static edu.kalinin.acc.event.Level.INFO;
import static edu.kalinin.acc.helper.JsonHelper.toJsonSafe;
import static edu.kalinin.acc.helper.LogHelper.log;
import static edu.kalinin.acc.selector.Channel.KAFKA;

@Component
@RequiredArgsConstructor
@Getter
@Setter
@Log4j2
public class MessageEventFabric {

    public static final String STATUS_ERROR = "error";
    private final IntegrationConfiguration integrationConfiguration;

    private final ResponseCodeService responseCodeService;

    public static void observe(String receiver, String processId, Runnable action) {
        try {
            action.run();
            log(received(receiver, processId));
        } catch (DataIntegrityViolationException ex) {
            log(doubled(receiver, processId));
        } catch (RuntimeException ex) {
            log(failed(receiver, processId));
        }
    }

    public static String topic(String receiver) {
        return getTopic(receiver);
    }

    public static void createInvalidatedMessageEvent(String message, String errorMsg) {
        ParseHelper.findInJson("accountingId", message).ifPresent(accountingId ->
                ParseHelper.findInJson("moduleId", message).ifPresent(moduleId ->
                        log(invalided(removeQuotes(moduleId), removeQuotes(accountingId), message, errorMsg))));
    }

    private static BusinessEvent received(String receiver, String processId) {
        return getBusinessEvent(receiver, processId, "success", "Message has been received", asIdStr(processId));
    }

    private static BusinessEvent doubled(String receiver, String processId) {
        return getBusinessEvent(receiver, processId, STATUS_ERROR, "Message is double", asIdStr(processId));
    }

    private static BusinessEvent failed(String receiver, String processId) {
        return getBusinessEvent(receiver, processId, STATUS_ERROR, "Message receive failed", asIdStr(processId));
    }

    private static BusinessEvent invalided(String receiver, String processId, String msg, String errorMsg) {
        return getBusinessEvent(receiver, processId, STATUS_ERROR, "Message is invalid", String.format("id: %s; %s; msg: %s", processId, errorMsg, msg));
    }

    private static BusinessEvent getBusinessEvent(String receiver, String processId, String status, String message, String detail) {
        return new BusinessEvent(INFO,
                KAFKA,
                topic(receiver),
                processId,
                message,
                getResponseJson(
                        processId,
                        receiver,
                        message + " " + detail,
                        getCode(detail),
                        status));
    }

    public static String getTopic(String receiver) {
        var integrationConfiguration = SpringSupport.getContext().getBean(IntegrationConfiguration.class);
        return integrationConfiguration.getTopicOut(receiver);
    }

    private static BigDecimal getCode(String detail) {
        var responseCodeService = SpringSupport.getContext().getBean(ResponseCodeService.class);
        return responseCodeService.getCodes(detail).stream()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private static String getResponseJson(String processId, String receiver, String description, BigDecimal msgCode, String status) {
        var responseDTO = new ResponseDTO();
        responseDTO.setAccountingId(processId);
        responseDTO.setModuleId(receiver);
        if (!status.equals("success")) responseDTO.setDescription(description);
        responseDTO.setMsgCode(msgCode);
        responseDTO.setStatus(status);
        responseDTO.setMsgType("DTO received");
        responseDTO.setCreatedDate(FormatHelper.format(LocalDateTime.now()));
        return toJsonSafe(responseDTO);
    }

    private static String asIdStr(String processId) {
        return String.format("id: %s", processId);
    }

    private static String removeQuotes(String str) {
        return str.replace("\"", "");
    }
}