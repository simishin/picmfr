/** описание элемента пути к внешним справочникам построены на базовом классе, где
 *  время последнего изменения, путь, часовой пояс, источник элемента:
 *
 * Значения  typ для EiFile и EiPath
 * 0 - инициализация элемента до присвоения значений в том числе из командной строки
 * 1 - файлы и папки локальной базы
 * 2 - файлы и папки внешних данных не синхронизируемых (данные берутся но не проверяются)
 * 3,4,... - файлы и папки внешних данных подлежащих синхронизации изменения данных
 */
package qwr.model.Base;

import static qwr.util.BgFile.prnq;

public class EiPath extends BaseElement {
    public static final int sizeAr=8;//количество полей в текстовом файле данных
    //идентификатор chang;  //1) время последнего изменения внешнего справочника (* int)
    // наименование titul;  //2) путь к внешнему справочнику(* String)
    // описание     descr;  //3) наименование часового пояса внешнего справочника (* String)
    // способ       typ;    //4) источник получения элемента (* short)
    // используется isusr;  //5) доступность внешнего источника на данный момент (*)
    // разрешено    solv;   //6) разрешено использование (*)- возможно отключение
    // создавшего   owner;  //7) пользователь создавший (* int)
    // время        key;    //8) время регистрации в базе(* int)(ключ)

    //-- общий конструктор ----------------------------------------------------------
    public EiPath(String titul){super(titul,0);}
    public EiPath(String titul, int typ) { super(titul, typ);}
    //---копирование-----------------------------------------------------------------
    public EiPath(EiPath obj) {//копирование элемента
        super(obj.titul, obj.descr, obj.typ, obj.isusr, obj.solvd, obj.chang);
        this.owner = obj.owner;//подменяю значения пользователь создавший
        this.key = obj.key;//подменяю значения время создания
    }//EiPath
    @Override//----------------------------------------------------------------------
    public String writ() { return super.writ(); }//write

    public boolean read(String[] words) {return super.read(words,sizeAr); }//read
    //методы сравнения элементов
    public boolean equals(EiPath obj) { return
            (this.titul.equals(obj.titul)) ;             //8) дата регистрации в базе(* long)
    }//equals
    //слияние объектов бобавлением к существующему внешнего
    public boolean merger(EiPath obj) {//используется в FileGroupRecords > readExst
        if (!this.getTitul().equals(obj.getTitul())) return false;//идем дальше
        //совпал логин - забираю данные из внешнего источника
//        assert prnq("@@@ ["+this.getTyp()+"] "+this.getTitul());
        switch (this.typ & 3){
            case 0:
            case 1: //создан системой
            case 2://создан пользователем
            case 3://получен из внешнего источника
                //проверяю на совпадение
                    this.descr=obj.descr;
                    this.typ=obj.typ;
                    this.isusr= obj.isusr;
                    this.solvd=obj.solvd;
                    this.owner=obj.owner;
                    this.key=obj.key;
                break;
            default: prnq("*** Metod merger EiPath is not specify ***");return true;
        }//switch
        return true;
    }//merger
}//EiPath
