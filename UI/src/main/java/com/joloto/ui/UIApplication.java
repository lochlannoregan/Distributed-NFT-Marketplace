package com.joloto.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:mongodb-atlas.properties")
public class UIApplication {

	public static void main(String[] args) {
		SpringApplication.run(UIApplication.class, args);
	}

}
