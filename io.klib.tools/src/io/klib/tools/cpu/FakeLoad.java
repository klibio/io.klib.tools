package io.klib.tools.cpu;

public class FakeLoad {

	public void createThread(String name, int sleep) {
		System.out.println("Thread " + name + " started with sleep of " + sleep);
		new Thread(name) {
			@Override
			public void run() {
				for (;;) {
					// create cpu load
					long factorial = 100;
					for (long i = (10 - 1); i > 1; i--) {
						factorial = factorial * i;
					}

					try {
						Thread.sleep(sleep);
					} catch (NumberFormatException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

}
