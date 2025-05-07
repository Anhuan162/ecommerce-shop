<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh Toán Thành Công</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            color: #333;
        }

        .container {
            max-width: 600px;
            margin: 50px auto;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 20px;
            text-align: center;
        }

        .header {
            font-size: 24px;
            font-weight: bold;
            color: #1e90ff; /* Blue color */
        }

        .icon {
            font-size: 50px;
            color: #1e90ff;
            margin-top: 20px;
        }

        .message {
            font-size: 18px;
            color: #555;
            margin-top: 20px;
        }

        .details {
            font-size: 16px;
            color: #777;
            margin-top: 10px;
        }

        .button {
            margin-top: 30px;
            background-color: #1e90ff; /* Blue color */
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }

        .button:hover {
            background-color: #4682b4; /* Darker blue */
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        Thanh Toán Thành Công
    </div>
    <div class="icon">
        &#x1F4B5; <!-- tiền - icon -->
    </div>
    <div class="message">
        Cảm ơn bạn! Thanh toán của bạn đã được xử lý thành công.
    </div>
    <div class="details">
        <p><strong>Mã giao dịch:</strong> ${vnp_TxnRef}</p>

        <%
            long amount = Long.parseLong(request.getAttribute("vnp_Amount").toString()) / 100;
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedAmount = formatter.format(amount);
        %>

        <p><strong>Số tiền:</strong> <%= formattedAmount %> VND</p>
    <%--        <p><strong>Số tiền:</strong> ${vnp_Amount/100} VND</p>--%>
        <p><strong>Mô tả:</strong> ${vnp_OrderInfo}</p>
    </div>
    <button class="button" onclick="window.location.href='/ecommerceshop'">Quay lại trang chủ</button>
</div>
</body>
</html>
