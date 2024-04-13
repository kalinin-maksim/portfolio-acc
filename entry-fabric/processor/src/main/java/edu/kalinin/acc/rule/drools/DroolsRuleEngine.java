package edu.kalinin.acc.rule.drools;

import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import edu.kalinin.acc.data.DataBase;
import edu.kalinin.acc.data.Input;
import edu.kalinin.acc.data.Output;
import edu.kalinin.acc.rule.RuleEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DroolsRuleEngine implements RuleEngine {

    private static final String IMPORT = "import ";
    private static final String RULE_HEAD = "" +
            IMPORT + Input.class.getName() + "\n" +
            IMPORT + DataBase.class.getName() + "\n" +
            IMPORT + Output.class.getName() + "\n" +
            "global java.util.List<" + Output.class.getName() + "> outputs";

    private final KieSession kieSession;

    public DroolsRuleEngine(List<DroolsRuleContent> droolsRuleContents) {

        var kieHelper = new KieHelper();

        var rule = RULE_HEAD + "\n" +
                droolsRuleContents.stream()
                        .map(droolsRuleContent -> {
                            if (!droolsRuleContent.getBody().isEmpty()) {
                                return "rule \"" + droolsRuleContent.getName() + "\"\n" +
                                        "\tdialect \"mvel\"\n" +
                                        droolsRuleContent.getBody() + ";";
                            } else {
                                return "rule \"" + droolsRuleContent.getName() + "\"\n" +
                                        "\tdialect \"mvel\"\n" +
                                        "\twhen\n" +
                                        "\t\tinput: Input(" + droolsRuleContent.getWhen() + ")\n" +
                                        "\tthen \n" +
                                        "\t\tOutput output = new Output();\n" +
                                        Arrays.stream(droolsRuleContent.getThen().split(",")).map(String::trim).map("\t\toutput."::concat).collect(Collectors.joining(";\n", "", ";\n"))
                                                .replace("<comma>", ",") +
                                        "\t\toutputs.add(output);";
                            }
                        })
                        .collect(Collectors.joining("\nend\n", "\n", "\nend\n"));

        kieHelper.addContent(rule, ResourceType.DRL);

        var kieBase = kieHelper.build(EqualityBehaviorOption.IDENTITY);
        kieSession = kieBase.newKieSession();
    }

    public List<Output> getOutput(Input input, DataBase dataBase) {
        List<Output> output = new ArrayList<>();
        kieSession.setGlobal("outputs", output);
        kieSession.insert(dataBase);
        kieSession.insert(input);
        kieSession.fireAllRules();
        return output;
    }

    @Override
    public void close() {
        kieSession.dispose();
    }
}
