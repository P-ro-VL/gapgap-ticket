package vn.hoangshitposting.gapgapticket.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ShippingService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String LOGIN_URL = "https://partner.viettelpost.vn/v2/user/Login";
    private static final String PRICE_URL = "https://partner.viettelpost.vn/v2/order/getPriceAllNlp";

    public String getAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Cookie", "SERVERID=E");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("USERNAME", "0787097309");
            requestBody.put("PASSWORD", "Linh123!@#");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    LOGIN_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return data != null ? (String) data.get("token") : null;
            } else {
                throw new RuntimeException("Failed to get token, status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> calculateShippingFee(String address, double totalPrice) {
        try {
            String token = getAccessToken();
            if (token == null) throw new RuntimeException("Token is null");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", token);
            headers.set("Cookie", "SERVERID=E");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("SENDER_ADDRESS", "207 Giải Phóng, phường Đồng Tâm, quận Hai Bà Trưng, thành phố Hà Nội");
            requestBody.put("RECEIVER_ADDRESS", address);
            requestBody.put("PRODUCT_TYPE", "HH");
            requestBody.put("PRODUCT_WEIGHT", 200);
            requestBody.put("PRODUCT_PRICE", totalPrice);
            requestBody.put("MONEY_COLLECTION", "0");
            requestBody.put("PRODUCT_LENGTH", 0);
            requestBody.put("PRODUCT_WIDTH", 0);
            requestBody.put("PRODUCT_HEIGHT", 0);
            requestBody.put("TYPE", 1);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    PRICE_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) response.getBody().get("RESULT");
                if (result != null) {
                    return result.stream()
                            .filter(item -> "STK".equals(item.get("MA_DV_CHINH")))
                            .findFirst()
                            .orElse(null);
                }
            }
        } catch (Exception e) {
            System.err.println("Shipping Fee Error: " + e.getMessage());
        }

        return null;
    }
}
