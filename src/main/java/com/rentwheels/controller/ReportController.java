package com.rentwheels.controller;

import com.rentwheels.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public String viewReport(Authentication authentication, Model model) {
        String username = (authentication != null) ? authentication.getName() : "System Operator";
        String reportText = reportService.generateSystemReport(username);
        model.addAttribute("reportContent", reportText);
        model.addAttribute("activeTab", "reports");
        return "reports";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "System Operator";
        String reportText = reportService.generateSystemReport(username);
        byte[] content = reportText.getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rentwheels_report.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }
}
