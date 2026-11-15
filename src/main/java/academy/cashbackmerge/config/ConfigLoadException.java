package academy.cashbackmerge.config;

import java.io.IOException;

public class ConfigLoadException extends IOException {
    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
