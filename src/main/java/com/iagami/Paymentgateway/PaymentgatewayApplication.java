package com.iagami.Paymentgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PaymentgatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentgatewayApplication.class, args);
    }

}
