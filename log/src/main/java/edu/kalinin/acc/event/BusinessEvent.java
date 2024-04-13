package edu.kalinin.acc.event;

import org.springframework.context.ApplicationEvent;
import edu.kalinin.acc.selector.Channel;

public class BusinessEvent extends ApplicationEvent {

    private final Level loglevel;
    private final Channel channel;
    private final String address;
    private final String processId;
    private final String message;
    private final String responseJson;

    public BusinessEvent(Level loglevel, Channel channel, String address, String processId, String message, String responseJson) {
        super(processId);
        this.loglevel = loglevel;
        this.channel = channel;
        this.address = address;
        this.processId = processId;
        this.message = message;
        this.responseJson = responseJson;
    }

    public Level getLoglevel() {
        return loglevel;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getAddress() {
        return address;
    }

    public String getProcessId() {
        return processId;
    }

    public String getMessage() {
        return message;
    }

    public String getResponseJson() {
        return responseJson;
    }
}