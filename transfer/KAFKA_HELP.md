# Transfer Microservice Help (Kafka Edition)

## Содержание

- [Описание модуля](#описание-модуля)
- [Функциональные возможности](#функциональные-возможности)
- [Архитектура](#архитектура)
    - [Сущности (Entities)](#сущности-entities)
    - [DTO (Data Transfer Objects)](#dto-data-transfer-objects)
    - [Репозитории (Repositories)](#репозитории-repositories)
    - [Сервисный слой (Services)](#сервисный-слой-services)
    - [AOP для аудита](#aop-для-аудита)
    - [Kafka (Producer & Consumer)](#kafka-producer--consumer)
- [Основные сценарии обработки сообщений](#основные-сценарии-обработки-сообщений)
- [Технологический стек](#технологический-стек)
- [Обработка ошибок (Exception Handling)](#обработка-ошибок-exception-handling)
- [Безопасность](#безопасность)
- [Логирование и мониторинг](#логирование-и-мониторинг)
- [Развертывание](#развертывание)
- [Дополнительные требования](#дополнительные-требования)

---

## **Описание модуля**

Transfer Microservice предназначен для обработки переводов (по номеру счета, карты, телефона) через систему обмена сообщениями Kafka.\
**Аудит записывается автоматически** при обработке событий.

## **Функциональные возможности**

- Взаимодействие через Kafka (Producer/Consumer).
- Автоматический аудит через AOP.
- Интеграция с PostgreSQL.
- JWT-аутентификация.
- Логирование и мониторинг.

---

## **Архитектура**

### **Сущности (Entities)**

- `AccountTransfer` – перевод по номеру счета.
- `CardTransfer` – перевод по номеру карты.
- `PhoneTransfer` – перевод по номеру телефона.
- `Audit` – лог изменений операций.

### **DTO (Data Transfer Objects)**

- `AccountTransferDto` – DTO для перевода по счету.
- `CardTransferDto` – DTO для перевода по карте.
- `PhoneTransferDto` – DTO для перевода по телефону.
- `AuditDto` – DTO для получения истории операций.

### **Репозитории (Repositories)**

- `AccountTransferRepository` – управление переводами по счету.
- `CardTransferRepository` – управление переводами по карте.
- `PhoneTransferRepository` – управление переводами по телефону.
- `AuditRepository` – управление историей операций.

### **Сервисный слой (Services)**

- `TransferService` – интерфейс бизнес-логики переводов.
- `TransferServiceImpl` – реализация бизнес-логики.
- `AuditService` – интерфейс для получения истории операций.
- `AuditServiceImpl` – реализация получения данных аудита.

### **AOP для аудита**

- `AuditAspect` – аспект, автоматически записывающий изменения в `audit`-таблицу.

### **Kafka (Producer & Consumer)**

- `TransferProducer` – сервис, отправляющий события переводов в Kafka-топики.
- `TransferConsumer` – сервис, обрабатывающий события переводов из Kafka.
- `AuditConsumer` – сервис, перехватывающий события и записывающий их в `audit`-таблицу.

---

## **Основные сценарии обработки сообщений**

### **1. Перевод по номеру счета**
- **Producer:** Отправляет сообщение в топик `transfer.account`
- **Consumer:** Обрабатывает сообщение, выполняет перевод
- **Аудит:** Запись фиксируется автоматически через `AuditConsumer`

### **2. Перевод по номеру карты**
- **Producer:** Отправляет сообщение в топик `transfer.card`
- **Consumer:** Обрабатывает сообщение, выполняет перевод
- **Аудит:** Запись фиксируется автоматически через `AuditConsumer`

### **3. Перевод по номеру телефона**
- **Producer:** Отправляет сообщение в топик `transfer.phone`
- **Consumer:** Обрабатывает сообщение, выполняет перевод
- **Аудит:** Запись фиксируется автоматически через `AuditConsumer`

---

## **Технологический стек**

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Lombok
- MapStruct
- Spring Security (JWT)
- Apache Kafka
- Logback (Slf4j)
- AOP (Aspect-Oriented Programming)

---

## **Обработка ошибок (Exception Handling)**

- `GlobalExceptionHandler` – глобальный обработчик исключений.
- Обрабатывает:
    - `EntityNotFoundException`
    - `ValidationException`
    - `IllegalArgumentException`
    - `DataIntegrityViolationException`
- Возвращает структурированные ошибки.

---

## **Безопасность**

- Kafka продюсер и консьюмеры работают через **Spring Security**.
- Поддерживается аутентификация с JWT.
- Логируются все действия пользователей.

---

## **Логирование и мониторинг**

- Используется `Slf4j` для логирования операций.
- Логируются:
    - Успешные операции.
    - Ошибки.
    - Доступ пользователей.
- Поддержка Prometheus/Grafana.

---

## **Развертывание**

- Контейнеризация через Docker.
- Kubernetes (Helm Charts).
- Конфигурация через `application.yml`.

---

## **Дополнительные требования**

- Код соответствует принципам **SOLID**.
- Использование **Enum** для типов операций.
- Покрытие кода unit- и интеграционными тестами.
- Готовность к **CI/CD (GitLab CI/GitHub Actions)**.

---
