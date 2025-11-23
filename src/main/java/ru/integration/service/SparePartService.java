package ru.integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.integration.dto.SparePartDto;
import ru.integration.model.SparePart;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис для работы с запчастями в БД
 */
public class SparePartService {
    private static final Logger logger = LoggerFactory.getLogger(SparePartService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final EntityManagerFactory emf;
    
    public SparePartService() {
        emf = Persistence.createEntityManagerFactory("integrationPU");
    }
    
    /**
     * Добавить или обновить запчасть в БД
     * @return "added" если добавлена, "updated" если обновлена
     */
    public String upsertSparePart(SparePartDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Ищем существующую запчасть
            SparePart existing = em.createQuery(
                "SELECT s FROM SparePart s WHERE s.spareCode = :code", SparePart.class)
                .setParameter("code", dto.getSpareCode())
                .getResultStream()
                .findFirst()
                .orElse(null);
            
            String result;
            if (existing != null) {
                // Обновляем существующую запись
                updateSparePart(existing, dto);
                result = "updated";
                logger.debug("Обновлена запчасть: {}", dto.getSpareCode());
            } else {
                // Создаем новую запись
                SparePart newSpare = createSparePart(dto);
                em.persist(newSpare);
                result = "added";
                logger.debug("Добавлена новая запчасть: {}", dto.getSpareCode());
            }
            
            em.getTransaction().commit();
            return result;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.error("Ошибка при сохранении запчасти {}: {}", dto.getSpareCode(), e.getMessage(), e);
            return null;
        } finally {
            em.close();
        }
    }
    
    private SparePart createSparePart(SparePartDto dto) {
        LocalDate updatedAt = parseDate(dto.getUpdatedAt());
        return new SparePart(
            dto.getSpareCode(),
            dto.getSpareName(),
            dto.getSpareDescription() != null ? dto.getSpareDescription() : "",
            dto.getSpareType(),
            dto.getSpareStatus(),
            new BigDecimal(dto.getPrice()),
            dto.getQuantity(),
            updatedAt
        );
    }
    
    private void updateSparePart(SparePart spare, SparePartDto dto) {
        spare.setSpareName(dto.getSpareName());
        spare.setSpareDescription(dto.getSpareDescription() != null ? dto.getSpareDescription() : "");
        spare.setSpareType(dto.getSpareType());
        spare.setSpareStatus(dto.getSpareStatus());
        spare.setPrice(new BigDecimal(dto.getPrice()));
        spare.setQuantity(dto.getQuantity());
        spare.setUpdatedAt(parseDate(dto.getUpdatedAt()));
    }
    
    private LocalDate parseDate(String dateStr) {
        // Обрабатываем формат с временем или без
        String dateOnly = dateStr.split("T")[0];
        return LocalDate.parse(dateOnly, DATE_FORMATTER);
    }
    
    /**
     * Получить все запчасти из БД
     */
    public List<SparePart> getAllSpareParts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM SparePart s", SparePart.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Получить количество запчастей в БД
     */
    public long getCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM SparePart s", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

