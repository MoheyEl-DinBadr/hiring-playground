package com.celfocus.hiring.kickstarter;

import com.celfocus.hiring.kickstarter.db.repo.ProductRepository;
import com.celfocus.hiring.kickstarter.util.ProductsLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KickstarterApplication implements ApplicationRunner {
    private final ProductRepository productRepository;

    public KickstarterApplication(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(KickstarterApplication.class, args);
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var products = ProductsLoader.loadProducts(new ObjectMapper());

        productRepository.saveAll(products);
    }
}
