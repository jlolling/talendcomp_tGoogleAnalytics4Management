package de.jlo.talendcomp.google.analytics.ga4.v1;

import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.NumericValue;
import com.google.analytics.data.v1beta.Filter.NumericFilter;
import com.google.analytics.data.v1beta.Filter.StringFilter;
import com.google.analytics.data.v1beta.Filter.NumericFilter.Operation;
import com.google.analytics.data.v1beta.Filter.StringFilter.MatchType;

public class FilterExpressionBuilder {

	/* Filter operators for String and Numeric filters */
	
	private static final String FILTER_OP_EXACT        = "==";
	private static final String FILTER_OP_EXACT_NOT    = "!=";
	
	private static final String STRING_FILTER_OP_BEGINS_WITH  = "=^";
	private static final String STRING_FILTER_OP_ENDS_WITH    = "=$";
	private static final String STRING_FILTER_OP_REGEX_INCL   = "=~";
	private static final String STRING_FILTER_OP_REGEX_EXCL   = "!~";
	private static final String STRING_FILTER_OP_CONTAINS     = "=@";
	private static final String STRING_FILTER_OP_CONTAINS_NOT = "!@";
	
	public static FilterExpression buildStringFilterExpression(String oneFilterTerm) throws Exception {
		if (oneFilterTerm != null && oneFilterTerm.trim().isEmpty() == false) {
			String operand1 = null;
			String operand2 = null;
			FilterExpression fe = null;
			int posOp = -1;
			posOp = oneFilterTerm.indexOf(FILTER_OP_EXACT);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setStringFilter(StringFilter.newBuilder()
								.setMatchType(MatchType.EXACT)
								.setCaseSensitive(false)
								.setValue(operand2)))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(FILTER_OP_EXACT_NOT);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setNotExpression(FilterExpression.newBuilder()
							.setFilter(Filter.newBuilder()
								.setFieldName(operand1)
								.setStringFilter(StringFilter.newBuilder()
									.setMatchType(MatchType.EXACT)
									.setCaseSensitive(false)
									.setValue(operand2))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(STRING_FILTER_OP_BEGINS_WITH);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setStringFilter(StringFilter.newBuilder()
								.setMatchType(MatchType.BEGINS_WITH)
								.setCaseSensitive(false)
								.setValue(operand2)))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(STRING_FILTER_OP_ENDS_WITH);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setStringFilter(StringFilter.newBuilder()
								.setMatchType(MatchType.ENDS_WITH)
								.setCaseSensitive(false)
								.setValue(operand2)))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(STRING_FILTER_OP_REGEX_INCL);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setStringFilter(StringFilter.newBuilder()
								.setMatchType(MatchType.FULL_REGEXP)
								.setCaseSensitive(false)
								.setValue(operand2)))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(STRING_FILTER_OP_REGEX_EXCL);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setNotExpression(FilterExpression.newBuilder()
							.setFilter(Filter.newBuilder()
								.setFieldName(operand1)
								.setStringFilter(StringFilter.newBuilder()
									.setMatchType(MatchType.FULL_REGEXP)
									.setCaseSensitive(false)
									.setValue(operand2))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(STRING_FILTER_OP_CONTAINS);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setStringFilter(StringFilter.newBuilder()
								.setMatchType(MatchType.CONTAINS)
								.setCaseSensitive(false)
								.setValue(operand2)))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(STRING_FILTER_OP_CONTAINS_NOT);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setNotExpression(FilterExpression.newBuilder()
							.setFilter(Filter.newBuilder()
								.setFieldName(operand1)
								.setStringFilter(StringFilter.newBuilder()
									.setMatchType(MatchType.CONTAINS)
									.setCaseSensitive(false)
							.setValue(operand2))))
						.build();
				return fe;
			}
			if (posOp == -1) {
				throw new Exception("Unknown or missing operator in dimension filter: " + oneFilterTerm);
			}
		}
		return FilterExpression.newBuilder().build(); // dummy
	}

	/* Filter operators for Numeric filters */

	private static final String NUMERIC_FILTER_OP_NOT_EQUALS = "<>";
	private static final String NUMERIC_FILTER_OP_LESS = "<";
	private static final String NUMERIC_FILTER_OP_LESS_OR_EQUALS = "<=";
	private static final String NUMERIC_FILTER_OP_GREATER = ">";
	private static final String NUMERIC_FILTER_OP_GREATER_OR_EQUALS = ">=";

	public static FilterExpression buildNumericFilterExpression(String oneFilterTerm) throws Exception {
		if (oneFilterTerm != null && oneFilterTerm.trim().isEmpty() == false) {
			String operand1 = null;
			String operand2 = null;
			FilterExpression fe = null;
			int posOp = -1;
			posOp = oneFilterTerm.indexOf(FILTER_OP_EXACT);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setNumericFilter(
								NumericFilter.newBuilder()
									.setOperation(Operation.EQUAL)
									.setValue(
										NumericValue.newBuilder()
											.setDoubleValue(Double.parseDouble(operand2)))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(FILTER_OP_EXACT_NOT);
			if (posOp == -1) {
				posOp = oneFilterTerm.indexOf(NUMERIC_FILTER_OP_NOT_EQUALS);
			}
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setNotExpression(
							FilterExpression.newBuilder()
								.setFilter(Filter.newBuilder()
									.setFieldName(operand1)
									.setNumericFilter(
										NumericFilter.newBuilder()
											.setOperation(Operation.EQUAL)
											.setValue(
													NumericValue.newBuilder()
														.setDoubleValue(Double.parseDouble(operand2))))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(NUMERIC_FILTER_OP_LESS_OR_EQUALS);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setNumericFilter(NumericFilter.newBuilder()
								.setOperation(Operation.LESS_THAN_OR_EQUAL)
								.setValue(NumericValue.newBuilder()
										.setDoubleValue(Double.parseDouble(operand2)))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(NUMERIC_FILTER_OP_LESS);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 1).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setNumericFilter(NumericFilter.newBuilder()
								.setOperation(Operation.LESS_THAN)
								.setValue(NumericValue.newBuilder()
										.setDoubleValue(Double.parseDouble(operand2)))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(NUMERIC_FILTER_OP_GREATER_OR_EQUALS);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 2).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setNumericFilter(NumericFilter.newBuilder()
								.setOperation(Operation.GREATER_THAN_OR_EQUAL)
								.setValue(NumericValue.newBuilder()
										.setDoubleValue(Double.parseDouble(operand2)))))
						.build();
				return fe;
			}
			posOp = oneFilterTerm.indexOf(NUMERIC_FILTER_OP_GREATER);
			if (posOp > 0) {
				operand1 = oneFilterTerm.substring(0, posOp).trim();
				operand2 = oneFilterTerm.substring(posOp + 1).trim();
				fe = FilterExpression.newBuilder()
						.setFilter(Filter.newBuilder()
							.setFieldName(operand1)
							.setNumericFilter(NumericFilter.newBuilder()
								.setOperation(Operation.GREATER_THAN)
								.setValue(NumericValue.newBuilder()
										.setDoubleValue(Double.parseDouble(operand2)))))
						.build();
				return fe;
			}
			if (posOp == -1) {
				throw new Exception("Unknown or missing operator in metric filter: " + oneFilterTerm);
			}
		}
		return FilterExpression.newBuilder().build(); // dummy
	}

}
