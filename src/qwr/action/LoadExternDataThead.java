package qwr.action;

import qwr.model.Base.Records;

import static qwr.util.CollectUtl.prnq;

public class LoadExternDataThead {
	public void start(){
		thread3.start();

		Thread thread6 = new Thread(LoadExternDataThead::threadMethod);
		thread6.start();
	}
	//Если новый класс для потока создавать накладно, то можно сделать всё в анонимном классе.
	Thread thread3 = new Thread() {
		@Override
		public void run(){
			assert prnq("$ LoadExternDataThead thread3 $");

				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
					assert prnq("$ LoadExternDataThead thread3 catch $");
				}
		}
	};
	Thread thread5 = new Thread(() -> {
		System.out.println("new Thread(() -> { })");
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
		System.out.println(5);
	});

	private static void threadMethod() {
	System.out.println("new Thread(Main::threadMethod)");
	try {
		Thread.sleep(100);
	} catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
	System.out.println(6);
	}
	//------------------------------------------------------------------------
/*
Дополнение локальных данных из внешних источников собранных в
Records.quNewExtElement.
Поскольку нужно ситуационное вмешательство пользователя делаю через демонов
 */
	public static void beginIntegrate(){
		Thread thrIntegrate = new Thread(LoadExternDataThead::workIntegrate);
		thrIntegrate.setDaemon(true);
		assert prnq("$ LoadExternDataThead.beginIntegrate");
		thrIntegrate.start();
	}//beginIntegrate
	private static void workIntegrate(){
		assert prnq("$ LoadExternDataThread.workIntegrate");
		while (true){
			if (Records.quNewExtElement.isEmpty()){
				try {
					Thread.sleep(3000*60);//3 минуты задержки
				} catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
				continue;
			}//если пусто
			Records x = Records.quNewExtElement.get(0);
//			switch (x.integrateExt()){//попытка добавить элемент. 1-добавлен, 0-требует вмешательства, -1-устарел
//				case -1: Records.quOldExtElement.add(x);
//					break;
//				case 0: Records.quUsrExtElement.add(x);
//					break;
//				default:
//			}//switch
			//---------------------------------------------------
			boolean q=true;
			for (Records y: x.linkList() ){
				if (x.key() == y.key()){
					q=false;
					break;
				}
				if (x.change()==y.key()){ break; }//добавляю замену
				if (x.key()==y.change()){
					Records.quOldExtElement.add(x);
					q=false;
					break;
				}
				if (x.change()==y.change()){
					Records.quUsrExtElement.add(x);
					q=false;
					break;
				}
			}//for
			if (q) x.linkList().add(x);
			Records.quNewExtElement.remove(0);
			try { Thread.sleep(100);//возможность старта другим потокам
			} catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
		}//while
	}//workIntegrate

}//class LoadExternDataThead
