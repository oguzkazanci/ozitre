package com.trend.ozitre.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile("h2")
public class SeedRunner implements CommandLineRunner {
    private final Path marker = Paths.get("data/.seed.done");
    @Override public void run(String... args) throws Exception {
        if (Files.exists(marker)) return;
        Files.createDirectories(marker.getParent());
        Files.createFile(marker);
    }
}
