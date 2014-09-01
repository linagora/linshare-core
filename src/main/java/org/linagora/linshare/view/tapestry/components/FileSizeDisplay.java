package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.FileSizeVo;

public class FileSizeDisplay {

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private Long value;

	@Property
	private String display;

	private FileSizeVo unit;

	@Inject
	private Messages messages;

	public void setupRender() {
		if (value == null) {
			display =  "";
			unit = FileSizeVo.NONE;
		} else {
			final int mul = 1000;
			if (value < mul) {
				display = "" + value;
				unit = FileSizeVo.BYTE;
			} else {
				int exp = (int) (Math.log(value) / Math.log(mul));
				display = String.format("%.1f ", value / Math.pow(mul, exp));
				unit = FileSizeVo.fromInt(exp);
			}
		}
		display += messages.get("FileSizeVo." + unit.name());
	}
}
