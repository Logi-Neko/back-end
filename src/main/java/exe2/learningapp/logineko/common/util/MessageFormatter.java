package exe2.learningapp.logineko.common.util;

import exe2.learningapp.logineko.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Utility class for formatting error messages with parameters
 */
@Component
public class MessageFormatter {

    /**
     * Format error message with parameters
     * @param errorCode The error code containing the message template
     * @param params The parameters to replace in the message
     * @return Formatted message
     */
    public static String format(ErrorCode errorCode, Object... params) {
        if (params == null || params.length == 0) {
            return errorCode.getMessage();
        }

        try {
            return MessageFormatter.format(ErrorCode.valueOf(errorCode.getMessage()), params);
        } catch (IllegalArgumentException e) {
            // If formatting fails, return original message
            return errorCode.getMessage();
        }
    }

    /**
     * Simple replacement for single parameter messages using {0} placeholder
     * @param errorCode The error code
     * @param param The parameter to replace {0} with
     * @return Formatted message
     */
    public static String formatSingle(ErrorCode errorCode, String param) {
        return errorCode.getMessage().replace("{0}", param);
    }

    /**
     * Simple replacement for dual parameter messages using {0} and {1} placeholders
     * @param errorCode The error code
     * @param param1 The parameter to replace {0} with
     * @param param2 The parameter to replace {1} with
     * @return Formatted message
     */
    public static String formatDual(ErrorCode errorCode, String param1, String param2) {
        return errorCode.getMessage()
                .replace("{0}", param1)
                .replace("{1}", param2);
    }
}