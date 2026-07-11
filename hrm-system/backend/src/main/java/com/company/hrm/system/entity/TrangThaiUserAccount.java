package com.company.hrm.system.entity;

/**
 * Trạng thái tài khoản người dùng hệ thống.
 *
 * <ul>
 *   <li>{@link #ACTIVE}: hoạt động bình thường.</li>
 *   <li>{@link #LOCKED}: bị khoá bởi admin (sai mật khẩu nhiều lần, hoặc nghỉ việc).</li>
 *   <li>{@link #PENDING}: mới tạo, chưa kích hoạt email.</li>
 * </ul>
 */
public enum TrangThaiUserAccount {
    ACTIVE,
    LOCKED,
    PENDING
}