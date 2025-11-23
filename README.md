# Интеграционное приложение для синхронизации данных

Приложение для синхронизации данных между системами, реализованное на Java с использованием Apache Camel:
- **CMS** (система управления контентом) - источник данных о запчастях
- **БД исторических данных** - хранилище всех исторических данных (H2 по умолчанию)
- **Report API** - система отчетности, принимающая CSV файлы

## Описание

Приложение выполняет два основных потока данных с использованием Apache Camel маршрутов:

1. **Поток 1: CMS → БД**
   - Получает данные о запчастях из CMS API (с учетом пагинации)
   - Сравнивает с данными в БД
   - Добавляет новые записи и обновляет существующие

2. **Поток 2: БД → Report API**
   - Формирует CSV файл из данных БД
   - Отправляет CSV в Report API

## Технологии

- **Java 11+**
- **Apache Camel 3.21.0** - ESB фреймворк для интеграции
- **Hibernate/JPA** - для работы с БД
- **H2 Database** - встроенная БД (можно заменить на PostgreSQL, MySQL и т.д.)
- **Jackson** - для работы с JSON
- **SLF4J/Logback** - для логирования

## Требования

- Java 11 или выше
- Maven 3.6+
- База данных (H2 по умолчанию, можно использовать PostgreSQL, MySQL, MongoDB)

## Запуск

После сборки запустите приложение:

```bash
java -jar target/integration-app-1.0.0.jar
```

Или через Maven:

```bash
mvn exec:java -Dexec.mainClass="ru.integration.Application"
```

## Структура проекта

```
integration/
├── pom.xml                                    # Maven конфигурация
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ru/integration/
│   │   │       ├── Application.java          # Главный класс
│   │   │       ├── Config.java               # Конфигурация
│   │   │       ├── model/
│   │   │       │   └── SparePart.java        # JPA Entity
│   │   │       ├── dto/
│   │   │       │   └── SparePartDto.java     # DTO для API
│   │   │       ├── service/
│   │   │       │   ├── SparePartService.java # Сервис для работы с БД
│   │   │       │   └── CsvGenerator.java     # Генератор CSV
│   │   │       └── route/
│   │   │           ├── CmsToDbRoute.java      # Маршрут CMS -> БД
│   │   │           └── DbToReportRoute.java   # Маршрут БД -> Report API
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── persistence.xml          # JPA конфигурация
│   │       └── logback.xml                   # Конфигурация логирования
└── README.md
```

## Apache Camel Маршруты

### CmsToDbRoute
Маршрут для синхронизации данных из CMS в БД:
- `direct:cms-page` - получение одной страницы из CMS
- `direct:cms-sync` - основной маршрут синхронизации с пагинацией

### DbToReportRoute
Маршрут для отправки данных в Report API:
- `direct:db-to-report` - получение данных из БД, генерация CSV и отправка
