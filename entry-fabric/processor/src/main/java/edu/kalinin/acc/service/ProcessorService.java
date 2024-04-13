package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.api.BalanceApi;

import java.util.List;

import static edu.kalinin.acc.helper.ProcessHelper.failSaveProcess;

@Component
@RequiredArgsConstructor
@Log4j2
public class ProcessorService {

    private final BalanceApi balanceApi;
    private final List<GenericProcessorService> genericProcessorServices;

    public void processNewMessages() {
        failSaveProcess(genericProcessorServices,
                GenericProcessorService::processNewMessages,
                (service, exception) -> log.warn("message process cause error ", exception));
        balanceApi.createPostingAndBalance();
    }

    public void processOldMessages() {
        failSaveProcess(genericProcessorServices,
                GenericProcessorService::processOldMessages,
                (service, exception) -> log.warn("message process cause error ", exception));
    }
}
