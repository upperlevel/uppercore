package xyz.upperlevel.uppercore.config.exceptions;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Getter
public class InvalidConfigurationException extends RuntimeException{
    private final String configError;
    private List<String> locations;

    public InvalidConfigurationException(String configError, String... locations) {
        super(configError);
        this.configError = configError;
        this.locations = new ArrayList<>(Arrays.asList(locations));
    }

    public InvalidConfigurationException(String configError, Throwable cause, String... locations) {
        super(configError, cause);
        this.configError = configError;
        this.locations = new ArrayList<>(Arrays.asList(locations));
    }

    public String getErrorMessage(String initialMessage)  {
        List<String> locations = getLocations();
        Lists.reverse(locations);

        if(locations.size() > 0)
            initialMessage += ": ";

        StringJoiner joiner = new StringJoiner(", ");
        for(String loc : locations)
            joiner.add(loc);
        initialMessage += joiner.toString();
        initialMessage += " " + getConfigError();
        return initialMessage;
    }

    @Override
    public String getMessage() {
        return getErrorMessage("Error");
    }

    public void addLocation(String location) {
        locations.add(location);
    }

}
