# Java-shareIt - микросервисное приложение для аренды вещей.
#### Приложение ShareIt позволяет обмениваться с друзьями вещами на время: инструментами, гаджетами, книгами и так далее. Как каршеринг, только для вещей.

![328_1717671631.png](328_1717671631.png)

#### Используемые инструменты: 
### Java, Spring Boot, Spring Data JPA, PostgreSQL, REST API, Lombok, Docker, Mockito, JUnit. 

#### Приложение содержит два микросервиса:
- ShareIt-gateway - сервис по валидации входящих данных;
- ShareIt-server - основной сервис с бизнес-логикой.

Запуск настроен через Docker. Приложения shareIt-server, shareIt-gateway и база данных PostgreSQL запускаются в отдельном Docker-контейнере каждый и их общение происходит через REST.

### Эндпоинты:
##### 1. USER:
- POST /users - добавление пользователя;
- PATCH /users/{userId} - обновление данных пользователя;
- GET /users/{userId} - получение данных пользователя;
- GET /users/ - получение списка пользователей;
- DELETE /users/{userId} - удаление пользователя.

##### 2. ITEM:
- POST /items - добавление вещи;
- PATCH /items/{itemId} - обновление данных вещи;
- GET /items/{itemId} - получение данных вещи;
- GET /items/ - получение списка вещей;
- DELETE /items/{itemId} - удаление вещи;
- GET /items/search - поиск вещей по тексту в параметре text;
- POST /items/{itemId}/comment - добавление отзыва к вещи после завершенного бронирования.

##### 3. REQUEST:
- POST /requests - добавление запроса на бронирование;
- GET /requests/{requestId} - получение бронирования;
- GET /requests/all - получение списка бронирований;
- GET /requests - получение списка бронирований по id пользователя в заголовке запроса.

##### 4. BOOKING:
- POST /bookings - добавление бронирования;
- PATCH /bookings/{bookingId} - одобрение или отклонение бронирования по параметру approved;
- GET /bookings/{bookingId} - получение данных о бронировании;
- GET /bookings/ - получение бронирований текущего пользователя;
- GET /bookings/owner - получение списка бронирований для всех вещей текущего пользователя.
