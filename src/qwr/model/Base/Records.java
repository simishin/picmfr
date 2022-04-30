package qwr.model.Base;

import java.time.Instant;
import java.util.List;

public interface Records {
	long getKey(); //ключ записи

	/**
	 * Поиск свободного кода для использования его в качестве ключа новой записи
	 * @param list list список записей для которого готовится новый элемент
	 * @param keyUser код пользователя создающего новый элемент для списка
	 * @return код для генерации нового элемента
	 */
	static long makingKey(List<Records> list, int keyUser){
		long u = (Instant.now().getEpochSecond()-PublStat.begSecund) & 0xFFFFFFFF;
		long y = (keyUser<<16) | u;
		if (list.isEmpty()){ return y; }
		boolean qsteep;
		while (true) {
			qsteep = false;
			for (Records x : list) if (x.getKey()==y ){ qsteep=true; break; }
			if (qsteep) y--;
			else return y;
		}//while
	}//makingKey
}//interface Records
