package com.di.kit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author d
 */
public class IdGenerator {
	private static final SimpleDateFormat df = new SimpleDateFormat(
			"yyMMddHHmmss");
	private static AtomicLong lastTime = getMinute();
	private static final long maxCount = 999998;
	private static AtomicInteger count = new AtomicInteger(1);
	private static AtomicBoolean run = new AtomicBoolean(false);
	static void start() {
		run.getAndSet(true);
		Calendar date = Calendar.getInstance();
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
				date.get(Calendar.DATE), date.get(Calendar.HOUR),
				date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
		long period = 1 * 1000;
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				AtomicLong newTime = getMinute();
				if (newTime.get() > lastTime.get()) {
					lastTime = newTime;
					count.set(1);
				}
			}
		}, date.getTime(), period);
	}

	public static final long nextId() throws RuntimeException {
		if (count.get() > maxCount) {
			throw new RuntimeException("产生的id超过最大限制");
		}
		if (!run.get()) {
			start();
		}
		return getId();
	}

	private static long getId() {
		return lastTime.get() + count.getAndIncrement();
	}

	private static AtomicLong getMinute() {
		return new AtomicLong(Long.valueOf(df.format(new Date()) + "000000"));
	}
}
