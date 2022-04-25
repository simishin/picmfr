package qwr;

import qwr.model.SharSystem.FileType;
import qwr.model.SharSystem.RiProdject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static qwr.util.CollectUtl.prnq;
import static qwr.util.CollectUtl.prnt;

public class MainStart {
    public static void main(String[] args) {
        prnq("\n------- Start ----------");
//        prnq(BgFile.getNowData());
        Stream<String> hComSrtRead = RiProdject.parsingComandString(args);
        String[ ] filesg = hComSrtRead.toArray(String[]::new);


//        hComSrtRead.forEach(System.out::println);
        for (String js: filesg ) {
            if (Files.notExists(Path.of(js))) prnt("- \t");
            else prnt("Yes\t ");
            prnq(">> "+js);
        }
        prnq("Файл инициализации\t "+RiProdject.getjPtFlIni()+"\t"+RiProdject.isqExFlIni());
        prnq("Файл конфигурации\t "+RiProdject.getjPtFlCfg()+"\t"+RiProdject.isqExFlCfg());
        RiProdject.curTitul="atitulbeg";//наименование текущего проекта
        RiProdject.curName="anumebeg"; //код текущего проекта
        RiProdject.curCreat=456789;  //время создания текущего проекта

        /*
        Ступени начала работы программы:
        1) разбор командной строки. Поиск файла инициализации и файлов конфигурации.
        при просмотре директорий нахожу файлы ( пока без проверки на ссылочный тип(ярлыки))
        Список файлов конфигурации возвращаю в виде потока.
        2) загружаю файл инициализации. Если файл может отсутствовать или быть пустым.
        При этом данные по файлам конфигурации не актуализируются.
        3) просматриваю список из командной, сравниваю с общим списком и добавляю в общий.
        3.1) Если файл не существует и новый, то создаю и добавляю;
        3.2) Если файл существует но недоступен и новый, то добавляю и нулевыми данными (create=0);
        3.3) Если файл читается и новый, то читаю и добавляю;
        3.4) Если файл не существует и есть в списке, то ни чего не делаю;
        3.5) Если файл существует, недоступен и есть в списке, то ни чего не делаю;
        3.6) Если файл читается и есть в списке, то сравниваю данные:
        3.6.1) Если основные данные совпадают, то обновляю информацию;
        3.6.2) Если основные данные пустые в одном из файлов, то использую данные другого;
        3.6.3) Если основные данные разные, то использую более свежую информацию.
        4) сохраняю файл инициализации
        5) загружаю файл текущей конфигурации в полном объеме
         */

        FileType.ini.definePach(RiProdject.getjPtFlIni());
//        FileType.ini.load("");//загружаю из файла список проектов
        prnq("-(2)---------------");
        FileType.ini.loadLoc();//загружаю из файла список проектов
        RiProdject.printList("После загрузки из файла списка проектов");
        prnq("-(3)---------------");
        RiProdject.printDef();

        //дописываю в список проектов проекты, полученные из командной строки.
        // А для этого они должны быть проанализированы
        int za = RiProdject.addCfgFromComStrR(Arrays.stream(filesg));
        assert prnq("Добавлено проектов к списку: "+za);
        RiProdject.printList("После дописанния в общий список проектов из командной строки");
        assert RiProdject.list.size()>0: "~GrRecords:writPL~RiProdject.list.size=null";
        //Загрузка текущего проекта
        RiProdject.loadCurProdject();
        prnq("-50---------------");
        //охранение списка проектов
        FileType.ini.save();

        prnq("--End program normal--");
    }//main
}//class MainStart
