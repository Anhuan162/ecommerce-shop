package com.ecommerceshop.api.admin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerceshop.dto.SanPhamDto;
import com.ecommerceshop.dto.SearchSanPhamObject;
import com.ecommerceshop.entities.ResponseObject;
import com.ecommerceshop.entities.SanPham;
import com.ecommerceshop.service.SanPhamService;
import com.ecommerceshop.validator.SanPhamDtoValidator;

@RestController
@RequestMapping("api/san-pham")
public class SanPhamApi {

	@Autowired
	private SanPhamDtoValidator validator;

	@Autowired
	private SanPhamService sanPhamService;

	@InitBinder("sanPhamDto")
	protected void initialiseBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	// lấy tất cả san phẩm theo tiêu chí, mặc địch lấy tất cả 
	@GetMapping("/all")
	public Page<SanPham> getAllSanPhamByFilter(@RequestParam(defaultValue = "1") int page, 
			 @RequestParam String danhMucId, @RequestParam String hangSXId, @RequestParam String donGia, @RequestParam String sapXepTheoGia) {
		SearchSanPhamObject searchObject = new SearchSanPhamObject();
		searchObject.setDanhMucId(danhMucId);
		searchObject.setHangSXId(hangSXId);
		searchObject.setDonGia(donGia);
		searchObject.setSapXepTheoGia(sapXepTheoGia);
		
		Page<SanPham> listSanPham = sanPhamService.getAllSanPhamByFilter(searchObject, page-1, 10);
		return listSanPham;
	}
	
	@GetMapping("/latest")
	public List<SanPham> getLatestSanPham(){
		return sanPhamService.getLatestSanPham();
	}

	// API mới với Paging
	@GetMapping("/danhmuc/{danhMucId}/similar/{excludeId}/paging")
	public ResponseEntity<Map<String, Object>> getSimilarProductsWithPaging(
			@PathVariable Long danhMucId,
			@PathVariable Long excludeId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		try {
			Page<SanPham> productsPage = sanPhamService.getSimilarProductsWithPaging(danhMucId, excludeId, page, size);

			Map<String, Object> response = new HashMap<>();
			response.put("products", productsPage.getContent());
			response.put("currentPage", productsPage.getNumber());
			response.put("totalItems", productsPage.getTotalElements());
			response.put("totalPages", productsPage.getTotalPages());
			response.put("hasNext", productsPage.hasNext());
			response.put("hasPrevious", productsPage.hasPrevious());
			response.put("isFirst", productsPage.isFirst());
			response.put("isLast", productsPage.isLast());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", "Không thể lấy dữ liệu sản phẩm");
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	// lấy sản phẩm theo id
	@GetMapping("/{id}")
	public SanPham getSanPhamById(@PathVariable long id) {
		return sanPhamService.getSanPhamById(id);
	}
	
	
	// lấy sản phẩm theo tên
	@GetMapping("/")
	public Page<SanPham> getSanPhamById(@RequestParam String tenSanPham, @RequestParam(defaultValue = "1") int page) {
		return sanPhamService.getSanPhamByTenSanPhamForAdmin(tenSanPham, page-1, 10 );
	}

	// lưu sản phẩm vào db
	@PostMapping(value = "/save")
	public ResponseObject addSanPham(@ModelAttribute @Valid SanPhamDto newSanPhamDto, BindingResult result,
			HttpServletRequest request) {

		ResponseObject ro = new ResponseObject();

		// nếu có lỗi xảy ra ( validate)
		if (result.hasErrors()) {
			Map<String, String> errors = result.getFieldErrors().stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
			errors.forEach((k, v) -> System.out.println(" test: Key : " + k + " Value : " + v));
			ro.setErrorMessages(errors);
			ro.setStatus("fail");
			errors = null;
		} else {
			// lưu sản phẩm
			SanPham sp = sanPhamService.save(newSanPhamDto);
			ro.setData(sp);
			saveImageForProduct(sp, newSanPhamDto, request);
			ro.setStatus("success");
		}
		return ro;
	}
	
	
	@DeleteMapping("/delete/{id}")
	public String deleteSanPham(@PathVariable long id) {
		sanPhamService.deleteById(id);
		return "OK !";
	}

	
	// lưu ảnh của sản phẩm vào thư mục
	public void saveImageForProduct(SanPham sp, SanPhamDto dto, HttpServletRequest request) {

		MultipartFile productImage = dto.getHinhAnh();
		String rootDirectory = request.getSession().getServletContext().getRealPath("/");
		Path path = Paths.get(rootDirectory + "/resources/images/" + sp.getId() + ".png");
		System.out.println(productImage != null && !productImage.isEmpty());
		if (productImage != null && !productImage.isEmpty()) {

			try {
				productImage.transferTo(new File(path.toString()));
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("Product image saving failed", ex);
			}
		}
	}
}
