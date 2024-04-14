package test.rest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is CDI injected using @Inject annotation.
 * See MyApplication.java and OrderService.java
 */
public class MyService {
	private AtomicInteger counter = new AtomicInteger(0);
	public MyService() {
		//System.out.println("**"+this.getClass().getName()+","+System.currentTimeMillis());
	}
	
	public int getCounter() {
		return counter.incrementAndGet();
	}
}
