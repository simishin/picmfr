package qwr.action;

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
}//class LoadExternDataThead
