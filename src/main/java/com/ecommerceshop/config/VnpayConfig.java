package com.ecommerceshop.config;

import static com.ecommerceshop.ulti.HmacSHA512.hmacSHA512;

import java.util.*;

public class VnpayConfig {
  public static final String vnp_TmnCode = "INH1VFUR";
  public static final String vnp_HashSecret = "7830GKCBXWAV8AXC03AJ91Y5N2478FV0";
  public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
  public static final String vnp_ReturnUrl = "http://localhost:8080/ecommerceshop/vnpay-return";

  // Util for VNPAY
  public static String hashAllFields(Map fields) {
    List fieldNames = new ArrayList(fields.keySet());
    Collections.sort(fieldNames);
    StringBuilder sb = new StringBuilder();
    Iterator itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = (String) itr.next();
      String fieldValue = (String) fields.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        sb.append(fieldName);
        sb.append("=");
        sb.append(fieldValue);
      }
      if (itr.hasNext()) {
        sb.append("&");
      }
    }
    return hmacSHA512(vnp_HashSecret, sb.toString());
  }
}
