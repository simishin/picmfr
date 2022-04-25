/**     Базовый Элемент всех справочников
 * имеет наименование, описание, дату создания и кто создал, флаг далее не
 * использовать, флаг использования в проектах, способ появления элемента:
 * 1(3)-получен из глобальных справочников, 2(2)-из внешних справочников,
 * 3(1)-создан в локально, 5(-1)-создан локально с возможностью одностороннего
 * изменения, 6(-2)-из внешнего источника с возможностью одностороннего изменения,
 * 4(0)-получен из справочников по умолчанию, (-3) инициализация элемента.
 * (4)альтернативное обозначение элемента. При использовании в проекте ставится флаг.
 * Флаг разрешения использования сбрасывается если флаг используется не
 * установлен или данный флаг сброшен во внешних справочниках. Если оба флага
 * сброшены и он не используется во внешних проектах то элемент может быть удален,
 * кроме как из справочника по умолчанию. Процедуру удаления может запустить только
 * администратор. Изменение элемента может произвести только администратор, если
 * данный элемент не задействован во внешних проектах. При подключении нового
 * проекта проверяется соответствие элементов, если обнаружено несоответствие проект
 * не синхронизируется (флаг сброшен). В момент создания элемента проверяется
 * существование одноименного элемента во внешних справочниках с которыми установлена
 * синхронизация. Элементы с возможностью изменения не могут быть применены в
 * назначениях и пока он не будет зарегистрирован во внешних синхронизующихся
 * справочниках. Для обеспечения паралельного ввода к числу секунд даты создания
 * добавляется в старшие разряды числовой код пользователя (сложный ключ).
 * При создании проекта создается первый администратор и от него все остальное */
package qwr.model.Base;

import qwr.util.BgFile;
import qwr.util.DateTim;

import static qwr.util.BgFile.prnq;
import static qwr.util.BgFile.sepr;

public abstract class BaseElement {//Guid=справочник,руководство
    private static int count=0;//cчетчик для элементов по умолчанию
    protected int       chang;  //1) время последней регистрации (* int)
    protected String    titul;  //2) наименование (*)
    protected String    descr;  //3) описание (*)
    protected int       typ;    //4) источник получения элемента (* short)
    protected boolean   isusr;  //5) используется в текущем проекте
    protected boolean   solvd;  //6) разрешено использование
    protected int       owner;  //7) идентификатор пользователя создавшего элемент
    protected int       key;    //8) время создания в секундах с 1 января 1970г.(ключ)
    //общий конструктор--------------------------------------------------------------
    public BaseElement(String titul, String descr, int typ, boolean isusr,
                       boolean solvd, int chang){
        this.titul  = titul;        //наименование
        this.descr  = descr;        //описание
        this.typ    = typ;        //способ появления элемента:
        this.isusr  = isusr;        //используется в текущем проекте
        this.solvd  = solvd;         //разрешено использование
        this.chang  = chang;        //идентификатор
        this.owner  = BgFile.getUserIdPrj();//идентификатор пользователя создавшего элемент
        this.key    = DateTim.newSeconds();//cек1.01.1970
    }//ElGuid
    //конструктор элемента из справочника
    public BaseElement(String titul, String descr){//
        this.titul  = titul;        //наименование
        this.descr  = descr;        //описание
        this.typ    = 1;            //способ появления элемента- из справочника
        this.isusr  = false;        //используется в текущем проекте
        this.solvd  = true;         //разрешено использование
        this.chang  = DateTim.newSeconds(); //идентификатор-ссылка на базовый элемент
        this.owner  = 0;//идентификатор пользователя создавшего элемент-система
        this.key    = count++; //время создания (ключ)
    }//ElGuid
    //конструктор для создаваемых элементов
    public BaseElement(String titul, int typ){//
        this.titul  = titul;        //наименование
        this.descr  = null;         //описание
        this.typ    = typ;  //создан владельцем для регистрации
        this.isusr  = false;        //используется в текущем проекте
        this.solvd  = true;         //разрешено использование
        this.chang  = 0;            //идентификатор-ссылка на базовый элемент
        this.owner  = 0;//идентификатор пользователя создавшего элемент-система
        this.key    = DateTim.newSeconds(); //время создания (ключ)
    }//ElGuid------------------------------------------------------------------------
    public static void clear(){ count=0; }//сброс счетчика элементов
    public void     setChang(int chang) { this.chang = chang; }//1) идентификатор (*)
    public void     setTitul(String titul) { this.titul = titul; }//2) наименование (*)
    public void     setDescr(String descr) { this.descr = descr; }//3) описание (*)
    public void     setTyp(int typ) { this.typ = typ; }//4тип появления
    public void     setIsusr(boolean isusr){this.isusr=isusr;}//используется в проекте
    public void     setSolvd(boolean solvd){this.solvd = solvd;}//разрешено использование
    public void     setOwner(int owner){ this.owner = owner; }//пользователь
    public void     setKey(int key) { this.key = key; }//время создания
    public String   getTitul() { return titul; }//наименование
    public String   getDescr() { return descr; }//описание
    public int getTyp() { return typ; }//способ появления элемента
    public boolean  isIsusr()  { return isusr; }//используется в текущем проекте
    public boolean  isSolvd()   { return solvd;  }//разрешено использование
    public int      getChang()  { return chang;  }//идентификатор
    public int      getOwner() { return owner; }//идентификатор пользователя создавшего элемент
    public int      getKey() { return key; }//времени создания
    public String   writ() { return sepr+//создание строки для записи в текстовый файл
            key +sepr+ owner +sepr+ (solvd ? "Y":"n")+sepr+ typ +sepr+
        (isusr ? "Y":"n")+sepr+ chang +sepr+ titul+sepr+ descr+sepr;
    }//write-------------------------------------------------------------------------
    protected boolean read(String[] words, int sizeAr){
        assert sizeAr>7:"--BaseElement size arrey < 8";
        if (words.length<=sizeAr) { //проверяю количество элементов в массиве
            assert prnq("read> Error size array ("+sizeAr+")");return true;}//аварийное завершение
        key   = Integer.parseInt(words[1]);//8) время регистрации в базе(* int)(ключ)
        owner = Integer.parseInt(words[2]);//7) пользователь создавший
        solvd = words[3].startsWith("Y");//6) доступно чтение(*)
        typ   = Integer.parseInt(words[4]);//4) источник получения элемента (* short)
        isusr = words[5].startsWith("Y");//5) синхронизация выполнена (*)
        chang = Integer.parseInt(words[6]);//1) время последнего изменения внешнего справочника
        titul = words[7];//2) путь к внешнему справочнику(* String)
        descr = words[8];//3) наименование часового пояса внешнего справочника
        return false;
    }//read--------------------------------------------------------------------------
    public boolean  equals(BaseElement obj) { return
        (this.titul.equals(obj.titul)) && //2) путь к внешнему справочнику(* String)
        (this.descr.equals(obj.descr)) && //3) наименование часового пояса внешнего справочника
        (this.key ==obj.key);          //8) время регистрации в базе(* int)(ключ)
    }//equals------------------------------------------------------------------------
    public abstract boolean read(String[] words);//создание элемента из строки файла
    @Override//----------------------------------------------------------------------
    public String toString(){ return this.titul; }
    public String print(){return key+"\t ["+typ+"] \t"+owner+"\t  "+
            (isusr ? "Y":"n")+"  \t"+(solvd ? "Y":"n")+"  \t"+titul+"\t"+descr+" $";}
    public static boolean prTitl()//печать шакки
        {prnq("\tkey\t\t[typ] owner isusr solvd\t\ttitul");return true;}
}//BaseElement