@echo off
REM 모든 프로젝트 빌드 스크립트 (Windows)

echo === Gradle 프로젝트 빌드 시작 ===

REM 1. pickup-common 빌드 (의존성)
echo 1. pickup-common 빌드 중...
call gradlew.bat :pickup-common:build -x test

REM 2. pickup-server 빌드 (pickup-common 포함)
echo 2. pickup-server 빌드 중...
call gradlew.bat :pickup-server:bootJar -x test

REM 3. admin-api 빌드 (pickup-common 포함)
echo 3. admin-api 빌드 중...
call gradlew.bat :admin-api:bootJar -x test

REM 4. admin-web 빌드
echo 4. admin-web 빌드 중...
cd admin-web
call npm install
call npm run build
cd ..

echo === 모든 프로젝트 빌드 완료 ===

