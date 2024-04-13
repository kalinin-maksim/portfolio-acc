package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import edu.kalinin.acc.api.MessageService;
import edu.kalinin.acc.data.DataBase;
import edu.kalinin.acc.data.Input;
import edu.kalinin.acc.data.Output;
import edu.kalinin.acc.dto.MessageDto;
import edu.kalinin.acc.entity.Entry;
import edu.kalinin.acc.exception.EntryException;
import edu.kalinin.acc.fabric.EntryEventFabric;
import edu.kalinin.acc.helper.Observer;
import edu.kalinin.acc.repository.DroolsRuleRepository;
import edu.kalinin.acc.rule.RuleEngine;
import edu.kalinin.acc.rule.drools.DroolsRuleContent;
import edu.kalinin.acc.rule.drools.DroolsRuleEngineFabric;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public abstract class GenericProcessorService {

    private final MessageService messageService;
    private final EntryService entryService;
    private final DroolsRuleRepository droolsRuleRepository;

    protected abstract String getTopicIn();

    protected abstract Observer.UnSafeFunction<MessageDto, String> processing(RuleEngine ruleEngine);

    public void processNewMessages() {
        process(() -> messageService.findAllOrderByCreated(getTopicIn()));
    }

    public void processOldMessages() {
        process(() -> messageService.findOldOrderByCreated(getTopicIn()));
    }

    protected String process(RuleEngine ruleEngine,
                             MessageDto message,
                             Input input,
                             Function<Output, Entry> update,
                             String moduleId,
                             String accountingId) throws EntryException {
        var entries = entryFrom(ruleEngine, input, update);
        var observer = observer(moduleId, accountingId).combine(getSavingObserver(message));
        for (Entry entry : entries) {
            entryService.trySave(entry, observer);
        }
        return message.getId();
    }

    private void process(Supplier<List<MessageDto>> messageDtosListSupplier) {
        var messages = messageDtosListSupplier.get();
        if (messages.isEmpty()) return;

        var droolsRuleContents = droolsRuleRepository.findAll().stream()
                .map(rule -> DroolsRuleContent.of(rule.getName(), rule.getBody(), rule.getPattern(), rule.getResult()))
                .collect(Collectors.toList());
        if (droolsRuleContents.isEmpty()) return;

        try (var ruleEngine = DroolsRuleEngineFabric.create(droolsRuleContents)) {
            processRules(messages, ruleEngine);
        } catch (Exception ex) {
            log.warn("cause error", ex);
        }
    }

    private List<Entry> entryFrom(RuleEngine ruleEngine, Input input, Function<Output, Entry> update) throws EntryException {

        var dataBase = new DataBase(Collections.emptyList());

        var outputs = ruleEngine.getOutput(input, dataBase);
        var errors = outputs.stream()
                .map(Output::getError)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new EntryException(errors);
        }

        return outputs
                .stream()
                .map(update)
                .collect(Collectors.toList());
    }

    private Observer<String> observer(String moduleId, String accountingId) {
        return getLogObserver()
                .combine(EntryEventFabric.observer(moduleId, accountingId));
    }

    private Observer<String> getSavingObserver(MessageDto message) {
        return new Observer<>(id -> {
        }, ex -> {
            log.warn(String.format("message process error, message id: %s", message.getId()), ex);
            messageService.toRetry(message.getId(), ex.getMessage());
        });
    }

    private void processRules(List<MessageDto> messages, RuleEngine ruleEngine) {

        for (MessageDto message : messages) {
            observer(message).on(processing(ruleEngine)).accept(message);
        }
    }

    private Observer<String> getLogObserver() {
        return new Observer<>(
                id -> log.info("entry created, id: {}", id),
                ex -> log.warn("entry creation failed", ex));
    }

    private Observer<String> observer(MessageDto message) {
        return new Observer<>(
                id -> {
                    log.info(String.format("message processed, message id: %s", message.getId()), id);
                    messageService.softDelete(message);
                },
                ex -> {
                    log.warn(String.format("message process error, message id: %s", message.getId()), ex);
                    messageService.toRetry(message.getId(), ex.getMessage());
                });
    }

}
