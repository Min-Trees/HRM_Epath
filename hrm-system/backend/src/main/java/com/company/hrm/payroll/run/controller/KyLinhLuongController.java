package com.company.hrm.payroll.run.controller;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.common.security.RequiresRole;
import com.company.hrm.common.security.Role;
import com.company.hrm.payroll.run.dto.KyLinhLuongDto;
import com.company.hrm.payroll.run.dto.PayslipDto;
import com.company.hrm.payroll.run.service.KyLinhLuongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * T19 - REST controller cho Module Payroll Run.
 */
@RestController
@RequestMapping("/api/v1/payroll/run")
public class KyLinhLuongController {

    private final KyLinhLuongService service;

    public KyLinhLuongController(KyLinhLuongService service) {
        this.service = service;
    }

    @GetMapping("/ky-luong")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public List<KyLinhLuongDto> list() {
        return service.findAll();
    }

    @GetMapping("/ky-luong/{id}")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public KyLinhLuongDto get(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping("/ky-luong")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public KyLinhLuongDto create(@Valid @RequestBody KyLinhLuongDto dto) {
        return service.create(dto);
    }

    @PostMapping("/ky-luong/{id}/start")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public KyLinhLuongDto start(@PathVariable UUID id) {
        return service.startRun(id, AuthContext.currentUserIdOrNull());
    }

    @PostMapping("/ky-luong/{id}/approve-cap-1")
    @RequiresRole({Role.HR})
    public KyLinhLuongDto approveCap1(@PathVariable UUID id) {
        return service.approveCap1(id, AuthContext.currentUserIdOrNull());
    }

    @PostMapping("/ky-luong/{id}/approve-cap-2")
    @RequiresRole({Role.HR, Role.PAYROLL_ACCOUNTANT})
    public KyLinhLuongDto approveCap2(@PathVariable UUID id) {
        return service.approveCap2(id, AuthContext.currentUserIdOrNull());
    }

    @PostMapping("/ky-luong/{id}/pay-paid")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.PAYROLL_ACCOUNTANT})
    public KyLinhLuongDto payPaid(@PathVariable UUID id,
                                    @RequestParam(required = false) String fileZipUrl) {
        return service.payrollPaid(id, AuthContext.currentUserIdOrNull(), fileZipUrl);
    }

    @PostMapping("/ky-luong/{id}/cancel")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR})
    public KyLinhLuongDto cancel(@PathVariable UUID id, @RequestParam String lyDo) {
        return service.cancel(id, AuthContext.currentUserIdOrNull(), lyDo);
    }

    @GetMapping("/ky-luong/{id}/payslip/{nhanVienId}")
    @RequiresRole({Role.PAYROLL_ACCOUNTANT, Role.HR, Role.PAYROLL_ACCOUNTANT, Role.EMPLOYEE})
    public ResponseEntity<byte[]> payslip(@PathVariable UUID id, @PathVariable UUID nhanVienId) {
        PayslipDto p = service.getPayslip(id, nhanVienId);
        byte[] bytes = renderPayslipHtml(p).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=phieu-luong-" + p.getMaNv() + "-" +
                        String.format("%02d", p.getThang()) + "-" + p.getNam() + ".html")
                .body(bytes);
    }

    private String renderPayslipHtml(PayslipDto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-8\"><title>Phieu luong</title>").append(
                "<style>body{font-family:sans-serif;padding:30px;max-width:760px;margin:auto;}");
        sb.append("h1{text-align:center;}table{width:100%;border-collapse:collapse;}");
        sb.append("th,td{border:1px solid #cbd5e1;padding:8px;text-align:left;}");
        sb.append("th{background:#f1f5f9;}.right{text-align:right;}");
        sb.append(".total{font-weight:bold;background:#fef9c3;}</style></head><body>");
        sb.append("<h1>PHIEU LUONG</h1>");
        sb.append("<p>Ky: ").append(String.format("%02d", p.getThang())).append("/").append(p.getNam()).append("</p>");
        sb.append("<p>Ma NV: <b>").append(p.getMaNv()).append("</b> - Ho ten: <b>").append(p.getHoTen()).append("</b></p>");
        sb.append("<h3>I. Cac khoan thu</h3>");
        sb.append("<table><tr><th>Khoan</th><th class=\"right\">So tien (VND)</th></tr>");
        for (var l : p.getChiTietThuong()) {
            sb.append("<tr><td>").append(l.getTenKhoan()).append("</td>")
                    .append("<td class=\"right\">").append(l.getSoTien().toPlainString()).append("</td></tr>");
        }
        sb.append("<tr class=\"total\"><td>Tong thu</td><td class=\"right\">")
                .append(p.getTongKhoanThu().toPlainString()).append("</td></tr></table>");
        sb.append("<h3>II. Cac khoan tru</h3>");
        sb.append("<table><tr><th>Khoan</th><th class=\"right\">So tien (VND)</th></tr>");
        for (var l : p.getChiTietKhauTru()) {
            sb.append("<tr><td>").append(l.getTenKhoan()).append("</td>")
                    .append("<td class=\"right\">").append(l.getSoTien().toPlainString()).append("</td></tr>");
        }
        sb.append("<tr class=\"total\"><td>Tong tru</td><td class=\"right\">")
                .append(p.getTongKhoanTru().toPlainString()).append("</td></tr></table>");
        sb.append("<h3 style=\"color:#dc2626\">THUC LINH: ")
                .append(p.getThucLinh().toPlainString()).append(" VND</h3>");
        sb.append("<p style=\"margin-top:40px;font-size:12px;color:#64748b\">Phieu luong duoc tao tu dong boi He thong HRM_Epath.</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
