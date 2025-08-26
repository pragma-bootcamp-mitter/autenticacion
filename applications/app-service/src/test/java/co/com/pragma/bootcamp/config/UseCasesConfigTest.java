package co.com.pragma.bootcamp.config;

import co.com.pragma.bootcamp.model.user.gateways.RepositorioUsuario;
import co.com.pragma.bootcamp.usecase.user.UsuarioCasoDeUso;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("CasoDeUso")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'CasoDeUso' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Mock
        private RepositorioUsuario repositorioUsuario;

        @Bean
        public MiCasoDeUso miCasoDeUso() {
            return new MiCasoDeUso();
        }

        @Bean
        public UsuarioCasoDeUso usuarioCasoDeUso() {
            return new UsuarioCasoDeUso(repositorioUsuario);
        }
    }

    static class MiCasoDeUso {
        public String execute() {
            return "MiCasoDeUso Test";
        }
    }
}