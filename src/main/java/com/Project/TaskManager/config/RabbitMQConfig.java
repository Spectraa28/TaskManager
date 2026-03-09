package com.Project.TaskManager.config;


import java.util.concurrent.Executor;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RabbitMQConfig {
    public static final String NOTIFICATION_QUEUE 
                    = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE
                    = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY
                    =  "notification_routing_key";

    @Bean
    public Queue notificationQueue(){
        return new Queue(NOTIFICATION_QUEUE,true);
            
        };

    @Bean
    public TopicExchange notificatioExchange(){
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean 
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange){
            return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean(name = "taskExecutor")
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("cTaskExecutor-");
    executor.initialize();
    return executor;
}

    @Bean
    @SuppressWarnings("deprecation")
    public MessageConverter jacksonMessageConverter() {
    return new Jackson2JsonMessageConverter();
}

    @Bean
    @SuppressWarnings("deprecation")  
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
         rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
}
        }

