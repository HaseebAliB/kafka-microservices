📦 Kafka Microservices – Event-Driven E-Commerce Platform

This repository demonstrates an event-driven microservices architecture using Spring Boot, Kafka, PostgreSQL, Redis, and WebSockets. The system supports a product catalogue with order placement using a Saga pattern via Kafka events, and it includes ready-to-use Kubernetes deployment guidance.

🧠 System Overview

The platform models a simple e-commerce order flow:

➡️ Client creates an order
➡️ Inventory is checked
➡️ Payment is authorized
➡️ Order status is propagated in real time via WebSockets

Communication between services is asynchronous via Kafka, enabling loose coupling and scalable, resilient workflows.

🧩 Services

📌 product-service

Manages the product catalogue and inventory

Exposes REST APIs for product queries

Emits inventory validation events to Kafka

📌 order-service

Entry point for placing orders

Implements the Order Saga orchestration using Kafka events

Emits order lifecycle events (e.g., OrderCreated, OrderConfirmed, OrderRejected)

📌 payment-service

Handles payment processing simulation

Listens for order events and emits payment results

📌 ws-gateway (WebSocket service)

Subscribes to Kafka order events

Broadcasts real-time status updates to connected clients

📌 Kafka (infrastructure)

Messaging backbone for async events and saga coordination

Hosted via local Docker or Kubernetes manifests in kafka-docker (zookeeper + Kafka setup)

📌 PostgreSQL

Each service uses its own database schema

Ensures data ownership and isolation

📌 Redis

Used for caching / fast lookup where appropriate


🔁 Order Saga Event Flow

OrderService receives a client request to place an order

It publishes an OrderCreated event to Kafka

ProductService consumes the event → checks inventory

ProductService emits inventory result (InventoryOk / InventoryFailed)

PaymentService processes payment based on inventory result

A final order status (OrderConfirmed / OrderFailed) is emitted

WebSocket Gateway broadcasts status updates to clients

This pattern achieves eventual consistency, fault tolerance, and decoupled services.


☸️ Deploying on Kubernetes

This project includes Kubernetes artifacts designed to work with your services and Kafka stack.

🔧 Prerequisites

Kubernetes cluster (Minikube, KIND, or cloud provider)

kubectl configured

Docker images built and pushed to a registry accessible by the cluster

Services configured with the k8 Spring profile
(e.g., in application-k8s.yml)

🎯 Configuration Notes

Each Spring Boot service should have an application-k8s.yml

Kafka bootstrap address (e.g., kafka:9092)

PostgreSQL host/port

Redis host/port

Activate via environment variable:

SPRING_PROFILES_ACTIVE=k8s

🛠 Apply Deployments

Apply all K8 manifests in the recommended order:

# Kafka 
kubectl apply -f kafka-k8s/kafka/

# Databases
kubectl apply -f kafka-k8s/postgres/

# Redis
kubectl apply -f kafka-k8s/redis/

# Microservices
kubectl apply -f kafka-k8s/product-service/
kubectl apply -f kafka-k8s/order-service/
kubectl apply -f kafka-k8s/payment-service/
kubectl apply -f kafka-k8s/ws-service/

📌 Validate 
 kubectl get pods 

 kubectl get services

🎯 What This Project Demonstrates

✔ Kafka-based async communication
✔ Saga orchestration pattern
✔ Real-time notifications via WebSockets
✔ Kubernetes deployment of microservices
✔ Clear service boundaries with dedicated data stores
