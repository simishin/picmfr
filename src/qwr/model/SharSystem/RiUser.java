/** Класс создан 270322 взамен классов qwr.model.Base.EiUser extends BaseElement
 * и более раннего класса qwr.model.SharSystem.usrItm
 * ключ элемента формируется interface Records makingKeyUser на основе ключа текущего
 * пользователя и времени создания.
 * Первый пользователь создается вместе с проектом и всегда имеет нулевой код.
 *
 *
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
import qwr.model.Base.PublStat;
import qwr.model.Base.Records;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static qwr.util.CollectUtl.*;

public record RiUser(int key, String login, String titul, String descr,
                     int user, int pass, int change, int order) implements Records {
    public static List<Records> list = new CopyOnWriteArrayList<>();
    private static          String userPrj=null;    //имя текущего пользователя
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

    public static String getUserPrj() { return userPrj; }
    public static void setUserPrj(String userPrj) { RiUser.userPrj = userPrj; }
    public static void prnUserPrj(){ prnq("UserPrj: "+userPrj+"  Users: "+list.size()); }

    protected static int genKey(){
//        PublStat.keyRecord(list,123);//---------------------------------------------
        long z = Records.makingKey(RiUser.list,123);//вызов статистического метода интерфейса
        int k=(PublStat.dupDay()& 0x3FFF)<<2;
        if (maxKey>=k) return maxKey+1;
        return k;
    }//genKey

    @Override
    public int hashCode() { return  key & 0xFFFFFFFF; }

    @Override //из интерфейса
    public long getKey() {
        return key;
    }

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
     * (int key, String login, String titul, String descr,
     *  int user, int pass, int change, int order)
     */
//    @Override
    public static int integrate(String[] words, int src)  {
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
        boolean jPresent=true;
        //просматриваю на наличие по key
        for (Records x : list) {
            RiUser y = (RiUser) x;
            if (y.key == z.key)
                if (y.login.equals(z.login)) {
                    jPresent = false;
                    break;
                }
                else  return 0; //частичное совпадение ключей - игнорирую дополнение
        }
        //если не найден, то добавляю
        if (jPresent) { list.add(z); return 3; }//добавлен  так как отсутствует
        /* выполняю слияние исходя из новизны элемента и его источника
        приоритет остается за более новыми данными, за исключением нулевых значений.
        Для источников одного уровня. Для источников разного уровня:
        низший - (3) - файлы и папки внешних данных не синхронизируемых = просто дописываю
        (0) - - из документов и по умолчанию
        (1)- файлы и папки локальной базы, если наложение, то корректирую локальные данные
        средний (4,5,6,7) - файлы и папки внешних данных подлежащих синхронизации
        высший () - данные из глобальных справочников
         */


//****************************************************************************************
        return 0;
    }//integrate


//    protected RiUser readRi(String[] words, int sizeAr){
//        assert sizeAr>7:"--BaseElement size arrey < 8";
//        if (words.length<=sizeAr) { //проверяю количество элементов в массиве
//            assert prnq("read> Error size array ("+sizeAr+")");return null;}//аварийное завершение
//        try { return new RiUser(Integer.parseInt(words[1]),Integer.parseInt(words[2]), words[3],
//                words[4], words[5], Integer.parseInt(words[6]), Integer.parseInt(words[7]));
//        }
//        catch (IndexOutOfBoundsException ex){ prne("Выход за пределы");  }
//        catch (Exception ex) { return null;}
//        return null;
//    }//read--------------------------------------------------------------------------
//    /**
//     * Проверка на обновление элемента. Если есть изменения, то элемент пересоздается
//     * с ними и замещает существующий элемент по данному индексу Если это обсолютно
//     * новый элемент, то возвращается пустота. После прохождения по всем элементам
//     * списка и если ни разу не был возвращен элемент, то он добавляется в список
//     * Если элементы идентичны то в служебном поле eqRi возвращается true для continue.
//     * Приоритет отдается данным более нового элемента.
//     * @param obj проверяемый элемент
//     * @return обновленный вновь пересозданный элемент или null если новый
//     * false+null=Элементы не совпали. Продолжаем цикл.
//     * false+RiUser=не используется
//     * true+null=элементы эквивалентны или изменений не требуется. Выход из цикла.
//     * true+RiUser=производим замещение элемента. Выход из цикла.
//     *
//     */
//    public RiUser merger(Object obj) {
//        eqRi=true;
//        if (this == obj) { return null;}//ссылается сам на себя
//        if (obj == null || getClass() != obj.getClass()) return null;
//        RiUser x = (RiUser) obj;
//        if (key != x.key || !login.equals(x.login)) {eqRi=false; return null;}//это новый элемент
//        if (change==x.change){ return null;}//элементы совпадают по изменению
//        if (key == x.key && login.equals(x.login)){
//            if (change < x.change) return new RiUser( x.key, x.login, x.titul, x.descr,
//                    x.user, x.pass , x.change, x.order );
//            else { return null;} //сохраняю значение без изменения
//        }
//        //логика до конца не отлажена
//        eqRi=false;
//        return null;
//    }//merger

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

//    public int key() { return key; }
//    public static int Count() { return count; }
//    public static void setCount(int count) { RiUser.count = count; }
//    public static void incCount(){count++;}
//    @Override//----------------------------------------------------------------------

    /**
     * Определяю нулевого и первоначальных пользователей
     * Если список пуст, то создаю пользователя, иначе задаю
     * пользователя по умолчанию, если он не указан в командной строке.
     * Если пользователь указан в командной строке, то проверяю его
     * существование и при отсутствии добавляю его в список.
     */
    public static RiUser definePrimaryUsers(){
        assert prnq("$ RiUser.definePrimaryUsers $");
//        boolean jPresent=true;
        RiUser z = null;
        int y;
        if (userPrj==null){//Указание на пользователя в командной строке нет
            if (list.isEmpty()){//список пустой
                y=(RiProdject.curCreat& 0x3FFFC000)<<2;//нулевой пользователь
                z=new RiUser(y,RiProdject.curName,RiProdject.curTitul,"",0,0,0,count++);
                list.add( z);
                assert prnq("RiUser.definePrimaryUsers > create user");
            } else {//в списке есть пользователи
                z = (RiUser) list.get(0);
                assert prnq("RiUser.definePrimaryUsers > set current user");
            }
        } else { //Пользователь задан в командной строке
            if (! list.isEmpty()) for (Records x:list ) //просматриваю список на наличие
                if (((RiUser) x).login.equals(userPrj)) {
//                    jPresent=false;
                    z=(RiUser)x;
                    break;
                }

            if (z==null){//элемент не найден - добавляю
                y=Records.makingKeyUser(list,(RiProdject.curCreat& 0x3FFFC000)>>14);
                z=new RiUser(y,userPrj,userPrj,"",0,0,0,count++);
                list.add( z);
                assert prnq("RiUser.definePrimaryUsers > addition user");
            }
        }//else
        return z;
    }//definePrimaryUsers

    public String print(){ return " \tc"+this.order +" \t( "+this.key+" ) \t"+(this.pass & 7)
            +"p\t"+"\t"+this.user+"u\t"+this.change+this.login +"\t"+this.titul; }
}//record RiUser
