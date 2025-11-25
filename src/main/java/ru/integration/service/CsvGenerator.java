package ru.integration.service;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.integration.model.SparePart;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Генератор CSV файлов из данных БД
 */
public class CsvGenerator implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(CsvGenerator.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public void process(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        List<SparePart> spareParts = exchange.getIn().getBody(List.class);
        
        String csvContent = generateCsv(spareParts);
        exchange.getIn().setBody(csvContent);
        exchange.getIn().setHeader("Content-Type", "text/csv; charset=utf-8");
    }
    
    /**
     * Генерирует CSV файл из списка запчастей
     * Формат: без заголовка, разделитель ";", UTF-8
     */
    public String generateCsv(List<SparePart> spareParts) {
        StringBuilder csv = new StringBuilder();
        
        for (SparePart part : spareParts) {
            csv.append(escapeCsvField(part.getSpareCode())).append(";");
            csv.append(escapeCsvField(part.getSpareName())).append(";");
            csv.append(escapeCsvField(part.getSpareDescription() != null ? part.getSpareDescription() : "")).append(";");
            csv.append(escapeCsvField(part.getSpareType())).append(";");
            csv.append(escapeCsvField(part.getSpareStatus())).append(";");
            csv.append(part.getPrice()).append(";");
            csv.append(part.getQuantity()).append(";");
            csv.append(part.getUpdatedAt().format(DATE_FORMATTER));
            csv.append("\n");
        }
        
        logger.info("Сгенерирован CSV файл с {} запчастями", spareParts.size());
        return csv.toString();
    }
    
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Если поле содержит точку с запятой или кавычки, нужно экранировать
        if (field.contains(";") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}

