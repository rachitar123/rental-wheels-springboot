package com.rentwheels.config;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;

/**
 * Maps low-level database exceptions to user-friendly flash messages.
 * Controllers that catch Exception still work; this covers uncaught DB errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, RedirectAttributes redirectAttributes,
                                      WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage", resolveIntegrityMessage(ex));
        return redirectToReferer(request);
    }

    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccess(DataAccessException ex, RedirectAttributes redirectAttributes,
                                   WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "A database error occurred. Please check your data and try again.");
        return redirectToReferer(request);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public String handleEmptyResult(EmptyResultDataAccessException ex, RedirectAttributes redirectAttributes,
                                    WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage", "The requested record was not found.");
        return redirectToReferer(request);
    }

    @ExceptionHandler(SQLException.class)
    public String handleSql(SQLException ex, RedirectAttributes redirectAttributes, WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "Database connection or query failed. Please verify MySQL is running and the rentwheels schema exists.");
        return redirectToReferer(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes redirectAttributes,
                                        WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage",
                ex.getMessage() != null ? ex.getMessage() : "Invalid request.");
        return redirectToReferer(request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, RedirectAttributes redirectAttributes,
                                     WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage",
                ex.getMessage() != null ? ex.getMessage() : "Operation cannot be completed in the current state.");
        return redirectToReferer(request);
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, RedirectAttributes redirectAttributes, WebRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "An unexpected error occurred. Please try again or contact the administrator.");
        return redirectToReferer(request);
    }

    private String resolveIntegrityMessage(DataIntegrityViolationException ex) {
        String details = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        if (details == null) {
            return "This record conflicts with existing data and cannot be saved.";
        }
        String lower = details.toLowerCase();
        if (lower.contains("duplicate") || lower.contains("unique")) {
            return "A record with the same ID or unique field already exists.";
        }
        if (lower.contains("foreign key") || lower.contains("cannot delete") || lower.contains("constraint")) {
            return "Cannot complete this action because related rental or return records exist.";
        }
        if (ex.getCause() instanceof ConstraintViolationException) {
            return "This change violates a database constraint. Please check related records.";
        }
        return "This record conflicts with existing data and cannot be saved.";
    }

    private String redirectToReferer(WebRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("localhost:8080")) {
            try {
                java.net.URI uri = java.net.URI.create(referer);
                String path = uri.getPath();
                if (path != null && !path.isBlank() && !path.equals("/login")) {
                    return "redirect:" + path;
                }
            } catch (Exception ignored) {
                // fall through
            }
        }
        return "redirect:/dashboard";
    }
}
