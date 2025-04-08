package com.iagami.Paymentgateway.Service;

import com.iagami.Paymentgateway.Dto.PaymentGetData;
import com.iagami.Paymentgateway.ServiceInterface.ServiceInterface;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class ServiceRazorpay implements ServiceInterface {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    private static final String WEBHOOK_SECRET = "Deva8838"; // Update to match your Razorpay dashboard

    @Override
    public String paymentapi(PaymentGetData paymentGetData) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecretKey);

        JSONObject options = new JSONObject();
        options.put("amount", paymentGetData.getAmount()); // in paise (10000 = ₹100)
        options.put("currency", paymentGetData.getCurrency());
        options.put("receipt", paymentGetData.getRecepitId());
        options.put("payment_capture", 1); // auto-capture

        Order order = razorpayClient.orders.create(options);
        System.out.println(order.toString());
        return order.toString();
    }

    @Override
    public void handleWebhook(String payload, String signature) throws Exception {
        String computedSignature = generateSignature(payload, WEBHOOK_SECRET);

        System.out.println("📩 Received Signature: " + signature);
        System.out.println("🧮 Computed Signature: " + computedSignature);
        System.out.println("📥 Payload: " + payload);

        if (!computedSignature.equals(signature)) {
            throw new Exception("Invalid webhook signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        switch (event) {
            case "payment.captured":
                System.out.println("✅ Payment captured logic here");
                break;
            case "payment.failed":
                System.out.println("❌ Payment failed logic here");
                break;
            case "order.paid":
                System.out.println("📦 Order paid logic here");
                break;
            case "refund.created":
                System.out.println("💸 Refund created logic here");
                break;
            case "subscription.charged":
                System.out.println("🔁 Subscription charged logic here");
                break;
            default:
                System.out.println("⚠️ Unhandled event: " + event);
        }
    }

    private String generateSignature(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }
}
