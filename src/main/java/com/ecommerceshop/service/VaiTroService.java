package com.ecommerceshop.service;

import java.util.List;

import com.ecommerceshop.entities.VaiTro;

public interface VaiTroService {

	VaiTro findByTenVaiTro(String tenVaiTro);
	List<VaiTro> findAllVaiTro();
}
