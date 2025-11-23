package ru.integration;

public class Config {

    public static final int STUDENT_ID = 6;
    

    public static final String BASE_API_URL = "http://212.237.219.35:8080/";
    
    public static final int CMS_PAGE_SIZE = 10; // Максимум 10 элементов на странице
    
    // Настройки БД
    public static final String DB_URL = "jdbc:h2:file:./historical_data;AUTO_SERVER=TRUE";
    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "";
}