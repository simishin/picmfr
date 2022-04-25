/**
 * перечень всех существующих типов файлов.
 * флаг загрузки проекта используется для выявления внешних файлов которые нужно
 * анализировать при автоматическом просторе внешних каталогов
 * ВНИМАНИЕ в  enum GrRecords используется ссылка на порядковый номер элемента
 */
package qwr.model.SharSystem;

import qwr.model.Base.EiPath;
//import qwr.util.BgFile;
import qwr.util.DateTim;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static qwr.util.CollectUtl.*;
//import static qwr.util.BgFile.*;


public enum FileType {
xlsx(345,"Файл электронных таблиц проекта",false){
    public String   loadFileName;   //имя и путь файла подгрузки данных из таблицы
    public boolean save(){
        assert prnq("\nСохранение результата работы в файле "+pach);
        assert pach!=null:"-->saveAs pach==null";
        try  {
//            FileOutputStream out = new FileOutputStream(new File(BgFile.flout));
//            BgFile.wbookPublic.write(out);
//            out.close();
            System.out.println( "Результат сохранен");
        } catch (Exception e){ e.printStackTrace(); }
        return true;
    }//saveAs
},//=================================================================================
lsf(0,"Файл списка всех файлов проекта",false) {
    public boolean save(){
        assert prnq("ext:TST>saveAs");
        assert pach!=null:"--ext:TST>saveAs pach==null";
        return true;
    }//saveAs
    //занесение в справочник файлов------------------------------------------------------
    @Override//--------занесение в справочник файлов---------------------------------
    public boolean include(Path path, EiPath dir){//беру путь и сканирую его в поисках файлов
        if (!Files.exists(path)){//проверяю на существование папки
            prnq("\tError directory is NOT >"+path);return true;}
        boolean b;
//        try (DirectoryStream<Path> files = Files.newDirectoryStream(path, BgFile.getGlob())){
//            for (Path entry : files) {//просматриваю файлы в указанной папке
////                assert prnq("30 >"+entry+"  ="+lsFile.size());
//                if(Files.exists(entry) && Files.isRegularFile(entry)){//он существует и он файл
//                    b=false;
//                    for (EiFile e:lsFile) {//добавляю в список файлов
//                        //assert prnq("FileType 32 include >"+e.getTitul()+"<>"+entry);
//                        if (e.getTitul().equals(entry.toString())) continue;//есть такой
//                        b=true;
//                    }//for lsFile
//                    if (b)  lsFile.add(new EiFile(entry.toString(),dir));//добавляю элемент
//                }//if exists
//            }//for files
//        } catch (IOException x) { System.err.println(">"+x); return true; }
        return true;
    }//includt All
},//==============================================================================
ini(12,"Файл инициализации проекта",false) {
}, //список проектов
//==============================================================================
cfg(32,"Файл конфигурации проекта",true){
}, //конфигурация проекта
//==============================================================================
mln(32,"Список входящих и исходящих писем",true) {
},//=========================
crt(32,"Список карточек заказчика",false) {
},//================================
rdc(32,"Список рабочей документации",false) {
},//==============================
gui(32,"Общие категории",false) {
},//==========================================
lui(32,"Локальные категории",false) {
};
//==============================================================================
private final int     l;      //задание минимального размера файла данного типа
private final String  ds;     //описание типа файла
private final String  exst;   //суффикс
private final boolean b;      //данный внешний файл нужно анализировать
private     boolean modif;  //флаг модификации данных в одном из полей
protected   String  pach;   //путь c именем к локальному файлу
private     boolean present; //файл имеется в наличии
//-- конструкторы --------------------------------------------------------------
FileType(int l, String ds, boolean b){
    exst=".".concat(name());
    pach=null; present =false;
    this.l =l; this.ds=ds; this.b=b; this.modif=false;}//конструктор


    //--- общие методы--------------------------------------------------------------
//public void    setB(boolean b) { this.b = b; }
//public boolean isB(){return b;}
public boolean  isPresent() { return present; }//файл существует и доступен
public void     setPresent(boolean present) { this.present = present; }//файл существует и доступен
public void     setModif() { this.modif = true; }
public void     clrModif() { this.modif = false; }
public boolean  isModif() { return modif; }
public String   getX() {return exst; }
public String   getDs(){return ds;}//описание типа файла
public long     size(){return l;}//задание минимального размера файла данного типа
public boolean  definePach(String nmPr){
    assert nmPr!=null:"set pach=null";
    this.pach=nmPr; return false;}
public String   getPach(){return this.pach;}
public void     setPach(String pach) { this.pach = pach; }

/**
 * создаю резервную копию файла методом переименования и удаления пердыдущей версии
 * вызывается BgFile.saveLocalDataSources(boolean isSave)
 * @return    */
public boolean backUp(){
    assert pach!=null: "! ext> backUp: pach = null !";
    try { if (!Files.exists(Paths.get(pach))) return true;//нет файла
            Files.move(Paths.get(pach),
                    Paths.get(pach.substring(0,pach.length()-1).concat("$")),
            StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) { e.printStackTrace(); return false;}
    return true;
}//backUp
//методы для перегрузки -----------------------------------------------------------

//занесение в справочник файлов---возвращает истина, если не тот тип файла-------
public boolean include(Path path, EiPath dir){
    assert prnq("Absent Metod");return false;}//----------------------------------

public  boolean save(){ return save(pach); }
//saveAs----------------------------------------------------------------------------
public  boolean save(String jpath){
    assert prnt("\nFileType:"+name()+">save (");
    assert jpath!=null:"--FileType>save pach==null";
    //создаю резервную копию
    try { if (Files.exists(Paths.get(jpath))) //нет файла
        Files.move(Paths.get(jpath),
                Paths.get(jpath.substring(0,jpath.length()-1).concat("$")),
                StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) { e.printStackTrace(); return false;}
    assert prnt("& ");
    //записываю файл
    try(BufferedWriter bw = new BufferedWriter(new FileWriter(jpath)))
    {   bw.write(this.getDs()+"\n" );//назначение файла
        GrRecords.TITUL.writPL(bw);//описание данного файла
        for(GrRecords recrd: GrRecords.values()) if(recrd.getTypFile().equals(name()))
        {recrd.writPL(bw);
            assert prnt("`"+recrd.name());
        }//for-if
        GrRecords.ENDFL.writPL(bw);//установка метки завершение записи
        prnq(" ) File: "+ jpath+" is UpLoad");
    }catch(IOException ex){System.out.println(ex.getMessage());return false;}//catch
    return true;
}//saveAs----------------------------------------------------------------------------
/**
 * Чтение файла, указанного в переменной pach
 * @return истина, если загрузили данные полностью, иначе 
 * чтение файла не возможно
 */
public boolean loadLoc(){
    assert pach!=null: "! FileType.loadLoc: pach = null !";
    Path path = Paths.get(pach);
    present = Files.exists(path);
    if (!present) {prnq("ERROR file is no exists! "+path); return false;}
    //проверяю на читаемость и нулевую длинну файла
    if (!Files.isReadable(path)){prnq("ERROR not read file! "+path); return false;}
    try { if (Files.size(path) < l) {//проверяю длинну файла
        prnq("\nERROR file: "+path+" is litl < "+l); return false;}
    } catch (IOException e) { e.printStackTrace(); }

    try {
        Stream.of (Files.readAllLines(path, StandardCharsets.UTF_8)).forEach(readsrt -> {
            assert prnq("Load string "+readsrt.size()+" from "+path);
            for (String str: readsrt){//читаю последовательно строки файла
                if (str.length()<5) {assert prnq("Error length");continue;}
                String[] words =str.split(sepr);//создаю масив значений
                try { GrRecords.valueOf(words[0]);//если не существует значение
                }catch(IllegalArgumentException e){
//                    assert prnq("Not define Word {"+words[0]+"}");
                    continue;}
                if(GrRecords.valueOf(words[0]).readRecord(words,ordinal()*8+1)) break;
            }//for readsrt
        } );
    }catch(IOException e){e.printStackTrace();}

    return true;
}//loadLoc

/**
 * Тестовое чтение файлов для дальнейшего принятия решений
 * @param pach путь и имя файла
 * @return число как результат возможности чтения файла
 * отрицательное если не прочитали или относительное время изменения
 */
//static String tmpDescr=null;
//static String tmpTitul=null;
//public static int loadScan(String pach) {
////    assert prnq("$ FileType loadScan $");
//    assert pach != null : "! FileType.loadScan: pach = null !";
//    Path path = Paths.get(pach);
//    if (Files.isDirectory(path)) return -5;//это не файл
//    if (Files.isSymbolicLink(path)) return -4;//это ссылка на файл
//    if (!Files.exists(path)) {
//        if (Files.notExists(path)) {//файл не существует - создаю
//            assert prnq(" File {" + path + "} Not exist ");
//            return -3; //не существует
//        } else return -2; //не доступен для чтения
//    }//if exist
//    try {
//        if (Files.size(path) < 13) {//проверяю длинну файла
//            prnq("ERROR file: " + path + " is litl < 13");
//            return -1;
//        }
//    } catch (IOException e) {
//        e.printStackTrace();
//        return -7;
//    }
//    long z = 1;
//    tmpDescr = null;
//    tmpTitul = null;
//    AtomicBoolean q = new AtomicBoolean(false);
//    try {
//        z = (Files.getLastModifiedTime(path).toMillis()) / 1000 - DateTim.begSecund;
//        long finalZ = z;
//        Stream.of(Files.readAllLines(path, StandardCharsets.UTF_8)).forEach(readsrt -> {
////            assert prnq("Load string "+readsrt.size()+" from "+path);
//            for (String str : readsrt) {//читаю последовательно строки файла
//                if (str.length() < 5) {
//                    assert prnq("Error length");
//                    continue;
//                }
//                if (str.startsWith(GrRecords.ENDFL.name()) ||
//                        (tmpDescr != null && tmpTitul != null)) {
////                    assert prnq("@  RiProdject.integrate "+pach);
//                    q.set(RiProdject.integrTitul((int) finalZ, tmpTitul, tmpDescr, pach));//добавляю элемент из временных переменных
//                    break;
//                }
//                if (str.startsWith(GrRecords.DESCR.name())) tmpDescr = str;
//                else if (str.startsWith(GrRecords.TITUL.name())) tmpTitul = str;
//            }//for readsrt
//        });
//    } catch (IOException e) { e.printStackTrace(); return -6;}
//    if (q.get()) return (int) z;
//        return 0;
//   //относительное время последней модификации файла
//}//loadScan
}//enum FileType=====================================================================