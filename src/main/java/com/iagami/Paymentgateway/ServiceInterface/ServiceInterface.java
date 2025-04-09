package com.iagami.Paymentgateway.ServiceInterface;

import com.iagami.Paymentgateway.Dto.GetFromAnotherApi;
import com.iagami.Paymentgateway.Dto.PaymentGetData;
import com.razorpay.RazorpayException;

public interface ServiceInterface {

    String paymentapi(PaymentGetData paymentGetData) throws RazorpayException;

    void handleWebhook(String payload, String signature) throws Exception;
    
    public GetFromAnotherApi handledata(String name);
    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);
}
