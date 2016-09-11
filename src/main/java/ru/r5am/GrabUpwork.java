package ru.r5am;

/*  Created by Aleksandr Jashhuk (Zoer) on 09.09.2016.  */

import com.codeborne.selenide.Configuration;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;



class GrabUpwork {
    public static void main(String[] args) throws IOException {

        final String    configFileName = "grabupwork.json";                // файл конфигурации
        final String    configFilePath = ".grabupwork";                    // путь файлу конфигурации
        final String    userHomePath = System.getProperty("user.home");    // домашняя папка пользователя

        String fullConfigFileName = userHomePath + File.separator + configFilePath + File.separator + configFileName;

        // Расположение chromedriver.exe
        String PATH_TO_CHROMEDRIVER_EXE = "src\\main\\resources\\web_drivers\\chromedriver.exe"; // TODO: В конфиг!

        // Для вывода русских сообщений в консоль Windows
        PrintStream ps = new PrintStream(System.out, true, "CP866");

        // Форматируем дату и время, выставляем временнУю зону Москвы
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(tz);
        System.out.println("Начало работы: " + dateFormat.format(new Date()));


        String jsonFromConfigFIle = readConfigFile(fullConfigFileName);    // читаем JSON-файл в строку
//        System.out.println("File content: " + jsonFromConfigFIle);



        Gson gson = new Gson();
        Configs configs = Configs.getInstance();
        try {
            configs = gson.fromJson(jsonFromConfigFIle, Configs.class);
        } catch (Exception e) {

            System.out.println("Error read JSON file: " + fullConfigFileName + "\n");
            ps.println("Ошибка чтения JSON файла: " + fullConfigFileName + "\n");
            e.printStackTrace();
            System.exit(1);
        }
//        System.out.println(configs.username + "\n" + configs.password + "\n" + configs.min_fee + "\n" +
//                           configs.max_fee + "\n" + configs.experience_level + "\n" + configs.fee_type + "\n" +
//                           configs.output_path + "\n" + configs.output_filename + "\n" + configs.search_word
//        );

        // Использовать Chrome
        System.setProperty("webdriver.chrome.driver", PATH_TO_CHROMEDRIVER_EXE);
        Configuration.browser = "chrome";

        open("https://www.upwork.com");

        $(By.linkText("LOGIN")).click();
        $(By.id("login_username")).append(configs.username);
        $(By.id("login_password")).append(configs.password);
        $(By.xpath("//label/span[@class='checkbox-replacement-helper']")).click();
        $(By.xpath("//div[@class='form-group m-lg-right hidden-xs']/button[@type='submit' and contains(text(),'Log In')]")).click();


        sleep(10000);
        close();




    }

    // Singleton  ;^)
    private static class Configs {
        private static Configs instance;

        String username;
        String password;
        String search_word;
        String fee_type;
        String experience_level;
        int min_fee;
        int max_fee;
        String output_path;
        String output_filename;

        private Configs(){}

        static synchronized Configs getInstance() {
            if (instance == null) {
                instance = new Configs();
            } else {
                System.out.println("Error: Second Singleton!");
                System.exit(1);
            }
            return instance;
        }
    }

    private static String readConfigFile(String nameConfigFile) throws IOException {
        String fileContent = null;
        try {
             fileContent =  new String(Files.readAllBytes(Paths.get(nameConfigFile)));
        } catch (Exception e) {
            System.out.println("Error read config file: " + nameConfigFile + "\n");
            // Для вывода русских сообщений в консоль Windows
            PrintStream ps = new PrintStream(System.out, true, "CP866");
            ps.println("Ошибка чтения конфигурационного файла: " + nameConfigFile + "\n");
        }
        return fileContent;
    }
}
