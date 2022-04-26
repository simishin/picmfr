/**
 * Перечень возможных полей в файлах проекта
 * каждое поле может храниться только в одном типе файла проекта
 * поле может быть основным и нести в себе драные или
 * вспомогательным для хранения контрольных значений или описаний - не подгружается
 * каждое поле имеет свою структуру данных в соответствии для какой модели создано
 * Для реализации параллельного выполнения процедур чтения и записи для шинчтво полей
 * ассоциируются с соответствующими структурами данных и содержат флаг и\или счетчики
 * изменений для запуска процессов сохранения данных. А так же хранят время последнего
 * процесса работы с файлом для отслеживания временных промежутков между поиском
 * изменений во внешних файлах и сохранения изменений в локальных файлах. При завершении
 * работы программы генерируется флаг/число -1 в счетчике для выполнения
 * принудительного сохранения результатов работы.
 * Данное перечисление позволяет унифицировать сохранение и чтение внешних данных структур.
 * Поскольку внешних данных много, то данное перечисление ориентирую на локальные данные.
 * Добавляю поля: флаг изменений??????
 */
package qwr.model.SharSystem;

import qwr.model.Base.EiUser;
/*
import qwr.model.nexus.ECard;
import qwr.model.nexus.EDraft;
import qwr.model.nexus.EMail;
import qwr.model.reference.*;

 */
import qwr.model.Base.RiPath;
//import qwr.util.BgFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static qwr.Inizial.InGuid.*;
import static qwr.util.CollectUtl.prnq;
import static qwr.util.CollectUtl.sepr;
//import static qwr.util.BgFile.*;

public enum GrRecords {
TITUL {//описание данного файла
    /*
    Титульный заголовок содержит следующую информацию для сбора аналитики:
    Код проекта принадлежности, название проекта и время его создания
     */
    public void writPL(BufferedWriter bw) {
        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
        try { bw.write(this.name()+sepr+    //0)наименование поля
            RiProdject.getCurName()+sepr+          //1)Код проекта
            RiProdject.getCurCreat()+sepr+            //2)время создания
            RiProdject.getCurTitul()+"\n");     //3)название проекта
        }catch(IOException ioException){ioException.printStackTrace();return;}
        return;
    }//writPL
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
//        assert prnq("$ GrRecords TITUL readRecord $ "+src/8);
        //анализ для файлов инициализации
        if ((src & 248)==(2*8)) RiProdject.setCurNTC(words);
        return false;//условие дальнейшего анализа файла
    }//readExst TITUL
}, //============================================================================
PRODJ( FileType.ini) {//список проектов для инициализации, класс RiProdject
    public void writPL(BufferedWriter bw) {
        assert RiProdject.list.size()>0: "~GrRecords:writPL~RiProdject.list.size=null";
        writPL(RiProdject.list.stream(), bw);//вызов обобщенного метода в этом перечислении
        return;
    }//writPL

    /**
     * Вызывается из enum FileType loadLoc() для обработки считанной строки
     * @param words массив слов считанной строки
     * @param src тип источника массива/потока строк
     * @return истина если прекратить обработку потока строк или лож для продолжения
     */
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
        int j = RiProdject.integrate(words,src & 7);//задаю принадлежность к классу
        if (j<0) prnq("@\tRiProdject.integrate="+j);
        return false;//условие дальнейшего анализа файла
    }//readExst

},//PRODJ----------------------------------------------------------------------------------------------------
DESCR( FileType.cfg) {
    @Override
    public void writPL(BufferedWriter bw) {
        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
        try { bw.write(this.name()+sepr+    //0)наименование поля
                RiProdject.getCurDescr()+"\n"); //1)описание проекта
        }catch(IOException ioException){ioException.printStackTrace();return;}
        return;
    }//writPL

    @Override
    public boolean readRecord(String[] words, int src) {
        assert prnq("$ GrRecords DESCR readRecord $");
        //анализ для файлов инициализации
        if ((src & 248)==(2*8)) RiProdject.curDescr = words[1];
        return false;
    }//
}, //-------------------------------------------------------------------------------------------------------------
PATHS( FileType.cfg){//вспомогательный - количество путей
    @Override
    public void writPL(BufferedWriter bw) {
//        assert RiPath.list.size()>0: "~GrRecords:writPL~RiProdject.list.size=null";
        assert prnq("PATHS : "+RiPath.list.size());
        writPL(RiPath.list.stream(), bw);//вызов обобщенного метода в этом перечислении
//        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
//        try { bw.write(this.name()+sepr+ lpPath.size()+"\n");
//        }catch(IOException ioException){ioException.printStackTrace();return;}
        return;
    }//writPL
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
        int x = RiPath.integrate(words,src & 7);//задаю принадлежность к классу
        assert prnq("RiPath.integrate="+x);
        return false;//условие дальнейшего анализа файла
    }//readExst
},//PATHS
//=============================================================================
//PATHR( FileType.cfg)   {//основной список путей с параметрами
//    @Override
//    public void writPL(BufferedWriter bw) {
//        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
//        if (lpPath.size()>0)
//            for(EiPath itm: lpPath ) {
//                try { bw.write(this.name()+itm.writ()+"\n"); }
//                catch(IOException ioException){ioException.printStackTrace();return;}
//            }//for
//        return;
//    }//writPL
//    @Override //---------------------------------------------------------------------
//    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
////        assert prnq("* "+name());
//        EiPath itm = new EiPath("");
//        if( itm.read(words)){ assert prnq("Error GrRecords:115 "+words);
////            BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
//            return false; }//строка не разобралась
//        //пополняю список
//        for (EiPath e:lpPath) if ( e.merger(itm))return false;//корректирую значение в полях
//        //добавляю элемент
//        long j=itm.getKey();
//        while (lpPath.contains(j)) j--;
//        itm.setKey(Math.toIntExact(j));
//        lpPath.add(itm);//добавляю элемент
//        return false;//условие дальнейшего анализа файла
//    }//readExst
//    /**
//     * Создание нового объекта в списке с проверкой на совпадение ключа
//     * @param obj интегрируемый в список объект
//     * @return истина, если найден одноименный объект и интеграция не возможна
//     */
//    public boolean create(Object obj) {//разбор строки внеш.файла
////        assert prnq("*** Metod create EiPath *** ");
//        assert obj instanceof EiPath: "Object is NOT EiPath";
//        EiPath itm=(EiPath) obj;
//        for (EiPath e:lpPath) {
////            assert prnq(">>>>>> "+e.getTitul());
//            if (e.equals(itm)) {
//            return true;}}//проверяю совпадение
//        long j=itm.getKey();
//        while (lpPath.contains(j)) j--;
//        itm.setKey(Math.toIntExact(j));
////        while (lpPath.contains(itm.getKey())){ itm.incKey(); }//проверяю наложение ключа
//        lpPath.add(itm);//добавляю элемент
//        prnq(" > add:"+itm.getKey()+"~"+itm.getTitul());
//        return false;//
//    }//create
//},//PATHR
//=============================================================================
USERS( FileType.cfg){//вспомогательный - количество пользователей
    @Override
    public void writPL(BufferedWriter bw) {
        assert prnq("USERS : "+RiUser.list.size());
        writPL(RiUser.list.stream(), bw);

//        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
//        try { bw.write(this.name()+sepr+ mpUser.size()+"\n");
//        }catch(IOException ioException){ioException.printStackTrace();return;}
//        return;
    }//writPL
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
        int x = RiUser.integrate(words,src & 7);//задаю принадлежность к классу
        assert prnq("RiPath.integrate="+x);
        return false;//условие дальнейшего анализа файла
    }//readExst
},//URS
//=============================================================================
USERI( FileType.cfg) {//основной список пользователей
    @Override
    public void writPL(BufferedWriter bw) {
        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
        if (mpUser.size()>0) for (EiUser s: mpUser.values() ){
            try { bw.write(this.name()+ s.writ()+"\n");
            }catch(IOException ioException){ioException.printStackTrace();return;}
        }//for
        return;
    }//writPL
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
//        assert prnq("* "+name());
        EiUser itm = new EiUser("");
        if( itm.read(words)){ assert prnq("Error GrRecords:115 "+words);
//            BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
            return false; }
        //пополняю список пользователей
        for (EiUser vl:mpUser.values()) if (vl.merger(itm)) return false;//корректирую
        long j=itm.getKey();
        while (mpUser.containsKey(j)) j--;
        itm.setKey(Math.toIntExact(j));
        mpUser.put(Long.valueOf(itm.getKey()),itm);//добавляю элемент
        assert prnq("Added user: "+itm.getTitul());
        return false;//условие дальнейшего анализа файла
    }//readExst
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Создание нового объекта в списке с проверкой на совпадение ключа
     * @param obj интегрируемый в список объект
     * @return истина, если найден одноименный объект и интеграция не возможна
     */
    public boolean create(Object obj) {//разбор строки внеш.файла
        assert prnq("*** Metod create EiUser *** ");
        assert obj instanceof EiUser: "Object is NOT EiUser";
        EiUser itm=(EiUser) obj;
        for (EiUser e:mpUser.values())
            if (e.getTitul().equals(itm.getTitul())) return true;
        long j=itm.getKey();
        while (mpUser.containsKey(j)) j--;
        itm.setKey(Math.toIntExact(j));

        mpUser.put((long)itm.getKey(),itm);//добавляю элемент
        prnq(" > add:"+itm.getKey()+"~"+itm.getTitul());
        return false;//
    }//create
},//USR
/*    //=============================================================================
    MAILJ( FileType.mln) {//писок писем
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lsMail.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (EMail.mar.size()>0) for (InfcElm s: EMail.mar.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL

        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
//                if(AltTitle.isOverlap(LDocPrj.lnRDoc,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<= EMail.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("EMail> Error size array ("+words.length+"~"+EMail.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(EMail.mar,new EMail(words),src)>0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst

    },//MAL==========================================================================
    AUTHR( FileType.lui) {//автор письма
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lnAuthor.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (lnAuthor.size()>0) for (InfcElm s: lnAuthor.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL

        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
                if(AltTitle.integrate(lnAuthor,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<= EjAuthor.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("LineGuide> Error size array ("+words.length+"~"+ EjAuthor.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(lnAuthor,new EjAuthor(words),src)!=0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst

    },//AUTHR==========================================================================
    BUILD( FileType.gui) {//здания
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lnBuild.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (lnBuild.size()>0) for (InfcElm s: lnBuild.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL
        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
                if(AltTitle.integrate(lnBuild,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<= EjBuild.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("LineGuide> Error size array ("+words.length+"~"+ EjBuild.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(lnBuild,new EjBuild(words),src)!=0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst
    },//BUILD==========================================================================

    CHPTR( FileType.lui) {//разделы РД
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lnChaptr.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (lnChaptr.size()>0) for (InfcElm s: lnChaptr.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL
        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
                if(AltTitle.integrate(lnChaptr,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<=EiChaptr.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("LineGuide> Error size array ("+words.length+"~"+EiChaptr.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(lnChaptr,new EiChaptr(words),src)!=0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst------------------------------------------------------------------
    },//CHPTR========================================================================
    GRORG( FileType.lui) {//виды, группы организаций
        @Override //-внесение недостающих элементов в коллекции из подчиненных ------
        public void lackItem(){
            HashSet<String> dearth = new HashSet<String>();
            if(TreeGuide.bondingTree(lnGrOrgz,lnOrganz,dearth)) {
                EiGrOrgz.addisen(dearth, 17); //косвенное создание
                TreeGuide.bondingTree(lnGrOrgz,lnOrganz,dearth);
            }
        }//внесение недостающих элементов в коллекции из подчиненных
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lnGrOrgz.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (lnGrOrgz.size()>0) for (InfcElm s: lnGrOrgz.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL
        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
                prnq("!!!!!!!!!!!!!!!!!!!!!!!!!");
                if(AltTitle.integrate(lnGrOrgz,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта

            if (words.length<=EiGrOrgz.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("LineGuide> Error size array ("+words.length+"~"+EiGrOrgz.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(lnGrOrgz,new EiGrOrgz(words),src)!=0) shift();
            return false;//условие дальнейшего анализа файла
         }//readExst------------------------------------------------------------------
    },//GRORG========================================================================
    HEADR( FileType.lui) {//главы
        @Override //-внесение недостающих элементов в коллекции из подчиненных ------
        public void lackItem(){
            HashSet<String> dearth = new HashSet<String>();
            if(TreeGuide.bondingTree(lnHeader,lnBuild,dearth)) {
                EiHeader.addisen(dearth, 17);
                TreeGuide.bondingTree(lnHeader,lnBuild,dearth);
            }
        }//внесение недостающих элементов в коллекции из подчиненных
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lnHeader.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (lnHeader.size()>0) for (InfcElm s: lnHeader.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL
        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
                if(AltTitle.integrate(lnHeader,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<=EiHeader.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("LineGuide> Error size array ("+words.length+"~"+EiHeader.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(lnHeader,new EiHeader(words),src)!=0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst
    },//HEADR==========================================================================
    ORGNZ( FileType.gui) {//организации
        @Override //-----------------------------------------------------------------
        public int      sizeArray(){return lnOrganz.size();}
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (lnOrganz.size()>0) for (InfcElm s: lnOrganz.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL
        @Override //-----------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
                if(AltTitle.integrate(lnOrganz,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<= EjOrganz.sizeAr) { //проверяю количество элементов в массиве
                assert prnq("LineGuide> Error size array ("+words.length+"~"+ EjOrganz.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(lnOrganz,new EjOrganz(words),src)!=0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst
    },//CHPTR==========================================================================
    DRAFT( FileType.rdc) {//список чертежей
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (!EDraft.mar.isEmpty()) for (InfcElm s: EDraft.mar.values() ){
                try { bw.write(this.name()+ s.writ()+"\n");
                }catch(IOException ioException){ioException.printStackTrace();
                    return; }
            }//for
            return;
        }//writPL
        @Override //---------------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
//                if(AltTitle.isOverlap(LDocPrj.lnRDoc,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length<= EDraft.sizeAr) { //проверяю количество элементов в массиве
                assert prnq(this.name()+" Error size array ("+words.length+"~"+EDraft.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение
            if(LineGuide.integrate(EDraft.mar,new EDraft(words),src)>0) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst
    },//DRAFT
    //=============================================================================
    CARTJ( FileType.crt) {//список карточек
        @Override
        public void writPL(BufferedWriter bw) {
            assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
            if (!ECard.lst.isEmpty()) {
                try { bw.write(this.name()+ sepr+ ECard.lst.size()+"\n");
                } catch (IOException e) { e.printStackTrace(); return; }
                for (ListElm o: ECard.lst ){
                    try { bw.write(this.name()+ o.writ()+"\n");
                    } catch (IOException e) { e.printStackTrace(); return; }
                }//for
            } //if
            return;
        }//writPL

        @Override //---------------------------------------------------------------------
        public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
            if (words.length==3){//читаю альтернативное название объекта
//                if(AltTitle.isOverlap(LDocPrj.lnRDoc,words)) shift();//инсриментирую флаг изменения списка
                return false;
            }  //читаю альтернативное название объекта
            if (words.length==2){ //устанавливаю размер коллекции
                ECard.setSize(ECard.lst,Integer.parseInt (words[1]));
     //           ECard.setSizAr(Integer.parseInt (words[1]));
                return false;
            }
            if (words.length<= ECard.sizeAr) { //проверяю количество элементов в массиве
                assert prnq(this.name()+" Error size array ("+words.length+"~"+ECard.sizeAr+")");
                BgFile.setDecompositionError();//ставлю флаг остановки анализа строки
                return false;//условие дальнейшего анализа файла
            }//аварийное завершение

//            assert prnq("* "+name()+" "+words.length+"/"+ECard.sizeAr);
//            if(LineGuide.addition(ECard.lst,new ECard(words),src)>0) shift();

//---            if((new ECard(words)).addElm(src)) shift();
            return false;//условие дальнейшего анализа файла
        }//readExst

    },//CRT

    // =============================================================================
FILIRD{
    @Override
    public void writPL(BufferedWriter bw)
    { assert prnq("*** Metod create enum GrRecords is not specify *** ");}
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
        assert prnq("*** Metod create enum FileGroupRecords is not specify *** ");
        return true;//условие прекращения дальнейшего анализа файла
    }//readExst

 */
    /**
     * Создание нового объекта в списке с проверкой на совпадение ключа
     * @param obj интегрируемый в список объект
     * @return истина, если найден одноименный объект и интеграция не возможна
     */
    /*
    public boolean create(Object obj) {//разбор строки внеш.файла
        assert prnq("*** Metod create *** ");
        assert obj instanceof EiFile: "Object is NOT EiFile";
        EiFile itm=(EiFile) obj;
        for (EiFile e:lsFile) if (e.equals(itm)) return true;
        long j=itm.getKey();
        while (lsFile.contains(j)) j--;
        itm.setKey(Math.toIntExact(j));
//        while (lsFile.contains(itm.getKey())){ itm.incKey(); }
        lsFile.add(itm);//добавляю элемент
        prnq(" > add:"+itm.getKey()+"~"+itm.getTitul());
        return false;//
    }//create
    },

     */
//=============================================================================
ENDFL {
    @Override
    public void writPL(BufferedWriter bw) {
        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
        try { bw.write(this.name() + "\n");
              bw.flush();//очистка буффера
        }catch(IOException ioException){ioException.printStackTrace();return;}
        return;
    }//writPL
    @Override //---------------------------------------------------------------------
    public boolean readRecord(String[] words,int src) {//разбор строки внеш.файла
//        BgFile.setReadFile(false,0);//флаг процесса чтения файла
//        assert prnq("430 FileGroupRecords *"+name());
        return true;//условие прекращения дальнейшего анализа файла
    }//readExst
};//END
    //==============================================================================
    private int         iadd;//количество добавленных элементов в список для сохранения
    private final FileType    typfl;//в каком типе файла хранится
    public static final int   lengName = 5;//длинна маркера с разделителем
    public  FileType    getTypfl(){return typfl;}
    public  String      getTypFile(){return typfl.toString();}
    public  void        shift(){iadd++;}
    public  void        clrIadd(){iadd=0;}
    public  int         getIadd() { return iadd; }

    //конструктор
    GrRecords(FileType t){this.typfl =t; iadd=0;}
    GrRecords()          {this.typfl =FileType.lsf; iadd=0;}
    //методы
    public abstract void    writPL(BufferedWriter bw);//для переопределения

    /**
     * Вызывается из GrRecords.ХХХХХ.writPL(BufferedWriter bw)
     * @param list список требуемого класса объекта записи
     * @param bw буфер для записи, наследуемый при вызове
     * @param <T> тип объекта записи, задается при вызове
     * @return возвращает истина, если запись прошла без сбоев
     */
    public <T> boolean writPL(Stream<T> list, BufferedWriter bw) {
        assert bw!=null:"-- GrRecords:writPL > BufferedWriter==null";
        AtomicBoolean z= new AtomicBoolean(true);
        list.forEach(q-> {
            try { bw.write(this.name()+q.toString()+"\n"); }
            catch (IOException e) { e.printStackTrace(); z.set(false);}
        });
        return z.get();
    }//writPL

    /**
     * Вызывается из enum FileType loadLoc() для обработки считанной строки
     * @param words массив слов считанной строки
     * @param src тип источника массива/потока строк- 3 младших бита и сдвинутого номера типа файлов
     * @return истина если прекратить обработку потока строк или лож для продолжения
     */
    public abstract boolean readRecord(String[] words,int src);//распознавание записи в строке
//    public int      sizeArray(){return -1;}//количество элементов определенного класса
//    public boolean  create(Object obj) {//разбор строки внеш.файла
//        assert prnq("*** Metod create enum GrRecords is not specify *** ");
//        return false;//условие дальнейшего анализа файла
//    }//create
//    public void lackItem(){}//внесение недостающих элементов в коллекции из подчиненных

}//enum FileGroupRecords
