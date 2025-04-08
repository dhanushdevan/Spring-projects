package com.iagami.Paymentgateway.ServiceInterface;

import com.iagami.Paymentgateway.Dto.PaymentGetData;
import com.razorpay.RazorpayException;

public interface ServiceInterface {

    String paymentapi(PaymentGetData paymentGetData) throws RazorpayException;

    void handleWebhook(String payload, String signature) throws Exception;
}
