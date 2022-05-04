package qwr.model.Base;
/**
 *  новый подход к формировапнию ключей записей о 290422
 *   0x3FFFF=262143=повторяемость 29,9 лет  при ежечасовом создании проекта
 *    0x3FFF= 16383 дней = повтряемость 44.88 лет при создании пользователя
 *    При формировании ключа секунды обрезаются сдвигом и наложением маски
 *    исходя из коэффициента цикличности примерно в 30 лет или 15 или 7 для пользователя
 *    Возможноприменение коэффициента цикличности для основного ключа записей
 *    расчет: сдвиг определяет шаг в секундах, а маска (0хFFF) цикличность как произведения
 *    шага на максимальное значение маски.
 *    Цикличность проекта 29.9 лет, пользователя 7 лет
 *    Если происходит наложение на ключ, тогда проверяю значение минус один для проекта
 *    и пользоватеоля. Для записей - положительное приращение. И так до положительного рашения.
 *  0x3FFF= 16383
 *  7 лет= 2555 дней примерно 4 часа; 4 часа = 14400 секунд; ближайщее 16384 сдвиг на 15
 * тогда 3FFF.FFFF = 1073741823 секунд = 34 года цикл повторения
 * 0xFFFF = 65536c = 18.2 часа
 * int = 4 байт = 0xFF FF FF FF
 */

import java.time.Instant;
import java.util.List;

public interface Records {
	long begSecund = 1610643835L;//2021-01-15T00:03 точка отсчета
	long getKey(); //ключ записи для метода генерации ключа makingKey
	/**
	 * Поиск свободного кода для использования его в качестве ключа новой записи
	 * @param list list список записей для которого готовится новый элемент
	 * @param keyUser код пользователя создающего новый элемент для списка
	 * @return код для генерации нового элемента
	 */
	static long makingKey(List<Records> list, int keyUser){
		long u = (Instant.now().getEpochSecond()-begSecund) & 0xFFFFFFFF;
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

	/**
	 * ключ не мможет быть нулевым!!! нужно обойти эту ситуацию, зарезервировав
	 * для самого первого пользователя в проекте
	 * @param list
	 * @param keyUser
	 * @return
	 */
	static int makingKeyUser(List<Records> list, int keyUser){
		int u = (int) ((Instant.now().getEpochSecond()-begSecund) & 0x3FFFC000);
		//проверяю и создаю нулевого пользователя проекта
		int y = (keyUser<<16) | (u >>14);
		if ( list.isEmpty()){ return y; }
		boolean qsteep;
		while (true) {
			qsteep = false;
			for (Records x : list) if (x.getKey()==y ){ qsteep=true; break; }
			if (qsteep) y--;
			else return (int) y;
			if ( (y & 0x3FFF) ==0 ) y--;//пропускаю нулевое значение
		}//while
	}//makingKey

}//interface Records
