package com.adrianalexandru.payment_gateway;

import org.springframework.boot.SpringApplication;

public class TestPaymentGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.from(PaymentGatewayApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
