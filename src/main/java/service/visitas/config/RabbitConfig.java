package service.visitas.config;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;


@Configuration
public class RabbitConfig {



    public static final String TOPIC_EXCHANGE_NAME = "realtyhub.events";
    public static final String ROUTING_KEY = "lead.assigned";
    public static final String NAME_QUEUE = "visitas.lead-assigned";


    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }


    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE_NAME, true, false);
    }


    @Bean
    public Queue colaLeads(){
        return new Queue(NAME_QUEUE, true);
    }


    @Bean
    public Binding bindingLeads(Queue colaLeads, TopicExchange topicExchange){

        return BindingBuilder.bind(colaLeads)
                .to(topicExchange)
                .with(ROUTING_KEY);
    }


}
