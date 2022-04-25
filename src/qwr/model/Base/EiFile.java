/**
 * Элемент внешнего файла для подкачки и синхронизации элементов -------------------
 *
 * Заначения  typ для EiFile и EiPath
 * 0 - инициализация элемента до присвоения значений в том числе из командной строки
 * 1 - файлы и папки локальной базы
 * 2 - файлы и папки внешних данных не синхронизуемых (данные берутся но не проверяются)
 * 3 - файлы и папки внешних данных подлежащих синхронизации изменения данных
 */
package qwr.model.Base;

import java.io.File;

public class EiFile extends BaseElement {
    public static final int sizeAr=8;//количество полей в текстовом файле данных
    //идентификатор chang;   //1) время последнего изменения внешнего справочника (* int)
    // наименование titul;  //2) путь и имя внешнего справочника (* String)
    // описание     descr;  //3) наименование часового пояса внешнего справочника (* String)
    // способ       typ;    //4) источник получения элемента (* short) наследуется от пути
    // используется isusr;  //5) доступность пут!
    // разрешено    solv;   //6) разрешение(насл)
    // создавшего   owner;  //7) ключ пути(наслд)
    // время        key;    //8) время регистрации в базе(* int)(ключ)
    //-- общий конструктор ------------------------------------------------------
    public EiFile(String titul, int typ){super(titul,typ);}
    //-- частный конструктор ----------------------------------------------------
    public EiFile(String str, EiPath eiPath) {
        super(str,eiPath.descr,eiPath.typ,false,false,0); }
    //---копирование-----------------------------------------------------------------
    public EiFile(EiFile obj) {//копирование элемента
        super(obj.titul, obj.descr, obj.typ, obj.isusr, obj.solvd, obj.chang);
        this.owner = obj.owner;//подменяю значения пользователь создавший
        this.key = obj.key;//подменяю значения время создания
    }//EiPath
    @Override//----------------------------------------------------------------------
    public String writ() { return super.writ(); }//write

    public boolean read(String[] words) {return super.read(words,sizeAr); }//read
    //методы сравнения элементов
    public boolean equals(EiPath obj) { return super.equals(obj); }//equals
    //-------------------------------------------------------------------------------
    public boolean equals(EiPath eiPath, File item) {//false=разрешаю добавление элемента
//        assert prnq("~~~"+this.titul+" ? "+item.getAbsolutePath());
        return this.titul.equals(item.getAbsolutePath()); }

    //слияние объектов добавлением к существующему внешнего
    public boolean merger(EiFile obj) {
        if (!this.getTitul().equals(obj.getTitul())) return false;//идем дальше
        this.descr=obj.descr;
        this.typ=obj.typ;
        this.isusr= obj.isusr;
        this.solvd=obj.solvd;
        this.owner=obj.owner;
        this.key=obj.key;
        return true; }//merger
}//class EiFile
