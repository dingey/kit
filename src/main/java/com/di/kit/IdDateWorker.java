package com.di.kit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IdDateWorker {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private String prefix = "";
	private String lastTime = "";
	private Integer sequence = 0;
	private static IdDateWorker work = new IdDateWorker("");
	public IdDateWorker(String prefix) {
		this.prefix = prefix;
	}

	public String now() {
		return prefix + sdf.format(new Date());
	}

	public static IdDateWorker singleton() {
		return work;
	}

	public String nextId() {
		String now = now();
		if (lastTime.equals(now)) {
			sequence++;
		} else {
			lastTime = now;
			sequence = 0;
		}
		return lastTime + String.format("%05d", sequence);
	}

	public String nextHexId() {
		return Long.toHexString(Long.valueOf(nextId()));
	}

	public static synchronized String nextSyncId() {
		return singleton().nextId();
	}

	public static String nextSyncId(int n) {
		return Conversion.fromDecimal(Long.valueOf(nextSyncId()), n);
	}
}
