<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Chi tiết sản phẩm - ${sp.getTenSanPham()}</title>
	<!-- Bootstrap CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
	<!-- Font Awesome -->
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
	<!-- Custom CSS -->
	<link rel="stylesheet" href="<c:url value='/Frontend/css/detailsp.css'/>">
</head>
<body>
<div class="container-fluid">
	<div class="main-container fade-in">
		<div class="row g-0">
			<!-- Product Image Section -->
			<div class="col-lg-6">
				<div class="product-image-section">
					<img src="/ecommerceshop/img/${sp.getId()}.png"
						 alt="${sp.getTenSanPham()}"
						 class="product-image"
						 onerror="this.src='https://via.placeholder.com/400x300?text=No+Image'">
				</div>
			</div>

			<!-- Product Details Section -->
			<div class="col-lg-6">
				<div class="product-details">
					<p style="display:none" id="spid">${sp.getId()}</p>

					<h1 class="product-title slide-up">${sp.getTenSanPham()}</h1>

					<!-- Price Section -->
					<div class="price-section slide-up">
						<div class="price-label">
							<i class="fas fa-tag"></i> Giá bán
						</div>
						<div class="price-value" id="priceConvert">
							<!-- Giá sẽ được load bởi JavaScript -->
						</div>
					</div>

					<!-- Specifications -->
					<div class="specs-section slide-up">
						<h3 class="specs-title">
							<i class="fas fa-info-circle"></i>
							Thông số kỹ thuật
						</h3>

						<c:if test="${not empty sp.getCpu()}">
							<div class="spec-item">
								<span class="spec-label"><i class="fas fa-microchip"></i> CPU:</span>
								<span class="spec-value">${sp.getCpu()}</span>
							</div>
						</c:if>

						<c:if test="${not empty sp.getRam()}">
							<div class="spec-item">
								<span class="spec-label"><i class="fas fa-memory"></i> RAM:</span>
								<span class="spec-value">${sp.getRam()}</span>
							</div>
						</c:if>

						<c:if test="${not empty sp.getManHinh()}">
							<div class="spec-item">
								<span class="spec-label"><i class="fas fa-desktop"></i> Màn hình:</span>
								<span class="spec-value">${sp.getManHinh()}</span>
							</div>
						</c:if>

						<c:if test="${not empty sp.getHeDieuHanh()}">
							<div class="spec-item">
								<span class="spec-label"><i class="fab fa-windows"></i> Hệ điều hành:</span>
								<span class="spec-value">${sp.getHeDieuHanh()}</span>
							</div>
						</c:if>

						<c:if test="${not empty sp.getThietKe()}">
							<div class="spec-item">
								<span class="spec-label"><i class="fas fa-palette"></i> Thiết kế:</span>
								<span class="spec-value">${sp.getThietKe()}</span>
							</div>
						</c:if>

						<c:if test="${not empty sp.getDungLuongPin()}">
							<div class="spec-item">
								<span class="spec-label"><i class="fas fa-battery-three-quarters"></i> Pin:</span>
								<span class="spec-value">${sp.getDungLuongPin()}</span>
							</div>
						</c:if>

						<div class="spec-item">
							<span class="spec-label"><i class="fas fa-industry"></i> Hãng:</span>
							<span class="spec-value">${sp.hangSanXuat.tenHangSanXuat}</span>
						</div>
					</div>

					<!-- Additional Info -->
					<div class="specs-section slide-up">
						<h3 class="specs-title">
							<i class="fas fa-clipboard-list"></i>
							Thông tin chi tiết
						</h3>

						<div class="spec-item">
							<span class="spec-label"><i class="fas fa-info"></i> Thông tin chung:</span>
							<span class="spec-value">${sp.getThongTinChung()}</span>
						</div>

						<div class="spec-item">
							<span class="spec-label"><i class="fas fa-shield-alt"></i> Bảo hành:</span>
							<span class="spec-value">${sp.getThongTinBaoHanh()}</span>
						</div>
					</div>

					<!-- Action Section -->
					<div class="action-section slide-up">
						<button class="add-to-cart-btn btn-lg w-100" type="button">
							<i class="fas fa-shopping-cart"></i>
							Thêm vào giỏ hàng
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Similar Products Section -->
	<div class="similar-products-section">
		<div class="container">
			<h2 class="section-title">Sản phẩm cùng danh mục</h2>

			<!-- Product count info -->
			<div class="row mb-4">
				<div class="col-12 text-center">
					<p class="text-muted" id="productCountInfo">
						<!-- Thông tin số lượng sản phẩm -->
					</p>
				</div>
			</div>

			<!-- Products Grid -->
			<div class="row" id="similarProducts">
				<!-- Loading spinner -->
				<div class="loading-spinner">
					<div class="spinner"></div>
				</div>
			</div>

			<!-- Pagination -->
			<div class="row mt-4">
				<div class="col-12">
					<nav aria-label="Similar products pagination">
						<ul class="pagination justify-content-center" id="paginationControls">
							<!-- Pagination sẽ được tạo bởi JavaScript -->
						</ul>
					</nav>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/accounting.js/0.4.2/accounting.min.js"></script>

<!-- Product Data -->
<script type="text/javascript">
	// Biến global chứa thông tin sản phẩm
	var productData = {
		id: ${sp.getId()},
		donGia: ${sp.getDonGia()},
		danhMucId: ${sp.getDanhMuc().getId()}
	};
</script>

<!-- Load file JS riêng -->
<script src="<c:url value='/js/client/detailspAjax.js'/>" ></script>
</body>
</html>