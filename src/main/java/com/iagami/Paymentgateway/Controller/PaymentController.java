package com.iagami.Paymentgateway.Controller;
import com.iagami.Paymentgateway.Dto.GetFromAnotherApi;
import com.iagami.Paymentgateway.Dto.PaymentGetData;
import com.iagami.Paymentgateway.Dto.SimpleDTO;
import com.iagami.Paymentgateway.ServiceInterface.ServiceInterface;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*") // 🔥 Allow frontend (from any origin) to call this
@RestController
@RequestMapping("/payment")
public class PaymentController {
	  @Autowired
	    private ServiceInterface serviceInterface;
	  @PostMapping("/verify")
	    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> payload) {
	        String razorpayOrderId = payload.get("razorpay_order_id");
	        String razorpayPaymentId = payload.get("razorpay_payment_id");
	        String razorpaySignature = payload.get("razorpay_signature");

	        boolean isValid = serviceInterface.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

	        if (isValid) {
	            return ResponseEntity.ok("✅ Payment Verified Successfully. Payment ID: " + razorpayPaymentId);
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid Payment Signature");
	        }
	    }


  
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
    @GetMapping(value="/user/details/{id}",produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getexternaldata(@PathVariable String id) throws JAXBException{
    	
    	GetFromAnotherApi user = serviceInterface.handledata(id);
    	JAXBContext context = JAXBContext.newInstance(GetFromAnotherApi.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(user, writer);

        return ResponseEntity.ok(serviceInterface.handledata(id));	
    	
    }
    @GetMapping(value = "/user/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getUserAsXml() {
        try {
        	SimpleDTO user = new SimpleDTO("Dhanush", 23);

            JAXBContext context = JAXBContext.newInstance(SimpleDTO.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(user, writer);

            return ResponseEntity.ok(writer.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("<error>Failed to convert</error>");
        }
    }
   
}
