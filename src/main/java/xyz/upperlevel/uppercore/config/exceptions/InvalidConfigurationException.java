package xyz.upperlevel.uppercore.config.exceptions;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Getter
public class InvalidConfigurationException extends RuntimeException{
    private List<String> localizers;
    @Getter
    private final String configError;

    public InvalidConfigurationException(String configError, String... localizers) {
        super(configError);
        this.configError = configError;
        this.localizers = new ArrayList<>(Arrays.asList(localizers));
    }

    public InvalidConfigurationException(String configError, Throwable cause, String... localizers) {
        super(configError, cause);
        this.configError = configError;
        this.localizers = new ArrayList<>(Arrays.asList(localizers));
    }

    public String getErrorMessage(String initialMessage)  {
        List<String> localizers = getLocalizers();
        Lists.reverse(localizers);

        if(localizers.size() > 0)
            initialMessage += ": ";

        StringJoiner joiner = new StringJoiner(", ");
        for(String loc : localizers)
            joiner.add(loc);
        initialMessage += joiner.toString();
        initialMessage += " " + getConfigError();
        return initialMessage;
    }

    @Override
    public String getMessage() {
        return getErrorMessage("Error");
    }

    public void addLocalizer(String localizer) {
        localizers.add(localizer);
    }

}
