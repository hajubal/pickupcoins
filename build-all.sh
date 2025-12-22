#!/bin/bash

# 모든 프로젝트 빌드 스크립트

echo "=== Gradle 프로젝트 빌드 시작 ==="

# 1. pickup-common 빌드 (의존성)
echo "1. pickup-common 빌드 중..."
./gradlew :pickup-common:build -x test

# 2. pickup-server 빌드 (pickup-common 포함)
echo "2. pickup-server 빌드 중..."
./gradlew :pickup-server:bootJar -x test

# 3. admin-api 빌드 (pickup-common 포함)
echo "3. admin-api 빌드 중..."
./gradlew :admin-api:bootJar -x test

# 4. admin-web 빌드
echo "4. admin-web 빌드 중..."
cd admin-web
npm install
npm run build
cd ..

echo "=== 모든 프로젝트 빌드 완료 ==="




