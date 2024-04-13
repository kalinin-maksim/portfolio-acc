package edu.kalinin.acc.rule.drools;

import lombok.experimental.UtilityClass;
import edu.kalinin.acc.rule.RuleEngine;

import java.util.List;

@UtilityClass
public class DroolsRuleEngineFabric {
    public static RuleEngine create(List<DroolsRuleContent> droolsRuleContents) {
        return new DroolsRuleEngine(droolsRuleContents);
    }
}
