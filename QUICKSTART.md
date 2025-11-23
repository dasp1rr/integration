# Быстрый старт

## 1. Настройка конфигурации

Откройте файл `src/main/java/ru/integration/Config.java` и убедитесь, что установлены правильные значения:

```java
public static final int STUDENT_ID = 6;  // Ваш Student ID
public static final String BASE_API_URL = "http://212.237.219.35:8080/";
```

## 2. Сборка проекта

```bash
mvn clean package
```

## 3. Запуск приложения

```bash
java -jar target/integration-app-1.0.0.jar
```

Или через Maven:

```bash
mvn exec:java -Dexec.mainClass="ru.integration.Application"
```

## Что происходит при запуске

1. Приложение получает все данные из CMS API (с автоматической обработкой пагинации)
2. Сохраняет/обновляет данные в БД (H2 Database)
3. Генерирует CSV файл из всех данных БД
4. Отправляет CSV в Report API

## Проверка результатов

- Логи сохраняются в файл `sync.log`
- База данных создается в файле `historical_data.mv.db`
- В логах будет видна статистика синхронизации

## Важно

- Убедитесь, что преподаватель зафиксировал за вами `STUDENT_ID`
- Запускайте приложение до декабря и после для сохранения данных обоих периодов

