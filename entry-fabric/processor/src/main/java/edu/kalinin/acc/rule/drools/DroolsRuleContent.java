package edu.kalinin.acc.rule.drools;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class DroolsRuleContent {
    private String name;
    private String body;
    private String when;
    private String then;
}
