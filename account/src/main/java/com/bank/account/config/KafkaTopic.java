package com.bank.account.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Создает топики для Кафки
 */
@Configuration
public class KafkaTopic {

    /**
     * Топик для получения запроса из другого сервиса на создание нового аккаунта
     *
     * @return топик с именем account.create
     */
    @Bean
    public NewTopic accountCreateTopic() {
        return TopicBuilder.name("account.create").build();
    }

    /**
     * Топик для получения запроса на обновление данных аккаунта из другого сервиса
     *
     * @return топик с именем account.update
     */
    @Bean
    public NewTopic accountUpdateTopic() {
        return TopicBuilder.name("account.update").build();
    }

    /**
     * Топик для получения запроса на удаление аккаунта из другого сервиса
     *
     * @return топик с именем account.delete
     */
    @Bean
    public NewTopic accountDeleteTopic() {
        return TopicBuilder.name("account.delete").build();
    }

    /**
     * Топик для получения запроса на выдачу списка всех аккаунтов от другого сервиса
     *
     * @return топик с именем account.get
     */
    @Bean
    public NewTopic accountGetTopic() {
        return TopicBuilder.name("account.get").build();
    }

    /**
     * Топик для получения запроса на выдачу конкретного аккаунта по id от стороннего сервиса
     *
     * @return топик с именем account.getById
     */
    @Bean
    public NewTopic accountGetByIdTopic() {
        return TopicBuilder.name("account.getById").build();
    }

    /**
     * Топик для получения запроса на отправку логов
     *
     * @return топик с именем audit.log
     */
    @Bean
    public NewTopic accountLogsTopic() {
        return TopicBuilder.name("audit.logs").build();
    }

    /**
     * Топик для отправки результатов сохранения нового аккаунта другому сервису
     *
     * @return топик с именем external.account.create
     */
    @Bean
    public NewTopic accountCreateExternalTopic() {
        return TopicBuilder.name("external.account.create").build();
    }

    /**
     * Топик для отправки результатов изменения данных аккаунта другому сервису
     *
     * @return топик с именем external.account.update
     */
    @Bean
    public NewTopic accountUpdateExternalTopic() {
        return TopicBuilder.name("external.account.update").build();
    }

    /**
     * Топик для получения результата выполнения удаления аккаунта другим сервисом
     *
     * @return топик с именем external.account.delete
     */
    @Bean
    public NewTopic accountDeleteExternalTopic() {
        return TopicBuilder.name("external.account.delete").build();
    }

    /**
     * Топик для получения результата запроса на выдачу списка всех аккаунтов другому сервису
     *
     * @return топик с именем external.account.get
     */
    @Bean
    public NewTopic accountGetExternalTopic() {
        return TopicBuilder.name("external.account.get").build();
    }

    /**
     * Топик для получения результата выполнения запроса на выдачу конкретного аккаунта по id другому сервису
     *
     * @return топик с именем external.account.getById
     */
    @Bean
    public NewTopic accountGetByIdExternalTopic() {
        return TopicBuilder.name("external.account.getById").build();
    }

    /**
     * Топик для получения записей ошибок
     * @return топик с именем error.logs
     */
    @Bean
    public NewTopic errorLogsTopic() {
        return TopicBuilder.name("error.logs").build();
    }

    /**
     * Топик для получения результатов записи логов
     *
     * @return топик с именем external.audit.logs
     */
    @Bean
    public NewTopic accountExternalLogs() {
        return TopicBuilder.name("external.audit.logs").build();
    }
}
