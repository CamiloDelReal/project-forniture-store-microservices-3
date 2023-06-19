#!/bin/sh

echo [!] Creating Docker image for Configuration Service
cd ../configuration-service
docker build . --tag microservices3/configuration-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Configuration Service is ready
else
    echo [-] Operation failed for Configuration Service
fi

echo [!] Creating Docker image for Discovery Service
cd ../discovery-service
docker build . --tag microservices3/discovery-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Discovery Service is ready
else
    echo [-] Operation failed for Discovery Service
fi

echo [!] Creating Docker image for Gateway Service
cd ../gateway-service
docker build . --tag microservices3/gateway-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Gateway Service is ready
else
    echo [-] Operation failed for Gateway Service
fi

echo [!] Creating Docker image for Authorization Service
cd ../authorization-service
docker build . --tag microservices3/authorization-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Authorization Service is ready
else
    echo [-] Operation failed for Authorization Service
fi

echo [!] Creating Docker image for Customer Service
cd ../customer-service
docker build . --tag microservices3/customer-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Customer Service is ready
else
    echo [-] Operation failed for Customer Service
fi

echo [!] Creating Docker image for Forniture Service
cd ../forniture-service
docker build . --tag microservices3/forniture-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Forniture Service is ready
else
    echo [-] Operation failed for Forniture Service
fi

echo [!] Creating Docker image for Cart Service
cd ../cart-service
docker build . --tag microservices3/cart-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Cart Service is ready
else
    echo [-] Operation failed for Cart Service
fi

echo [!] Creating Docker image for Payment Service
cd ../payment-service
docker build . --tag microservices3/payment-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Payment Service is ready
else
    echo [-] Operation failed for Payment Service
fi

echo [!] Creating Docker image for Delivery Service
cd ../delivery-service
docker build . --tag microservices3/delivery-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Delivery Service is ready
else
    echo [-] Operation failed for Delivery Service
fi

echo [!] Creating Docker image for Support Service
cd ../support-service
docker build . --tag microservices3/support-service --force-rm=true
status=$?
if [ $status -eq 0 ]; then
    echo [+] Docker image for Support Service is ready
else
    echo [-] Operation failed for Support Service
fi