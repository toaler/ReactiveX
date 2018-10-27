package org.bpt.reactivex;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rx.Observable;

public class RxJavaTest {

	@Test
	public void just() {
		var observer = Observable.just("foo");
		observer.subscribe(e -> assertThat(e).isEqualTo("foo"));
	}

	@Test
	public void range() {
		var counter = new AtomicLong();
		Observable.range(1, 100).map(e -> e * 2).subscribe(e -> counter.incrementAndGet());
		assertThat(counter.get()).isEqualTo(100);

	}

	@Test(expected = rx.exceptions.OnErrorNotImplementedException.class)
	public void error() {
		Observable.range(1, 100).map(e -> {
			if (e == 2)
				throw new RuntimeException("Something went wrong");
			return e * 2;
		}).subscribe();

	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
