package org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters;

public class IntegerParameterDto extends ParameterDto<Integer> {

	public IntegerParameterDto() {
		super();
	}

	public IntegerParameterDto(boolean hidden, boolean readonly, NestedParameterDto<Integer> defaut,
			NestedParameterDto<Integer> maximum) {
		super(hidden, readonly, defaut, maximum);
	}

	@Override
	public String toString() {
		return "IntegerParameterDto [hidden=" + hidden + ", readonly=" + readonly + ", defaut=" + defaut + ", maximum="
				+ maximum + "]";
	}

}
