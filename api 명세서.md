#예약 시스템 API 명세서

- **BASE URL**: `https://localhost:8080`
- **VERSION**: v1.0
- **Content-Type**: `application/json`
- **Authentication**: JWT (Bearer Token)

---
[//]: # 리소스 쪽은 관리자 페이지 생성해서 다뤄야하고 전체적인 api 확인 및 수정 요망

## 1. 인증 (Auth)

### 1.1 로그인 (일반)

| 항목 | 내용 |
|------|-----|
|Method|`POST`|
|URL|`/login`|
|설명|사용자 로그인 및 JWT 발급|
|인증|불필요|

#### request body
```json
{
    "username": "user1",
    "password": "password"

}
```

#### response (200 OK)
```json
{
    "accessToken": "jwt token",
    "refreshToken": "refresh token"
}
```

---

### 1.2 로그인 (OAuth2)

| 항목 | 내용 |
|------|-----|
|Method|`POST`|
|URL|`/oauth2/authorization/{provider}`|
|설명|사용자 로그인(OAuth2) 및 JWT 발급|
|인증|불필요|

#### 요청 및 응답은 각 provider api 사용

### 1.3 회원가입

| 항목 | 내용 |
|------|-----|
|Method|`POST`|
|URL|`/register`|
|설명|신규 사용자 등록|
|인증|불필요|

#### request body
```json
{
    "username": "user1",
    "password": "password",
    "email": "email@example.com"
}
```

#### response (201 Created)
```json
{
    "id": 1,
    "username": "user1",
    "email": "email@example.com"
}
```

### 1.4 로그아웃

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/logout`|
|설명|로그아웃 시 리프레시 토큰 삭제|
|인증|불필요|

#### response (200 ok)

---

## 2. 사용자

### 2.1 사용자 정보 조회

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/users/detail`|
|설명|사용자 정보 출력|
|인증|필요|

#### response (200 OK)
```json
{
    "id": 1,
    "username": "user1",
    "email": "example@email.com"
}
```

### 2.2 사용자 정보 수정

| 항목 | 내용 |
|------|-----|
|Method|`PUT`|
|URL|`/users/edit`|
|설명|사용자 정보 수정|
|인증|필요|

#### request body
```json
{
    "username": "new_username",
    "email": "new@email.com"
}
```

#### response (204 No Content)
```
HTTP/1.1 204 No Content
```

### 2.3 사용자 삭제

| 항목 | 내용 |
|------|-----|
|Method|`DELETE`|
|URL|`/user_detail/delete`|
|설명|사용자 삭제|
|인증|필요|

#### response (204 No Content)
```
HTTP/1.1 204 No Content
```

### 2.4 이메일 중복 확인

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/check_email`|
|설명|회원가입 시 이메일 중복 확인|
|인증|불필요|

### request body
```json
{
    "email": "test@email.com"
}
```

#### response (200 ok)
```json 
{
    "message": "사용 가능한 이메일입니다.",
    "available": true
}
```

## 3. 예약

### 3.1 예약 조회

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/reservation/{date}`|
|설명|해당 일자의 예약 정보 조회|
|인증|불필요|

#### response (200 OK)
```json
{
    {
        "id": 1,
        "date": "2025-01-01",
        "startTime": "00:00:00",
        "endTime": "01:00:00",
        "username": "username",
        "resourceName": "resourceName"
    },
    {
        "id": 2,
        "date": "2025-01-01",
        "startTime": "01:00:00",
        "endTime": "02:00:00",
        "username": "username2",
        "resourceName": "resourceName"
    }
}
```

### 3.2 예약 생성

| 항목 | 내용 |
|------|-----|
|Method|`POST`|
|URL|`/reservation/{date}`|
|설명|해당 일자에 예약 생성|
|인증|필요|

#### request body
```json
{
    "date": "2025-01-01",
    "startTime": "00:00:00",
    "endTime": "01:00:00",
    "resourceId": 0
}
```

#### response (201 Created)
```json
{
    "id": 1,
    "date": "2025-01-01",
    "startTime": "00:00:00",
    "endTime": "01:00:00",
    "status": "CONFIRMED"
}
```

### 3.3 예약 수정

| 항목 | 내용 |
|------|-----|
|Method|`PUT`|
|URL|`/reservation/{id}/detail`|
|설명|예약 정보 수정|
|인증|필요|

#### request body
```json
{
    "id": 1,
    "date": "2025-01-01",
    "startTime": "01:00",
    "endTime": "02:00"
}
```

#### response (204 no Content)
```
HTTP/1.1 204 No Content
```

### 3.4 예약 취소 

| 항목 | 내용 |
|------|-----|
|Method|`DELETE`|
|URL|`/reservation/{id}/detail`|
|설명|예약 취소|
|인증|필요|

#### response (204 no Content)
```
HTTP/1.1 204 No Content
```

---

### 3.5 사용자의 예약 확인 

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/reservation/detail`|
|설명|현재 사용자의 모든 예약 내역 확인|
|인증|필요|

#### response (204 no Content)
```json
{
    {
        "id": 1,
        "date": 2025-01-01,
        "startTime": "00:00",
        "endTime": "01:00",
        "username": "user1",
        "resourceId": "resource1"
    },
    {
        "id": 2,
        "date": 2025-01-02,
        "startTime": "01:00",
        "endTime": "02:00",
        "username": "user1",
        "resourceId": "resource2"
    }
}
```

---

### 3.5 점유 시간 확인 sse

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/reservation/{resourceId}/sse`|
|설명|해당 날짜의 점유 시간 확인하기 위한 sse|
|인증|불필요|

#### request body
```json
{
    "date": 2025-11-01
}
```

#### response
```
Content type = text/event-stream
Body = event:timeslots
    data:
    {
        "09:00-10:00": false,
        "10:00-11:00": false,
        "11:00-12:00": false,
        "12:00-13:00": false,
        "13:00-14:00": false,
        "14:00-15:00": false,
        "15:00-16:00": false,
        "16:00-17:00": false,
        "17:00-18:00": false,
    }
```

---

## 4. 예약 개체 

### 4.1 예약 개체 리스트

| 항목 | 내용 |
|------|-----|
|Method|`GET`|
|URL|`/resource/list`|
|설명|모든 예약 개체 리스트 반환|
|인증|불필요|

#### response (200 OK)
```json
{
    "resourceList" : {
       {
           "id": 1,
           "name": "resourceName",
           "docname": "name"
       }
    }
}
```

### 4.2 예약 개체 추가

| 항목 | 내용 |
|------|-----|
|Method|`POST`|
|URL|`/resource/add`|
|설명|예약 개체 추가|
|인증|필요(admin)|

#### request
```json
{
    "name": "newResource",
    "docname": "newName"
}
```

#### response (204 NO CONTENT)
```json
{
    "resourceList" : {
       {
           "id": 2,
           "name": "newResource",
           "docname": "newName"
       }
    }
}
```

### 4.3 예약 개체 삭제

| 항목 | 내용 |
|------|-----|
|Method|`POST`|
|URL|`/resource/{id}/delete`|
|설명|예약 개체 삭제|
|인증|필요(admin)|

#### response (204 NO CONTENT)

### 4.4 예약 개체 수정

| 항목 | 내용 |
|------|-----|
|Method|`PUT`|
|URL|`/resource/{id}/edit`|
|설명|예약 개체 수정|
|인증|필요(admin)|

#### request
```json
{
    "name": "editResource",
    "docname": "editName"
}
```

#### response (204 NO CONTENT)

---


## 5. 공통 에러 형식

```json
{
    "code": "RESOURCE_NOT_FOUND",
    "message": "해당 리소스를 찾을 수 없습니다",
    "timestamp": "2025-01-01T00:00"
}
```

| 필드명 | 설명 |
|--------|------|
| code | 에러 코드 |
| message | 에러 메시지 |
| timestamp | 발생 시각 |

## 5. 상태 코드 규칙

| 코드 | 의미 |
|------|------|
| 200 | 요청 성공 |
| 201 | 생성 성공 |
| 204 | 내용 없음 |
| 400 | 잘못된 요청 |
| 401 | 인증 실패 |
| 403 | 접근 권한 없음 |
| 404 | 리소스 없음 |
| 409 | 예약 시간 중복, 이메일 중복 |
| 500 | 서버 오류 |

