# Page Pulse

A small web tool that audits any URL — fetches the page and returns HTTP status,
response time, title, meta description, H1 count, images missing `alt` text, and
approximate word count.

Built for the Digital Heroes SDE Training Task.

## Live Demo

- Live URL: _add your deployed Render/Railway URL here_
- GitHub repo: _add your public repo URL here_

## Setup

**Prerequisites:** Java 17, Maven

```bash
git clone <your-repo-url>
cd page-pulse
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` — the frontend is served directly at
that root URL, and the API is available at `/api/audit`.

To run the tests:

```bash
./mvnw test
```

## API Contract

### `POST /api/audit`

**Request body**
```json
{ "url": "https://example.com" }
```

**Success response — `200 OK`**
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

**Error response — `400 Bad Request`** (invalid URL, unreachable host, non-HTML content, timeout)
```json
{
  "timestamp": "2026-07-24T10:15:30Z",
  "status": 400,
  "error": "Could not resolve host for: https://not-a-real-domain-xyz.com"
}
```

**Error response — `500 Internal Server Error`** (unexpected failure)
```json
{
  "timestamp": "2026-07-24T10:15:30Z",
  "status": 500,
  "error": "Something went wrong while processing the request."
}
```

## Design Decisions

1. **Jsoup for fetch + parse, not a separate HTTP client + HTML parser.**
   Jsoup handles both the network call and DOM parsing in one library, which
   keeps the service layer small and avoids juggling two dependencies for what
   is fundamentally one operation: "get this page and read its structure."

2. **`ignoreHttpErrors(true)` and `ignoreContentType(true)` on the fetch, with
   manual checks afterward.**
   By default Jsoup throws on non-2xx status codes and non-HTML content types.
   I turned that off deliberately so a 404 or a redirect loop becomes a normal,
   reportable result instead of an exception — the tool is supposed to *report*
   on broken pages, not just succeed on healthy ones. I then check the content
   type explicitly and raise a clear, user-facing error only when the response
   truly can't be audited (e.g. a PDF or an image was returned instead of HTML).

3. **All failure paths funnel through one custom exception (`UrlAuditException`)
   and one `@RestControllerAdvice` handler.**
   Malformed URL, DNS failure, timeout, and non-HTML response are different
   causes but the same *kind* of problem from the API's point of view: "this
   URL can't be audited, here's why." Centralizing them means the controller
   stays a thin pass-through, and the client always gets the same JSON error
   shape regardless of which specific thing went wrong.

## What I'd change with another day

- Cache recent audits (e.g. by URL, short TTL) so repeated checks on the same
  page don't refetch every time.
- Add a `robots.txt` check before fetching, to be a better-behaved crawler.
- **Done:** I have already replaced the live-network unit tests with mocked HTTP responses
  (using WireMock) so the test suite doesn't depend on internet
  access or example.com's uptime!

## AI usage disclosure

_Fill this in honestly before submitting — e.g. which parts you generated with
AI assistance, what you changed or rewrote afterward, and where you made the
calls yourself (error-handling behavior, content-type check, design decisions
in this README, etc.)._
