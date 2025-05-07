calculateOrder()
var moneyOnlinePayment;
function calculateOrder()
{
	var element = document.getElementsByClassName("total");
	var res = 0;
	for (i = 0; i < element.length; i++) {
		res = res + parseInt(element[i].textContent);
	}
	var element2 = document.getElementById("ordertotal");
	
	resConvert = accounting.formatMoney(res);
	element2.innerHTML = resConvert+ " VND";
	var element3 = document.getElementById("tongGiaTri");
	moneyOnlinePayment = res;
	console.log(moneyOnlinePayment);
	element3.setAttribute("value",res);
	if(res == 0)
	{
		document.getElementById("submit").disabled = true;		
	}	
}


document.addEventListener("DOMContentLoaded", function () {
	const payButton = document.getElementById("pay-online-button");

	payButton.addEventListener("click", function (e) {
		e.preventDefault(); // Chặn form cũ submit

		// Tạo form ẩn để POST
		const form = document.createElement('form');
		form.method = 'POST';
		form.action = '/ecommerceshop/create-payment';

		// Lấy dữ liệu từ form gốc
		const hoTenNguoiNhan = document.querySelector('input[name="hoTenNguoiNhan"]').value;
		const sdtNhanHang = document.querySelector('input[name="sdtNhanHang"]').value;
		const diaChiNhan = document.querySelector('textarea[name="diaChiNhan"]').value;
		const tongGiaTri = document.querySelector('input[name="tongGiaTri"]').value;

		// Tạo input ẩn gửi kèm
		const amountInput = document.createElement('input');
		amountInput.type = 'hidden';
		amountInput.name = 'amount';
		amountInput.value = parseInt(moneyOnlinePayment);

		const hoTenInput = document.createElement('input');
		hoTenInput.type = 'hidden';
		hoTenInput.name = 'hoTenNguoiNhan';
		hoTenInput.value = hoTenNguoiNhan;

		const sdtInput = document.createElement('input');
		sdtInput.type = 'hidden';
		sdtInput.name = 'sdtNhanHang';
		sdtInput.value = sdtNhanHang;

		const diaChiInput = document.createElement('input');
		diaChiInput.type = 'hidden';
		diaChiInput.name = 'diaChiNhan';
		diaChiInput.value = diaChiNhan;

		const tongGiaTriInput = document.createElement('input');
		tongGiaTriInput.type = 'hidden';
		tongGiaTriInput.name = 'tongGiaTri';
		tongGiaTriInput.value = tongGiaTri;

		// Đưa các input vào form
		form.appendChild(amountInput);
		form.appendChild(hoTenInput);
		form.appendChild(sdtInput);
		form.appendChild(diaChiInput);
		form.appendChild(tongGiaTriInput);

		document.body.appendChild(form);
		form.submit();
	});
});
