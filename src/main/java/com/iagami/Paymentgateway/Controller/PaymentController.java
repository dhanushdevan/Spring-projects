package com.iagami.Paymentgateway.Controller;

import com.iagami.Paymentgateway.Dto.PaymentGetData;
import com.iagami.Paymentgateway.ServiceInterface.ServiceInterface;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private ServiceInterface serviceInterface;

    @PostMapping("/billPay")
    public ResponseEntity<?> paymentGeneration(@RequestBody PaymentGetData paymentGetData) throws RazorpayException {
        String orderResponse = serviceInterface.paymentapi(paymentGetData);
        return ResponseEntity.ok().body(orderResponse);
    }

    @PostMapping("/razorpay/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request,
                                                @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            System.out.println("📥 Payload: " + payload);
            System.out.println("📩 Received Signature: " + signature);

            serviceInterface.handleWebhook(payload, signature);
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
