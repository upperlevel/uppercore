package xyz.upperlevel.uppercore.config.exceptions;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

// TODO: REMOVE
@Getter
public class InvalidConfigException extends RuntimeException {
    private final String configError;
    private List<String> locators;

    public InvalidConfigException(String configError, String... locators) {
        super(configError);
        this.configError = configError;
        this.locators = new ArrayList<>(Arrays.asList(locators));
    }

    public InvalidConfigException(String configError, Throwable cause, String... locators) {
        super(configError, cause);
        this.configError = configError;
        this.locators = new ArrayList<>(Arrays.asList(locators));
    }

    private String getErrorMessage(String initMsg) {
        List<String> locators = Lists.reverse(getLocators());
        if (locators.size() > 0) {
            initMsg += ": ";
            StringJoiner joiner = new StringJoiner(", ");
            for (String loc : locators) {
                joiner.add(loc);
            }
            initMsg += joiner.toString();
        }
        initMsg += " " + configError;
        return initMsg;
    }

    @Override
    public String getMessage() {
        return getErrorMessage("Error");
    }

    public void addLocation(String location) {
        locators.add(location);
    }

}
