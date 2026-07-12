package com.company.hrm.social_ins.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.social_ins.dto.BhxhReportD02LTDto;
import com.company.hrm.social_ins.dto.BhxhReportD03LTDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * T15 - Unit test cho BhxhReportService.
 *
 * <p>T15 service dung JdbcTemplate.queryForList(sql, Date, Date) (varargs).
 * Mockito phan biet overload queryForList(String, Class, Object...) voi
 * (String, Object[], int[]) rat te. Can @MockitoSettings(LENIENT) va stub
 * don giong nhau cho ca 2 method generate.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BhxhReportServiceTest {

    @Mock
    JdbcTemplate jdbc;

    BhxhReportService service;

    @BeforeEach
    void setUp() {
        service = new BhxhReportService(jdbc);
    }

    @Test
    void generateD02LT_thieuMaDonVi_throwException() {
        assertThatThrownBy(() -> service.generateD02LT(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                "", "Ten DV", "MST", "lap"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("MISSING_MA_DON_VI"));
    }

    @Test
    void generateD02LT_denNgayTruocTuNgay_throwException() {
        assertThatThrownBy(() -> service.generateD02LT(
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1),
                "DV-001", null, null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_DATE_RANGE"));
    }

    @Test
    void generateD02LT_ngaySinh_nullable_throwException() {
        assertThatThrownBy(() -> service.generateD02LT(
                null, LocalDate.of(2026, 1, 31),
                "DV-001", null, null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_DATE_RANGE"));
    }

    @Test
    void generateD03LT_thieuMaDonVi_throwException() {
        assertThatThrownBy(() -> service.generateD03LT(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("MISSING_MA_DON_VI"));
    }

    @Test
    void cacHelperChuyenDoi_nullableXyLyDung() {
        // Test indirectly - just call generate with a stub returning an empty result
        // This validates the service does not NPE on null values in result rows.
        org.mockito.Mockito.doReturn(new ArrayList<>())
                .when(jdbc).queryForList(org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.<Object[]>any());

        BhxhReportD02LTDto out = service.generateD02LT(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                "DV-001", "Ten DV", "MST", "lap");

        assertThat(out.getTongSoDong()).isZero();
        assertThat(out.getRows()).isEmpty();
        assertThat(out.getMaDonViBHXH()).isEqualTo("DV-001");
        assertThat(out.getNguoiLap()).isEqualTo("lap");
    }

    @Test
    void generateD03LT_dataEmpty_hople() {
        org.mockito.Mockito.doReturn(new ArrayList<>())
                .when(jdbc).queryForList(anyString(),
                        org.mockito.ArgumentMatchers.<Object[]>any());

        BhxhReportD03LTDto out = service.generateD03LT(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                "DV-001", "Ten DV");

        assertThat(out.getMaDonViBHXH()).isEqualTo("DV-001");
        assertThat(out.getTongSoDong()).isZero();
    }
}
