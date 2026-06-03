# WebbySkyTracker — Microservices Backend

Бэкенд для мобильного приложения трекинга привычек, ежедневных метрик здоровья и персональных ИИ-рекомендаций.

## Содержание

- [Архитектура](#архитектура)
- [Технологии](#технологии)
- [Сервисы](#сервисы)
- [Быстрый старт](#быстрый-старт)
- [Переменные окружения](#переменные-окружения)
- [API Reference](#api-reference)
- [База данных](#база-данных)
- [Коды ошибок](#коды-ошибок)

---

## Архитектура

```
Android Client
      │
      ▼
┌─────────────┐
│ api-gateway │  :8080
└──────┬──────┘
       ├──────────────────┬──────────────────┐
       ▼                  ▼                  ▼
┌─────────────┐  ┌─────────────────┐  ┌───────────┐
│users-service│  │metrics-service  │  │ai-service │
│   :8081     │  │    :8082        │  │  :8085    │
└──────┬──────┘  └────────┬────────┘  └─────┬─────┘
       │                  │                  │
    PostgreSQL         PostgreSQL         PostgreSQL
    Redis :6379       schema_metrics      schema_ai
       │
    Kafka :9092
       │
       ▼
┌──────────────────────┐       ┌──────────────┐
│sender-to-email       │:8083  │eureka-server │:8761
│service               │       │              │
└──────────────────────┘       └──────────────┘
                                      ▲
                              все сервисы регистрируются
```

Все сервисы регистрируются в **Eureka**. API Gateway маршрутизирует запросы по схеме `lb://service-name`.

---

## Технологии

| | |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.7 |
| Spring Cloud | 2025.0.1 (Eureka, Gateway) |
| PostgreSQL | 15+ |
| Redis | 7 |
| Apache Kafka | 7.5.0 |
| JWT (JJWT) | 0.11.5 |
| Gradle | 8.13 |
| LM Studio | OpenAI-compatible API |
| LLM Model | mistralai/ministral-3-3b (GGUF Q4_K_M) |

---

## Сервисы

| Сервис | Порт | Описание |
|---|---|---|
| `eureka-server` | 8761 | Service discovery |
| `api-gateway` | 8080 | Единая точка входа, маршрутизация |
| `users-service` | 8081 | Регистрация, вход, JWT, сброс пароля |
| `metrics-service` | 8082 | Привычки, выполнения, ежедневные метрики |
| `sender-to-email-service` | 8083 | Отправка email через Kafka → SMTP |
| `ai-service` | 8085 | ИИ-рекомендации через локальную LLM |

### Маршруты API Gateway

| Путь | Сервис |
|---|---|
| `/api/v1/auth/**` | `lb://users-service` |
| `/api/v1/metrics/**` | `lb://metrics-service` |
| `/api/v1/ai/**` | `lb://ai-service` |

---

## Быстрый старт

### Требования

- Java 21+
- PostgreSQL 15+
- Docker (для Redis и Kafka)
- [LM Studio](https://lmstudio.ai/) с загруженной моделью `mistralai/ministral-3-3b`

### 1. Запустить инфраструктуру

```bash
docker-compose up -d
```

Запустятся: **Redis** (6379), **Zookeeper** (2181), **Kafka** (9092).

### 2. Создать базу данных

```sql
CREATE DATABASE "webbysky-tracker-db";
\c webbysky-tracker-db
CREATE SCHEMA schema_metrics;
CREATE SCHEMA schema_ai;
```

### 3. Настроить переменные окружения

```env
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET_KEY=your_base64_secret_256bit
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

> Для генерации JWT-секрета:
> ```bash
> openssl rand -base64 32
> ```

### 4. Запустить LM Studio

Открой LM Studio → загрузи модель **mistralai/ministral-3-3b** → нажми **Start Server** (порт 1234).

### 5. Запускать сервисы в порядке

```
1. eureka-server            → http://localhost:8761
2. users-service
3. metrics-service
4. sender-to-email-service
5. ai-service
6. api-gateway              → http://localhost:8080
```

---

## Переменные окружения

| Переменная | Сервисы | Описание |
|---|---|---|
| `DB_USERNAME` | users, metrics, ai | Пользователь PostgreSQL |
| `DB_PASSWORD` | users, metrics, ai | Пароль PostgreSQL |
| `JWT_SECRET_KEY` | users, metrics, ai | Base64-encoded секрет (≥256 бит). **Должен совпадать во всех сервисах** |
| `MAIL_USERNAME` | sender-to-email | Gmail адрес |
| `MAIL_PASSWORD` | sender-to-email | Gmail App Password |

---

## API Reference

Все запросы идут через Gateway: `http://localhost:8080`

Все эндпоинты кроме `/api/v1/auth/**` требуют заголовка:
```
Authorization: Bearer <accessToken>
```

---

### Аутентификация

| Метод | Endpoint | Описание |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Регистрация. Отправляет код верификации на email |
| `POST` | `/api/v1/auth/verify-email` | Подтверждение email по коду |
| `POST` | `/api/v1/auth/login` | Вход → `{accessToken, refreshToken}` |
| `POST` | `/api/v1/auth/refresh` | Обновление access token по refresh token |
| `POST` | `/api/v1/auth/logout` | Выход (инвалидация refresh token) |
| `POST` | `/api/v1/auth/forgot-password` | Запрос сброса пароля (`?email=`) |
| `POST` | `/api/v1/auth/reset-password` | Установка нового пароля |

**Токены:**
- Access token — срок жизни 30 000 мин (~20 дней)
- Refresh token — срок жизни 7 дней, хранится в Redis

---

### Привычки

| Метод | Endpoint | Описание |
|---|---|---|
| `POST` | `/api/v1/metrics/habits` | Создать привычку (лимит: **5 на пользователя**) |
| `GET` | `/api/v1/metrics/habits` | Все привычки пользователя |
| `GET` | `/api/v1/metrics/habits/{id}` | Привычка по ID |
| `PUT` | `/api/v1/metrics/habits/{id}` | Обновить привычку |
| `DELETE` | `/api/v1/metrics/habits/{id}` | Удалить (каскадно удаляет выполнения) |

<details>
<summary>Пример запроса</summary>

```json
POST /api/v1/metrics/habits
{
  "name": "Читать книгу",
  "color": "#9C27B0"
}
```
</details>

---

### Выполнения привычек

| Метод | Endpoint | Описание |
|---|---|---|
| `POST` | `/api/v1/metrics/completions` | Отметить привычку выполненной |
| `DELETE` | `/api/v1/metrics/completions?habitId=&date=` | Снять отметку |

> Ограничение: уникальная пара `(habitId, date)` — нельзя выполнить дважды в один день (`409 Conflict`).

<details>
<summary>Пример запроса</summary>

```json
POST /api/v1/metrics/completions
{
  "habitId": 1,
  "date": "2026-06-02"
}
```
</details>

---

### Ежедневные метрики

| Метод | Endpoint | Описание |
|---|---|---|
| `POST` | `/api/v1/metrics/daily` | Сохранить / обновить метрики (upsert по дате) |
| `GET` | `/api/v1/metrics/daily/today` | Метрики за сегодня |
| `GET` | `/api/v1/metrics/daily?date=YYYY-MM-DD` | Метрики за конкретную дату |
| `GET` | `/api/v1/metrics/daily/all` | Все метрики пользователя (дата desc) |

**Допустимые значения полей:**

| Поле | Тип | Диапазон |
|---|---|---|
| `date` | LocalDate | Дата записи (по умолчанию — сегодня) |
| `sleepHours` | Float | Любое положительное |
| `mood` | Integer | 1 – 10 |
| `productivity` | Integer | 1 – 10 |
| `energy` | Integer | 1 – 10 |
| `waterGlasses` | Integer | 0 – 25 |
| `exerciseMinutes` | Integer | 0 / 15 / 30 / 45 / 60 / 90 / 120 |
| `note` | String | До 500 символов |

Все поля опциональны — передавай только те, что нужно обновить.

<details>
<summary>Пример запроса</summary>

```json
POST /api/v1/metrics/daily
{
  "sleepHours": 7.5,
  "mood": 8,
  "productivity": 7,
  "energy": 6,
  "waterGlasses": 8,
  "exerciseMinutes": 30,
  "note": "Хороший день"
}
```
</details>

---

### ИИ-рекомендации

| Метод | Endpoint | Описание |
|---|---|---|
| `GET` | `/api/v1/ai/recommendations/latest` | Последняя рекомендация (`204` если ещё нет) |
| `POST` | `/api/v1/ai/recommendations` | Сгенерировать новую (лимит: **1 в день**) |
| `POST` | `/api/v1/ai/recommendations/force` | Принудительная регенерация без лимита |

> При превышении дневного лимита — `429 Too Many Requests`.

**Как работает:**
1. Сервис читает привычки и метрики за последние **14 дней**
2. Формирует промпт и отправляет в LM Studio (`POST localhost:1234/v1/chat/completions`)
3. Сохраняет ответ в БД и возвращает клиенту

**Формат ответа LLM:**
```
### 💡 Общая оценка
...

### 🎯 Рекомендации по привычкам
...

### ⚡ Топ-3 действия на эту неделю
1. ...
2. ...
3. ...
```

---

## База данных

### schema_metrics

```sql
-- Привычки
CREATE TABLE schema_metrics.habits (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    color       VARCHAR(7) DEFAULT '#4CAF50',
    is_active   BOOLEAN,
    created_at  TIMESTAMP
);

-- Выполнения привычек
CREATE TABLE schema_metrics.habit_completions (
    id           BIGSERIAL PRIMARY KEY,
    habit_id     BIGINT REFERENCES schema_metrics.habits(id) ON DELETE CASCADE,
    completed_at DATE NOT NULL,
    note         VARCHAR(500),
    created_at   TIMESTAMP,
    UNIQUE (habit_id, completed_at)
);

-- Ежедневные метрики
CREATE TABLE schema_metrics.daily_metrics (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    date              DATE NOT NULL,
    sleep_hours       FLOAT,
    mood              INTEGER,
    productivity      INTEGER,
    energy            INTEGER,
    water_glasses     INTEGER,
    exercise_minutes  INTEGER,
    note              VARCHAR(1000),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    UNIQUE (user_id, date)
);
```

### schema_ai

```sql
-- ИИ-рекомендации
CREATE TABLE schema_ai.recommendations (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    generated_at  TIMESTAMP NOT NULL,
    model_used    VARCHAR(100),
    content       VARCHAR(10000) NOT NULL
);
```

---

## Коды ошибок

| Код | Ситуация |
|---|---|
| `400 Bad Request` | Невалидные данные запроса |
| `401 Unauthorized` | Отсутствует или истёк JWT |
| `404 Not Found` | Ресурс не найден |
| `409 Conflict` | Дубликат (привычка уже выполнена сегодня, email занят) |
| `422 Unprocessable Entity` | Превышен лимит привычек (5 шт.) |
| `429 Too Many Requests` | Превышен дневной лимит ИИ-рекомендаций |
| `503 Service Unavailable` | LM Studio недоступен |

---

## Структура проекта

```
webbysky-tracker-microservices/
├── docker-compose.yaml          # Redis + Kafka + Zookeeper
├── eureka-server/
├── api-gateway/
├── users-service/
├── metrics-service/
├── sender-to-email-service/
└── ai-service/
```

Каждый сервис — независимый Gradle-проект со своим `build.gradle.kts` и `settings.gradle.kts`.
