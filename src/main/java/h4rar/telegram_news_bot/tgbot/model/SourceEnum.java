package h4rar.telegram_news_bot.tgbot.model;

public enum SourceEnum {
    MIT{
        public String getName(){
            return "MIT News";
        }
    },
    Harvard{
        public String getName(){
            return "Harvard Gazette";
        }
    },
    MIPT{
        public String getName(){
            return "Новости и события МФТИ";
        }
    },
    Samara{
        public String getName(){
            return "НОВОСТИ САМАРСКОГО УНИВЕРСИТЕТА";
        }
    },
    MSU{
        public String getName(){
            return "Новости МГУ";
        }
    },
    ;

    public abstract String getName();

    public static SourceEnum getSourceEnum(String source){
        SourceEnum sourceEnum = null;
        if(source.equals("MIT News")){
            sourceEnum = SourceEnum.MIT;
        }
        else if(source.equals("Harvard Gazette")){
            sourceEnum = SourceEnum.Harvard;
        }
        else if(source.equals("Новости и события МФТИ")){
            sourceEnum = SourceEnum.MIPT;
        }
        else if(source.equals("НОВОСТИ САМАРСКОГО УНИВЕРСИТЕТА")){
            sourceEnum = SourceEnum.Samara;
        }
        else if(source.equals("Новости МГУ")){
            sourceEnum = SourceEnum.MSU;
        }
        return sourceEnum;
    }
}
