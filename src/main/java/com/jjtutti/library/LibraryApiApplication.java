package com.jjtutti.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LibraryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

	@Value("${spring.datasource.url}") String url;

	@EventListener(ApplicationStartedEvent.class)
	void onReady() {
		System.out.println("Hello World");
		System.out.println(url);
	}

}
