package jieyi.lu.huanjuweflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
@EnableReactiveMongoRepositories
@EnableR2dbcRepositories
public class HuanJuWeFluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuanJuWeFluxApplication.class, args);
    }

}
