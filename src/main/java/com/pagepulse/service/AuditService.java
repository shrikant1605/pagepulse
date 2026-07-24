package com.pagepulse.service;

import com.pagepulse.dto.AuditResponse;
import com.pagepulse.exception.UrlAuditException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@Service
public class AuditService {

    private static final int TIMEOUT_MS = 8000;
    private static final String USER_AGENT = "PagePulse/1.0 (+Digital Heroes Training Task)";

    public AuditResponse audit(String rawUrl) {
        String url = normalizeAndValidate(rawUrl);

        long start = System.currentTimeMillis();
        Connection.Response response = fetch(url);
        long responseTimeMs = System.currentTimeMillis() - start;

        String contentType = response.contentType();
        if (contentType == null || !contentType.toLowerCase().contains("html")) {
            throw new UrlAuditException(
                    "The URL did not return an HTML page (content-type: " + contentType + "). Only HTML pages can be audited.");
        }

        Document doc = parseBody(response);

        String title = doc.title();
        String metaDescription = extractMetaDescription(doc);
        int h1Count = doc.select("h1").size();
        int imagesMissingAlt = countImagesMissingAlt(doc);
        int wordCount = countWords(doc);

        return new AuditResponse(
                url,
                response.statusCode(),
                responseTimeMs,
                title,
                metaDescription,
                h1Count,
                imagesMissingAlt,
                wordCount
        );
    }

    /**
     * Basic sanity check + normalization (adds https:// if the scheme is missing)
     * before we ever attempt a network call.
     */
    private String normalizeAndValidate(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new UrlAuditException("URL must not be empty.");
        }

        String candidate = rawUrl.trim();
        if (!candidate.startsWith("http://") && !candidate.startsWith("https://")) {
            candidate = "https://" + candidate;
        }

        try {
            URL parsed = URI.create(candidate).toURL();
            if (parsed.getHost() == null || parsed.getHost().isBlank()) {
                throw new UrlAuditException("'" + rawUrl + "' is not a valid URL.");
            }
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new UrlAuditException("'" + rawUrl + "' is not a valid URL.");
        }

        return candidate;
    }

    private Connection.Response fetch(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
        } catch (UnknownHostException e) {
            throw new UrlAuditException("Could not resolve host for: " + url);
        } catch (SocketTimeoutException e) {
            throw new UrlAuditException("Request timed out after " + TIMEOUT_MS + "ms for: " + url);
        } catch (Exception e) {
            throw new UrlAuditException("Failed to fetch URL: " + url + " (" + e.getMessage() + ")");
        }
    }

    private Document parseBody(Connection.Response response) {
        try {
            return response.parse();
        } catch (UnsupportedMimeTypeException e) {
            throw new UrlAuditException("The URL did not return a parseable HTML document.");
        } catch (Exception e) {
            throw new UrlAuditException("Failed to parse the page content: " + e.getMessage());
        }
    }

    private String extractMetaDescription(Document doc) {
        Element meta = doc.selectFirst("meta[name=description]");
        if (meta == null) {
            return "";
        }
        return meta.attr("content");
    }

    private int countImagesMissingAlt(Document doc) {
        Elements images = doc.select("img");
        int missing = 0;
        for (Element img : images) {
            String alt = img.attr("alt");
            if (alt == null || alt.isBlank()) {
                missing++;
            }
        }
        return missing;
    }

    private int countWords(Document doc) {
        String visibleText = doc.body() != null ? doc.body().text() : "";
        if (visibleText.isBlank()) {
            return 0;
        }
        return visibleText.trim().split("\\s+").length;
    }
}
