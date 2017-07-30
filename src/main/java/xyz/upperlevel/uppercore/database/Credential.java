package xyz.upperlevel.uppercore.database;

import lombok.Data;

@Data
public class Credential {
    private final String database, username, password;
}
