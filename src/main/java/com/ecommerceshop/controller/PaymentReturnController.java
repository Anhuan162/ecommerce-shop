package com.ecommerceshop.controller;

import com.ecommerceshop.config.VnpayConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentReturnController {
    //
//    @GetMapping("/vnpay-return")
//    public String forwardVnpayReturn(@RequestParam Map<String, String> allParams, Model model) {
//        Map<String, String> fields = new HashMap<>();
//        for (Map.Entry<String, String> entry : allParams.entrySet()) {
//            String fieldName = URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII);
//            String fieldValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII);
//            fields.put(fieldName, fieldValue);
//        }
//
//        String vnp_SecureHash = allParams.get("vnp_SecureHash");
//
//        fields.remove("vnp_SecureHashType");
//        fields.remove("vnp_SecureHash");
//
//        String signValue = VnpayConfig.hashAllFields(fields);
//
//        // Put all necessary attributes into model
//        model.addAttribute("vnp_TxnRef", allParams.get("vnp_TxnRef"));
//        model.addAttribute("vnp_Amount", allParams.get("vnp_Amount"));
//        model.addAttribute("vnp_OrderInfo", allParams.get("vnp_OrderInfo"));
//        model.addAttribute("vnp_ResponseCode", allParams.get("vnp_ResponseCode"));
//        model.addAttribute("vnp_TransactionNo", allParams.get("vnp_TransactionNo"));
//        model.addAttribute("vnp_BankCode", allParams.get("vnp_BankCode"));
//        model.addAttribute("vnp_PayDate", allParams.get("vnp_PayDate"));
//        model.addAttribute("transactionStatus", allParams.get("vnp_TransactionStatus"));
//
//        model.addAttribute("isValidSignature", signValue.equals(vnp_SecureHash));
//
//        return "client/payment-success";
//    }
    @GetMapping("/vnpay-return")
    public String showPaymentSuccess(@RequestParam Map<String, String> params, Model model) {
        // Lấy các tham số từ URL và đưa vào model để hiển thị trên trang JSP
        model.addAttribute("vnp_TxnRef", params.get("vnp_TxnRef"));
        model.addAttribute("vnp_Amount", params.get("vnp_Amount"));
        model.addAttribute("vnp_OrderInfo", params.get("vnp_OrderInfo"));

        return "client/payment-success"; // tên file JSP (payment_success.jsp)
    }
}
