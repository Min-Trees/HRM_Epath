package com.company.hrm.hr.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PhongBanTreeNode {
    private UUID phongBanId;
    private String maPhongBan;
    private String tenPhongBan;
    private UUID phongBanChaId;
    private int dinhBien;
    private int capDo;
    private boolean active;
    private List<PhongBanTreeNode> children = new ArrayList<>();

    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public String getMaPhongBan() { return maPhongBan; }
    public void setMaPhongBan(String maPhongBan) { this.maPhongBan = maPhongBan; }
    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }
    public UUID getPhongBanChaId() { return phongBanChaId; }
    public void setPhongBanChaId(UUID phongBanChaId) { this.phongBanChaId = phongBanChaId; }
    public int getDinhBien() { return dinhBien; }
    public void setDinhBien(int dinhBien) { this.dinhBien = dinhBien; }
    public int getCapDo() { return capDo; }
    public void setCapDo(int capDo) { this.capDo = capDo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<PhongBanTreeNode> getChildren() { return children; }
    public void setChildren(List<PhongBanTreeNode> children) { this.children = children; }
}