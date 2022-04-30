package qwr.model.Base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static qwr.util.CollectUtl.prnq;

/**
 * Статический класс для формирования ключа и извлечения его составляющих
 * структура ключа:
 * ".rrrr.rrrr.ssss.uuuu.uuuu.uuuu.uuUU.tttt&tttt.tttt.tttt.tttt.tttt.tttt.TTTT.TTTT"
 * где r-зарезервированы, s = xstruc <= 15 определяет принадлежность к структуре данных
 * u-часть кода пользователя полученная их даты создания пользователя 14 бит= 16383 дня = 45лет
 * U-часть кода пользователя в виде счетчика созданных пользователей в один день,
 * t-часть кода записи полученная из количества секунд, прошедших с даты
 * T-часть кода записи в виде счетчика записей созданных в одно время
 новый подход к формировапнию ключей записей о 290422
  0x3FFFF=262143=повторяемость 29,9 лет  при ежечасовом создании проекта
   0x3FFF= 16383 дней = повтряемость 44.88 лет при создании пользователя
   При формировании ключа секунды обрезаются сдвигом и наложением маски
   исходя из коэффициента цикличности примерно в 30 лет или 15 или 7 для пользователя
   Возможноприменение коэффициента цикличности для основного ключа записей
   расчет: сдвиг определяет шаг в секундах, а маска (0хFFF) цикличность как произведения
   шага на максимальное значение маски.
   Цикличность проекта 29.9 лет, пользователя 7 лет
   Если происходит наложение на ключ, тогда проверяю значение минус один для проекта 
   и пользоватеоля. Для записей - положительное приращение. И так до положительного рашения.
 0x3FFF= 16383
 7 лет= 2555 дней примерно 4 часа; 4 часа = 14400 секунд; ближайщее 16384 сдвиг на 15
тогда 3FFF.FFFF = 1073741823 секунд = 34 года цикл повторения
 */
public class PublStat <E>  {
    private static int keyUser;
    protected static final long begSecund = 1610643835L;//2021-01-15T00:03 точка отсчета
//
//    /**
//     *
//     * @param list список записей для которого готовится новый элемент
//     * @param keyUser код пользователя создающего новый элемент для списка
//     * @param <E> тип записи базы данных
//     * @return код для генерации нового элемента
//     */
//    public static <E> long keyRecord(Collection<E> list, int keyUser) {
//        long timer= Instant.now().getEpochSecond()-begSecund;
//        timer &=0xFFFFFFFF;
//        long y = keyUser<<16;
//        y |= timer;
//        if (list.isEmpty()){
//            return y;
//        }
//        boolean qnext=true, qsteep;
//        while (qnext){
//            qsteep=false;
//            for (E x: list) if (x.hashCode()==timer){ qsteep=true; break; }
////            x.
//        }
//        return 0;
//    }//keyRecord
//    /**
//     * Вызывается из GrRecords.ХХХХХ.writPL(BufferedWriter bw)
//     * @param list список требуемого класса объекта записи
//     * @param bw буфер для записи, наследуемый при вызове
//     * @param <T> тип объекта записи, задается при вызове
//     * @return возвращает истина, если запись прошла без сбоев
//     */
//    public <T> boolean writPL(Stream<T> list, BufferedWriter bw) {
//        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
//        AtomicBoolean z= new AtomicBoolean(true);
//        list.forEach(q-> {
//            try { bw.write(this.name()+q.toString()+"\n"); }
//            catch (IOException e) { e.printStackTrace(); z.set(false);}
//        });
//        return z.get();
//    }//writPL

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

//    @Override
//    public long key() {
//        return 0;
//    }
}//PublStat
