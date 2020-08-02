package h4rar.telegram_news_bot.tgbot.utils;

import java.io.File;

public class FileManager {
    public static void createFolder(String path){
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
}
