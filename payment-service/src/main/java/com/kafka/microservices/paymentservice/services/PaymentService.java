package com.kafka.microservices.paymentservice.services;

import com.kafka.microservices.common.PaymentRequestEvent;

public interface PaymentService {

    void processPayment(PaymentRequestEvent paymentRequestEvent);

}


