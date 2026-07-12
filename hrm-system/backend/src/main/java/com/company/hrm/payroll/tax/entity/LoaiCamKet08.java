package com.company.hrm.payroll.tax.entity;

/**
 * T16 - Loai cam ket 08 dang ky voi co quan thue.
 *
 * <p>Theo Thong tu 92/2015:
 * <ul>
 *   <li>{@link #UY_QUYEN_QTT} - NV uy quyen cho DN quyet toan thay (mau 02/QTT)</li>
 *   <li>{@link #NV_TU_QTT} - NV tu quyet toan voi co quan thue</li>
 *   <li>{@link #CHUA_CO} - NV chua dang ky (mac dinh truoc 31/3 nam sau)</li>
 * </ul>
 */
public enum LoaiCamKet08 {
    UY_QUYEN_QTT,
    NV_TU_QTT,
    CHUA_CO
}
