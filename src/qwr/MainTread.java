package qwr;

import qwr.model.SharSystem.FileType;
import qwr.model.SharSystem.RiProdject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static qwr.util.CollectUtl.prnq;
import static qwr.util.CollectUtl.prnt;

public class MainTread {
	public static void main(String[] args) {
		prnq("\n------- Start ----------");
		Stream<String> hComSrtRead = RiProdject.parsingComandString(args);
		String[ ] filesg = hComSrtRead.toArray(String[]::new);
		for (String js: filesg ) {
			if (Files.notExists(Path.of(js))) prnt("- \t");
			else prnt("Yes\t ");
			prnq(">> "+js);
		}//for
		prnq("Файл инициализации\t "+RiProdject.getjPtFlIni()+"\t"+RiProdject.isqExFlIni());
		prnq("Файл конфигурации\t "+RiProdject.getjPtFlCfg()+"\t"+RiProdject.isqExFlCfg());
		RiProdject.curTitul="atitulbeg";//наименование текущего проекта
		RiProdject.curName="anumebeg"; //код текущего проекта
		RiProdject.curCreat=456789;  //время создания текущего проекта
		FileType.ini.definePach(RiProdject.getjPtFlIni());
		assert prnq("-(2)---------------");
	}//main
}//class MainTread
