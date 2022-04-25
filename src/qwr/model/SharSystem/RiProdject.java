/** Класс создан 280322 для формирования списка проектов.
 * Должен заменить классы EiPath и EiFile
 * Содержит код проекта {name}, наименование {titul}, описание {descr}
 * дата создания проекта {create}, дата последнего изменения конфигурации {change}
 * порядок следования в списке {order}, флаги {flag} проекта( количество синхронизируемых зеркал,
 * отношение текущего пользователя к проекту-уровень доступа),
 * путь к файлу конфигурации {fileCfg}
 * Ключем записи является код проекта, соответствующий имени файла конфигурации и дата создания
 * Значения флага: 0-не доступен файл, 1- создан, -1 -сбой при чтении файла
 * Список проектов хранится в файле INI в виде списка файлов конфигурации и их
 * последовательности. Файл INI пополняется проектами указанными в командной строке
 * в виде пути к файлам или имен самих файлов. Заполнение полей происходит при чтении
 * данных из файлов конфигурации. Если файл INI не найден, то создается по первому
 * известному пути. Если несколько файлов INI, то используется первый найденный.
 */
package qwr.model.SharSystem;


import qwr.util.DateTim;
import qwr.util.PrHelp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

//import static qwr.Inizial.InGuid.lpPath;
//import static qwr.util.BgFile.lsfpr;
import static qwr.util.CollectUtl.sepr;
import static qwr.util.CollectUtl.*;


public record RiProdject(int create, int change, int order, int flag, String name,
                         String titul, String descr, String fileCfg) {
    //----var------------------------------------------------------
    public  static          ArrayList<RiProdject> list = new ArrayList<>();//список конфигурации
    private static          String userPrj;    //имя пользователя
    private static          String jPtFlIni =null;    //путь и имя файла инициализации
    private static          String jPtFlCfg =null;    //путь и имя файла конфигурации
    private static boolean  qExFlIni =false;    //файл инициализации существует
    private static boolean  qExFlCfg =false;    //файл конфигурации существует
    private static HashSet<String> lsfpr=new HashSet<String>();//список файлов конфигурации
    private static boolean  tinst=false;    //флаг выполнения метода comstring
    public  static String   curName;        //код текущего проекта
    public  static String   curTitul;       //наименование текущего проекта
    public  static int      curCreat;       //время создания текущего проекта
    public  static String   curDescr;       //описание текущего проекта

    public static String    getCurDescr() { return curDescr; }
    public static String    getCurName() { return curName; }
    public static String    getCurTitul() { return curTitul; }
    public static int       getCurCreat() { return curCreat; }
    public static String    getjPtFlIni() { return jPtFlIni; }
    public static String    getjPtFlCfg() { return jPtFlCfg; }
    public static boolean   isqExFlIni() { return qExFlIni; }
    public static boolean   isqExFlCfg() { return qExFlCfg; }

    //----init-&-constructor--------------------------------------------------------
    static {
        assert prnq("~iniziall record RiProdject");
    }//init

    public RiProdject(int order, String fileCfg) {
        this(0, 0, order, 0, null, null, null, fileCfg);
    }
//
//    public RiProdject(int create, int order, int flag, String name, String titul, String descr, String fileCfg) {
//        this(create, 0, order, flag, name, titul, descr, fileCfg);
//    }
//
//    public RiProdject(int create, int change, int order, int flag, String name, String titul, String descr, String fileCfg) {
//        this.create = create;   //+дата создания проекта
//        this.change = change;   //+дата последнего изменения конфигурации
//        this.order = order;     //порядок следования в списке
//        this.flag = flag;       //количество синхронизируемых зеркал,
//        this.name = name;       //+код проекта
//        this.titul = titul;     //+наименование
//        this.descr = descr;     //+описание
//        this.fileCfg = fileCfg; //+путь к файлу конфигурации
//    }

    @Override
    public String toString() {//создание строки для записи в текстовый файл
        return sepr+ create +sepr+ change +sepr+ order +sepr+ flag +sepr+ name
                +sepr+ titul+sepr + fileCfg +sepr + (descr.isBlank()?"@":descr)+sepr;
    }//toString


    /**
     * Установка проекта по умолчанию на основании TITUL файла ini
     * вызывается GrRecords.TITUL.readRecord при чтении файла инициализации
     * @param words строка заголовка разобранная не лексемы
     */
    public static void setCurNTC(String[] words) {
        try {
            curName = words[1];
            curTitul = words[3];
            curCreat = Integer.parseInt(words[2]);
        }
        catch (InputMismatchException ex){ return;}
        catch (Exception ex) {System.out.println(ex.getMessage());return;}
//        assert prnq("$ RiProdject setCurNTC $");
    }//setdefault----------------------------------------------------------
    
    //Загрузка текущего проекта
     public static void loadCurProdject(){
        assert prnq("$ RiProdject loadCurProdject $");
     	//нахожу его в списке
     	RiProdject y=null;
     	for (RiProdject x : list)
            if (x.create == curCreat && x.name.equals( curName)) {
            	y=x;
            	break;//если проект найден прекращаю поиск
        }
     	if (y==null) {
     	    assert prnq("!!! такого проекта нет : "+curName);
     	    y= list.get(list.size() - 1);
     	    curName= y.name;
     	    curCreat= y.create;
     	    curTitul= y.titul;
     	    curDescr= y.descr;
//     	    return;//такого проекта нет
        }
        FileType.cfg.definePach(y.fileCfg);//запоминаю путь
         assert prnq("definePach: "+y.fileCfg);
        FileType.cfg.loadLoc();//запускаю процесс загрузки
     }//loadCurProdject-------------------------------------------------------

    public RiProdject clarify(RiProdject x){//уточнить
        return new RiProdject(1,"zz");
    }
    //-----Методы-------------------------------------------------------------------

    /** интеграция данных в коллекцию
     * вызывается из GrRecords.*.readRecord
     * @param words поток с исходными данными из массива слов строки ввода
     * @param src тип источника элемента
     * 0 - из документов и по умолчанию
     * 1 - файлы и папки локальной базы
     * 2 - создан или получен из командной строки
     * 3 - файлы и папки внешних данных не синхронизируемых (данные берутся но не проверяются)
     * 4,5,6,7. - файлы и папки внешних данных подлежащих синхронизации изменения данных
     * @return 0 без изменений, 1 переписаны поля, 2 заменяем, 3 добавлен, 4 первый,
     * -1 пропускаю элемент, -2 игнорировать по несответствию, -3 запрещенное состояние
     * RiProdject(int create, int change, int order, int flag, String name,
     *                          String titul, String descr, String fileCfg)
     */
    public static int integrate(String[] words, int src){
        if (words.length<9) {
            for (int i = 0; i < words.length; i++) prnt("+  "+i+"-"+words[i]);prnq("~"+words.length);
            return -2; //недостаточное количество элементов
        }
        RiProdject z;
        try {
            z = new RiProdject(
                    Integer.parseInt(words[1]),//int create
                    Integer.parseInt(words[2]),//int change
                    Integer.parseInt(words[3]),//int order
                    Integer.parseInt(words[4]),//int flag
                    words[5],//String name
                    words[6],//String titul
                    words[8],//String descr
                    words[7] //String fileCfg
            );
        }
        catch (InputMismatchException ex){ return -4;}
        catch (Exception ex) {System.out.println(ex.getMessage());return -3;}
        if (list.size()<1) { list.add(z); return 4;}
        switch (src) {
            case 1:
                for (RiProdject x : list) {
                    if (x.create == z.create && x.name.equals(z.name))  return -1;
                }
                list.add(z);
                return 3;
            default:return -7;
        }//switch
//        return -5;
    }//integrate

    /**
     * Добавление файла конфигураццц в общий список проектов
     * вызывается из FileType.loadScan
     * @param change время изменения в секундах
     * @param titul строка из заголовка файла конфигурации
     * @param descr описание проекта
     * @param pach путь к файлу конфигурации
     * @return истина если добавление произошли
     */
//    public static boolean integrTitul(int change, String titul, String descr, String pach) {
//        String[] words =titul.split(sepr);//создаю массив значений
//        RiProdject z;
////        assert prnq("integrTitul=> "+pach);
//        try {
//            z = new RiProdject(
//                    Integer.parseInt(words[2]),//int create
//                    change,//int change
//                    list.size()+1,//int order
//                    0,//int flag
//                    words[1],//String name
//                    words[3],//String titul
//                    descr.substring(GrRecords.lengName),//String descr
//                    pach //String fileCfg
//            );
//        }
//        catch (InputMismatchException ex){ return false;}
//        catch (Exception ex) {System.out.println(ex.getMessage());return false;}
//        if (list.size()<1) { list.add(z); return true;}
//        for (RiProdject x : list)
//            if (x.create == z.create && x.name.equals(z.name)) {
//                if (x.fileCfg==null) break;
//                assert prnq(" Duble ");
//                return false;
//            }
//        list.add(z);
//        assert prnq("add: "+z.name);
//        return true;
//    }//integrate

    /**
     * Просматриваю список файлов конфигурации полученный из командной строки
     * Считываю минимально необходимую информацию и заношу ее в список проектов
     * Если конфигурация не существует, то создаю
     * @param hComSrtRead список файлов из командной строки
     * @return
     */
    /*  3) просматриваю список из командной, сравниваю с общим списком и добавляю в общий.
        3.1) Если файл не существует и новый, то создаю и добавляю;
        3.2) Если файл существует но недоступен и новый, то добавляю и нулевыми данными (create=0);
        3.3) Если файл читается и новый, то читаю и добавляю;
        3.4) Если файл не существует и есть в списке, то ни чего не делаю;
        3.5) Если файл существует, недоступен и есть в списке, то ни чего не делаю;
        3.6) Если файл читается и есть в списке, то сравниваю данные:
        3.6.1) Если основные данные совпадают, то обновляю информацию;
        3.6.2) Если основные данные пустые в одном из файлов, то использую данные другого;
        3.6.3) Если основные данные разные, то использую более свежую информацию.
     */
    public static int addCfgFromComStrR(Stream<String> hComSrtRead) {
		assert prnq("$ addCfgFromComStrR $");
        AtomicInteger z = new AtomicInteger(0);//счетчик
//        final int xd = DateTim.newSeconds();//текущее время
        hComSrtRead.forEach( x-> {    	
    		//проверяю наличие в списке
    		int xb = x.lastIndexOf(fileSeparator);
            xb = (xb < 0) ? 0 : xb+1;
            String xname = x.substring(xb, x.lastIndexOf(FileType.cfg.name()) - 1);
            int jPresent=-1;
            for (int j = 0; j < list.size(); j++)
                if (x.equals(list.get(j).fileCfg)){
                    //if (xname.regionMatches(0,list.get(j).name,0,list.get(j).name.length())){
                    prnq("The file: "+x+" is available");
                    jPresent = j;
                    break;
            }//for
            assert prnt("Проход для "+x+" @ "+jPresent+"\t<"+xname+">\t");

//            boolean qPresent = false;//проверка на существование. Надо добавлять
//            if (list.size() > 1)
//                for (RiProdject y : list)
//                //не корректное сравнение-нужно сравнивать пути
//                if (xname.regionMatches(0,y.name,0,y.name.length())){
//                    prnq("The file: "+x+" is available");
//                    qPresent = true;
//                    break;
//                }
            //q показывает нахождение в общем списке файлов конфигурации
            int xorder = list.size() + 1;//формирование int order
            int xcreat = DateTim.newSeconds();//текущее время
			//проверяю существование такого файла            
            if (Files.notExists(Path.of(x))) {//файл не существует - создаю
                assert prnq(" File {"+x+"} Create ");
                if (jPresent<0) {//3.1) Если файл не существует и новый, то создаю и добавляю;
//                    if (!qPresent) {//3.1) Если файл не существует и новый, то создаю и добавляю;

                    if(FileType.cfg.save(x)){//истина если файл создался нормально
                    z.getAndIncrement();
                    list.add(new RiProdject(
                        xcreat,//int create
                        xcreat,//int change
                        xorder,//int order
                        1,//int flag
                        xname,//String name
                        xname,//String titul
                        "",//String descr
                        x //String fileCfg
                    )); }//if q
                }//if qPresent
                //3.4) Если файл не существует и есть в списке, то ни чего не делаю;
            }//файл не существует - создаю
            else {//файл существует
            //проверяю возможность чтения файла
	     		 if (Files.exists(Path.of(x))) {//файл читается нормально
	     		     assert prnt("read  ");
                     RiProdject y=loadCfgFileTitul(x);
                     if (y!=null) {
	     		         if (jPresent<0){//3.3) Если файл читается и новый, то читаю и добавляю;
	     		             //if (!qPresent){//3.3) Если файл читается и новый, то читаю и добавляю;
                             assert prnt("Add  ");
	     		             z.getAndIncrement();
                             list.add(y);
	     		         } else {//3.6) Если файл читается и есть в списке, то сравниваю данные:
                             assert prnt("edit  ");
	     		             RiProdject h=compare(y,list.get(jPresent));
	     		             if (h!=null) list.set(jPresent,h);//делаю замену по месту
                         }//if qPresent
                     }//no NULL прочитался нормально
     			 } else{ //файл не доступен для чтения
                    assert prnt("NO read  ");
                    if (jPresent<0) { //3.2) Если файл существует но недоступен и новый, то добавляю и нулевыми данными (create=0);
                        //if (!qPresent) { //3.2) Если файл существует но недоступен и новый, то добавляю и нулевыми данными (create=0);
                        list.add(new RiProdject(
                            0,//int create
                            xcreat,//int change
                            xorder,//int order
                            0,//int flag
                            xname,//String name
                            xname,//String titul
                            "",//String descr
                            x //String fileCfg
                        ));
                        z.getAndIncrement();
                    }//if qPresent
                    //3.5) Если файл существует, недоступен и есть в списке, то ни чего не делаю;
                 }// else файл не доступен для чтения
            }//else файл существует
            assert prnq(" @@@");
        });//for
        return z.get();    	
    }//addCfgFromComStrR====================================
    /**
     * Чтение заголовков файлов конфигурации
     * @param pach путь и имя файла
     * @return объект проекта либо NULL
     */
    static String tmpDescr=null;
    static String tmpTitul=null;
    private static RiProdject loadCfgFileTitul(String pach){
        assert pach != null : "! RiProdject.loadCfgFileTitul: pach = null !";
        Path path = Paths.get(pach);
        try {
            if (Files.size(path) < 13) {//проверяю длинну файла
                prnq("ERROR file: " + path + " is litl < 13");
                return null;
            }
        } catch (IOException e) { e.printStackTrace(); return null; }
        long change = 0;
        tmpDescr = null;
        tmpTitul = null;
        try {
            change = (Files.getLastModifiedTime(path).toMillis()) / 1000 - DateTim.begSecund;
            Stream.of(Files.readAllLines(path, StandardCharsets.UTF_8)).forEach(readsrt -> {
                for (String str : readsrt) {//читаю последовательно строки файла
                    if (str.length() < 5) {
                        assert prnq("Error length");
                        continue;
                    }
                    if (str.startsWith(GrRecords.ENDFL.name()) ||
                            (tmpDescr != null && tmpTitul != null)) { break; }
                    if (str.startsWith(GrRecords.DESCR.name())) tmpDescr = str;
                    else if (str.startsWith(GrRecords.TITUL.name())) tmpTitul = str;
                }//for readsrt
            });
        } catch (IOException e) { e.printStackTrace(); return null;}
        String[] words =tmpTitul.split(sepr);//создаю массив значений
        RiProdject z;
        try {
            z = new RiProdject(
                    Integer.parseInt(words[2]),//int create
                    (int) change,//int change
                    list.size()+1,//int order
                    0,//int flag
                    words[1],//String name
                    words[3],//String titul
                    tmpDescr.substring(GrRecords.lengName),//String descr
                    pach //String fileCfg
            );
        }
        catch (InputMismatchException ex){ return null;}
        catch (Exception ex) {System.out.println(ex.getMessage());return null;}
        return z;
    }//loadCfgFileTitul

    /**
     * Сопоставление элементов и получение на их основе нужного
     * i create, i change, i order, i flag, S name, S titul, S descr, S fileCfg
     * ключем является связка (i) create & (S) name
     * @param x элемент полученный вновь
     * @param y элемент существующий в общем списке
     * @return результирующий элемент
     */
    private static RiProdject compare(RiProdject x, RiProdject y){
        if (x.create != y.create | !(x.name.equals(y.name))) return null;
        int hx = x.fileCfg.length();
        int hy = y.fileCfg.length();
        int jx = x.fileCfg.lastIndexOf(fileSeparator);
        int jy = y.fileCfg.lastIndexOf(fileSeparator);
        jx = (jx < 0) ? 0 : jx;
        jy = (jy < 0) ? 0 : jy;
        if (x.fileCfg.regionMatches(jx,y.fileCfg,jy,hx-jx-3)){

        }
        return null;
    }//compare

    /**
     *
      * @param hComSrtRead
     * @return
     */
//    public static int addCfgFromComStr(Stream<String> hComSrtRead) {
//        assert prnq("$ RiProdject testGfgFile $");
//        AtomicInteger z = new AtomicInteger(0);//счетчик
//        hComSrtRead.forEach( x-> {
////            prnq(" analiz: " + x);
//            //анализирую ситуацию: файл не существует, файл не доступен,
//            //файл не соответствует по структуре, файл прочитался нормально
//            if (Files.exists(Path.of(x))) {//файл читается нормально
////                prnt("@@@- \t");
////                FileType.testFile(x);
//                int q =FileType.loadScan(x);
//                if (q>0) z.getAndIncrement();
//                assert prnq("=> "+q);
//            } else {//файл на момент анализа не доступен
//                int xd = DateTim.newSeconds();//текущее время
//                int xb = x.lastIndexOf(fileSeparator);
//                xb = (xb < 0) ? 0 : xb;
//                String xs = x.substring(xb, x.lastIndexOf(FileType.cfg.name()) - 1);
//                boolean q = true;//проверка на существование, совпадение
//                int xi = list.size() + 1;//формирование int order
//                if (list.size() < 1)
//                    for (RiProdject y : list)
//                        if (y.create == xd && xs.equals(y.name)) {
//                            q = false;
//                            break;
//                        }
//                if (Files.notExists(Path.of(x))) {//файл не существует - создаю
//                    assert prnq(" File {"+x+"} Create ");
////                    if (q) {
//                        list.add(new RiProdject(
//                                xd,//int create
//                                xd,//int change
//                                xi,//int order
//                                1,//int flag
//                                xs,//String name
//                                xs,//String titul
//                                "",//String descr
//                                x //String fileCfg
//                        ));
//                        z.getAndIncrement();
////                    }
//                    //создвю новый файл
////                    FileType.cfg.definePach(x);
//                    FileType.cfg.save(x);
//                }//файл не существует - создаю
//                else //файл не доступен для чтения
//                    if (q) {
//                        list.add(new RiProdject(
//                                xd,//int create
//                                xd,//int change
//                                xi,//int order
//                                0,//int flag
//                                xs,//String name
//                                xs,//String titul
//                                "",//String descr
//                                x //String fileCfg
//                        ));
//                        z.getAndIncrement();
//                    }
//            }//файл на момент анализа не доступен
//        });//for
//        return z.get();
//    }//addCfgFromComStr--------------------------------------------------------------------------------

    /**
     * ---- разбор списка значений командной строки под задачу проекта --------------
     * @param args командная строка
     */
    public static Stream<String> parsingComandString(String[] args){
        assert prnq("$ record RiProdject parsingComandString $");
        tinst=true;//флаг прохождения инициализации
        String pfPrj=System.getProperty("user.dir");//текущая системная директория
        if (args.length!=0) { //аргументы есть
            for(int i=0; i < args.length; i++) { //просмотр строки аргументов
                String s = args[i];
                assert prnq(i+"~"+s);
                if(s.startsWith("-v")||s.startsWith("-V")){
                    PrHelp.version(); return null;}
                if(s.startsWith("-h")||s.startsWith("-H")){
                    PrHelp.helplst(); return null; }//if
                if(s.startsWith("-u")||s.startsWith("-U")){ // задаю пользователя
                    int d=s.indexOf(':')<0?s.indexOf('='):s.indexOf(':');
                    if(d>=0) {
                        String y = s.substring(d + 1);
                        if (y.length()>0){
                            assert prnq("new user: "+y);
                            userPrj=y;
                        }
                        else System.out.println("No detect user name ");
                    }//if d>=0
                    else System.out.println("Not operators ':' or '=' new user");
                    continue; //переход к следующему аргументу командной строки
                }//if -u
                Path path=Path.of(s);
                if (Files.exists(path)){//элемент существует
                    if (Files.isRegularFile(path)) detectFiles(path);
                    else {//это директория
                        pfPrj= String.valueOf(path.toAbsolutePath());
                        try (DirectoryStream<Path> files=Files.newDirectoryStream(path)){
                            for (Path entry : files) {
                                if (Files.isRegularFile(entry)) detectFiles(entry);
                            }//for stream
                        } catch (IOException x) { System.err.println(">"+x);}//убрал  continue;
                    }//if isRegularFile
                } else {
                    assert prnq("Элемент { "+String.valueOf(path.toAbsolutePath())
                            +" } не существует, добавляю в список");
                    detectFiles(path);
                }
            }//for args
        }// наличие аргументов
        else {
            prnq("Аргументы командной строки отсутствуют");
            //проверяю состояние текущей системной директории
            try (DirectoryStream<Path> files=Files.newDirectoryStream(Path.of(pfPrj))){
                for (Path entry : files)
                    if (Files.isRegularFile(entry)) detectFiles(entry);
            } catch (IOException x) { System.err.println(">"+x);}//убрал  continue;
        }// наличие аргументов
        //проверяю наличие файла инициализации
        if (jPtFlIni ==null || jPtFlCfg==null ) {//файл не существует
            //проверяю корневую директорию на наличие файла инициализации
            try (DirectoryStream<Path> files=Files.newDirectoryStream(
                    Paths.get(System.getProperty("user.dir")))){
                for (Path entry : files)
                    if (Files.isRegularFile(entry)) detectFiles(entry);
            } catch (IOException x) { System.err.println(">"+x);}//убрал  continue;
        }//if exists
        if (jPtFlIni ==null )  jPtFlIni =pfPrj.concat(fileSeparator).concat("listprj.").concat(FileType.ini.name());
        if (jPtFlCfg ==null ) {//файл не существует
            jPtFlCfg =pfPrj.concat(fileSeparator).concat("firstprj.").concat(FileType.cfg.name());//устанавливаю текущий проект
            lsfpr.add(jPtFlCfg);
        }
//    prnq("\n Количество найденных конфигураций: "+lsfpr.size()+" в том числе:" );
        //печатаю список подгруженных проектов на старте программы
//    for (String s: lsfpr ){ System.out.println(": "+s ); }
        //возвращаю поток имен файлов конфигурации
        return lsfpr.stream().distinct();
        //возвращаю поток имен файлов конфигурации и в конце инициализации
//        return Stream.concat(lsfpr.stream(), Stream.of(jPtFlIni ==null?"@": jPtFlIni)).distinct();
    }//parsingComandString
//вспомогательные функции

    /**
     * Добавление файла в список файлов конфигурации или в переменную имени файла инициализации
     * и определение начального файла конфигурации из списка
     * @param path имя и путь к проверяемому файлу
     * @return результат проверки: истина если это не тот файл и ложь если файл обработан
     */
    private static boolean detectFiles(Path path){
        if (String.valueOf(path).endsWith(FileType.ini.name())){
            if (!qExFlIni){//файл не существует
                jPtFlIni = path.toString();//определяю рабочий файл инициализации
                if(Files.exists(path)) qExFlIni=true;
            }
            return false;
        } else if (!String.valueOf(path).endsWith(FileType.cfg.name())) return true;//не тот тип
        lsfpr.add(String.valueOf(path.toAbsolutePath()));//добавляю в список файлов локальных проекта
        //lsfpr.add(String.valueOf(path));//добавляю в список файлов локальных проекта
        if (!qExFlCfg) {//файл не существует
            jPtFlCfg=path.toAbsolutePath().toString();//устанавливаю текущий проект
            if(Files.exists(path)) qExFlCfg=true;
        }
        return false;
    }//detectFiles
//@Deprecated
////функционал перенесен в enum FileType.loadLoc()
//    public static boolean loadIni(String path){
//        assert path!=null: "! ext:INI> load: pach = null !";
//        try(BufferedReader br=new BufferedReader(new FileReader(path)))
//        {
//            if (Files.size(Path.of(path))< FileType.ini.size()) {//проверяю длину файла
//                prnq("ERROR file is litl ");return false;}
//            String str;
//            while ((str = br.readLine()) != null) { //читаю файл пока не закончится
//                if (str.length()<5) {assert prnq("Error length");continue;}
//                String[] words =str.split(CollectUtl.sepr);//создаю массив значений
//                try { GrRecords.valueOf(words[0]);//если не существует значение
//                }catch(IllegalArgumentException e){continue;}
//                if(GrRecords.valueOf(words[0]).readRecord(words,1)) break;
//                ///-------------------------------------------------------------------
////                lsfpr.add(str);//дописываю проекты из файла
//            }//while
//
//        } catch (IOException ex){System.out.println(ex.getMessage());return false;}
//        assert BgFile.prnq("файл инициализации загружен >"+path);
//        return true;
//    }//loadIni

//    public String writ() {//создание строки для записи в текстовый файл
//        return sepr+ create +sepr+ change +sepr+ order +sepr+ flag +sepr+ name
//                +sepr+ titul +sepr+ descr +sepr + fileCfg +sepr;
//    }//write-------------------------------------------------------------------------

    public static void printDef(){
        prnq("RiProdject.printDef = текущий проект установлен:");
        prnq("name: "+curName+" \tcreate: "+curCreat+" \ttitul: "+curTitul);
        prnq("descript: "+curDescr+"\n--------");
    }

    public static void printList(String str){
        prnq("Список проектов из :"+list.size()+"   "+str);
        prnq("[order\tname\tflag\ttitul\tcreate\tfileCfg\tdescr]");
        for (RiProdject x:list) prnq(x.order+"   \t"+x.name+" \t["+x.flag+"] \t"+
                x.titul+" \t"+x.create+ " \t-"+x.fileCfg+"- \t{"+x.descr+"}");
        prnq("___________");
    }//printList

}//RiProdject
