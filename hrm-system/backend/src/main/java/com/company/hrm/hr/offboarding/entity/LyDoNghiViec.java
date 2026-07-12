package com.company.hrm.hr.offboarding.entity;

/**
 * Enum mapping cho bang ENUM {@code hr.ly_do_nghi_viec} (V14).
 *
 * <p>Cac hang muc ly do nghi viec:
 * <ul>
 *   <li>{@link #NGHI_VIEC_TU_NGUYEN} - NLD tu nguyen nghi (BLLD D44)</li>
 *   <li>{@link #SA_THAI} - Don phuong sa thai (BLLD D36)</li>
 *   <li>{@link #HET_HAN_HDLD} - Het han hop dong khong gia han</li>
 *   <li>{@link #NGHI_HUU} - Nghi huu (khong co tro cap thoi viec)</li>
 *   <li>{@link #THOI_VIEC} - Thoi viec theo thoa thuan</li>
 *   <li>{@link #KHAC}</li>
 * </ul>
 */
public enum LyDoNghiViec {
    NGHI_VIEC_TU_NGUYEN,
    SA_THAI,
    HET_HAN_HDLD,
    NGHI_HUU,
    THOI_VIEC,
    KHAC
}
