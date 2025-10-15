# Spring Boot Blog Application

AWS RDS PostgreSQL과 OAuth2 인증을 사용하는 Spring Boot 블로그 애플리케이션입니다.

---

## 목차

1. [기술 스택](#기술-스택)
2. [사전 요구사항](#사전-요구사항)
3. [환경 설정](#환경-설정)
4. [실행 방법](#실행-방법)
5. [프로젝트 구조](#프로젝트-구조)
6. [API 엔드포인트](#api-엔드포인트)
7. [주의사항](#주의사항)
8. [문제 해결](#문제-해결)

---

## 기술 스택

- **Language:** Java 17
- **Framework:** Spring Boot 3.0.2
- **Build Tool:** Gradle 8.14
- **Database:** PostgreSQL (AWS RDS)
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security + OAuth2 (Google)
- **Template Engine:** Thymeleaf

---

## 사전 요구사항

실행 전 다음이 설치되어 있어야 합니다:

- JDK 17 이상
- Gradle 8.14 이상 (또는 프로젝트의 Gradle Wrapper 사용)
- PostgreSQL 데이터베이스 (로컬 또는 AWS RDS)
- Google OAuth2 클라이언트 ID 및 시크릿

---

## 환경 설정

### 1단계: 프로젝트 클론

bash
git clone https://github.com/your-username/shin-springboot-developer.git
cd shin-springboot-developer

### 2단계: CICD
AWS 설정
