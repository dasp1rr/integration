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

## Установка и сборка

1. **Клонируйте репозиторий:**
```bash
git clone <repository-url>
cd integration
```

2. **Соберите проект:**
```bash
mvn clean package
```

3. **Настройте конфигурацию:**
   
   Отредактируйте файл `src/main/java/ru/integration/Config.java`:
   ```java
   public static final int STUDENT_ID = ваш_student_id;
   public static final String BASE_API_URL = "http://212.237.219.35:8080/";
   ```
   
   **⚠️ ВАЖНО:** Убедитесь, что преподаватель зафиксировал за вами `STUDENT_ID`!

4. **Настройте БД (опционально):**
   
   Для использования другой БД отредактируйте `src/main/resources/META-INF/persistence.xml`:
   - PostgreSQL: замените драйвер и URL
   - MySQL: замените драйвер и URL
   - И т.д.

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

## Формат CSV

CSV файл генерируется в следующем формате:
- Без заголовка
- Разделитель: `;` (точка с запятой)
- Кодировка: UTF-8
- Колонки: `spareCode;spareName;spareDescription;spareType;spareStatus;price;quantity;updatedAt`

Пример:
```
SP12345;Brake Pad;Передние тормозные колодки;RADIATOR;AVAILABLE;15.99;100;2025-09-09
```

## API Endpoints

### CMS API
- **GET** `/students/{studentId}/cms/spares`
  - Параметры: `page` (int, начиная с 0), `size` (int, максимум 10)
  - Возвращает список запчастей в формате JSON

### Report API
- **POST** `/students/{studentId}/report/csv`
  - Тело запроса: CSV файл в формате, описанном выше
  - Content-Type: `text/csv; charset=utf-8`

## База данных

По умолчанию используется H2 Database (файловая БД `historical_data.mv.db`).

Для использования другой БД:
1. Добавьте зависимость JDBC драйвера в `pom.xml`
2. Измените настройки в `persistence.xml`
3. Обновите `Config.java` если необходимо

Пример для PostgreSQL:
```xml
<property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/dbname"/>
<property name="javax.persistence.jdbc.user" value="user"/>
<property name="javax.persistence.jdbc.password" value="password"/>
```

## Логирование

Логи сохраняются в файл `sync.log` и выводятся в консоль. Конфигурация в `src/main/resources/logback.xml`.

## Важные замечания

1. **Student ID**: Обязательно установите ваш `STUDENT_ID` в `Config.java`
2. **Пагинация**: Приложение автоматически обрабатывает пагинацию CMS API
3. **Историчность**: БД хранит все данные, включая удаленные из CMS
4. **Периоды синхронизации**: Запускайте приложение до декабря и после для сохранения данных обоих периодов
5. **Apache Camel**: Использование ESB фреймворка Apache Camel соответствует требованиям задания

## Разработка

Для разработки используйте IDE с поддержкой Maven (IntelliJ IDEA, Eclipse, VS Code).

Запуск в режиме разработки:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="ru.integration.Application"
```

## Лицензия

Учебный проект
