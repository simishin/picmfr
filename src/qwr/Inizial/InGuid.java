/**
 *  определение всех колекций как для работы системы, так и для проекта
 *  инициализация значений справочников по умолчанию
 */
package qwr.Inizial;

//import qwr.footing.InfcElm;
import qwr.model.Base.EiUser;
//import qwr.model.reference.*;
//import qwr.reports.Elcol;

import java.util.HashMap;
import java.util.Map;

import static qwr.util.CollectUtl.prnq;

public class InGuid {
    /**
     * Возможные уровни группировки
     * 1-по главам (список) > 2, 7
     * 2-по зданиям (список) > 3
     * 3-по разделам РД (список) > 6
     * 4-по системам (дерево) > 6
     * 5-по рабочей документации (список-дерево) > 4, 6
     * 6-по видам, этапам работ (список)
     * 7-по технологическим группам зданий-сооружений (дерево) > 4
     */
    //справочники системы
    //lsFile формируется при просмотре lpPath в util.BgFile.loadConfiguration()
    //lsFile используется для подгрузке данных из внешних источников в
    //util.BgFile.ploadDataExternalSources()
    //данные из lsFile сохраняются и восстанавливаются из файла типа "lsf" FileType
//    public static ArrayList<EiFile> lsFile=new ArrayList<>(950);//файлы
//    public static Map<Long,EiUser>  mpUser=new HashMap<Long,EiUser>(32);
//    public static ArrayList<EiPath> lpPath=new ArrayList<>();//спиок путей
/*    public static ArrayList<EiCorrect> lsCorr=new ArrayList<>();//список транзакций
    //справочники проекта
    public static ArrayList<EiRDoc> lsRDoc=new ArrayList<>(950);//чертежи
    public static ArrayList<EiCart> lsCart=new ArrayList<>(950);//карточки
    public static Map<Long,Eimail>  lsMail=new HashMap<Long,Eimail>(1200);//письма
    //справочники нормализации данных статические
    public static Map<String, InfcElm> lnAuthor=new HashMap<>(80);//авторы
    public static Map<String, InfcElm> lnHeader=new HashMap<>(15);//главы
    public static Map<String, InfcElm> lnChaptr=new HashMap<>(80);//разделы РД
    public static Map<String, InfcElm> lnGrOrgz=new HashMap<>(80);//виды фирм
    //справочники нормализации данных динамические
    public static Map<String, InfcElm>  lnBuild=new HashMap<>(80);//здания
    public static Map<String, InfcElm> lnOrganz=new HashMap<>(80);//организации
    //глобальные переменные
    private static boolean tstini=false; //контрольодного прохода инициализации

 */

    static {   //начало блока инициализатора
        //определение расчетных констант на основе структуры программного кода
        assert prnq("$ class InGuid static block initialization 44 $");
        //создаю первого пользовтеля и болванку под файл инициализации

//        mpUser.putIfAbsent(1L,new EiUser(BgFile.getCmpName(),"This Computer"));
//        lpPath.add(new EiPath(System.getProperty("user.dir"),0));
    }//конец блока инициализатора ***************************************************

/*
    public static void print(String s, Map<String, InfcElm> array) {
        prnq("--"+s+"  "+array.size());
        for (InfcElm value : array.values()) prnq(value.print()); }
 /*
    public static void printw(String s, Map<String, LoadXlsx> array) {
        prnq("--"+s+"  "+array.size());
        for (LoadXlsx value : array.values()) prnq(value.print()); }

  */
    /**
     *
     */
    /*
    public static void beg(){
        assert prnq("$ class InGuid beg 61 $");
        if (tstini){ prnq("Seconds Call IniGl:beg"); return; }tstini=true; //контрольодного прохода
        //инициализация групп органзаций
//        EiGrOrgz.addisen("З","Заказчик");
//        EiGrOrgz.addisen("К","Контролирующие органы");
//        EiGrOrgz.addisen("Г","Генподрядные организации");
//        EiGrOrgz.addisen("П","Пректные организации");
        EiGrOrgz.addisen("С","Организации поставщики");
        EiGrOrgz.addisen("И","Подрядные организации");
        EiGrOrgz.addisen("Г","Генподрядные организации");
//        prnq("+++"); for (InfcElm value : lnGrOrgz.values()) prnq(value.print());
        //инициализация органзаций
        EjOrganz.addisen("СХК","З");
        EjOrganz.addisen("ЩМЯ","Р");
        EjOrganz.addiAli("СХК","TITL");
        EjOrganz.addiAli("TITL","АО \"XREN\"");
        EjOrganz.addiAli("БАЭС","ООО \"УС БАЭС\"");
        EjOrganz.addiAli("СХК","АО \"СХК\"");

        EjOrganz.addisen("TITL","Г");
//        InGuid.print("lnGrOrgz",lnGrOrgz);
//        InGuid.print("lnOrganz",lnOrganz);
        GrRecords.GRORG.lackItem(); //-внесение недостающих элементов в коллекции из подчиненных
        HashSet<String> dearth = new HashSet<String>();
        /*
        if(TreeGuide.bondingTree(lnGrOrgz,lnOrganz,dearth)) {
            EiGrOrgz.addisen(dearth, 17);
            TreeGuide.bondingTree(lnGrOrgz,lnOrganz,dearth);
        }

         */

//        InGuid.print("lnGrOrgz",lnGrOrgz);
//        InGuid.print("lnOrganz",lnOrganz);
//        prnq("\t---------->>"+lnOrganz.size());
//        for (InfcElm vl:lnOrganz.values()){
//            if (vl instanceof AltTitle) prnq("&"+vl.print()); else prnq("#"+vl.print());
        //    prnq(vl.print());
//        }
        //инициализация глав
/*        EiHeader.addisen("01","Глава 1. Подготовка территории.");
        EiHeader.addisen("02","Глава 2. Основные объекты строительства");
        EiHeader.addisen("03","Глава 3. Объекты подсобного и обслуживающего назначения");
        EiHeader.addisen("04","Глава 4. Объекты энергетического хозяйства");
        EiHeader.addisen("05","Глава 5. Объекты транспортного хозяйства и связи");
        EiHeader.addisen("06","Глава 6. Наружные сети и сооружения водоснабжения, водоотведения, теплоснабжения и газоснабжения");
        EiHeader.addisen("07","Глава 7.Благоустройство и озеленение территории");
        EiHeader.addisen("08","Глава 8. Временные здания и сооружения");
        EiHeader.addisen("09","Глава 9. Прочие работы и затраты");
        EiHeader.addisen("12","Глава 12 Проектные и изыскательские работы");
        EiHeader.addisen("13","Глава 13 Подготовительный период");
//        InGuid.print("lnHeader",lnHeader);
        //инициализация глав
        //****************************************************************************************************
        EjBuild.addisen("Зд4","02","Здание 4-здание МФР");
        EjBuild.addisen("Зд4пр","02","Здание 4-здание МФР");
        EjBuild.addisen("Зд4А","03","Здание 4А-здание переработки САО и НЛО");
        EjBuild.addisen("Зд5","03","Здание 5-временное хранилище кондиционированных САО, НАО и ОНАО");
        EjBuild.addisen("Зд16","03","Здание 16-адмннистративно-бытовой комплекс (с ЗПУ ПД АС) с людским контрольно-пропускным пунктом");
        EjBuild.addisen("Зд22","03","Здание 22 - здание санпропускника");
        EjBuild.addisen("Ср30","03","Сооружение 30 - сооружение ГО (А- II1-600-2002), резервуары запаса воды");
        EjBuild.addisen("Зд33","03","Здание 33 - центральный материальный склад и склад химреагентов");
        EjBuild.addisen("Ср5/4А","03","Сооружения 5/4А, - пешеходно-технологические галереи");
        EjBuild.addisen("Ср64/22/4","03","Сооружения  64/22, 22/4, - пешеходно-технологические галереи");
        EjBuild.addisen("Ср22/16","03","Сооружения 22/16 - пешеходно-технологические галереи");
        EjBuild.addisen("Ср41","03","Сооружение 41 - метеорологическая площадка");
        EjBuild.addisen("НаблСкваж","03","Наблидательные скважины по периметру зданий 4,4А и 5");
        EjBuild.addisen("Ср7А","04","Сооружение 7 А - дизельная электростанция с вынесенным топливо хранилищем");
        EjBuild.addisen("Ср63","04","Сооружение 63 — КРУ-6 кВ");
        EjBuild.addisen("Ср15Б","04","Сооружение 15Б - блочно-модульные трансформаторная подстанция");
        EjBuild.addisen("Ср15В","","Сооружение 15В - блочно модульная трансформаторная подстанция");
        EjBuild.addisen("Ср15А","06","Здание 15А - объединенная насосная станция хозпитьевого, производственного и противопож.водоснабжен.");
        EjBuild.addisen("Ср65","06","Сооружение 65 - блочно-модульные трансформаторная подстанция");
//        lEiBuild.addisen("НрСетиЭлек","","04","Наружные сети электроснабжения");
        EjBuild.addisen("Ср31","05","Сооружение 31 - пункт дезактивации вагонов");
        EjBuild.addisen("КПП32","05","Людской контрольно-пропускной пункт на городской зоне");
        EjBuild.addisen("Ср32А","05","Сооружения 32А - автотранспортные КПП");
        EjBuild.addisen("Ср32Б","05","Сооружения 32Б - автотранспортные КПП");
        EjBuild.addisen("Ср32В","05","Сооружения 32В - автотранспортные КПП");
        EjBuild.addisen("Ср32Г","05","Сооружение 32Г - железнодорожный КПП");
        EjBuild.addisen("Зд34","05","Здание 34 - центр физ.защиты (караульное помещение, центральный пункт управления, убежище 34А)");
        EjBuild.addisen("Зд34А","05","Здание 34А - Здание 34А. Убежище ГО на 60 укрываемых");
        EjBuild.addisen("Ср37","05","Сооружение 37 - площадка погрузки/разгрузки железнодорожных вагонов");
        EjBuild.addisen("Ср21","06","Сооружение 21 - газобаллонная");
        EjBuild.addisen("Ср24А","06","Сооружение 24А - резервуары запаса питьевой воды 2x1000 м3");
        EjBuild.addisen("Ср24Б","06","Сооружение 24Б - резервуары запаса питьевой воды 2x1900 м3");
        EjBuild.addisen("Ср24В","06","Сооружение 24B - фильтры - поглотители");
        EjBuild.addisen("Ср27","06","Сооружение 27 - аргонная станция");
        EjBuild.addisen("Ср28","06","Сооружения 28 - компрессорная низкого давления");
        EjBuild.addisen("Ср29","06","Сооружение 29 - сооружение учета теплоты");
        EjBuild.addisen("Ср61","06","Сооружение 61 - азотно-водородная станция");
        EjBuild.addisen("Ср62","06","Сооружение 62 - площадка размещения газификаторов для азота");
        EjBuild.addisen("Ср64","06","Сооружение 64 - холодильная станция");
        EjBuild.addisen("Ср36","06","Сооружение 36 - очистные сооружения промливневых стоков и стоков, содержащих нефтепродукты");
        EjBuild.addisen("Ср36А","06","Сооружение 36А - очистные сооружения хозяйственно-бытовых сточных вод зоны свобод. и контр. доступа");
        EjBuild.addisen("Ср4Б","06","Сооружение 4Б - вытяжная труба");
        EjBuild.addisen("Ср38","06","Сооружение 38 - дренажная насосная станция");
        EjBuild.addisen("Ср1-7","06","Сооружения 1-7 - канализационные насосные станции бытовых стоков, производственно-дождевых стоков");
        EjBuild.addisen("НрСетиЭлек","04","Наружные сети электроснабжения");
        EjBuild.addisen("НрСетиКан","06","Внутриквартальные наружные сети водопровода и канализации");
        EjBuild.addisen("НрСетиТепл","06","Наружные сети теплоснабжения");
        EjBuild.addisen("НрСетиСв","05","Наружные сети связи и сигнализации");
        EjBuild.addisen("НрСетиГаз","06","Наружные сети газоснабжения, холодоснабжения, сжатого воздуха");
        EjBuild.addisen("А/Д","05","Автодороги и подъезды");
        EjBuild.addisen("Ж/Д","55","Железнодорожные пути");
        EjBuild.addisen("СФЗ","05","Периметр площадки ОДЭК. Система физической защиты");
        EjBuild.addisen("АБК","08","Административно-бытовой городок");
//        InGuid.print("lnBuild",lnBuild);
        GrRecords.HEADR.lackItem();//-внесение недостающих элементов
        /*
        if(TreeGuide.bondingTree(lnHeader,lnBuild,dearth)) {
            EiHeader.addisen(dearth, 17);
            TreeGuide.bondingTree(lnHeader,lnBuild,dearth);
        }

         */
//        InGuid.print("lnBuild+",lnBuild);
    ///------------------------------------------------------------------------------------------------------------------
/*        EjBuild.addisen("Зд4","4");
        EjBuild.addisen("Зд4пр","4пр");
        EjBuild.addisen("Зд4А","4A");
        EjBuild.addisen("Зд5","5");
        EjBuild.addisen("Зд16","16");
        EjBuild.addisen("Зд22","22");
        EjBuild.addisen("Ср30","30");
        EjBuild.addisen("Зд33","33");
        EjBuild.addisen("Ср5/4А","5/4А");
        EjBuild.addisen("Ср64/22/4","64/22+22/4");
        EjBuild.addisen("Ср22/16","22/16");
        EjBuild.addisen("Ср41","41");
        EjBuild.addisen("НаблСкваж","НбСк");
        EjBuild.addisen("Ср7А","7А");
        EjBuild.addisen("Ср63","63");
        EjBuild.addisen("Ср15Б","15Б");
        EjBuild.addisen("Ср15В","15В");
        EjBuild.addisen("Ср15А","15А");
        EjBuild.addisen("Ср65","65");
//        lEiBuild.addisen("НрСетиЭлек","");
        EjBuild.addisen("Ср31","31");
        EjBuild.addisen("КПП32","32");
        EjBuild.addisen("Ср32А","32А");
        EjBuild.addisen("Ср32Б","32Б");
        EjBuild.addisen("Ср32В","32В");
        EjBuild.addisen("Ср32Г","32Г");
        EjBuild.addisen("Зд34","34");
        EjBuild.addisen("Зд34А","34А");
        EjBuild.addisen("Ср37","37");
        EjBuild.addisen("Ср21","21");
        EjBuild.addisen("Ср24А","24А");
        EjBuild.addisen("Ср24Б","24Б");
        EjBuild.addisen("Ср24В","24В");
        EjBuild.addisen("Ср27","27");
        EjBuild.addisen("Ср28","28");
        EjBuild.addisen("Ср29","29");
        EjBuild.addisen("Ср61","61");
        EjBuild.addisen("Ср62","62");
        EjBuild.addisen("Ср64","64");
        EjBuild.addisen("Ср36","36");
        EjBuild.addisen("Ср36А","36А");
        EjBuild.addisen("Ср4Б","4Б");
        EjBuild.addisen("Ср38","38");
        EjBuild.addisen("Ср1-7","КНС-6");
        EjBuild.addisen("НрСетиЭлек","НЭС");
        EjBuild.addisen("НрСетиКан","НВК");
        EjBuild.addisen("НрСетиТепл","НТС");
        EjBuild.addisen("НрСетиСв","НСС");
        EjBuild.addisen("НрСетиГаз","НГС");
        EjBuild.addisen("А/Д","АД");
        EjBuild.addisen("Ж/Д","ЖД");
//        EiBuild.addisen("СФЗ","Перм");
        EjBuild.addisen("СФЗ","Перимет");
        EjBuild.addisen("СФЗ","СисФизЗа");
        EjBuild.addisen("АБК","АБК");
        //***************************************************************************************************

        //инициализация разделов РД
        EiChaptr.addisen("АС","Архитектурно-строительные решения");
        EiChaptr.addisen("КЖ","Конструкции железобетонные");
//        lstRz.add(new ElGuid("КЖ.7","Конструкции железобетонные"));
//        lstRz.add(new ElGuid("КЖ.8","Конструкции железобетонные"));
//        lstRz.add(new ElGuid("КЖ.9","Конструкции железобетонные"));
        EiChaptr.addisen("ГР","Гидроизоляция решения");
        EiChaptr.addisen("КМ","Конструкции металлические");
        EiChaptr.addisen("КМ обл","Облицовка");
        EiChaptr.addisen("ГП","Генеральный план");
        EiChaptr.addisen("АР","Архитектурные решения.");
        EiChaptr.addisen("ОВ","Вентиляция,  отопление и теплоснабжение");
        EiChaptr.addisen("ОТ","Отопление и теплоснабжение.");
        EiChaptr.addisen("АОВ","Управление и автоматика.");
        EiChaptr.addisen("ВК","Системы водоснабжения и водоотведения");
        EiChaptr.addisen("АВК","Управление и автоматика.");
        EiChaptr.addisen("СК","Спец. канализация");
        EiChaptr.addisen("ЭМ","Силовое электрооборудование");
        EiChaptr.addisen("ЭО","Электроосвещение.");
        EiChaptr.addisen("ТХ","Технология производства");
        EiChaptr.addisen("КИП","Технологический контроль.");
        EiChaptr.addisen("АК","Автоматизация комплексная.");
        EiChaptr.addisen("ТВК","Система телевизионного контроля");
        EiChaptr.addisen("ТС","Тепломеханические решения.");
        EiChaptr.addisen("ХС","Система холодоснабжения.");
        EiChaptr.addisen("СРК","Система радиационного контроля.");
        EiChaptr.addisen("ГВС","Система газо-воздухоснабжения");
        EiChaptr.addisen("САС","Система аварийной сигнализации.");
        EiChaptr.addisen("СУиК ЯМ","Система учета и контроля ЯМ,РВ и РАО");
        EiChaptr.addisen("СМИК","Система мониторинга инжененрных систем.");
        EiChaptr.addisen("СС","Сети связи.");
        EiChaptr.addisen("ПС","Пожарная сигнализация.");
        EiChaptr.addisen("СУДОС","СУДОС");
        EiChaptr.addisen("СОЭН","СОЭН");
        EiChaptr.addisen("СОТС","СОТС");
        EiChaptr.addisen("ИССФЗ","ИС СФЗ");
        //виды документов
        EiTypDoc.addisen("РЧ","Чертежи");
        EiTypDoc.addisen("ОЛ","Опросные листы");
        EiTypDoc.addisen("СП","Спецификации");
        EiTypDoc.addisen("ЗЗИ","Задание заводу изготовителю");
        EiTypDoc.addisen("ИТ","Исходные требования");
        EiTypDoc.addisen("РС","Расчеты");
        // Статус документа
        EiStag.addisen("А","Аннулирован");
        EiStag.addisen("К","Корректировка");
        assert prnq(" > exit beg $");

//        prnq("+++"); for (InfcElm value : lnGrOrgz.values()) prnq(value.print());
    }//beg
    // выкопировка кода из проекта - Analiz
    //инициализация списка содержания отчета отсортированной документации
    //элемент отсортированой документации описан классом class ElmRDS
    public static ArrayList<Elcol> wtu = new ArrayList<>(36);
    public static void inCol(){
        wtu.add(new Elcol(1,1200,5,"№ п.п.",4));//4
//        wtu.add(new Elcol(1200,"№№",1,14,4));
        wtu.add(new Elcol(1,2300,4,"№ РД",8));//3
        wtu.add(new Elcol(1,1000,5,"№ изм.",9));//4
        wtu.add(new Elcol(1,2600,8,"дата измен РД",14));//7
        wtu.add(new Elcol(1,2000,4,"Здание",7));//3
        wtu.add(new Elcol(1,1100,9,"Раздел",11));//8
        wtu.add(new Elcol(1,1000,9,"Вид",12));//8
        wtu.add(new Elcol(1,800,9,"Статус",13));//8
        wtu.add(new Elcol(1,10000,4,"наименование РД",10));//3
        wtu.add(new Elcol(1,2600,8,"дата  пред измен РД",15));//7
        wtu.add(new Elcol(1,1000,5,"№ пред изм.",16));//4
    }//inCol

 */

}//class IniGl
