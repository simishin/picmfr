/** Класс создан 270322 взамен классов qwr.model.Base.EiUser extends BaseElement
 * и более раннего класса qwr.model.SharSystem.usrItm
 * код элемента класса используется при формировании кода записи
 * ".rrrr.rrrr.ssss.uuuu.uuuu.uuuu.uuUU.tttt&tttt.tttt.tttt.tttt.tttt.tttt.TTTT.TTTT"
 * где r-зарезервированы, s = xstruc <= 15 определяет принадлежность к структуре данных
 * u-часть кода пользователя полученная их даты создания пользователя 14 бит= 16383 дня = 45лет
 * U-часть кода пользователя в виде счетчика созданных пользователей в один день,
 * t-часть кода записи полученная из количества секунд, прошедших с даты
 * T-часть кода записи в виде счетчика записей созданных в одно время
 * точка отсчета для самой ранней даты является 2021-01-15T00:03 = 1610643835L
 *
 * Класс описывающий пользователей проектов
 * имеет логин, полное имя, дополнительную информацию о пользователе,
 * дату создания (код пользователя) и кто создал (код пользователя) {user},
 * код использования в проектах (возможно хранение пароля) {pass}:
 * 1-администратор проекта, создания и редактирования прав пользователей и просмотра записей
 * 2-пользователь, имеющий право изменения справочников и очистке записей помеченных под удаление
 * 3-пользователь проекта, имеющий право создания записей
 * 4-пользователь имеющий право редактирования отдельных полей (внесение факта)
 * 5-прользователь, имеющий право просмотра записей
 * 6-пользователь участвующий в создании записей других проектов
 * 7-более пользователь не встречается в кодах записей
 * вспомогательное поле времени создания (или номера/счетчика) записи, без сохранения
 *
 * Администратор может создавать пользователей проекта,
 * редактировать внешние пути, запускать процедуру удаления
 * объекта, но не может пополнять списки в отличии от
 * пользователя в данном проекте что бы сократить регистрации
 * под администратором ( в байте уровня может храниться пароль)
 */
package qwr.model.SharSystem;
import qwr.model.Base.EiUser;
import qwr.model.Base.PublStat;
import qwr.model.Base.RiPath;

import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static qwr.util.CollectUtl.*;

public record RiUser(int key, String login, String titul, String descr,
                     int user, int pass, int change, int order) {
    public static List<RiUser> list = new CopyOnWriteArrayList<>();

    public static final int sizeAr=9;//количество полей в текстовом файле данных
    public static boolean eqRi=false;//служебное поле для сравнения элементов
    private static int count;
    private static int maxKey;
    static {
        count=0;
        maxKey= (PublStat.dupDay()& 0x3FFF)<<2-1;
        assert prnq("~iniziall record RiUser");
    }//-----------------------------------------------------------------------
    public RiUser(int key,int user,String login,String titul,String descr,int pass,int change) {
        this(key, login, titul, descr, user, pass, change, count++);
    }
    public RiUser(int key, int user, String login, String titul, String descr, int pass) {
        this(key, login, titul, descr, user, pass, PublStat.changeTime(), count++);
    }
    public RiUser(String login, String titul, String descr, int pass) {
        this(genKey(), login, titul, descr, PublStat.getKeyUser(), pass,
                PublStat.changeTime(), count++);
        maxKey=genKey();
    }

    public RiUser(RiUser x, int i) { this(x.key, x.login, x.titul, x.descr,
            PublStat.getKeyUser(), x.pass & 0xFFFFFFF8 | i, PublStat.changeTime(), count++);
    }

    protected static int genKey(){
        int k=(PublStat.dupDay()& 0x3FFF)<<2;
        if (maxKey>=k) return maxKey+1;
        return k;
    }//genKey

    @Override
    public String toString() {//создание строки для записи в текстовый файл
        return sepr+ key +sepr+ user +sepr+ login +sepr+ titul +sepr+ descr
                +sepr+ pass +sepr+ pass +sepr + change +sepr;
    }//toString-------------------------------------------------------------------------
    /** интеграция данных в коллекцию вызывается из GrRecords.*.readRecord
     * @param words поток с исходными данными из массива слов строки ввода
     * @param src тип источника элемента
     * 0 - из документов и по умолчанию
     * 1 - файлы и папки локальной базы
     * 2 - создан или получен из командной строки
     * 3 - файлы и папки внешних данных не синхронизируемых (данные берутся но не проверяются)
     * 4,5,6,7. - файлы и папки внешних данных подлежащих синхронизации изменения данных
     * @return 0 без изменений, 1 переписаны поля, 2 заменяем, 3 добавлен, 4 первый,
     * -1 пропускаю элемент, -2 игнорировать по несответствию, -3 запрещенное состояние
     */
    public static int integrate(String[] words, int src) {
        if (words.length < sizeAr) {
            for (int i = 0; i < words.length; i++) prnt("+  "+i+"-"+words[i]);prnq("~"+words.length);
            return -2; //недостаточное количество элементов
        }
        RiUser z;
        try { z= new RiUser(Integer.parseInt(words[1]),Integer.parseInt(words[2]), words[3],
                words[4], words[5], Integer.parseInt(words[6]), Integer.parseInt(words[7]));
        }
        catch (Exception ex) {ex.printStackTrace();return -2;}
        if (list.size()<1) { list.add(z); return 4;}
        boolean q=true;
        //просматриваю на наличие
        for (RiUser x : list) if (x.login.equals(z.login) && x.key==z.key) {q=false; break; }
        //если не найден, то добавляю
        if (q) { list.add(z); return 3; }
//****************************************************************************************
        return 0;
    }//integrate


    protected RiUser readRi(String[] words, int sizeAr){
        assert sizeAr>7:"--BaseElement size arrey < 8";
        if (words.length<=sizeAr) { //проверяю количество элементов в массиве
            assert prnq("read> Error size array ("+sizeAr+")");return null;}//аварийное завершение
        try { return new RiUser(Integer.parseInt(words[1]),Integer.parseInt(words[2]), words[3],
                words[4], words[5], Integer.parseInt(words[6]), Integer.parseInt(words[7]));
        }
        catch (IndexOutOfBoundsException ex){ prne("Выход за пределы");  }
        catch (Exception ex) { return null;}
        return null;
    }//read--------------------------------------------------------------------------
    /**
     * Проверка на обновление элемента. Если есть изменения, то элемент пересоздается
     * с ними и замещает существующий элемент по данному индексу Если это обсолютно
     * новый элемент, то возвращается пустота. После прохождения по всем элементам
     * списка и если ни разу не был возвращен элемент, то он добавляется в список
     * Если элементы идентичны то в служебном поле eqRi возвращается true для continue.
     * Приоритет отдается данным более нового элемента.
     * @param obj проверяемый элемент
     * @return обновленный вновь пересозданный элемент или null если новый
     * false+null=Элементы не совпали. Продолжаем цикл.
     * false+RiUser=не используется
     * true+null=элементы эквивалентны или изменений не требуется. Выход из цикла.
     * true+RiUser=производим замещение элемента. Выход из цикла.
     * (int key, String login, String titul, String descr,
     *                      int user, int pass, int change, int order)
     */
    public RiUser merger(Object obj) {
        eqRi=true;
        if (this == obj) { return null;}//ссылается сам на себя
        if (obj == null || getClass() != obj.getClass()) return null;
        RiUser x = (RiUser) obj;
        if (key != x.key || !login.equals(x.login)) {eqRi=false; return null;}//это новый элемент
        if (change==x.change){ return null;}//элементы совпадают по изменению
        if (key == x.key && login.equals(x.login)){
            if (change < x.change) return new RiUser( x.key, x.login, x.titul, x.descr,
                    x.user, x.pass , x.change, x.order );
            else { return null;} //сохраняю значение без изменения
        }
        //логика до конца не отлажена
        eqRi=false;
        return null;
    }//merger

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiUser riUser = (RiUser) o;
        return key == riUser.key && login.equals(riUser.login);
    }//equals

//    @Override
//    public int hashCode() { return key; }
//    public int hashCode() { return Objects.hash(key, login); }

    public int key() { return key; }
    public static int Count() { return count; }
    public static void setCount(int count) { RiUser.count = count; }
    public static void incCount(){count++;}
//    @Override//----------------------------------------------------------------------
    public String print(){ return " \tc"+this.order +" \t( "+this.key+" ) \t"+(this.pass & 7)
            +"p\t"+"\t"+this.user+"u\t"+this.change+this.login +"\t"+this.titul; }
}//record RiUser
