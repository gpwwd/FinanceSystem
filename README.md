# Financial System

Финансовая система, разработанная на Spring Boot, предоставляющая API для управления финансовыми операциями.

## Технологии

- Java 21
- Spring Boot 3.3.5
- PostgreSQL 13
- Docker & Docker Compose
- Spring Security
- JWT Authentication
- Flyway для миграций базы данных
- Lombok
- SpringDoc (OpenAPI) для документации API

## Требования

- Java 21
- Maven
- Docker и Docker Compose
- PostgreSQL (если запуск без Docker)

## Структура проекта

```
src/main/java/com/financialsystem/
├── controller/    # REST контроллеры
├── service/       # Бизнес-логика
├── repository/    # Работа с базой данных
├── domain/        # Доменные модели
├── dto/           # Data Transfer Objects
├── mapper/        # Мапперы для преобразования объектов
├── security/      # Конфигурация безопасности
├── exception/     # Обработка исключений
├── util/          # Утилитные классы
└── rowMapper/     # Мапперы для JDBC
```

## Запуск проекта

### Через Docker (рекомендуемый способ)

1. Убедитесь, что Docker и Docker Compose установлены
2. Запустите Docker Desktop
3. Выполните команду:
```bash
docker-compose up --build
```

Приложение будет доступно по адресу: http://localhost:8080

### Локальный запуск

1. Установите Java 21
2. Установите Maven
3. Установите PostgreSQL
4. Соберите проект:
```bash
mvn clean install
```
5. Запустите приложение:
```bash
mvn spring-boot:run
```

## Конфигурация

Основные настройки находятся в файле `application.properties`:
- Настройки базы данных
- Настройки безопасности
- Настройки приложения

## API Документация

После запуска приложения, документация API доступна по адресу:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## База данных

Проект использует PostgreSQL. При запуске через Docker, база данных автоматически создается и настраивается.
Основные параметры подключения:
- Database: financeApp
- Username: postgres
- Password: postgres
- Port: 5432

## Миграции базы данных

Проект использует Flyway для управления миграциями базы данных. Миграции находятся в директории `src/main/resources/db/migration`.

## Безопасность

- Аутентификация через JWT токены
- Spring Security для защиты эндпоинтов
- Конфигурация безопасности в пакете `security`

## Логирование

Логи приложения сохраняются в директории `logs/`.