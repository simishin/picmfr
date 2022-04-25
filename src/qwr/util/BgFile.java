/************************************************************************************=
 * Каласс работы с файламию: Анализ комендной строки и работа с файлом инициализации
 * сохранение результата в файлах XLSX, установка файла загрузки по умолчанию и т.д.
 */
package qwr.util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import qwr.model.Base.BaseElement;
import qwr.model.Base.EiFile;
import qwr.model.Base.EiPath;
import qwr.model.Base.EiUser;
import qwr.model.SharSystem.FileType;
import qwr.model.SharSystem.GrRecords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.join;
import static qwr.Inizial.InGuid.*;

public class BgFile {
    //определяю разделитель элементов пути определенных операционной системой
    public  static final String fileSeparator=System.getProperty("file.separator");
    // fileSeparator = "/" or "\"------------------------------------------------
    private static       String cmpName;    //имя компьютера
    private static       String userPrj;    //имя пользователя
    private static       String namePrj;    //имя проекта (для формирования имен)
    private static      boolean readFile;   //флаг процесса чтения файла
    private static       int    userId;     //идентификатор пользователя по времени
    private static          int changLoadFile;//дата последней модификации файла

    public  static       XSSFWorkbook wbookPublic;    //общая электронная таблица
    //-------------
    public  static final String sepr="\t";  //разделитель значений в файлах данных
    public  static String   flin  ="2021-01.xlsx";    //имя файла результата
    public  static String   flout;                    //имя файла результата
    private static final    String mask ="tempPlan";
    public  static boolean  prnq(String s){System.out.println(s); return true;}
    public  static boolean  prnt(String s){System.out.print(s); return true;}
    private static boolean  tinst=false;    //флаг выполнения метода comstring
    private static boolean  decompositionError=false; //ошибка при разложенни строк
//-----------------------------------------------------------------------------------
    public  static HashSet<String> lsfpr=new HashSet<String>();//спиок файлов конфигурации
    public  static boolean isReadFile() { return readFile; }
    public  static void    setReadFile(boolean readFile, int ch)
        { BgFile.readFile = readFile; if(ch>2000000) BgFile.changLoadFile=ch;}
    public static int getChangLoadFile() { return changLoadFile; }
    //----------------------------------------------------------------------------------
    public  static String  getNowData(){return DateTim.currentG();}//текущая дата
    public  static int     getUserIdPrj(){return userId;}//идентификатор пользователя
    public  static String  getUserPrj(){return userPrj;}    //имя пользователя
    public  static void    setUserPrj(String s){userPrj=s;} //имя пользователя
    public  static String  getCmpName(){return cmpName;}    //имя компьютера
    public  static void    setDecompositionError(){decompositionError=true;}
    public  static void    resetDecompositionError(){decompositionError=false;}
    public  static boolean isNoDecompositionError(){return !decompositionError;}
    public  static int     getLengthSuffix() { return lengthSuffix; }//длина суфикса
    public  static String  getGlob() { return glob; }//маска фильтрации типов файлов в каталоге

    //-------------------------------------------------------------------------------
    private static final int     lengthSuffix;//длина суфикса определяющего тип файла
    private static String  glob="*{xxx";//маска фильтрации типов файлов в каталоге
    static {   //начало блока инициализатора ****************************************
        //определение расчетных констант на основе структуры программного кода
        assert prnq("$ class BgFile static block initialization 63 $");
        lengthSuffix=FileType.ini.getX().length();  //длина суфикса типа файла
        //-  формирование маски для просмотра папок на наличие нужных файлов
        for (FileType typFile : FileType.values()) {//просматриваю все типы файлов
            if (typFile.ordinal() < 3 ) continue;   //пропускаю INI-файл
            glob=join(",",glob,typFile.getX());
        }//for FileType
        glob=glob.concat("}");//фильтр для просмотра папок сформирован
    }//конец блока инициализатора ===================================================
/**
* --- Предопределенное имя файла исходных данных -----------------------------------
* @param s - новое имя файла
*/
public static void setDefFileInput(String s){
    assert s!=null:"Error name input file not default";
    if (tinst) {
        prnq(" Установка имени файла исходных данных прошла ранее"
        +"Данный метод запоздал. Выполнение програмы завершено.");
        System.exit(9);
    }
    assert s.length()>5:"Error name input file length < 5 chars";
    flin=s;
    assert prnq("$ class BgFile setDefFileInput 83 $\n" +
            "Предопределенное имя файла исходных данных изменено на \n"+s);
}//setDefFileInput
/**
* --- определение предустановок ОС компьютера для текущей задачи -------------------
* @return
*/
public static String defineComputerUserSeting(){
    //определяю параметр компиляции
    assert prnq("$ class BgFile defineComputerUserSeting 92 $");
    boolean tstAssert = false;
    assert tstAssert = true;
    if (!tstAssert){ System.out.println("!!!  Assertions are disabled. set (-ea)");}
    //анализирую параметры операционной системы
    java.net.InetAddress localMachine = null;
    try { localMachine = java.net.InetAddress.getLocalHost();
    } catch (UnknownHostException e) { e.printStackTrace(); }
    cmpName=localMachine.getHostName();
    //текущее время
    long datcr  = Instant.now().getEpochSecond();//cекунды прошедшие с 1.01.1970г
    prnq("Количество секунд :"+datcr+" сегодня "+ LocalDateTime.now());

    String pfPrj=System.getProperty("user.dir");//текущая директория
    return pfPrj;
}//defineComputerUserSeting
/**
* --- разбор списка значений командной строки ------------------------------------
* @param args - командная строка
* @return всегда истоно
*/
public static boolean parsingComandStringXlsx(String[] args){//разбор командной строки
    assert prnq("$ class BgFile parsingComandStringXlsx 115 $");
    tinst=true;//флаг прохождения инициализации
    boolean tstAssert = false;
    assert tstAssert = true;
    if (tstAssert) { System.out.println("!!!       Assertions are enabled");
    } else { System.out.println("!!!     Assertions are disabled. set (-ea)"); }
    //--------
    PrHelp.prntitl();//печать титула программы
    if (args.length!=0) { //аргументы есть
        for (String s:args){
            assert prnq("~"+s);
            if(s.startsWith("-h")||s.startsWith("-H")||args.length>1){
                prnq("Один аргумент - имя исходного файла XLSX");
                System.exit(0);
            }//if

            if (!s.endsWith(".xlsx")) continue; //это не файл  XLSX.
            File item = new File(s);//существует внутри цикла
            if (item.exists()){ flin = s; break;}//файл существует конец просмотра арг
        }//for args
    }
    //проверяю существование файла
    File item = new File(flin);//существует внутри цикла
    if (!item.isFile()) {
        prnq("Файл с именем "+flin+" не найден. Выполнение программы завершено.");
        System.exit(1);
    }//if
    //создаю имя файла результата
    flout= flin.replaceFirst(".x",mask.concat(".x"));//резуль2021-01tempPlan.xlsx
    return true;
}//parsingComandStringXlsx
/**
* --- сохранение результата работы в файле ------------------------------------
* @param wbook
*/
public static void savefl(XSSFWorkbook wbook){//сохранение результата работы
    prnq("\nСохранение результата работы в файле "+ BgFile.flout);
    try  {
        FileOutputStream out = new FileOutputStream(new File(BgFile.flout));
        wbook.write(out);
        out.close();
        System.out.println( "Результат сохранен");
    } catch (Exception e){ e.printStackTrace(); }
}//savefl
//----------------------------------------------------------------------------------
public static void savefl(XSSFWorkbook wbook, String fnout){//сохранение результата
    prnq("\nСохранение результата работы в файле "+ fnout);
    try  {
        FileOutputStream out = new FileOutputStream(new File(fnout));
        wbook.write(out);
        out.close();
        System.out.println( "Результат сохранен");
    } catch (Exception e){ e.printStackTrace(); }
}//savefl

//-------------------------------------------------------------------------------
/**
* ---- разбор списка значений командной строки под задачу проекта --------------
* @param args командная строка
*/
/*public static void parsingComandString(String[] args){
    assert prnq("$242 class BgFile parsingComandString $");
    tinst=true;//флаг прохождения инициализации
    String pfPrj=System.getProperty("user.dir");//текущая системная директория
    if (args.length!=0) { //аргументы есть
//        assert prnq(args.length+" аргументов в командной строке");
        for(int i=0; i < args.length; i++) { //просмотр строки аргументов
            String nf=null;
            String s = args[i];
//            assert prnq(i+"~"+s);
            if(s.startsWith("-v")||s.startsWith("-V")){
                PrHelp.version(); return;}
            if(s.startsWith("-h")||s.startsWith("-H")){
                PrHelp.helplst(); return; }//if
            if(s.startsWith("-u")||s.startsWith("-U")){ // задаю пользоваткеля
                int d=s.indexOf(':')<0?s.indexOf('='):s.indexOf(':');
                if(d>=0) {
                    String y = s.substring(d + 1);
                    if (y.length()>0){
                        assert prnq("new user: "+y);
                        userPrj=y;
                        GrRecords.USERI.create(new EiUser(y));//**************
                    }
                    else System.out.println("No detect user name ");
                }//if d>=0
                else System.out.println("Not operators ':' or '=' new user");
                continue; //переход к следующему аргументу командной строки
            }//if -u
            Path path=Path.of(s);
            if (Files.exists(path)){//элемент существует
                if (Files.isRegularFile(path)){//это файл
//                   assert prnq("395");
                    FileType.ini.include(path,lpPath.get(0));
                    FileType.cfg.include(path,lpPath.get(0));
                }else {//это директория
                    pfPrj= String.valueOf(path.toAbsolutePath());
                    try (DirectoryStream<Path> files=Files.newDirectoryStream(path)){
                        for (Path entry : files) {
                            FileType.ini.include(entry,lpPath.get(0));
                            FileType.cfg.include(entry,lpPath.get(0));
                        }//for stream
                    } catch (IOException x) { System.err.println(">"+x);}//убрал  continue;
                }//if isRegularFile
            }//if exists
        }//for args
    }// наличие аргументов
    else {
        prnq("Аргументы командной строки отсутствуют");
        try (DirectoryStream<Path> files=Files.newDirectoryStream(Path.of(pfPrj))){
            for (Path entry : files) {
                FileType.ini.include(entry,lpPath.get(0));
                FileType.cfg.include(entry,lpPath.get(0));
            }//for stream
        } catch (IOException x) { System.err.println(">"+x);}//убрал  continue;
    }// наличие аргументов
    //проверяю конфигурацию
    if (FileType.cfg.definePach(pfPrj)){//файл не существует
        FileType.cfg.save();}//создаю файл конфигурации
    //проверяю наличие файла инициализации
    if (FileType.ini.getPach()==null) {//файл не существует
        //проверяю корневую директорию
        try (DirectoryStream<Path> files=Files.newDirectoryStream(
                Paths.get(System.getProperty("user.dir")))){
            for (Path entry : files) {
                FileType.ini.include(entry,lpPath.get(0));
            }//for stream
        } catch (IOException x) { System.err.println(">"+x);}//убрал  continue;
        if (FileType.ini.definePach(pfPrj)){//файл не существует
            FileType.ini.save();}//создаю файл конфигурации
    }//if exists
//    assert prnq("------------------------------------------------------");
//    prnq("путь(pfPrj):"+pfPrj);
//    prnq("Файл инициализации  (pnLst):"+FileType.ini.getPach());
//    prnq("Теущая конфигурация (pnPrj):"+FileType.cfg.getPach());
//    prnq("\n Количество найденых конфигураций: "+lsfpr.size()+" в том числе:" );
    //печатаю список подгруженных проектов на старте программы
//    for (String s: lsfpr ){ System.out.println(": "+s ); }
//    assert prnq("------------------------------------------------------");
    FileType.ini.load("");//загружаю из файла список проектов
    //тестирую спикок проектов на актуальность
    HashSet<String> lsDel = new HashSet<String>();
    for (String s: lsfpr ){//проверяю список конфигураций на существование
        if (! Files.isReadable(Path.of(s))) lsDel.add(s);//список под удаление
    } //if tst for
    lsfpr.removeAll(lsDel);//удаляю елементы коллекции из текущей коллекции
    for (String s: lsDel ) System.out.println("   File is NOT read: "+s );
    FileType.ini.backUp();//далаю резервную копию
    FileType.ini.save();//сохраняю в файле списка проектов
    boolean first=true;
//    for (String s: lsfpr ) prnq("   File is Actual: "+s );
    assert prnq("(BgFile 331) Tекущий проект "+
            FileType.cfg.getPach()+"\n"+ "Tекущий пользователь (userPrj) "+userPrj+
            "\n===");
    return;
}//parsingComandString
public static void steepPrtocol(int typ){//отработка протокола изменений

}

 */

/**
 * Загрузка локальной конфигурации проекта с пределением находжения локальных
 * файлов данных, определение локальных путей. Если в файле кнфигурации
 * отсутствует локальный путь(=1), то берется путь из текущего каталога
 */
public static void loadConfiguration() {
    assert prnq("$ BgFile > loadConfiguration $");
    if (lsFile.size()<1){
        prnq("ERROR нет подгруженных файлов в <lsFile>");
        return;
    }
    EiFile ilFile=lsFile.get(0);
    prnq("Читаю конфигурацию :"+FileType.cfg.getPach()+"\t("+ilFile.getTitul()+")");
    Path path = Paths.get(FileType.cfg.getPach());
    if (!Files.isReadable(path)) {
        prnq("ERROR файл конфигурации не читается!");
        return;
    }//проврка читаемости
    ilFile.setSolvd(true);//6) доступно чтение/ разрешено использовать
    try { if (Files.size(path)< FileType.cfg.size()) {//проверяю длинну файла
        prnq("ERROR file is litl ");return;}
    } catch (IOException e) { e.printStackTrace(); }
    try{ List<String> readsrt= Files.readAllLines(path, StandardCharsets.UTF_8);
        prnt("\tLoad string "+readsrt.size()+"\t");
        for (String str: readsrt){//читаю последовательно строки файла
            if (str.length()<5) {assert prnq("Error length");continue;}
            String[] words =str.split(sepr);//создаю масив значений
            try { GrRecords.valueOf(words[0]);//если не существует значение
            }catch(IllegalArgumentException e){continue;}
            if(GrRecords.valueOf(words[0]).readRecord(words,1)) break;
        }//for readsrt
    }catch(IOException e){e.printStackTrace();}//файл конфигурации прочитан
    //определяю имя проекта на основе имени файла конфигурации без учета пути
    int d=ilFile.getTitul().lastIndexOf(fileSeparator);
    namePrj=ilFile.getTitul().substring(d+1,ilFile.getTitul().length()-lengthSuffix);
    prnq("Имя проекта {"+namePrj+"}");
    //проверяю содержимое полученных из конфигурации путей
    boolean tl=true;
    for (EiPath iPath:lpPath) {
        if (iPath.getTyp() == 1){ //проверяю на локальные
            //проверяю на существование
            Path pth = Path.of(iPath.getTitul());
            if(Files.exists(pth)) tl = false;
            else {  iPath.setSolvd(false);//разрешение
                    iPath.setIsusr(false);}//доступность пут
        }//проверяю на локальные
    }//for lpPath
    if (tl){ //добавляю локальный путь если других локальных путей нет
        assert prnq("Добавляю путь "+ilFile.getTitul().substring(0,d));
        lpPath.add(new EiPath(ilFile.getTitul().substring(0,d),1));
    }//добавляю локальный путь
    //сканирую локальные пути в поисках локальных файлов проекта
    for (EiPath iPath:lpPath) {
        if (iPath.getTyp()!= 1) continue;//обхожу все кроме локальных
        if ( ! iPath.isSolvd()) continue;//обхожу не существующие
//        assert prnq("path local>\t"+iPath.getTitul());
        Path pth = Path.of(iPath.getTitul());
        //собираю локальные файлы проекта по указанному пути в lsFile
        if (FileType.lsf.include(pth,iPath)) {iPath.setIsusr(false); continue;}
    }//for lpPath
    for (EiFile iFile: lsFile){ //просмаьриваю сформированный ранее список файлов
        if (iFile.getTyp()!= 1) continue;//обхожу все кроме локальных
        Path pth = Paths.get(iFile.getTitul());
        if (pth.getFileName().toString().startsWith(namePrj)){//файл проекта
            iFile.setSolvd(true);//разрешение - файл проекта
            if (Files.isReadable(pth)) iFile.setIsusr(true);//файл читается
            else continue;//файл не читается - пропускаю
            //записываю в переменную пути для данного типа файла
            if (FileType.valueOf(pth.toString().substring(pth.toString().length()-
                    lengthSuffix+1)).getPach()!=null){
                prnq("ERROR данный тип файл уже найден! ("+pth+")");
                continue;
            }
            FileType.valueOf(pth.toString().substring(pth.toString().length()-
                    lengthSuffix+1)).definePach(pth.toString());
        }//файл проекта
//        assert prnq("file local>>\t"+iFile.writ());
    }//for lsFile
    //просматриваю перечень всех путей к локальным файлам в поисках пропущеных
    for (FileType typFile : FileType.values()) {//просматриваю все типы файлов
        if (typFile.ordinal() < 3 ) continue;   //пропускаю INI-файл
//        assert prnq("?\t"+typFile.name()+">"+typFile.getPach());
        if(typFile.getPach()==null) //если задан то пропускаю
        //создаю путь на основе текущего пути файла конфигурации
        typFile.setPach(FileType.cfg.getPach().substring(0,FileType.
                cfg.getPach().length() -lengthSuffix).concat(typFile.getX()));
//        assert prnq("? "+typFile.name()+"\t"+typFile.getPach());
    }//for FileType
    assert prnq("$-- loadConfiguration");
}//loadConfiguration -----------------------------------------------------------------

    /**
     *  Сохранение локальных данных в файлах проекта указанных в переменной пути
     * @param isSave истина если запись должна быть повторной и обязательной
     * @return истина, если не все данные записались и нужно повторить запись
     */
    public static boolean saveLocalDataSources(boolean isSave) {
        assert prnq("$ BgFile > saveLocalDataSources $");
//    boolean isSave=true; //проверка на необходимость записи, если были изменения
        //просматриваю поля и если они изменились делаю соответствующую отметку в файлах
//    assert prnq("\t\t\tname()\tgetIadd()\tgetTypfl()\tsizeArray()");
        for (GrRecords recrd: GrRecords.values()){
//        assert prnq("$ saveLocalDataSources> "+recrd.name()+" \t"+recrd.getIadd()
//                +"\t"+recrd.getTypfl()+"\t"+recrd.sizeArray());
            if (recrd.getIadd()!=0) {//счетчик не нулевой
                FileType.valueOf(recrd.getTypfl().toString()).setModif();
                recrd.clrIadd();//сбрасываю счетчик
                isSave=true; //проверка на необходимость записи, если были изменения
            }//счетчик не нулевой
            //проверяю на существование файла и наличие записей в справочнике
            if(recrd.sizeArray()>0 &&
                    (! FileType.valueOf(recrd.getTypfl().toString()).isPresent())) {
                isSave=true;
                FileType.valueOf(recrd.getTypfl().toString()).setModif();
                prnq("$ saveLocalDataSources > Принудительная запись "+recrd.getTypfl());
            }
        }//for FileGroupRecords
        if (!isSave) return false;//проверка на необходимость записи, если были изменения
        for (FileType typFile : FileType.values()) {//просматриваю все типы файлов
            if (typFile.ordinal() < 4 ) continue;   //пропускаю INI & CFG -файл
            if (!typFile.isModif()) continue;   //данный тип файла не модифицировался
            typFile.backUp();
            if (!typFile.save()) typFile.clrModif();//запись прошла успешно сбрасываю флаг
        }//for FileType
        //проверяю на успешность всех записей по сброшенному флагу
        boolean rez=false;
        for (FileType typFile : FileType.values()) rez |=typFile.isModif();
        assert prnq("\nSecond save { "+rez+" }");
        return rez;
    }//saveLocalDataSources

    public static void printLoadStatist(String s){
        prnq("name() tgetIadd() getTypfl() sizeArray()\t"+s);
        for (GrRecords recrd: GrRecords.values()){
            prnq("* "+recrd.name()+" \t"+recrd.getIadd()
                    +"\t"+recrd.getTypfl()+"\t"+recrd.sizeArray());
        }//for FileGroupRecords
        prnq("$--- "+s);
    } //printLoadStatist-----------------------------------------------------------------


/**
 * Загрузка локальных данных из всех файлов проекта указанных в переменной пути
 * 1) цикл по типам файлов. пропускаю первые типы файлов. Проверяю существование.
 * Проверяю длинну файла.Забираю в колекцию строки файла. В цикле прохожу коллекцию
 * с вызовом перечисления GrRecords по первому слову строки.
 * Вызываю GrRecords.ХХХХХ.readRecord.
 * 2) циклом по перечислению GrRecords вывожу на печать аналитику.
 */
public static void loadLocalDataSources() {
    assert prnq("$ BgFile > loadLocalDataSources $");
    //просматриваю перечень всех путей к локальным файлам
    for (FileType typFile : FileType.values()) {//просматриваю все типы файлов
        if (typFile.ordinal() < 4 ) continue;   //пропускаю INI & CFG -файл
//        assert prnq("?\t"+typFile.name()+">"+typFile.getPach());
//        if(typFile.getPach()==null) //если задан то пропускаю
            //создаю путь на основе текущего пути файла конфигурации
//            typFile.setPach(FileType.cfg.getPach().substring(0,FileType.cfg.getPach().length()
//                    -lengthSuffix).concat(typFile.getX()));
//        assert prnq("Load local "+typFile.name()+"\t"+typFile.getPach());
        Path path = Paths.get(typFile.getPach());
        typFile.setPresent(Files.exists(path));//сохраняю факт существования для логика записи
        if (!Files.exists(path)){prnq("ERROR file is no exists! "+path);continue;}
        if (!Files.isReadable(path)){prnq("ERROR not read file! "+path);continue;}
        try { if (Files.size(path) < typFile.size()) {//проверяю длинну файла
                prnq("\nERROR file is litl "+path);continue;}
        } catch (IOException e) { e.printStackTrace(); }
        try { List<String> readsrt= Files.readAllLines(path, StandardCharsets.UTF_8);
            assert prnq("Load string "+readsrt.size()+" from "+path);
            for (String str: readsrt){//читаю последовательно строки файла
                if (str.length()<5) {assert prnq("Error length");continue;}
                String[] words =str.split(sepr);//создаю масив значений
                try { GrRecords.valueOf(words[0]);//если не существует значение
                }catch(IllegalArgumentException e){continue;}
                if(GrRecords.valueOf(words[0]).readRecord(words,1)) break;
            }//for readsrt
        }catch(IOException e){e.printStackTrace();}
    }//for FileType
    //просматриваю поля и если они изменились делаю соответствующую отметку в файлах
//    for (GrRecords recrd: GrRecords.values()){
//        assert prnq("$ loadLocalDataSources> "+recrd.name()+" \t"+recrd.getIadd()
//                +"\t"+recrd.getTypfl()+"\t"+recrd.sizeArray());
//    }//for FileGroupRecords
}//loadLocalDataSources--------------------------------------------------------------
    /**
     * ----  подгрузка данных из внешних источников  ---------------------
     */
    public static void   uploadDataExternalSources(){
        assert prnq("\n181 BgFile> uploadDataExternalSources  \n"+"Внешних путей: "+
                lpPath.size()+ "  Зарегистрировано файлов:"+ lsFile.size());
        for (EiPath iPath: lpPath) prnq("P "+iPath.print());
        prnq("178------ BgFile> uploadDataExternalSources ---------------");
//    assert prnq("$$$  >"+glob);
        //-----------------------------------------------------
        if (lpPath.size()>0) for (EiPath iPath: lpPath ){//перебираю все внешние пути
            if (!iPath.isSolvd()) continue;//данный путь разрешено использование
            //----------------------------------------
            //assert prnq("186 >"+iPath.getTitul());
            iPath.setIsusr(false);
            Path path = Path.of(iPath.getTitul());
            if (FileType.lsf.include(path,iPath)) {iPath.setIsusr(false); continue;}
            iPath.setIsusr(true);//директрия существует
        }//for lsPath
        //-------------------------------------------------
        assert prnq("Зарегистрированных файлов после сканирования :"+lsFile.size());
        for (EiFile iFile: lsFile) prnq("A "+iFile.writ());

        assert prnq("199------ BgFile> uploadDataExternalSources ---------------");
        //анализирую состояние файлов
        for (EiFile iFile: lsFile){ //---------------------просмаьриваю список файлов
            Path path = Paths.get(iFile.getTitul());
            if (!Files.isReadable(path)) {iFile.setSolvd(false);continue;}//не читается,пропускаю
            iFile.setSolvd(true);//6) доступно чтение
            //ищу тип файла
            String s= path.toString().substring(path.toString().length()-lengthSuffix+1);
            assert BaseElement.prTitl();
            assert prnt("Fn "+iFile.print());
            try { if (Files.size(path) < FileType.valueOf(s).size()) {//проверяю длинну файла
                prnq("ERROR file is litl ");continue;}
            } catch (IOException e) { e.printStackTrace(); }
            iFile.setIsusr(false);//сбросил флаг синхронизации - начинаю чтение
            try{ List<String> readsrt= Files.readAllLines(path, StandardCharsets.UTF_8);
                assert prnq("\tLoad string "+readsrt.size());
                for (String str: readsrt){//читаю последовательно строки файла
                    if (str.length()<5) {assert prnq("Error length");continue;}
                    String[] words =str.split(sepr);//создаю масив значений
                    try { GrRecords.valueOf(words[0]);//если не существует значение
                    }catch(IllegalArgumentException e){continue;}
                    if(GrRecords.valueOf(words[0]).readRecord(words,iFile.getTyp())) break;
                }//for readsrt
            }catch(IOException e){e.printStackTrace();}
        }//for lsFile
    }//uploadDataExternalSources
//-------------------------------------------------------------------------------
    public static void printFlagModif(){
        //просматриваю поля и если они изменились делаю соответствующую отметку в файлах
        assert prnq("@ BgFile > количество изменений");
        int i=0;
        for (GrRecords recrd: GrRecords.values()){
            if (recrd.ordinal()<5) continue;
            assert prnt(recrd.name()+":"+recrd.getIadd()+" ");
            i+=  recrd.getIadd();
        }//for FileGroupRecords
        assert prnq("="+i+" @");
    } //print----------------------------------------------------------------------
    public static void clearFlagModif(){
        assert prnq("@ BgFile > Очистка счетчика изменений");
        for (GrRecords recrd: GrRecords.values()){ recrd.clrIadd(); }
    }
}//class BgFile  =====================================================================