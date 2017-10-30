package io.onceonly.db.dao.tpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.onceonly.db.dao.impl.TblIdNameVal;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.db.tbl.OOEntity;
import io.onceonly.util.OOAssert;
import io.onceonly.util.Tuple2;

public class UpdateTpl<T> {
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
	private Class<T> tpl;
	private T val;
	public UpdateTpl(Class<T> tpl) {
		this.tpl = tpl;
		try {
			this.val = this.tpl.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public T getVal() {
		return val;
	}

	public void setVal(T val) {
		this.val = val;
	}

	public static <E extends OOEntity<?>> Tuple2<String,List<Object>> getSettings(TableMeta tm,E entity,UpdateTpl<E> tpl) {
		OOAssert.warnning(entity != null && tpl != null,"Are you sure to update a null value?");
		TblIdNameVal<E> idNameValArgs = new TblIdNameVal<>(tm.getColumnMetas(),Arrays.asList(entity));
		E opt = tpl.getVal();
		TblIdNameVal<E> idNameValOpts = new TblIdNameVal<>(tm.getColumnMetas(),Arrays.asList(opt));

		idNameValArgs.dropColumns("rm");
		idNameValArgs.dropAllNullColumns();
		idNameValOpts.dropColumns("rm");
		idNameValOpts.dropAllNullColumns();
		Map<String,Tuple2<Object,Object>> nameOptVal = new HashMap<>();
		for(int i = 0; i < idNameValOpts.getValsList().size(); i++) {
			String name = idNameValOpts.getNames().get(i);
			Object id = idNameValOpts.getIdAt(i);
			nameOptVal.put(name, new Tuple2<Object,Object>(id,null));
		}
		for(int i = 0; i < idNameValArgs.getValsList().size(); i++) {
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
				int val = (int)tuple.a;
				switch(val) {
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
