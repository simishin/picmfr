package qwr.model.Base;

import java.time.Instant;

import static qwr.util.BgFile.prnq;

/**
 * Статический класс для формирования ключа и извлечения его составляющих
 * структура ключа:
 * ".rrrr.rrrr.ssss.uuuu.uuuu.uuuu.uuUU.tttt&tttt.tttt.tttt.tttt.tttt.tttt.TTTT.TTTT"
 * где r-зарезервированы, s = xstruc <= 15 определяет принадлежность к структуре данных
 * u-часть кода пользователя полученная их даты создания пользователя 14 бит= 16383 дня = 45лет
 * U-часть кода пользователя в виде счетчика созданных пользователей в один день,
 * t-часть кода записи полученная из количества секунд, прошедших с даты
 * T-часть кода записи в виде счетчика записей созданных в одно время
 */
public class PublStat {
    private static int keyUser;
    private static final long begSecund = 1610643835L;//2021-01-15T00:03 точка отсчета

    public static long putKey(int xstruc){
        long timer= Instant.now().getEpochSecond()-begSecund;
        assert timer <= 134217727 : "Перевышение значения timer";
        assert xstruc <= 15 : "Перевышение значения xstruc";
        return (((long)timer & 0x7FFFFFF)<<8)|((long) xstruc & 15)<<52
                | ((long) keyUser & 0x3FFF)<<38;
    }//putKey

    public static long putKey(int timer, int xstruc, int user){
        assert timer <= 134217727 : "Перевышение значения timer";
        assert xstruc <= 15 : "Перевышение значения xstruc";
        assert user <= 16383 : "Перевышение значения user";
        return (((long)timer & 0x7FFFFFF)<<8)|((long) xstruc & 15)<<52
                | ((long) user & 0x3FFF)<<38;
    }//putKey

    public static int  getKeyUser() { return keyUser; }
    public static void setKeyUser(int k) { keyUser = k; }
    public int  putTime(long key){return (int) (((key>>8) & 0x7FFFFFF));}//134217727
    public int  putUser(long key){return (int) ((key>>38) & 0x3FFF);}//16383
    public int  putxStc(long key){return (int) ((key>>52) & 0xF);}//15

    public static int dupDay(){
        return (int) ((Instant.now().getEpochSecond()-begSecund)/(24 * 60 * 60));
    }//dupDay
    public static int changeTime(){
        return (int) ((Instant.now().getEpochSecond()-begSecund));
    }//cange
}//PublStat
