package edu.kalinin.acc.exception;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor
public class EntryException extends Exception{
    private final List<String> messages;

    @Override
    public String toString() {
        return new StringJoiner(", ", EntryException.class.getSimpleName() + "[", "]")
                .add("messages=" + messages)
                .toString();
    }

    @Override
    public String getMessage() {
        return messages.toString();
    }
}
