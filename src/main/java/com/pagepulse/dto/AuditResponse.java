package com.pagepulse.dto;

/**
 * JSON report returned for a successfully audited URL.
 */
public class AuditResponse {

    private String url;
    private int httpStatus;
    private long responseTimeMs;
    private String title;
    private String metaDescription;
    private int h1Count;
    private int imagesMissingAlt;
    private int wordCount;

    public AuditResponse() {
    }

    public AuditResponse(String url, int httpStatus, long responseTimeMs, String title,
                          String metaDescription, int h1Count, int imagesMissingAlt, int wordCount) {
        this.url = url;
        this.httpStatus = httpStatus;
        this.responseTimeMs = responseTimeMs;
        this.title = title;
        this.metaDescription = metaDescription;
        this.h1Count = h1Count;
        this.imagesMissingAlt = imagesMissingAlt;
        this.wordCount = wordCount;
    }

    public String getUrl() {
        return url;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public String getTitle() {
        return title;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public int getH1Count() {
        return h1Count;
    }

    public int getImagesMissingAlt() {
        return imagesMissingAlt;
    }

    public int getWordCount() {
        return wordCount;
    }
}
