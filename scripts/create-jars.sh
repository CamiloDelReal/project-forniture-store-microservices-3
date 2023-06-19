#!/bin/sh

echo [!] Creating Jar file for Configuration Service
cd ../configuration-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Configuration Service is ready
else
    echo [-] Operation failed for Configuration Service
fi

echo [!] Creating Jar file for Discovery Service
cd ../discovery-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Discovery Service is ready
else
    echo [-] Operation failed for Discovery Service
fi

echo [!] Creating Jar file for Gateway Service
cd ../gateway-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Gateway Service is ready
else
    echo [-] Operation failed for Gateway Service
fi

echo [!] Creating Jar file for Authorization Service
cd ../authorization-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Authorization Service is ready
else
    echo [-] Operation failed for Authorization Service
fi

echo [!] Creating Jar file for Customer Service
cd ../customer-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Customer Service is ready
else
    echo [-] Operation failed for Customer Service
fi

echo [!] Creating Jar file for Forniture Service
cd ../forniture-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Forniture Service is ready
else
    echo [-] Operation failed for Forniture Service
fi

echo [!] Creating Jar file for Cart Service
cd ../cart-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Cart Service is ready
else
    echo [-] Operation failed for Cart Service
fi

echo [!] Creating Jar file for Payment Service
cd ../payment-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Payment Service is ready
else
    echo [-] Operation failed for Payment Service
fi

echo [!] Creating Jar file for Delivery Service
cd ../delivery-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Delivery Service is ready
else
    echo [-] Operation failed for Delivery Service
fi

echo [!] Creating Jar file for Support Service
cd ../support-service
./gradlew clean build -x test
status=$?
if [ $status -eq 0 ]; then
    echo [+] Jar file for Support Service is ready
else
    echo [-] Operation failed for Support Service
fi