package io.onceonly.db.dao;

public class SelectTpl<E> {
	public static final boolean SHOW_BOOL = true;
	public static final byte SHOW_BYTE = 1;
	public static final short SHOW_SHORT = 1;
	public static final int SHOW_INT = 1;
	public static final long SHOW_LONG = 1L;
	public static final float SHOW_FLOAT = 1F;
	public static final double SHOW_DOUBLE = 1D;
	public static final char SHOW_CHAR = '1';
	public static final String SHOW_STR = "1";
	
	public static final byte COUNT_BYTE = 2;
	public static final short COUNT_SHORT = 2;
	public static final int COUNT_INT = 2;
	public static final long COUNT_LONG = 2L;
	public static final float COUNT_FLOAT = 2F;
	public static final double COUNT_DOUBLE = 2D;
	public static final char COUNT_CHAR = '2';
	public static final String COUNT_STR = "2";

	public static final byte DISTINCT_BYTE = 3;
	public static final int DISTINCT_INT = 3;
	public static final long DISTINCT_LONG = 3;
	public static final char DISTINCT_C = '3';
	public static final String DISTINCT_S = "3";
	
	public static final int SUM_INT = 2;
	public static final long SUM_LONG = 2;
	
	public static final long AVG_DECIMAL = 3;
	public static final long AVG_LONG = 3;
	public static final long AVG_INT = 3;
	
	public static final String CONCAT_AS_STR = "S";
	public static final String CONCAT_AS_ARR = "A";

	public static final byte ORDER_BY_ASC_B = 'A';
	public static final byte ORDER_BY_DESC_B = 'D';
	public static final char ORDER_BY_ASC_C = 'A';
	public static final char ORDER_BY_DESC_C = 'D';
	public static final int ORDER_BY_ASC_INT = 1;
	public static final int ORDER_BY_DESC_INT = 2;
	public static final long ORDER_BY_ASC_LONG = 1L;
	public static final long ORDER_BY_DESC_LONG = 2L;
	public static final String ORDER_BY_ASC_S = "A";
	public static final String ORDER_BY_DESC_S = "D";

}
