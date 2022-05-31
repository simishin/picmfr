package qwr.util;

import qwr.model.SharSystem.GrRecords;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static qwr.util.CollectUtl.prnq;
import static qwr.util.CollectUtl.sepr;

/*
Класс для примитивных операций с файловой системой
с дальнейшем расчетом на многопоточность
в данный класс выношу операции, которые раньше собирал в FileType
 */
public class FileOperations {
	/**
	 * Чтение файла внешних данных. Вызывается из ReadExtDbf.scanExtDbf
	 * Проверка доступности файла выполнена в вызывающем методе.
	 * @param path ссылка на поток читаемого файла, создана в вызывающем методе
	 * @return истина, если файл изменился
	 */
	public boolean loadExtDbf(Path path) {
		AtomicBoolean change = new AtomicBoolean(false);
		try {
			Stream.of (Files.readAllLines(path, StandardCharsets.UTF_8)).forEach(readsrt -> {
				assert prnq("Load string "+readsrt.size()+" from "+path);
				for (String str: readsrt){//читаю последовательно строки файла
					if (str.length()<5) {assert prnq("Error length");continue;}
					String[] words =str.split(sepr);//создаю массив значений
					//здесь возможно проверить на конец и тогда break, а в readRecord возвращать
					// флаг изменений
					try { if (GrRecords.valueOf(words[0]).endLoad()) break; }//если не существует значение
					catch(Exception e){ assert prnq("Not define Word {"+words[0]+"}"); continue;}
					if(GrRecords.valueOf(words[0]).readRecordExt(words,0)) change.set(true);;
				}//for readsrt
			} );
		}catch(IOException e){e.printStackTrace();}
		return change.get();
	}//loadExtDbf
}//class FileOperations
