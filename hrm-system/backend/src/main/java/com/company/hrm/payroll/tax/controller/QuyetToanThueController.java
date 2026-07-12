package com.company.hrm.payroll.tax.controller;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.payroll.tax.dto.CamKet08Dto;
import com.company.hrm.payroll.tax.dto.QuyetToan02QTTDto;
import com.company.hrm.payroll.tax.dto.QuyetToan05QTTDto;
import com.company.hrm.payroll.tax.service.QuyetToanThueService;
import com.company.hrm.payroll.tax.service.QuyetToanThueXmlExporter;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

/**
 * T16 - REST controller cho Quyet toan thue TNCN.
 *
 * <p>Endpoint:
 * <ul>
 *   <li>GET /api/v1/payroll/tax/qtt/02?nam=2026&maDonVi=... - JSON</li>
 *   <li>GET /api/v1/payroll/tax/qtt/02.xml?nam=2026&maDonVi=... - XML download</li>
 *   <li>GET /api/v1/payroll/tax/qtt/05?nam=2026&nhanVienId=... - JSON</li>
 *   <li>GET /api/v1/payroll/tax/qtt/05.xml?nam=2026&nhanVienId=... - XML download</li>
 *   <li>POST /api/v1/payroll/tax/cam-ket-08 - upsert</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/payroll/tax")
public class QuyetToanThueController {

    private final QuyetToanThueService service;
    private final QuyetToanThueXmlExporter xmlExporter;

    public QuyetToanThueController(QuyetToanThueService service,
                                   QuyetToanThueXmlExporter xmlExporter) {
        this.service = service;
        this.xmlExporter = xmlExporter;
    }

    @GetMapping("/qtt/02")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public QuyetToan02QTTDto qtt02Json(@RequestParam Integer nam,
                                         @RequestParam String maDonVi,
                                         @RequestParam(required = false) String tenDonVi,
                                         @RequestParam(required = false) String maSoThue) {
        String nguoiLap = String.valueOf(AuthContext.currentUserIdOrNull());
        return service.generate02QTT(nam, maDonVi, tenDonVi, maSoThue, nguoiLap);
    }

    @GetMapping(value = "/qtt/02.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public ResponseEntity<byte[]> qtt02Xml(@RequestParam Integer nam,
                                           @RequestParam String maDonVi,
                                           @RequestParam(required = false) String tenDonVi,
                                           @RequestParam(required = false) String maSoThue) {
        String nguoiLap = String.valueOf(AuthContext.currentUserIdOrNull());
        QuyetToan02QTTDto r = service.generate02QTT(nam, maDonVi, tenDonVi, maSoThue, nguoiLap);
        byte[] bytes = xmlExporter.export02QTT(r).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"02-QTT_" + maDonVi + "_" + nam + ".xml\"")
                .body(bytes);
    }

    @GetMapping("/qtt/05")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR, Role.MANAGER})
    public QuyetToan05QTTDto qtt05Json(@RequestParam Integer nam,
                                         @RequestParam UUID nhanVienId,
                                         @RequestParam(required = false) String tenDonVi,
                                         @RequestParam(required = false) String maSoThueDonVi) {
        return service.generate05QTT(nam, nhanVienId, tenDonVi, maSoThueDonVi);
    }

    @GetMapping(value = "/qtt/05.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR, Role.MANAGER})
    public ResponseEntity<byte[]> qtt05Xml(@RequestParam Integer nam,
                                           @RequestParam UUID nhanVienId,
                                           @RequestParam(required = false) String tenDonVi,
                                           @RequestParam(required = false) String maSoThueDonVi) {
        QuyetToan05QTTDto r = service.generate05QTT(nam, nhanVienId, tenDonVi, maSoThueDonVi);
        byte[] bytes = xmlExporter.export05QTT(r).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"05-QTT_" + r.getMaNv() + "_" + nam + ".xml\"")
                .body(bytes);
    }

    @PostMapping("/cam-ket-08")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public CamKet08Dto upsertCamKet08(@Valid @RequestBody CamKet08Dto dto) {
        return service.upsertCamKet08(dto);
    }
}
