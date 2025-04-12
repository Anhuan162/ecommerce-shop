package com.ecommerceshop.service;

import com.ecommerceshop.entities.GioHang;
import com.ecommerceshop.entities.NguoiDung;

public interface GioHangService {
	
	GioHang getGioHangByNguoiDung(NguoiDung n);
	
	GioHang save(GioHang g);
}
