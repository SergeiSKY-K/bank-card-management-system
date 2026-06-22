Bank Card Management System

Backend-приложение для управления банковскими картами.

Технологии
Java 17
Spring Boot
Spring Security
JWT
Spring Data JPA
PostgreSQL
Liquibase
Docker Compose
Swagger / OpenAPI
JUnit 5
Mockito
Возможности
Пользователь
Регистрация
Авторизация
Просмотр своих карт
Перевод между своими картами
Запрос блокировки карты
Администратор
Создание карт
Просмотр всех карт
Фильтрация карт по статусу
Пагинация
Блокировка карт
Активация карт
Удаление карт
Просмотр всех пользователей
Безопасность
Аутентификация через JWT
Роли: USER, ADMIN
/api/admin/** доступен только администратору
Пользователь видит только свои карты
Полный номер карты не возвращается наружу
Номер карты хранится в зашифрованном виде
В ответах отображается только маска карты, например:
**** **** **** 1234
Запуск PostgreSQL через Docker Compose
docker compose up -d
Запуск приложения
./mvnw spring-boot:run

или после сборки:

./mvnw clean package
java -jar target/bankcards-0.0.1-SNAPSHOT.jar
Тесты
./mvnw clean test

Результат:

Tests run: 10
Failures: 0
Errors: 0
BUILD SUCCESS
Swagger

После запуска приложения Swagger доступен по адресу:

http://localhost:8080/swagger-ui/index.html

OpenAPI docs:

http://localhost:8080/v3/api-docs
Основные endpoints
Auth
POST /api/auth/register
POST /api/auth/login
User cards
GET /api/cards/my
POST /api/cards/transfer
PATCH /api/cards/{id}/request-block
Admin cards
POST /api/admin/cards
GET /api/admin/cards
PATCH /api/admin/cards/{id}/block
PATCH /api/admin/cards/{id}/activate
DELETE /api/admin/cards/{id}
Admin users
GET /api/admin/users
Примеры запросов
Register
{
"username": "user1",
"password": "password"
}
Login
{
"username": "user1",
"password": "password"
}
Create card
{
"cardNumber": "1234567812345678",
"expirationDate": "2030-12-31",
"userId": 1,
"balance": 1000
}
Transfer
{
"fromCardId": 1,
"toCardId": 2,
"amount": 100
}
Роли
USER

Может:

смотреть свои карты
переводить деньги между своими картами
запрашивать блокировку своей карты
ADMIN

Может:

создавать карты
смотреть все карты
фильтровать карты по статусу
блокировать карты
активировать карты
удалять карты
смотреть всех пользователей