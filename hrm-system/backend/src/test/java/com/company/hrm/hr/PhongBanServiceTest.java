package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.dto.PhongBanRequest;
import com.company.hrm.hr.entity.PhongBan;
import com.company.hrm.hr.repository.PhongBanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PhongBanServiceTest {

    private PhongBanRepository repo;
    private PhongBanService service;

    @BeforeEach
    void setUp() {
        repo = mock(PhongBanRepository.class);
        service = new PhongBanService(repo);
        when(repo.save(any(PhongBan.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private PhongBanRequest req(String ma, String ten, UUID chaId) {
        PhongBanRequest r = new PhongBanRequest();
        r.setMaPhongBan(ma);
        r.setTenPhongBan(ten);
        r.setPhongBanChaId(chaId);
        r.setDinhBien(5);
        return r;
    }

    @Test
    void create_root_department_hasCapDo1() {
        when(repo.findByMaPhongBan("PGD")).thenReturn(Optional.empty());

        service.create(req("PGD", "Ban Giám đốc", null));

        ArgumentCaptor<PhongBan> captor = ArgumentCaptor.forClass(PhongBan.class);
        verify(repo).save(captor.capture());
        assertEquals(1, captor.getValue().getCapDo());
        assertNull(captor.getValue().getPhongBanChaId());
        assertTrue(captor.getValue().isActive());
    }

    @Test
    void create_child_department_inheritsCapDo() {
        UUID parentId = UUID.randomUUID();
        PhongBan parent = new PhongBan();
        parent.setPhongBanId(parentId);
        parent.setCapDo(2);
        when(repo.findByMaPhongBan("PIT")).thenReturn(Optional.empty());
        when(repo.findById(parentId)).thenReturn(Optional.of(parent));

        service.create(req("PIT", "Phòng IT", parentId));

        ArgumentCaptor<PhongBan> captor = ArgumentCaptor.forClass(PhongBan.class);
        verify(repo).save(captor.capture());
        assertEquals(3, captor.getValue().getCapDo());
    }

    @Test
    void create_duplicateMa_throws409() {
        when(repo.findByMaPhongBan("PGD")).thenReturn(Optional.of(new PhongBan()));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("PGD", "Trùng mã", null)));
        assertEquals("MA_PHONG_BAN_DUPLICATE", ex.getCode());
    }

    @Test
    void create_parentMissing_throws() {
        UUID ghost = UUID.randomUUID();
        when(repo.findByMaPhongBan("PXX")).thenReturn(Optional.empty());
        when(repo.findById(ghost)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("PXX", "x", ghost)));
        assertEquals("PHONG_BAN_CHA_NOT_FOUND", ex.getCode());
    }

    @Test
    void update_selfAsParent_throws() {
        UUID id = UUID.randomUUID();
        PhongBan e = new PhongBan();
        e.setPhongBanId(id);
        e.setMaPhongBan("A");
        e.setCapDo(1);
        when(repo.findById(id)).thenReturn(Optional.of(e));
        when(repo.findByMaPhongBan("A")).thenReturn(Optional.of(e));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(id, req("A", "A", id)));
        assertEquals("DEPARTMENT_PARENT_CYCLE", ex.getCode());
    }

    @Test
    void update_cycle_detected_throws() {
        UUID a = UUID.randomUUID(); // A là root
        UUID b = UUID.randomUUID(); // B là con của A
        PhongBan pa = new PhongBan();
        pa.setPhongBanId(a);
        pa.setCapDo(1);
        pa.setMaPhongBan("A");

        PhongBan pb = new PhongBan();
        pb.setPhongBanId(b);
        pb.setCapDo(2);
        pb.setMaPhongBan("B");
        pb.setPhongBanChaId(a);

        when(repo.findById(a)).thenReturn(Optional.of(pa));
        when(repo.findById(b)).thenReturn(Optional.of(pb));
        when(repo.findByMaPhongBan("A")).thenReturn(Optional.of(pa));

        // Thử đặt cha của A = B (B hiện đang là con A) -> tạo vòng lặp
        PhongBanRequest r = req("A", "A", b);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(a, r));
        assertEquals("DEPARTMENT_CYCLE_DETECTED", ex.getCode());
    }

    @Test
    void close_departmentWithChildren_throws() {
        UUID id = UUID.randomUUID();
        PhongBan e = new PhongBan();
        e.setPhongBanId(id);
        e.setActive(true);
        when(repo.findById(id)).thenReturn(Optional.of(e));
        when(repo.countByPhongBanChaId(id)).thenReturn(2L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.close(id));
        assertEquals("DEPARTMENT_HAS_CHILDREN", ex.getCode());
    }

    @Test
    void close_departmentWithoutChildren_succeeds() {
        UUID id = UUID.randomUUID();
        PhongBan e = new PhongBan();
        e.setPhongBanId(id);
        e.setActive(true);
        when(repo.findById(id)).thenReturn(Optional.of(e));
        when(repo.countByPhongBanChaId(id)).thenReturn(0L);

        service.close(id);
        assertFalse(e.isActive());
    }

    @Test
    void findTree_buildsNestedStructure() {
        UUID root = UUID.randomUUID();
        UUID child = UUID.randomUUID();
        PhongBan r = new PhongBan(); r.setPhongBanId(root); r.setMaPhongBan("R");
        r.setTenPhongBan("Root"); r.setCapDo(1); r.setActive(true);
        PhongBan c = new PhongBan(); c.setPhongBanId(child); c.setMaPhongBan("C");
        c.setTenPhongBan("Child"); c.setCapDo(2); c.setActive(true); c.setPhongBanChaId(root);
        when(repo.findAllByOrderByCapDoAscMaPhongBanAsc()).thenReturn(List.of(r, c));

        var tree = service.findTree();
        assertEquals(1, tree.size());
        assertEquals("R", tree.get(0).getMaPhongBan());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("C", tree.get(0).getChildren().get(0).getMaPhongBan());
    }
}