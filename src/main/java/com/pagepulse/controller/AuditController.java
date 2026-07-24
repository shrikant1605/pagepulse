package com.pagepulse.controller;

import com.pagepulse.dto.AuditRequest;
import com.pagepulse.dto.AuditResponse;
import com.pagepulse.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * POST /api/audit
     * Body: { "url": "https://example.com" }
     * Returns: AuditResponse JSON report, or a structured error (see GlobalExceptionHandler).
     */
    @PostMapping("/audit")
    public ResponseEntity<AuditResponse> audit(@Valid @RequestBody AuditRequest request) {
        AuditResponse result = auditService.audit(request.getUrl());
        return ResponseEntity.ok(result);
    }
}
