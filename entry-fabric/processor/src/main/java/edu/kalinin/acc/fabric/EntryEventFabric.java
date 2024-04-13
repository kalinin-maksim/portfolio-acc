package edu.kalinin.acc.fabric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.configuration.IntegrationConfiguration;
import edu.kalinin.acc.dto.external.output.ResponseDTO;
import edu.kalinin.acc.event.BusinessEvent;
import edu.kalinin.acc.event.Level;
import edu.kalinin.acc.helper.FormatHelper;
import edu.kalinin.acc.helper.Observer;
import edu.kalinin.acc.helper.SpringSupport;

import java.time.LocalDateTime;

import static edu.kalinin.acc.event.Level.INFO;
import static edu.kalinin.acc.event.Level.WARN;
import static edu.kalinin.acc.helper.JsonHelper.toJsonSafe;
import static edu.kalinin.acc.helper.LogHelper.log;
import static edu.kalinin.acc.selector.Channel.KAFKA;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class EntryEventFabric {

    private final IntegrationConfiguration integrationConfiguration;

    public static Observer<String> observer(String receiver, String processId) {
        return new Observer<>(
                id -> log(entryCreated(receiver, processId, id)),
                ex -> log(entryCreateFailed(receiver, processId)));
    }

    public static String topic(String receiver) {
        var fabric = SpringSupport.getContext().getBean(EntryEventFabric.class);
        return fabric.getTopic(receiver);
    }

    public String getTopic(String receiver) {
        return integrationConfiguration.getTopicOut(receiver);
    }

    private static BusinessEvent entryCreated(String receiver, String processId, String id) {
        return getBusinessEvent(processId, receiver, String.format("Entry has been created id: %s", id), INFO, "success", "DTO processed");
    }

    private static BusinessEvent entryCreateFailed(String receiver, String processId) {
        return getBusinessEvent(processId, receiver, String.format("Entry creation failed process id: %s", processId), WARN, "error", "DTO failed");
    }

    private static BusinessEvent getBusinessEvent(String processId, String receiver, String msgTemp, Level level, String status, String msgType) {
        return new BusinessEvent(
                level,
                KAFKA,
                topic(receiver),
                processId,
                String.format(msgTemp, processId),
                getResponseJson(
                        processId,
                        receiver,
                        msgTemp,
                        status, msgType));
    }

    private static String getResponseJson(String processId, String receiver, String description, String status, String msgType) {
        var responseDTO = new ResponseDTO();
        responseDTO.setAccountingId(processId);
        responseDTO.setModuleId(receiver);
        if (!status.equals("success")) responseDTO.setDescription(description);
        responseDTO.setStatus(status);
        responseDTO.setMsgType(msgType);
        responseDTO.setCreatedDate(FormatHelper.format(LocalDateTime.now()));
        return toJsonSafe(responseDTO);
    }
}