# Core JWT Architecture

Spring Boot ve modern Spring Security ile geliştirilmiş, **saf stateless (Zero-DB Hit)** JWT kimlik doğrulama ve rol bazlı yetkilendirme mimarisi.

## 🚀 Öne Çıkan Özellikler

- **Zero-DB Hit (Gerçek Stateless Mimari):** HTTP istekleri filtrelenirken token tek seferde parse edilir; kullanıcı adı ve yetkiler doğrudan token claim'lerinden inşa edilir. Her istekte veritabanına `SELECT` sorgusu atılmaz.
- **Modern JJWT (0.12.x):** Güncel `io.jsonwebtoken` API'si (`verifyWith`, `parseSignedClaims`, `getPayload`) kullanılarak imzalama ve doğrulama işlemleri gerçekleştirilir.
- **Composition over Inheritance:** `User` JPA entity'si Spring Security bağımlılıklarından temiz tutulmuş, `UserDetails` sözleşmesi ayrı bir `CustomUserDetails` wrapper sınıfı ile sağlanmıştır.
- **REST API Uyumlu Hata Yönetimi:** Spring Security'nin varsayılan HTML yönlendirmeleri yerine `AuthenticationEntryPoint` (401) ve `AccessDeniedHandler` (403) ile özel JSON hata yanıtları döner.
- **Modern Java:** Java 21 ve DTO'lar için immutable Java `record` yapıları.

---

> ⚠️ **Önemli Not:**  
> Bu proje bir öğrenme ve referans mimarisi projesidir. Kolay çalıştırılabilmesi ve pratik yapılabilmesi amacıyla `secret-key` ve veritabanı yapılandırmaları **kasıtlı olarak** `application.properties` dosyasına eklenmiştir. Bu durum bir güvenlik açığı değil, bilinçli bir eğitim tercihidir.

---

## 🛠️ Endpoint'ler

| Metot | Endpoint | Yetki | Açıklama |
|---|---|---|---|
| `POST` | `/api/auth/register` | Herkese Açık | Kullanıcı kaydı & token üretimi |
| `POST` | `/api/auth/login` | Herkese Açık | Kullanıcı girişi & token üretimi |
| `GET` | `/api/private` | Authenticated | Token sahibi tüm kullanıcılar |
| `GET` | `/api/private/user` | `USER` veya `ADMIN` | Rol kısıtlamalı endpoint |
| `GET` | `/api/private/admin` | `ADMIN` | Sadece admin erişimli endpoint |
