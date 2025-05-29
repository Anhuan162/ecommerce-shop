// Global variables
let currentPage = 0;
let totalPages = 0;
let totalItems = 0;
const pageSize = 10;

$(document).ready(function(){
	// Format giá sản phẩm
	var priceConvert = accounting.formatMoney(productData.donGia, "₫", 0, ".", ",") + ' VND';
	document.getElementById("priceConvert").innerHTML = priceConvert;

	// Load sản phẩm tương tự - trang đầu tiên
	loadSimilarProducts(0);

	// Xử lý thêm vào giỏ hàng với animation
	$(".add-to-cart-btn").click(function(){
		var $btn = $(this);
		var originalText = $btn.html();

		// Loading state
		$btn.html('<i class="fas fa-spinner fa-spin"></i> Đang thêm...');
		$btn.prop('disabled', true);

		ajaxGet($("#spid").text(), function(success) {
			setTimeout(function() {
				$btn.html(originalText);
				$btn.prop('disabled', false);
			}, 1000);
		});
	});

	function ajaxGet(id, callback){
		$.ajax({
			type: "GET",
			url: "http://localhost:8080/ecommerceshop/api/gio-hang/addSanPham?id="+id,
			success: function(result){
				if(result.status == "false") {
					showNotification("Sản phẩm đang hết hàng, quý khách vui lòng quay lại sau", "error");
				} else {
					showNotification("Đã thêm sản phẩm vào giỏ hàng thành công!", "success");
				}
				if(callback) callback(true);
			},
			error : function(e){
				showNotification("Có lỗi xảy ra, vui lòng thử lại", "error");
				console.log("Error" , e );
				if(callback) callback(false);
			}
		});
	}

	// Notification function
	function showNotification(message, type) {
		// Create notification element
		var notification = $('<div class="notification ' + type + '">' + message + '</div>');

		// Add styles
		notification.css({
			'position': 'fixed',
			'top': '20px',
			'right': '20px',
			'background': type === 'success' ? '#2ecc71' : '#e74c3c',
			'color': 'white',
			'padding': '15px 25px',
			'border-radius': '10px',
			'box-shadow': '0 5px 15px rgba(0,0,0,0.3)',
			'z-index': '9999',
			'opacity': '0',
			'transform': 'translateX(100%)',
			'transition': 'all 0.3s ease'
		});

		$('body').append(notification);

		// Show notification
		setTimeout(function() {
			notification.css({
				'opacity': '1',
				'transform': 'translateX(0)'
			});
		}, 100);

		// Hide notification
		setTimeout(function() {
			notification.css({
				'opacity': '0',
				'transform': 'translateX(100%)'
			});
			setTimeout(function() {
				notification.remove();
			}, 300);
		}, 3000);
	}
});

// Function load sản phẩm tương tự với pagination
function loadSimilarProducts(page){
	var currentProductId = $("#spid").text();
	var danhMucId = productData.danhMucId;

	console.log("Loading similar products - Page: " + page + ", Category: " + danhMucId + ", Exclude: " + currentProductId);

	// Show loading spinner cho lần đầu
	if (page === 0) {
		$("#similarProducts").html('<div class="loading-spinner"><div class="spinner"></div></div>');
	}

	$.ajax({
		type: "GET",
		url: "http://localhost:8080/ecommerceshop/api/san-pham/danhmuc/" + danhMucId + "/similar/" + currentProductId + "/paging",
		data: {
			page: page,
			size: pageSize
		},
		success: function(response){
			console.log("Similar products loaded: ", response);

			// Update global variables
			currentPage = response.currentPage;
			totalPages = response.totalPages;
			totalItems = response.totalItems;

			// Display products
			displaySimilarProducts(response.products);

			// Update pagination
			updatePagination(response);

			// Update product count info
			updateProductCountInfo(response);
		},
		error: function(e){
			console.log("Error loading similar products: ", e);
			$("#similarProducts").html('<div class="col-12 text-center"><p class="text-danger">Không thể tải sản phẩm tương tự</p></div>');
			$("#paginationControls").empty();
			$("#productCountInfo").html('<span class="text-danger">Có lỗi xảy ra khi tải dữ liệu</span>');
		}
	});
}

// Function hiển thị sản phẩm
function displaySimilarProducts(products){
	var html = '';
	if(products && products.length > 0) {
		$.each(products, function(index, product){
			var formattedPrice = accounting.formatMoney(product.donGia, "₫", 0, ".", ",") + ' VND';
			html += '<div class="col-lg-3 col-md-4 col-sm-6 mb-4">';
			html += '  <div class="product-card">';
			html += '    <div style="overflow: hidden;">';
			html += '      <img src="/ecommerceshop/img/' + product.id + '.png" ';
			html += '           class="card-img-top product-card-img" ';
			html += '           alt="' + product.tenSanPham + '"';
			html += '           onerror="this.src=\'https://via.placeholder.com/300x200?text=No+Image\'">';
			html += '    </div>';
			html += '    <div class="product-card-body">';
			html += '      <h5 class="product-card-title">' + product.tenSanPham + '</h5>';
			html += '      <p class="product-card-price">' + formattedPrice + '</p>';
			html += '      <a href="/ecommerceshop/san-pham/' + product.id + '" class="view-detail-btn">';
			html += '        <i class="fas fa-eye"></i> Xem chi tiết';
			html += '      </a>';
			html += '    </div>';
			html += '  </div>';
			html += '</div>';
		});
	} else {
		html = '<div class="col-12 text-center"><p class="text-muted">Không có sản phẩm tương tự nào.</p></div>';
	}

	$("#similarProducts").html(html);

	// Add animation to cards
	$("#similarProducts .product-card").each(function(index) {
		$(this).css('animation-delay', (index * 0.1) + 's');
		$(this).addClass('fade-in');
	});

	// Scroll to products section smoothly nếu không phải trang đầu
	if (currentPage > 0) {
		$('html, body').animate({
			scrollTop: $("#similarProducts").offset().top - 100
		}, 500);
	}
}

// Function cập nhật pagination
function updatePagination(response) {
	var html = '';
	var currentPage = response.currentPage;
	var totalPages = response.totalPages;

	if (totalPages > 1) {
		// Previous button
		if (response.hasPrevious) {
			html += '<li class="page-item">';
			html += '  <a class="page-link" href="#" onclick="loadSimilarProducts(' + (currentPage - 1) + '); return false;">';
			html += '    <i class="fas fa-chevron-left"></i> Trước';
			html += '  </a>';
			html += '</li>';
		} else {
			html += '<li class="page-item disabled">';
			html += '  <span class="page-link"><i class="fas fa-chevron-left"></i> Trước</span>';
			html += '</li>';
		}

		// Page numbers với smart pagination
		var startPage = Math.max(0, currentPage - 2);
		var endPage = Math.min(totalPages - 1, currentPage + 2);

		// First page
		if (startPage > 0) {
			html += '<li class="page-item">';
			html += '  <a class="page-link" href="#" onclick="loadSimilarProducts(0); return false;">1</a>';
			html += '</li>';
			if (startPage > 1) {
				html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
			}
		}

		// Page range
		for (var i = startPage; i <= endPage; i++) {
			if (i === currentPage) {
				html += '<li class="page-item active">';
				html += '  <span class="page-link">' + (i + 1) + '</span>';
				html += '</li>';
			} else {
				html += '<li class="page-item">';
				html += '  <a class="page-link" href="#" onclick="loadSimilarProducts(' + i + '); return false;">' + (i + 1) + '</a>';
				html += '</li>';
			}
		}

		// Last page
		if (endPage < totalPages - 1) {
			if (endPage < totalPages - 2) {
				html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
			}
			html += '<li class="page-item">';
			html += '  <a class="page-link" href="#" onclick="loadSimilarProducts(' + (totalPages - 1) + '); return false;">' + totalPages + '</a>';
			html += '</li>';
		}

		// Next button
		if (response.hasNext) {
			html += '<li class="page-item">';
			html += '  <a class="page-link" href="#" onclick="loadSimilarProducts(' + (currentPage + 1) + '); return false;">';
			html += '    Sau <i class="fas fa-chevron-right"></i>';
			html += '  </a>';
			html += '</li>';
		} else {
			html += '<li class="page-item disabled">';
			html += '  <span class="page-link">Sau <i class="fas fa-chevron-right"></i></span>';
			html += '</li>';
		}
	}

	$("#paginationControls").html(html);
}

// Function cập nhật thông tin số lượng sản phẩm
function updateProductCountInfo(response) {
	var currentPage = response.currentPage;
	var totalItems = response.totalItems;
	var totalPages = response.totalPages;

	if (totalItems > 0) {
		var startItem = (currentPage * pageSize) + 1;
		var endItem = Math.min((currentPage + 1) * pageSize, totalItems);

		$("#productCountInfo").html(
			'Hiển thị ' + startItem + ' - ' + endItem + ' trong tổng số ' + totalItems + ' sản phẩm tương tự ' +
			'(Trang ' + (currentPage + 1) + '/' + totalPages + ')'
		);
	} else {
		$("#productCountInfo").html('Không có sản phẩm tương tự nào');
	}
}