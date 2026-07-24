package com.pagepulse.exception;

/**
 * Thrown whenever a URL can't be fetched or audited for any reason:
 * malformed URL, connection timeout, non-HTML response, unreachable host, etc.
 * The message is written to be safe to show directly to the end user.
 */
public class UrlAuditException extends RuntimeException {

    public UrlAuditException(String message) {
        super(message);
    }

    public UrlAuditException(String message, Throwable cause) {
        super(message, cause);
    }
}
