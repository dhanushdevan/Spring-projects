package com.iagami.Paymentgateway.Service;

import com.iagami.Paymentgateway.Dto.APIGetDto;
import com.iagami.Paymentgateway.Dto.GetFromAnotherApi;
import com.iagami.Paymentgateway.Dto.PaymentGetData;
import com.iagami.Paymentgateway.ServiceInterface.ServiceInterface;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ServiceRazorpay implements ServiceInterface {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    @Autowired
    private RestTemplate restTemplate;
    private static final String WEBHOOK_SECRET = "Deva8838."; 
    List<GetFromAnotherApi> dataList = List.of(
    	    new GetFromAnotherApi("Dhanush", 101),
    	    new GetFromAnotherApi("Deva", 202),
    	    new GetFromAnotherApi("Kumar", 303)
    	);


    @Override
    public String paymentapi(PaymentGetData paymentGetData) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecretKey);

        JSONObject options = new JSONObject();
        options.put("amount", paymentGetData.getAmount()); // in paise (10000 = ₹100)
        options.put("currency", paymentGetData.getCurrency());
        options.put("receipt", paymentGetData.getRecepitId());
        options.put("payment_capture", 1); // auto-capture

        Order order = razorpayClient.orders.create(options);
//        Payment payment =razorpayClient.payments.capture(order., options);
        
        System.out.println(order.toString());
        return order.toString();
    }

    public void handleWebhook(String payload, String receivedSignature) throws Exception {
        String secret = "Deva8838."; // 🔐 Razorpay webhook secret (not API secret key)

        String computedSignature = generateSignature(payload, secret);

        System.out.println("🧮 Computed Signature: " + computedSignature);
        System.out.println("📩 Received Signature: " + receivedSignature);

        if (!computedSignature.equals(receivedSignature)) {
            throw new Exception("Invalid webhook signature");
        }

        // ✅ Process the payload here (convert to object, etc.)
        System.out.println("✅ Webhook signature verified!");
    }


    public static String generateSignature(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }


	@Override
	public GetFromAnotherApi handledata(String name) {
		GetFromAnotherApi  getFromAnotherApi= new GetFromAnotherApi();
		for(GetFromAnotherApi g:dataList) {
			if(g.getName().equals(name)){
				
				getFromAnotherApi.setName(name);
				getFromAnotherApi.setNumber(g.getNumber());
				getFromAnotherApi.setOrders(restTemplate.getForObject("http://localhost:8080/getorder/"+name,APIGetDto.class));
				
			}else {
				return getFromAnotherApi;
			}
		}
		return null;
	}
    @Override
	 public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
	        try {
	            String data = razorpayOrderId + "|" + razorpayPaymentId;
	            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecretKey);
	            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	            SecretKeySpec secretKey = new SecretKeySpec(apiSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	            sha256_HMAC.init(secretKey);

	            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
	            String generatedSignature = bytesToHex(hash);

	            System.out.println("🔢 Order ID: " + razorpayOrderId);
	            System.out.println("🔢 Payment ID: " + razorpayPaymentId);
	            System.out.println("📦 Data for HMAC: " + data);
	            System.out.println("✅ Generated Signature: " + generatedSignature);
	            System.out.println("🧾 Received Signature: " + razorpaySignature);
	            Payment payment = razorpayClient.payments.fetch(razorpayPaymentId);
	            Order order=razorpayClient.orders.fetch(razorpayOrderId);
	            System.out.println(order.toString());
System.out.println(payment.toString());
	            return generatedSignature.equals(razorpaySignature);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    private String bytesToHex(byte[] hash) {
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : hash) {
	            String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    }
}
