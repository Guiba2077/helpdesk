package br.com.edu.senai.fatesg.ads4.helpdesk_back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI helpdeskOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Helpdesk API: Sistema de Controle de Chamados de TI (Tema G3)")
                        .description("API REST em Spring Boot + PostgreSQL 16+ (Schemas app/audit, PgBouncer, "
                                + "RBAC rl_leitor/rl_escritor, RLS, pgcrypto, Triggers, Procedure, Function, "
                                + "Views e statement_timeout curto).")
                        .version("1.0.0")
                        .contact(new Contact().name("Grupo G3 - ADS4 SENAI FATESG")));
    }
}

