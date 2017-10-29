package io.onceonly.db.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.onceonly.db.meta.TableMeta;
import io.onceonly.db.tbl.OOEntity;
import io.onceonly.util.OOAssert;
import io.onceonly.util.Tuple2;

/**
 * 当POJO作为模板时，特定值对应的含义如下
 */
public final class DefTmpl {
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

	/**
	 * 暂时只支持单目操作
	 */
	public static final int U_ADD = 1;
	public static final int U_SUB = 2;
	public static final int U_MUL = 3;
	public static final int U_DIV = 4;
	public static final int U_AND = 5;
	public static final int U_OR = 6;
	public static final int U_NOT = 7;
	public static final int U_XOR = 8;
	public static final int LEFT_SHIFT = 9;
	public static final int RIGHT_SHIFT = 10;
	
	
	public static <E extends OOEntity<?>> Tuple2<String,List<Object>> getSettings(TableMeta tm,E entity,E tpl) {
		OOAssert.warnning(entity != null && tpl != null,"Are you sure to update a null value?");
		TblIdNameVal<E> idNameValArgs = new TblIdNameVal<>(tm.getColumnMetas(),Arrays.asList(entity));
		TblIdNameVal<E> idNameValOpts = new TblIdNameVal<>(tm.getColumnMetas(),Arrays.asList(tpl));

		idNameValArgs.dropColumns("rm");
		idNameValArgs.dropAllNullColumns();
		idNameValOpts.dropColumns("rm");
		idNameValOpts.dropAllNullColumns();
		Map<String,Tuple2<Object,Object>> nameOptVal = new HashMap<>();
		for(int i = 0; i < idNameValOpts.valsList.size(); i++) {
			String name = idNameValOpts.getNames().get(i);
			Object opt = idNameValOpts.getIdAt(i);
			nameOptVal.put(name, new Tuple2<Object,Object>(opt,null));
		}
		for(int i = 0; i < idNameValArgs.valsList.size(); i++) {
			String name = idNameValArgs.getNames().get(i);
			Object arg = idNameValArgs.getIdAt(i);
			Tuple2<Object,Object> tuple = nameOptVal.get(name);
			if(tuple != null) {
				tuple.b = arg;
			}
		}
		StringBuffer sql = new StringBuffer();
		List<Object> args =  new ArrayList<>();
		for(String col:nameOptVal.keySet()) {
			Tuple2<Object,Object> tuple = nameOptVal.get(col);
			if(tuple.a instanceof Integer || tuple.a instanceof Long|| tuple.a instanceof Short) {
				int opt = (int)tuple.a;
				switch(opt) {
				case U_ADD:
					sql.append(String.format("%s = %s+(?)", col,col));
					args.add(tuple.b);
					break;
				case U_SUB:
					sql.append(String.format("%s = %s-(?)", col,col));
					args.add(tuple.b);
					break;
				case U_MUL:
					sql.append(String.format("%s = %s*(?)", col,col));
					args.add(tuple.b);
					break;
				case U_DIV:
					sql.append(String.format("%s = %s/(?)", col,col));
					args.add(tuple.b);
					break;
				case U_AND:
					sql.append(String.format("%s = %s&(?)", col,col));
					args.add(tuple.b);
					break;
				case U_OR:
					sql.append(String.format("%s = %s|(?)", col,col));
					args.add(tuple.b);
					break;
				case U_NOT:
					sql.append(String.format("%s = ~(%s)", col,col));
					break;
				case U_XOR:
					sql.append(String.format("%s = %s#(?)", col,col));
					args.add(tuple.b);
					break;
				case LEFT_SHIFT:
					sql.append(String.format("%s = %s<<(?)", col,col));
					args.add(tuple.b);
					break;
				case RIGHT_SHIFT:
					sql.append(String.format("%s = %s>>(?)", col,col));
					args.add(tuple.b);
					break;
					default:
				}
			}else {
				//TODO
			}
		}
		return null;
	}
	
}
