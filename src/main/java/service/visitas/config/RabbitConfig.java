package service.visitas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "realtyhub.events";
    public static final String DEAD_LETTER_EXCHANGE = "realtyhub.events.dlx";

    public static final String RK_PROPIEDAD_ESTADO_CAMBIADO = "propiedad.estado_cambiado";

    public static final String RK_LEAD_DESCARTADO = "lead.descartado";

    public static final String QUEUE_PROPIEDAD_ESTADO_CAMBIADO = "visitas.propiedad-estado-cambiado";
    public static final String QUEUE_PROPIEDAD_ESTADO_CAMBIADO_DLQ = "visitas.propiedad-estado-cambiado.dlq";

    public static final String QUEUE_LEAD_DESCARTADO = "visitas.lead-descartado";
    public static final String QUEUE_LEAD_DESCARTADO_DLQ = "visitas.lead-descartado.dlq";

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue propiedadEstadoCambiadoQueue() {
        return QueueBuilder.durable(QUEUE_PROPIEDAD_ESTADO_CAMBIADO)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(QUEUE_PROPIEDAD_ESTADO_CAMBIADO_DLQ)
                .build();
    }

    @Bean
    public Queue propiedadEstadoCambiadoDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_PROPIEDAD_ESTADO_CAMBIADO_DLQ).build();
    }

    @Bean
    public Binding propiedadEstadoCambiadoBinding(Queue propiedadEstadoCambiadoQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(propiedadEstadoCambiadoQueue)
                .to(eventsExchange)
                .with(RK_PROPIEDAD_ESTADO_CAMBIADO);
    }

    @Bean
    public Binding propiedadEstadoCambiadoDeadLetterBinding(Queue propiedadEstadoCambiadoDeadLetterQueue,
                                                            DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(propiedadEstadoCambiadoDeadLetterQueue)
                .to(deadLetterExchange)
                .with(QUEUE_PROPIEDAD_ESTADO_CAMBIADO_DLQ);
    }

    @Bean
    public Queue leadDescartadoQueue() {
        return QueueBuilder.durable(QUEUE_LEAD_DESCARTADO)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(QUEUE_LEAD_DESCARTADO_DLQ)
                .build();
    }

    @Bean
    public Queue leadDescartadoDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_LEAD_DESCARTADO_DLQ).build();
    }

    @Bean
    public Binding leadDescartadoBinding(Queue leadDescartadoQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(leadDescartadoQueue)
                .to(eventsExchange)
                .with(RK_LEAD_DESCARTADO);
    }

    @Bean
    public Binding leadDescartadoDeadLetterBinding(Queue leadDescartadoDeadLetterQueue,
                                                   DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(leadDescartadoDeadLetterQueue)
                .to(deadLetterExchange)
                .with(QUEUE_LEAD_DESCARTADO_DLQ);
    }
}
