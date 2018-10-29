package org.bpt.reactivex;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.schedulers.Schedulers;

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

	@Test
	public void onError() {
		final CountDownLatch latch = new CountDownLatch(1);

		Observable.range(1, 100).map(e -> {
			if (e == 2)
				throw new RuntimeException("Something went wrong");
			return e * 2;
		}).subscribe(e -> System.out.println(e), err -> latch.countDown());

		assertThat(latch.getCount()).isEqualTo(0);
	}

	@Test
	public void separateThreadsMerged() {
		var a = Observable.create(s -> {
			new Thread(() -> {
				s.onNext("one");
				s.onNext("two");
				s.onCompleted();
			}).start();
		});

		var b = Observable.create(s -> {
			new Thread(() -> {
				s.onNext("three");
				s.onNext("four");
				s.onCompleted();
			}).start();
		});

		var c = Observable.merge(a, b);

		c.subscribe(e -> {
			System.out.println("merge: " + e);
		});

	}

	@Test
	public void zip() {
		var a = Observable.create(s -> {
			new Thread(() -> {
				s.onNext("one");
				s.onNext("two");
				s.onCompleted();
			}).start();
		});

		var b = Observable.create(s -> {
			new Thread(() -> {
				s.onNext("three");
				s.onNext("four");
				s.onCompleted();
			}).start();
		});

		Observable<String> c = Observable.zip(a, b, (x, y) -> {
			return String.format("%s %s", x, y);
		});

		c.subscribe(e -> {
			System.out.println("zip: " + e);
		});

	}

	@Test
	public void single() {
		// merge a & b into an Observable stream of 2 values
		Observable<String> a_merge_b = getDataA().mergeWith(getDataB());

		// not how two Single's are merged into an Observable. This could result in an
		// emission of [A, B] or [B, A], depending on which completes first.
	}

	private static Single<String> getDataA() {
		return Single.<String>create(o -> {
		}).subscribeOn(Schedulers.io());
	}

	private static Single<String> getDataB() {
		return Single.just("DataB").subscribeOn(Schedulers.io());
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
