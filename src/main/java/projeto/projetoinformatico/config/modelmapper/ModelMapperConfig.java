package projeto.projetoinformatico.config.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
/*
package projeto.projetoinformatico.config.modelmapper;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
               .setAccessLevel(AccessLevel.PRIVATE)
               .setFieldMatchingEnabled(true)
               .setFieldAccessLevel(AccessLevel.PRIVATE)
               .setMethodAccessLevel(AccessLevel.PRIVATE);
        return modelMapper;
    }
}
 */