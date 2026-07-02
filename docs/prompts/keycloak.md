Derleme başarılı. Kod tarafı tamamlandı — şimdi özet ve Keycloak'ta senin yapacağın adımlar:

## Yazdığım kod

| Dosya | Ne yapar |
|---|---|
| [product-service/pom.xml](product-service/pom.xml) ve [order-service/pom.xml](order-service/pom.xml) | `spring-boot-starter-oauth2-resource-server` bağımlılığı eklendi |
| [SecurityConfig.java (product)](product-service/src/main/java/com/etiya/productservice/security/SecurityConfig.java) | GET `/api/products/**` → USER veya ADMIN; POST/PUT/DELETE → sadece ADMIN. Actuator serbest. |
| [SecurityConfig.java (order)](order-service/src/main/java/com/etiya/orderservice/security/SecurityConfig.java) | GET ve POST `/api/orders/**` → USER veya ADMIN; PUT/DELETE → sadece ADMIN. Actuator ve `/h2-console` serbest (frame izni de verildi). |
| KeycloakRealmRoleConverter.java (her iki serviste) | Keycloak'ın token'a koyduğu `realm_access.roles` claim'ini `ROLE_` önekli Spring authority'lerine çevirir; `hasRole()` bu sayede çalışır. |
| [configs/product-service/application.yml](configs/product-service/application.yml) ve [configs/order-service/application.yml](configs/order-service/application.yml) | `issuer-uri: http://localhost:8090/realms/etiya-crm-project` eklendi — servisler token imzasını bu realm'in JWKS endpoint'inden doğrular |

⚠️ **Önemli:** Config-server, config'leri **GitHub'daki repo'dan** (master dalı) çekiyor — yani `configs/` altındaki değişiklikler commit + push edilmeden servisler bu ayarı göremez. İstersen commit'leyip push'layabilirim, söylemen yeterli.

## Keycloak'ta senin yapacakların (`etiya-crm-project` realm'i içinde)

**1. Realm rolleri oluştur**
- Sol menü → **Realm roles** → **Create role**
- İki rol oluştur: `ADMIN` ve `USER` (kod büyük/küçük harfe duyarlı, aynen böyle yaz)

**2. Client oluştur** (token almak için)
- Sol menü → **Clients** → **Create client**
- **Client ID:** `etiya-crm-client` (istediğin ismi verebilirsin)
- İleri → **Client authentication: ON**, **Direct access grants: ON** (Postman'den kullanıcı adı/şifre ile token alabilmek için), Standard flow açık kalabilir
- Kaydettikten sonra client'ın **Credentials** sekmesinden **Client Secret**'ı kopyala — token isteğinde lazım olacak

**3. Kullanıcılar oluştur**
- Sol menü → **Users** → **Create new user** → username gir (örn. `user1` ve `admin1`), **Email verified: ON**
- Kullanıcının **Credentials** sekmesi → **Set password** → şifre belirle, **Temporary: OFF**
- Kullanıcının **Role mapping** sekmesi → **Assign role** → filtreyi **Realm roles** yap → `user1`'e `USER`, `admin1`'e `ADMIN` ata

**4. (Kontrol) Rollerin token'a girdiğini doğrula**
- Varsayılan olarak realm rolleri token'da `realm_access.roles` altında gelir, ekstra ayar gerekmez. Emin olmak istersen aldığın token'ı [jwt.io](https://jwt.io)'ya yapıştırıp bu claim'i görebilirsin.

## Test etme

Token al (şifre grant'i ile):

```
POST http://localhost:8090/realms/etiya-crm-project/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
client_id=etiya-crm-client
client_secret=<Credentials sekmesindeki secret>
username=user1
password=<şifre>
```

Dönen `access_token`'ı gateway üzerinden istekte kullan:

```
GET http://localhost:8080/api/products
Authorization: Bearer <access_token>
```

Beklenen davranış: token'sız istek → **401**, `user1` ile POST `/api/products` → **403**, `admin1` ile → **201**.

İki dikkat noktası: token'ı mutlaka `localhost:8090` üzerinden al (`127.0.0.1` kullanırsan `iss` claim'i eşleşmez ve servisler token'ı reddeder) ve servisleri ayağa kaldırırken Keycloak'ın çalışıyor olması gerekir (başlangıçta issuer'dan metadata çekiliyor).