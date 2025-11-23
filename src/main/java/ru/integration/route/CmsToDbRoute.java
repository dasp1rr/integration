package ru.integration.route;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.integration.Config;
import ru.integration.dto.SparePartDto;
import ru.integration.service.SparePartService;

import java.util.ArrayList;
import java.util.List;

/**
 * Маршрут для синхронизации данных из CMS в БД (Поток данных №1)
 */
public class CmsToDbRoute extends RouteBuilder {
    private static final Logger logger = LoggerFactory.getLogger(CmsToDbRoute.class);
    private final SparePartService sparePartService = new SparePartService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void configure() throws Exception {
        // Маршрут для получения одной страницы из CMS
        from("direct:cms-page")
            .routeId("cms-get-page")
            .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
            .process(exchange -> {
                String url = exchange.getIn().getHeader("cmsUrl", String.class);
                // Добавляем bridgeEndpoint=true правильно (URL уже содержит ?)
                String finalUrl = url + "&bridgeEndpoint=true";
                exchange.getIn().setHeader("cmsUrl", finalUrl);
            })
            .toD("${header.cmsUrl}")
            .process(exchange -> {
                // Получаем JSON строку и парсим в список SparePartDto
                String jsonBody = exchange.getIn().getBody(String.class);
                List<SparePartDto> spareParts = objectMapper.readValue(
                    jsonBody, 
                    new TypeReference<List<SparePartDto>>() {}
                );
                exchange.getIn().setBody(spareParts);
            });
        
        // Основной маршрут синхронизации
        from("direct:cms-sync")
            .routeId("cms-to-db-sync")
            .log("Начало синхронизации: CMS -> БД")
            
            // Получаем все данные из CMS с пагинацией
            .process(exchange -> {
                List<SparePartDto> allSpares = new ArrayList<>();
                int page = 0;
                
                while (true) {
                    String url = String.format("%sstudents/%d/cms/spares?page=%d&size=%d",
                            Config.BASE_API_URL, Config.STUDENT_ID, page, Config.CMS_PAGE_SIZE);
                    
                    exchange.getIn().setHeader("cmsUrl", url);
                    
                    // Вызываем маршрут для получения страницы
                    Exchange pageExchange = exchange.getContext().createProducerTemplate()
                            .send("direct:cms-page", exchange);
                    
                    @SuppressWarnings("unchecked")
                    List<SparePartDto> pageSpares = pageExchange.getIn().getBody(List.class);
                    
                    if (pageSpares == null || pageSpares.isEmpty()) {
                        break;
                    }
                    
                    allSpares.addAll(pageSpares);
                    logger.info("Получено {} запчастей со страницы {}", pageSpares.size(), page);
                    
                    // Если получили меньше чем page_size, значит это последняя страница
                    if (pageSpares.size() < Config.CMS_PAGE_SIZE) {
                        break;
                    }
                    
                    page++;
                }
                
                logger.info("Всего получено {} запчастей из CMS", allSpares.size());
                exchange.getIn().setBody(allSpares);
            })
            
            // Разбиваем список на отдельные элементы
            .split(body())
                .process(exchange -> {
                    SparePartDto dto = exchange.getIn().getBody(SparePartDto.class);
                    String result = sparePartService.upsertSparePart(dto);
                    
                    if ("added".equals(result)) {
                        exchange.getIn().setHeader("action", "added");
                    } else if ("updated".equals(result)) {
                        exchange.getIn().setHeader("action", "updated");
                    }
                })
            .end()
            
            .process(exchange -> {
                // Подсчитываем статистику
                long totalInDb = sparePartService.getCount();
                logger.info("Синхронизация CMS -> БД завершена. Всего в БД: {}", totalInDb);
            })
            
            .log("Синхронизация CMS -> БД завершена");
    }
}

