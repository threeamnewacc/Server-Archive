package us.zonix.api;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import us.zonix.api.jedis.RedisController;
import us.zonix.api.repository.*;
import us.zonix.api.util.Search;

@Getter
@SpringBootApplication
@EntityScan("us.zonix.api.model")
@ComponentScan({"us.zonix.api.repository", "us.zonix.api.model", "us.zonix.api.controller"})
public class Application extends SpringBootServletInitializer {

	@Getter private static Application instance;

	@Getter private static RedisController controller;
	@Getter private static Search search;

	@Autowired private PlayerRepository playerRepository;
	@Autowired private PunishmentRepository punishmentRepository;
	@Autowired private PracticeStatsRepository practiceStatsRepository;
	@Autowired private PracticeMatchRepository practiceMatchRepository;
	@Autowired private MemeRepository memeRepository;

	public Application() {
		instance = this;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
