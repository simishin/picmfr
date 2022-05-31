package qwr.thread;

import qwr.model.SharSystem.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static qwr.util.CollectUtl.prnq;

public class ReadExtDbf extends Thread {
	static List<ReadFile> uActSmal = new ArrayList<ReadFile>();//12 min
	static List<ReadFile> uPasSmal = new ArrayList<ReadFile>();//60 min
	static List<ReadFile> uActHigh = new ArrayList<ReadFile>();//3  min
	static List<ReadFile> uPasHigh = new ArrayList<ReadFile>();//30 min
	static long	hActSmal;	//раннее время следующего просмотра списка
	static long	hPasSmal;
	static long	hActHigh;
	static long	hPasHigh;
	static int	kActSmal;	//указатель на элемент списка просмотра
	static int	kPasSmal;
	static int	kActHigh;
	static int	kPasHigh;
	static final int hCalm = 2*60*60*1000; //время штиля
	/**
	 * Чтение внешних данных
	 */
	public void run(){
		List<ReadFile> uTmp = new ArrayList<ReadFile>();
		while(!isInterrupted()){
			long hCur = System.currentTimeMillis();	//текущее время
			//System.out.println("Loop " );
			if (hActHigh<hCur && !uActHigh.isEmpty()){//есть файлы для сканирования 3  min
				if (kActHigh<0 || kActHigh>=uActHigh.size()){//проверяю указатель
					kActHigh = 0; //корректирую указатель
					//ищу ближайшее обновление
					hActHigh=uActHigh.get(0).jnext;
					uTmp.clear();
					for (ReadFile x: uActHigh ) {
						if (hActHigh>x.jnext) hActHigh=x.jnext;//ищу ближайшее обновление
						//Проверяю на снижение активности
						if (hCur> (x.jpast+hCalm)){ uTmp.add(x); }
					}//for
					if (!uTmp.isEmpty()){//Реагирую на снижение активности
						uPasHigh.addAll(uTmp);
						uActHigh.removeAll(uTmp);
//						uTmp.clear();
					}
				} else kActHigh++;
				//сканирование отдельного файла (истина, если файл изменился)
				scanExtDbf(uActHigh.get(kActHigh));//файл изменился
				uActHigh.get(kActHigh).jnext=hCur+(3*60*1000);//следующее время сканирования
			}//if-----------------------------------------------------------------------------
			if (hActSmal<hCur && !uActSmal.isEmpty()){//есть файлы для сканирования 12 min
				if (kActSmal<0 || kActSmal>=uActSmal.size()){//проверяю указатель
					kActSmal = 0; //корректирую указатель
					//ищу ближайшее обновление
					hActSmal=uActSmal.get(0).jnext;
					uTmp.clear();
					for (ReadFile x: uActSmal ) {
						if (hActSmal>x.jnext) hActSmal=x.jnext;//ищу ближайшее обновление
						//Проверяю на снижение активности
						if (hCur> (x.jpast+hCalm)){ uTmp.add(x); }
					}//for
					if (!uTmp.isEmpty()){//Реагирую на снижение активности
						uPasSmal.addAll(uTmp);
						uActSmal.removeAll(uTmp);
//						uTmp.clear();
					}
				} else kActSmal++;
				//сканирование отдельного файла (истина, если файл изменился)
				scanExtDbf(uActSmal.get(kActSmal));//файл изменился
				uActSmal.get(kActSmal).jnext=hCur+(12*60*1000);//следующее время сканирования
			}//if--------------------------------------------------------------------------------
			if (hPasHigh<hCur && !uPasHigh.isEmpty() ){//есть файлы для сканирования 30 min
				if (kPasHigh<0 || kPasHigh>=uPasHigh.size()){//проверяю указатель
					kPasHigh = 0; //корректирую указатель
					//ищу ближайшее обновление
					hPasSmal=uPasHigh.get(0).jnext;
					uTmp.clear();
					for (ReadFile x: uPasHigh ) {
						if (hPasHigh>x.jnext) hPasHigh=x.jnext;//ищу ближайшее обновление
					}//for
				} else kPasHigh++;
				//сканирование отдельного файла (истина, если файл изменился)
				if (scanExtDbf(uPasHigh.get(kPasHigh))) {//файл изменился
					uPasHigh.get(kPasHigh).jnext=hCur+(3*60*1000);//следующее время сканирования
					uActHigh.add(uPasHigh.get(kPasHigh));//переписываю
					uPasHigh.remove(kPasHigh);//удаляю элемент
				} else uPasHigh.get(kPasHigh).jnext=hCur+(12*60*1000);//следующее время сканирования
			}//if -------------------------------------------------------------------------------
			if (hPasSmal<hCur && !uPasSmal.isEmpty()){//есть файлы для сканирования 60 min
				if (kPasSmal<0 || kPasSmal>=uPasSmal.size()){//проверяю указатель
					kPasSmal = 0; //корректирую указатель
					//ищу ближайшее обновление
					hPasSmal=uPasSmal.get(0).jnext;
					uTmp.clear();
					for (ReadFile x: uPasSmal ) {
						if (hPasSmal>x.jnext) hPasSmal=x.jnext;//ищу ближайшее обновление
					}//for
				} else kPasSmal++;
				//сканирование отдельного файла (истина, если файл изменился)
				if (scanExtDbf(uPasSmal.get(kPasSmal))) {//файл изменился
					uPasSmal.get(kPasSmal).jnext=hCur+(30*60*1000);//следующее время сканирования
					uActSmal.add(uPasSmal.get(kPasSmal));//переписываю
					uPasSmal.remove(kPasSmal);//удаляю элемент
				} else uPasSmal.get(kPasSmal).jnext=hCur+(60*60*1000);//следующее время сканирования
			}//if

			hCur = 100 - System.currentTimeMillis()+hCur; // расчет времени задержки
			try{
				Thread.sleep(hCur < 1 ? 3 : hCur);
			}
			catch(InterruptedException e){
				System.out.println(getName() + " has been interrupted");
				//interrupt();    // повторно сбрасываем состояние
				break;  // выход из цикла
			}//catch
		}//while
		System.out.printf("%s finished... \n", Thread.currentThread().getName());
	}//run()

	/**
	 * Определение доступности файла и вызов соответствующего обработчика из enum FileType
	 * Вызывается при просмотре списков uActSmal,uPasSmal,uActHigh,uPasHigh из данного класса
	 * В списках находятся только существующие файла, но которые могут быть не доступны.
	 * @param readFile параметры файла для анализа
	 * @return истина, если файл изменился
	 */
	private boolean scanExtDbf(ReadFile readFile) {
		assert readFile!=null: "! readFile = null !";
		assert readFile.jpath != null: "readFile.jpath is NULL";
		if (readFile.jpath.isBlank()) return false;
		Path path = Paths.get(readFile.jpath);
		if (!Files.exists(path)) {prnq("ERROR file is no exists! "+path); return false;}
		if (!Files.isReadable(path)){prnq("ERROR not read file! "+path); return false;}
		//проверяю системное время создания и модификации файла
		long change;
		try {
			change = (Files.getLastModifiedTime(path).toMillis());
		} catch (IOException e) {
			change = 0;
			e.printStackTrace();
		}
		if (change == readFile.jpast){prnq("file not modifay "+path); return false;}
		readFile.jpast = change;

//		File fpath = new File(readFile.jpath);
//		long jModiFile = fpath.lastModified();
//		if (jModiFile == readFile.jpast){prnq("file not modifay "+path); return false;}
//		readFile.jpast = jModiFile;


			//определяю тип файла на основании enum FileType
		String jext = readFile.jpath.substring(readFile.jpath.lastIndexOf(".")+1);
		try { FileType.valueOf(jext); } //если не существует значение
		catch (Exception e) {
			assert prnq("Not define FileType {"+jext+"}");
			return false;
		}
		return FileType.valueOf(jext).loadExtDbf(path);//вызываю чтение содержимого файла
	}//scanExtDbf

	public class ReadFile{
		String	jpath;	//путь и имя файла
		long	jnext;	//следующее время просмотра
		long	jpast;	//последнее время изменения
		long	jcreat;	//время создания
	}//class ReadFile
}//class ReadExtDbf
