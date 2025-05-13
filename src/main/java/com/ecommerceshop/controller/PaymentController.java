package com.ecommerceshop.controller;

import com.ecommerceshop.config.VnpayConfig;
import com.ecommerceshop.dto.AmountMoney;
import com.ecommerceshop.entities.*;
import com.ecommerceshop.service.*;
import com.ecommerceshop.ulti.HmacSHA512;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.ecommerceshop.ulti.HmacSHA512.hmacSHA512;
import static com.ecommerceshop.ulti.Sha256.sha256;

@Controller
public class PaymentController {

    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private ChiMucGioHangService chiMucGioHangService;
    @Autowired
    private DonHangService donHangService;
    @Autowired
    private ChiTietDonHangService chiTietDonHangService;

    @PostMapping("/create-payment")
    public void createPayment(
            @RequestParam("amount") long amount,
            @ModelAttribute("donhang") DonHang donhang,
            HttpServletRequest req,
            HttpServletResponse response,
            Model model
    ) throws Exception {


        donhang.setNgayDatHang(new Date());
        donhang.setTrangThaiDonHang("Đang chờ giao");

        NguoiDung currentUser = getSessionUser(req);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<Long,String> quanity = new HashMap<Long,String>();
        List<SanPham> listsp = new ArrayList<SanPham>();
        List<ChiTietDonHang> listDetailDH = new ArrayList<ChiTietDonHang>();

        if(auth == null || auth.getPrincipal() == "anonymousUser")     //Lay tu cookie
        {
            DonHang d = donHangService.save(donhang);
            Cookie cl[] = req.getCookies();
            Set<Long> idList = new HashSet<Long>();
            for(int i=0; i< cl.length; i++)
            {
                if(cl[i].getName().matches("[0-9]+"))
                {
                    idList.add(Long.parseLong(cl[i].getName()));
                    quanity.put(Long.parseLong(cl[i].getName()), cl[i].getValue());
                }
            }
            listsp = sanPhamService.getAllSanPhamByList(idList);
            for(SanPham sp: listsp)
            {
                ChiTietDonHang detailDH = new ChiTietDonHang();
                detailDH.setSanPham(sp);
                detailDH.setSoLuongDat(Integer.parseInt(quanity.get(sp.getId())));
                detailDH.setDonGia(Integer.parseInt(quanity.get(sp.getId()))*sp.getDonGia());
                detailDH.setDonHang(d);
                listDetailDH.add(detailDH);
            }
        }else     //Lay tu database
        {
            donhang.setNguoiDat(currentUser);
            DonHang d = donHangService.save(donhang);
            GioHang g = gioHangService.getGioHangByNguoiDung(currentUser);
            List<ChiMucGioHang> listchimuc = chiMucGioHangService.getChiMucGioHangByGioHang(g);
            for(ChiMucGioHang c: listchimuc)
            {
                ChiTietDonHang detailDH = new ChiTietDonHang();
                detailDH.setSanPham(c.getSanPham());
                detailDH.setDonGia(c.getSo_luong()*c.getSanPham().getDonGia());
                detailDH.setSoLuongDat(c.getSo_luong());
                detailDH.setDonHang(d);
                listDetailDH.add(detailDH);

                listsp.add(c.getSanPham());
                quanity.put(c.getSanPham().getId(), Integer.toString(c.getSo_luong()));
            }

        }

        chiTietDonHangService.save(listDetailDH);










//        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_TxnRef = getRandomNumber(8);
        String vnp_OrderInfo = "Thanh toan don hang:" + vnp_TxnRef;
        String vnp_IpAddr = "127.0.0.1";
        String vnp_OrderType = "other";
//        long amount = 100000;
        System.out.println("qqqqqqqqqqww" + amount * 100);

        // Thời gian tạo và hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VnpayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // nhân 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_BankCode", "VNBANK");

        // Sắp xếp tham số theo alphabet
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8)).append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }
        String secureHash = hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        String paymentUrl = VnpayConfig.vnp_Url + "?" + query.toString();
        System.out.println("Sorted hashData: " + hashData.toString());
        System.out.println("SecureHash (local): " + secureHash);
        System.out.println("Payment URL: " + paymentUrl);
        response.sendRedirect(paymentUrl);
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public NguoiDung getSessionUser(HttpServletRequest request) {
        return (NguoiDung) request.getSession().getAttribute("loggedInUser");
    }
}
