package h4rar.telegram_news_bot.tgbot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Emojis {
    YES(EmojiParser.parseToUnicode(":white_check_mark:")),
    NO(EmojiParser.parseToUnicode(":x:")),
    LIKE(EmojiParser.parseToUnicode(":+1:"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}