package edu.kalinin.acc.service;

import lombok.SneakyThrows;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.api.BalanceApi;
import edu.kalinin.acc.api.impl.BalanceApiImpl;
import edu.kalinin.acc.api.impl.MessageServiceImpl;
import edu.kalinin.acc.configuration.IntegrationConfiguration;
import edu.kalinin.acc.dto.MessageDto;
import edu.kalinin.acc.entity.DroolsRule;
import edu.kalinin.acc.entity.Entry;
import edu.kalinin.acc.fabric.EntryEventFabric;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.mapper.*;
import edu.kalinin.acc.mapper.EntryMapperImpl;
import edu.kalinin.acc.mapper.MessageMapperImpl;
import edu.kalinin.acc.mapper.PostingMapperImpl;
import edu.kalinin.acc.mapper.RuleMapperImpl;
import edu.kalinin.acc.repository.DroolsRuleRepository;
import edu.kalinin.acc.repository.EntryRepository;
import edu.kalinin.acc.service.EntryService;
import edu.kalinin.acc.service.FTEventProcessorService;
import edu.kalinin.acc.sheduled.ProcessorJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.*;
import static edu.kalinin.acc.helper.JsonHelper.toJson;
import static edu.kalinin.acc.helper.TestDataFactory.EventFactory.TEST_SYSTEM_ID;
import static edu.kalinin.acc.helper.TestDataFactory.EventFactory.getEvent;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                SpringSupport.class,
                EntryRepository.class,
                ProcessorService.class,
                FTEventProcessorService.class,
                EntryService.class,
                MessageServiceImpl.class,
                EntryMapperImpl.class,
                PostingMapperImpl.class,
                MessageMapperImpl.class,
                RuleMapperImpl.class
        }
)
@TestPropertySource(
       properties = {
               "application.processor.rate-sec=1"
       }
)
class ProcessorJobTest {
    public static final String DB_ACCOUNT = "111111111";
    public static final UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    private ProcessorService processorService;
    @MockBean
    private MessageServiceImpl messageService;
    @MockBean
    private BalanceService balanceService;
    @MockBean
    private EntryRepository entryRepository;
    @MockBean
    private DroolsRuleRepository droolsRuleRepository;
    @MockBean
    private LockProvider lockProvider;
    @MockBean
    private EntryEventFabric entryEventFabric;
    @MockBean
    private IntegrationConfiguration integrationConfiguration;
    @MockBean
    private BalanceMapper balanceMapper;
    @MockBean
    private BalanceApi balanceApi;

    @Test
    @SneakyThrows
    void processTest() {
        //given
        var message = new MessageDto();
        message.setId(null);
        message.setBody(toJson(getEvent()));
        var rule = getDroolsRule("systemId == \"" + TEST_SYSTEM_ID + "\"", "debitAccount = \"" + DB_ACCOUNT + "\"");
        when(messageService.findAllOrderByCreated(any())).thenReturn(Collections.singletonList(message));
        when(entryEventFabric.getTopic(any())).thenReturn("");
        when(droolsRuleRepository.findAll()).thenReturn(Collections.singletonList(rule));
        when(entryRepository.save(any())).thenAnswer(invocation -> {
            Entry entry = invocation.getArgument(0);
            entry.setId(UUID.randomUUID().toString());
            return entry;
        });
        var entryCaptor = ArgumentCaptor.forClass(Entry.class);

        //when
        processorService.processNewMessages();

        //then
        verify(messageService,  times(1)).softDelete(any());
        verify(entryRepository).save(entryCaptor.capture());
        var entries = entryCaptor.getAllValues();
        then(entries).hasSize(1);
        then(entries.get(0)).matches(entry -> entry.getDebitAccount().equals(DB_ACCOUNT));

    }

    @Test
    @SneakyThrows
    void processDBFailTest() {
        //given
        var message = new MessageDto();
        message.setId(null);
        message.setBody(toJson(getEvent()));
        var rule = getDroolsRule("systemId == \"" + TEST_SYSTEM_ID + "\"", "debitAccount = \"" + DB_ACCOUNT + "\"");
        when(messageService.findAllOrderByCreated(any())).thenReturn(Collections.singletonList(message));
        when(droolsRuleRepository.findAll()).thenReturn(Collections.singletonList(rule));
        when(entryRepository.save(any())).thenAnswer(invocation -> {
            throw new RuntimeException("db fail");
        });

        //when
        processorService.processNewMessages();

        //then
        verify(messageService,  times(1)).toRetry(any(), any());

    }

    @Test
    @SneakyThrows
    void processFailedWithoutRuleTest(){
        //given
        var entryCaptor = ArgumentCaptor.forClass(Entry.class);

        //when
        processorService.processNewMessages();

        //then
        verify(messageService,  times(0)).softDelete(any());
        verify(entryRepository, times(0)).save(entryCaptor.capture());
        var entries = entryCaptor.getAllValues();
        then(entries).hasSize(0);

    }

    @Test
    @SneakyThrows
    void processExceptionTest() {
        //given
        var message = new MessageDto();
        message.setId(null);
        message.setBody(toJson(getEvent()));
        var rules = new ArrayList<DroolsRule>();
        var rule1 = getDroolsRule("reverse!=\"Y\"", "error = \"violation 1\"");
        rules.add(rule1);
        var rule2 = getDroolsRule("when\n" +
                "    \tdateBase: DataBase(currencies.size == 0)\n" +
                "    then\n" +
                "        outputs.add(new Output(\"violation 2\"))");
        rules.add(rule2);
        when(messageService.findAllOrderByCreated(any())).thenReturn(Collections.singletonList(message));
        when(droolsRuleRepository.findAll()).thenReturn(rules);
        var entryCaptor = ArgumentCaptor.forClass(Entry.class);

        //when
        processorService.processNewMessages();

        //then
        verify(messageService,  times(1)).toRetry(any(), any());
        verify(entryRepository, times(0)).save(entryCaptor.capture());
        var entries = entryCaptor.getAllValues();
        then(entries).hasSize(0);

    }

    private DroolsRule getDroolsRule(String pattern, String result) {
        var rule = new DroolsRule();
        rule.setId(ZERO_UUID.toString());
        rule.setBody("");
        rule.setPattern(pattern);
        rule.setResult(result);
        return rule;
    }

    private DroolsRule getDroolsRule(String body) {
        var rule2 = new DroolsRule();
        rule2.setName("rule 2");
        rule2.setId(ZERO_UUID.toString());
        rule2.setBody(body);
        return rule2;
    }

}