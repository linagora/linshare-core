package org.linagora.linshare.core.domain.vo;

import java.math.BigInteger;

import org.apache.tapestry5.beaneditor.NonVisual;

public enum FileSizeVo {
	@NonVisual
	NONE(Integer.MAX_VALUE),
	BYTE(0),
    KILO(1),
    MEGA(2),
    GIGA(3);

	private int pow;

	private FileSizeVo(int pow) {
		this.pow = pow;
	}

	public long getSiSize(final long size) {
		return size * BigInteger.valueOf(1000).pow(pow).longValue();
	}

	public static FileSizeVo fromInt(final int value) {
		for (FileSizeVo unit : values()) {
			if (unit.pow == value) {
				return unit;
			}
		}
		throw new IllegalArgumentException(
				"Doesn't match an existing FileSizeUnit");
	}
}
