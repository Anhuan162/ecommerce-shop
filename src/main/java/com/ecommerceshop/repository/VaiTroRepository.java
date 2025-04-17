package com.ecommerceshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerceshop.entities.VaiTro;

public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

	VaiTro findByTenVaiTro(String tenVaiTro);
}
