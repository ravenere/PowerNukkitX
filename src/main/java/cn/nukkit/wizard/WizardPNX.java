package cn.nukkit.wizard;

import cn.nukkit.console.NukkitConsole;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class WizardPNX {
    private String chosenLanguage;
    private String path;

    public void verify(final String filePath, NukkitConsole console, String predefinedLanguage) {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is empty.");
            return;
        }

        path = filePath;

        if (!new File(filePath + "worlds/").exists()) {
            new File(filePath + "worlds/").mkdirs();
        }
        if (!new File(filePath + "players/").exists()) {
            new File(filePath + "players/").mkdirs();
        }
        if (!new File(filePath + "plugins/").exists()) {
            new File(filePath + "plugins/").mkdirs();
        }

        if (!new File(filePath + "command_data/").exists()) {
            new File(filePath + "command_data/").mkdirs();
        }

        if (!new File(filePath + "pnx.yml").exists()) {
            log.info("{}Welcome! This is the first time you are using PNX.{}", TextFormat.YELLOW, TextFormat.RESET);
            this.getAvailableLanguages();
            chosenLanguage = selectLanguage(predefinedLanguage, console);
        }
    }

    private String selectLanguage(String predefinedLanguage, NukkitConsole console) {
        List<String> availableLanguages = getAvailableLanguages();
        log.info("Please select a language from the list below:");

        // Display available languages
        availableLanguages.forEach(lang -> log.info("- {}", lang));

        String chosenLanguage = null;
        while (chosenLanguage == null) {
            log.info("Type the language name or part of it:");
            String input = console.readLine().trim();

            // Check if the input matches a language
            List<String> matches = availableLanguages.stream()
                    .filter(lang -> lang.toLowerCase().contains(input.toLowerCase()))
                    .collect(Collectors.toList());

            if (matches.size() == 1) {
                chosenLanguage = matches.get(0).split(" => ")[0];
            } else if (matches.isEmpty()) {
                log.warn("No matching language found. Please try again.");
            } else {
                log.warn("Multiple matches found: {}. Please be more specific.", matches);
            }
        }
        return chosenLanguage;
    }

    private List<String> getAvailableLanguages() {
        try (InputStream languageList = getClass().getClassLoader().getResourceAsStream("language/language.list")) {
            if (languageList == null) {
                throw new IllegalStateException("language/language.list is missing. Ensure the file is in the classpath.");
            }
            return new BufferedReader(new InputStreamReader(languageList))
                    .lines()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the language list", e);
        }
    }

    public String getLanguage() {
        if (chosenLanguage == null) {
            File configFile = new File(this.path + "pnx.yml");

            Config configInstance = new Config(configFile);
            return chosenLanguage = configInstance.getString("settings.language", "eng");
        }
        return chosenLanguage;
    }

}
