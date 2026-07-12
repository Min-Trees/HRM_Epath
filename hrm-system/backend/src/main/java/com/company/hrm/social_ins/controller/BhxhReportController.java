package com.company.hrm.social_ins.controller;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.social_ins.dto.BhxhReportD02LTDto;
import com.company.hrm.social_ins.dto.BhxhReportD03LTDto;
import com.company.hrm.social_ins.service.BhxhReportService;
import com.company.hrm.social_ins.service.BhxhXmlExporter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * T15 - REST controller cho BHXH Reports (D02-LT, D03-LT).
 *
 * <p>2 nhom chuc nang:
 * <ol>
 *   <li>JSON: tra ve DTO day du cho frontend preview</li>
 *   <li>XML: tra ve file XML theo chuan BHXH VN (Content-Disposition attachment)</li>
 * </ol>
 *
 * <p>Endpoint pattern:
 * <ul>
 *   <li>{@code GET /api/v1/bhxh/reports/d02-lt?tuNgay=...&denNgay=...&maDonViBHXH=...}</li>
 *   <li>{@code GET /api/v1/bhxh/reports/d02-lt.xml?...}</li>
 *   <li>{@code GET /api/v1/bhxh/reports/d03-lt?...}</li>
 *   <li>{@code GET /api/v1/bhxh/reports/d03-lt.xml?...}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/bhxh/reports")
public class BhxhReportController {

    private final BhxhReportService reportService;
    private final BhxhXmlExporter xmlExporter;

    public BhxhReportController(BhxhReportService reportService, BhxhXmlExporter xmlExporter) {
        this.reportService = reportService;
        this.xmlExporter = xmlExporter;
    }

    @GetMapping("/d02-lt")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public BhxhReportD02LTDto d02ltJson(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam String maDonViBHXH,
            @RequestParam(required = false) String tenDonVi,
            @RequestParam(required = false) String maSoThueDonVi) {
        String nguoiLap = String.valueOf(AuthContext.currentUserIdOrNull());
        return reportService.generateD02LT(tuNgay, denNgay, maDonViBHXH, tenDonVi, maSoThueDonVi, nguoiLap);
    }

    @GetMapping(value = "/d02-lt.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public ResponseEntity<byte[]> d02ltXml(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam String maDonViBHXH,
            @RequestParam(required = false) String tenDonVi,
            @RequestParam(required = false) String maSoThueDonVi) {
        String nguoiLap = String.valueOf(AuthContext.currentUserIdOrNull());
        BhxhReportD02LTDto r = reportService.generateD02LT(
                tuNgay, denNgay, maDonViBHXH, tenDonVi, maSoThueDonVi, nguoiLap);
        byte[] bytes = xmlExporter.exportD02LT(r).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = String.format("D02-LT_%s_%s.xml", maDonViBHXH, tuNgay);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    @GetMapping("/d03-lt")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public BhxhReportD03LTDto d03ltJson(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam String maDonViBHXH,
            @RequestParam(required = false) String tenDonVi) {
        return reportService.generateD03LT(tuNgay, denNgay, maDonViBHXH, tenDonVi);
    }

    @GetMapping(value = "/d03-lt.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public ResponseEntity<byte[]> d03ltXml(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam String maDonViBHXH,
            @RequestParam(required = false) String tenDonVi) {
        BhxhReportD03LTDto r = reportService.generateD03LT(tuNgay, denNgay, maDonViBHXH, tenDonVi);
        byte[] bytes = xmlExporter.exportD03LT(r).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = String.format("D03-LT_%s_%s.xml", maDonViBHXH, tuNgay);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }
}
