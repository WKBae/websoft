package com.dbdbdeep.websoft.util;

public class Util {

	public static String joinPath(String base, String path, String name) {
		if ("/".equals(path)) {
			return base + '/' + name;
		} else {
			return base + path + '/' + name;
		}
	}

}
