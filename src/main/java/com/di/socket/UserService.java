package com.di.socket;

import java.util.Random;

/**
 * @author d
 */
public class UserService {
	public String say(String n) {
		return n + new Random().nextInt(100);
	}
}
