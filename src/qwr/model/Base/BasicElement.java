/** Базовый Элемент всех справочников создан 120322
 * как замена классу BaseElement,...
 * отличием является ключ содержащий три элемента
 * код записи ((31-5)+12)+код пользователя (14+4) + код структуры (7)
 * и длинной 63 бит, а так же добавлена ссылка на замещающий новый элемент
 * Источник получения элемента зашит в коде пользователя: 0-по умолчанию,
 * 1- из глобальных справочников, если код пользователя совпадает с текущим
 * пользователем тогда создан локально и т.д.
 * Все флаги сосредоточены в одной переменной, которая принимает значения:
 * 00 0  не известен
 * 01 1  присутствует в базе
 * 10 2  используется в базе
 * 11 3  блокирован локально
 * Флаг устарел заменено ссылкой на новую запись. Значение -1 означает удаление
 * без замены
 */
package qwr.model.Base;

import qwr.util.DateTim;

import static qwr.util.BgFile.prnq;
import static qwr.util.BgFile.sepr;

public abstract class BasicElement {
    private static int  count=0;//cчетчик для элементов по умолчанию
    private static int  lastTime=0; //время последнего созданного элемента(для массового ввода)
    protected long      key;    //1) время создания в секундах с 1 января 1970г.(ключ)
    protected String    titul;  //3) наименование (*)
    protected String    descr;  //4) описание (*)
    protected short     flags;  //5) флаги/статус/состояние
    protected long      link;   //6) ссылка на новый ключ
    protected short     order;  //7) порядок следования внутри группы
    //общий конструктор--------------------------------------------------------------

    //конструктор элемента из справочника
    public BasicElement(String titul, String descr){//
        this.titul  = titul;   //наименование
        this.descr  = descr;   //описание
        this.flags  = 1;       //статус
        this.link   = 0;       //ссылка на новый ключ
        this.key    = count++; //время создания (ключ)
        this.order  = 0;        //порядок следования внутри группы
    }//ElGuid
    //конструктор для создаваемых элементов
    //код записи ((31-5)+12)+код пользователя (14+4) + код структуры (7)
    public BasicElement(int timer, String titul, int xstruc, int user){//
        assert timer <= 134217727 : "Перевышение значения timer";
        assert xstruc <= 15 : "Перевышение значения xstruc";
        assert user <= 16383 : "Перевышение значения user";
        this.key = (((long)timer & 0x7FFFFFF)<<8)|((long) xstruc & 15)<<52
                | ((long) user & 0x3FFF)<<38;
        this.titul  = titul;        //наименование
        this.descr  = null;         //описание
        this.flags  = 1;       //статус
        this.link   = 0;       //ссылка на новый ключ
        this.order  = 0;        //порядок следования внутри группы
    }//ElGuid------------------------------------------------------------------------

    public int  putTime(){return (int) (((this.key>>8) & 0x7FFFFFF));}//134217727
    public int  putUser(){return (int) ((this.key>>38) & 0x3FFF);}//16383
    public int  putxStc(){return (int) ((this.key>>52) & 0xF);}//15

    public static void clear(){ count=0; }//сброс счетчика элементов
//    public void     setChang(int chang) { this.chang = chang; }//1) идентификатор (*)
    public void     setTitul(String titul) { this.titul = titul; }//2) наименование (*)
    public void     setDescr(String descr) { this.descr = descr; }//3) описание (*)
//    public void     setTyp(int typ) { this.typ = typ; }//4тип появления
    public void     setIsusr(boolean isusr){this.flags= (short) ((isusr)?this.flags|2:this.flags&0xFFFD);}
//    public void     setSolvd(boolean solvd){this.solvd = solvd;}//разрешено использование
//    public void     setOwner(int owner){ this.owner = owner; }//пользователь
    public void     setKey(int key) { this.key = key; }//время создания
    public String   getTitul() { return titul; }//наименование
    public String   getDescr() { return descr; }//описание
//    public int getTyp() { return typ; }//способ появления элемента
    public boolean  isIsusr()  { return (flags&2)==1; }//используется в текущем проекте
//    public boolean  isSolvd()   { return solvd;  }//разрешено использование
//    public int      getChang()  { return chang;  }//идентификатор
//    public int      getOwner() { return owner; }//идентификатор пользователя создавшего элемент
    public long     getKey() { return key; }//времени создания
    public String   writ() { return sepr+//создание строки для записи в текстовый файл
            key +sepr+ link +sepr+ flags +sepr+
            order +sepr+ titul+sepr+ descr+sepr;
    }//write-------------------------------------------------------------------------
    protected boolean read(String[] words, int sizeAr){
        assert sizeAr>7:"--BaseElement size arrey < 8";
        if (words.length<=sizeAr) { //проверяю количество элементов в массиве
            assert prnq("read> Error size array ("+sizeAr+")");return true;}//аварийное завершение
        key   = Long.parseLong(words[1]);//8) время регистрации в базе(* int)(ключ)
        link =  Long.parseLong(words[2]);//7) пользователь создавший
        flags = Short.parseShort(words[3]);//6) доступно чтение(*)
        order = Short.parseShort(words[4]);//4) источник получения элемента (* short)
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
    public String print(){return key+"\t ["+flags+"] \t"+link+"\t  "+
            order+"  \t"+titul+"\t"+descr+" $";}
    public static boolean prTitl()//печать шакки
    {prnq("\tkey\t\t[typ] owner isusr solvd\t\ttitul");return true;}

}//class BasicElement
