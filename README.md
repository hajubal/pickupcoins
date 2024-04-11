# 포인트 수집기
## 개요

네이버 포인트를 자동으로 수집하는 어플리케이션

## 기능 목록

1. 등록된 사이트의 게시판을 크롤링 하면서 네이버 포인트 URL을 수집
2. 네이버 포인트 URL을 호출

## 기술 스택

1. SpringBoot
2. SpringData, JPA
3. MySql
4. Slack API
5. Docker compose

## 시나리오

1. 사용자가 네이버 로그인 쿠키정보를 저장
2. 어플리케이션에 주기적으로 등록된 사이트들의 게시판을 크롤링하여 포인트 URL 수집
3. 포인트 URL을 로그인 쿠키정보를 포함하여 호출

## 릴리즈
### v1.1.0
- 대시보드 소수점 자리수 수정
- Report 시간 변경 9시 -> 7시
- 사이트 사용자들 기준으로 Report 발송
- SpringBoot version 3.0 -> 3.2
- QueryDSL 적용

### v1.0.12
- Admin 디자인 적용
- Dashboard 적용

### v1.0.11
- 일 수집 정보에 포인트 정보 추가

### v1.0.9
- actuator 적용

### v1.0.8
- 일 수집 요약 정보 slack 알람 전송

### v1.0.7
- cookie 삭제시 오류 수정
- table 화면 페이징 처리
- cookie 추가시 site_user_id 입력안되는 문제 수정

### v1.0.4
Docker compose 기반으로 어플리케이션 구동 방식 변경

### v1.0.0
2분 간격으로 application.yml에 설정된 정보를 기준으로 포인트 수집