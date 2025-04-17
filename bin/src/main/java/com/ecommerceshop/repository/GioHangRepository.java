package com.ecommerceshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerceshop.entities.GioHang;
import com.ecommerceshop.entities.NguoiDung;

public interface GioHangRepository extends JpaRepository<GioHang, Long>{
	
	GioHang findByNguoiDung(NguoiDung n);
	
}
