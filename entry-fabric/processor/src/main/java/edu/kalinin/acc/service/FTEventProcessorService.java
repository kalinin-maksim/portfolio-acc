package edu.kalinin.acc.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.api.MessageService;
import edu.kalinin.acc.configuration.ExternalSystem;
import edu.kalinin.acc.configuration.IntegrationConfiguration;
import edu.kalinin.acc.dto.Event;
import edu.kalinin.acc.dto.MessageDto;
import edu.kalinin.acc.helper.Observer.UnSafeFunction;
import edu.kalinin.acc.mapper.EntryMapper;
import edu.kalinin.acc.mapper.RuleMapper;
import edu.kalinin.acc.repository.DroolsRuleRepository;
import edu.kalinin.acc.rule.RuleEngine;

import static edu.kalinin.acc.helper.JsonHelper.getObject;


@Service
@Log4j2
public class FTEventProcessorService extends GenericProcessorService {

    private final IntegrationConfiguration integrationConfiguration;
    private final RuleMapper ruleMapper;
    private final EntryMapper entryMapper;

    public FTEventProcessorService(MessageService messageService, EntryService entryService, DroolsRuleRepository droolsRuleRepository, IntegrationConfiguration integrationConfiguration, RuleMapper ruleMapper, EntryMapper entryMapper) {
        super(messageService, entryService, droolsRuleRepository);
        this.integrationConfiguration = integrationConfiguration;
        this.ruleMapper = ruleMapper;
        this.entryMapper = entryMapper;
    }

    @Override
    protected String getTopicIn() {
        return integrationConfiguration.getTopicIn(ExternalSystem.FT.toString());
    }

    protected UnSafeFunction<MessageDto, String> processing(RuleEngine ruleEngine) {
        return message -> {
            var event = getObject(message, Event.class);
            var moduleId = event.getHeader().getModuleId();
            var accountingId = event.getHeader().getAccountingId();
            var input = ruleMapper.toInput(event.getHeader(), event.getBody());
            return process(ruleEngine,
                    message,
                    input,
                    output -> entryMapper.toEntry(event.getHeader(), output),
                    moduleId,
                    accountingId);
        };
    }
}
