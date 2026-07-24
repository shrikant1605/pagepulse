# Page Pulse

A lightweight, robust web tool that audits any URL — fetches the page and returns a detailed JSON report including HTTP status, response time, title, meta description, H1 count, images missing `alt` text, and approximate word count.

**Built for the Digital Heroes SDE Training Task.**

## Live Demo
- **Live URL:** https://pagepulse-7w78.onrender.com/
- **Loom Demo:** 

## Setup Instructions

**Prerequisites:** Java 17, Maven

Clone the repository and run the application locally:
```bash
git clone https://github.com/shrikant1605/pagepulse.git
cd pagepulse
mvn spring-boot:run
```

The application starts on `http://localhost:8080`. The frontend is served directly at the root URL, and the REST API is available at `/api/audit`.

To run the deterministic, offline-capable unit tests:
```bash
mvn test
```

## API Contract

### `POST /api/audit`

**Request Body**
```json
{ 
  "url": "https://example.com" 
}
```

**Success Response — `200 OK`**
```json
{
  "url": "https://example.com",
  "httpStatus": 200,
  "responseTimeMs": 184,
  "title": "Example Domain",
  "metaDescription": "",
  "h1Count": 1,
  "imagesMissingAlt": 0,
  "wordCount": 28
}
```

**Error Response — `400 Bad Request`** (Handled globally for invalid URLs, unreachable hosts, non-HTML content, and timeouts)
```json
{
  "timestamp": "2026-07-24T10:15:30Z",
  "status": 400,
  "error": "Could not resolve host for: https://not-a-real-domain-xyz.com"
}
```

## Design Decisions

1. **Jsoup for Fetching and Parsing:**
   Instead of using a standard `HttpClient` and a separate HTML parser, I utilized Jsoup to handle both the network call and DOM parsing. This keeps the service layer lean and avoids juggling two dependencies for what is fundamentally one operation.

2. **Treating 404s as Data, Not Exceptions:**
   I explicitly configured Jsoup with `ignoreHttpErrors(true)` and `ignoreContentType(true)`. By default, it throws on non-2xx status codes. I turned this off deliberately because the goal of this tool is to *audit* pages. A broken link (404) or a redirect loop is a valid, reportable result that should be returned cleanly in the JSON, rather than crashing the application. 

3. **GlobalExceptionHandler (`@RestControllerAdvice`):**
   All failure paths (Malformed URL, DNS failure, timeout, non-HTML responses) funnel through a custom `UrlAuditException`. I implemented a `@RestControllerAdvice` handler to intercept these. This means the controller stays incredibly thin, and the frontend client is guaranteed to receive a consistent, predictable JSON error shape regardless of what went wrong in the backend.

## AI Usage Disclosure

In the spirit of transparency for this task:
- **Backend (Java/Spring Boot):** I architected and wrote the core backend logic, API design, and error handling myself. I occasionally used AI (ChatGPT/Claude) as a sparring partner to quickly recall syntax (e.g., specific Jsoup selectors) and to brainstorm the best way to structure my WireMock unit tests.
- **Frontend & Documentation:** Since my core strength is backend development, I heavily leveraged AI to generate the CSS/HTML styling for the frontend to ensure it looked clean and presentable, and to help format this README file efficiently.
