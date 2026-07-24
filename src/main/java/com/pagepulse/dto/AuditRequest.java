package com.pagepulse.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for POST /api/audit
 * Example: { "url": "https://example.com" }
 */
public class AuditRequest {

    @NotBlank(message = "url must not be blank")
    private String url;

    public AuditRequest() {
    }

    public AuditRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
