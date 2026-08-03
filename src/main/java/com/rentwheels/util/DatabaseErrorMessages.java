package com.rentwheels.util;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Converts database / data-access exceptions into short user-facing messages.
 */
public final class DatabaseErrorMessages {

    private DatabaseErrorMessages() {
    }

    public static String toUserMessage(Throwable ex) {
        if (ex == null) {
            return "An unexpected error occurred.";
        }

        // Prefer explicit business validation messages
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            return safe(ex.getMessage(), "Invalid operation.");
        }

        Throwable root = rootCause(ex);
        String rootMsg = root.getMessage() != null ? root.getMessage().toLowerCase() : "";

        if (ex instanceof DataIntegrityViolationException || root instanceof DataIntegrityViolationException
                || rootMsg.contains("duplicate") || rootMsg.contains("unique")) {
            if (rootMsg.contains("duplicate") || rootMsg.contains("unique")) {
                return "A record with the same ID or unique field already exists.";
            }
            if (rootMsg.contains("foreign key") || rootMsg.contains("constraint")) {
                return "Cannot complete this action because related records exist.";
            }
            return "This record conflicts with existing data and cannot be saved.";
        }

        if (ex instanceof DataAccessException || root instanceof DataAccessException) {
            if (rootMsg.contains("communications link failure")
                    || rootMsg.contains("connection refused")
                    || rootMsg.contains("access denied")) {
                return "Cannot connect to MySQL. Ensure the server is running and credentials are correct.";
            }
            if (rootMsg.contains("unknown database")) {
                return "Database 'rentwheels' was not found. Create it and restart the application.";
            }
            return "A database error occurred. Please check your data and try again.";
        }

        return safe(ex.getMessage(), "An unexpected error occurred. Please try again.");
    }

    private static Throwable rootCause(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private static String safe(String message, String fallback) {
        return (message != null && !message.isBlank()) ? message : fallback;
    }
}
