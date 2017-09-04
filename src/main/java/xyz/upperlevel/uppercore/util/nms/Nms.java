package xyz.upperlevel.uppercore.util.nms;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;

public interface Nms {

	void initialize() throws Exception;

	default void load() {
		try {
			initialize();
		} catch (Exception e) {
			throw new UnsupportedVersionException(e);
		}
	}

	static void handleException(Exception e) {
		throw new UnsupportedVersionException(e);
	}
}
