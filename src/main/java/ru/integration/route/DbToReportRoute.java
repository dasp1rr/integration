package ru.integration.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.integration.Config;
import ru.integration.service.CsvGenerator;
import ru.integration.service.SparePartService;

import static org.apache.camel.builder.Builder.constant;

/**
 * Маршрут для синхронизации данных из БД в Report API (Поток данных №2)
 */
public class DbToReportRoute extends RouteBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DbToReportRoute.class);
    private final SparePartService sparePartService = new SparePartService();
    
    @Override
    public void configure() throws Exception {
        // Обработка исключений для всего маршрута
        onException(Exception.class)
            .log("Ошибка при отправке в Report API: ${exception.message}")
            .log("Стек ошибки: ${exception.stacktrace}")
            .handled(true)
            .end();
        
        from("direct:db-to-report")
            .routeId("db-to-report-sync")
            .log("Начало синхронизации: БД -> Report API")
            
            // Получаем все данные из БД
            .process(exchange -> {
                java.util.List<ru.integration.model.SparePart> spareParts = sparePartService.getAllSpareParts();
                logger.info("Получено {} запчастей из БД для отправки в Report API", spareParts.size());
                if (spareParts.isEmpty()) {
                    logger.warn("В БД нет данных для отправки в Report API");
                }
                exchange.getIn().setBody(spareParts);
            })
            
            // Генерируем CSV
            .process(new CsvGenerator())
            
            // Отправляем в Report API
            .process(exchange -> {
                String csvContent = exchange.getIn().getBody(String.class);
                logger.info("Подготовка к отправке CSV в Report API. Размер CSV: {} байт", 
                        csvContent != null ? csvContent.length() : 0);
                
                String url = String.format("%sstudents/%d/report/csv?bridgeEndpoint=true",
                        Config.BASE_API_URL, Config.STUDENT_ID);
                logger.info("Отправка POST запроса на URL: {}", url);
            })
            .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/csv; charset=utf-8"))
            .to(String.format("%sstudents/%d/report/csv?bridgeEndpoint=true",
                    Config.BASE_API_URL, Config.STUDENT_ID))
            .process(exchange -> {
                int statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                String responseBody = exchange.getIn().getBody(String.class);
                
                logger.info("Получен ответ от Report API. Статус: {}, Ответ: {}", statusCode, responseBody);
                
                if (statusCode == 200) {
                    logger.info("CSV успешно загружен в Report API");
                } else {
                    logger.error("Ошибка при загрузке CSV в Report API: статус {}", statusCode);
                }
            })
            
            .log("Синхронизация БД -> Report API завершена");
    }
}

