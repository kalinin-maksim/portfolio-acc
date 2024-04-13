package edu.kalinin.acc.rule;

import edu.kalinin.acc.data.DataBase;
import edu.kalinin.acc.data.Input;
import edu.kalinin.acc.data.Output;

import java.util.Collection;

public interface RuleEngine extends AutoCloseable {
    Collection<Output> getOutput(Input input, DataBase dataBase);
}
