package ru.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.integration.route.CmsToDbRoute;
import ru.integration.route.DbToReportRoute;

/**
 * Главный класс интеграционного приложения
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("=".repeat(60));
        logger.info("Интеграционное приложение для синхронизации данных");
        logger.info("Student ID: {}", Config.STUDENT_ID);
        logger.info("Base API URL: {}", Config.BASE_API_URL);
        logger.info("=".repeat(60));

        CamelContext context = new DefaultCamelContext();
        
        try {
            // Добавляем маршруты
            context.addRoutes(new CmsToDbRoute());
            context.addRoutes(new DbToReportRoute());
            
            // Запускаем контекст
            context.start();
            logger.info("Camel контекст запущен");
            
            // Запускаем синхронизацию
            triggerSync(context);
            
            // Ждем завершения обработки (увеличено время ожидания)
            Thread.sleep(10000);
            
            // Останавливаем контекст
            context.stop();
            logger.info("Camel контекст остановлен");
            
        } catch (Exception e) {
            logger.error("Ошибка при выполнении приложения", e);
            System.exit(1);
        }
    }
    
    private static void triggerSync(CamelContext context) {
        try {
            logger.info("Запуск синхронизации CMS -> БД");
            context.createProducerTemplate().sendBody("direct:cms-sync", null);
            logger.info("Синхронизация CMS -> БД завершена");
            
            // Небольшая пауза между синхронизациями
            Thread.sleep(1000);
            
            logger.info("Запуск синхронизации БД -> Report API");
            context.createProducerTemplate().sendBody("direct:db-to-report", null);
            logger.info("Синхронизация БД -> Report API завершена");
            
        } catch (Exception e) {
            logger.error("Ошибка при запуске синхронизации", e);
            e.printStackTrace();
        }
    }
}

