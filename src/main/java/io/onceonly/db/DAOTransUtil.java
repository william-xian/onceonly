package io.onceonly.db;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.RowMapper;

public final class DAOTransUtil {
	private static final ConcurrentMap<Class<?>,Field[]> map = new ConcurrentHashMap<>();
	public static <T> RowMapper<T> defaultRowMapper(Class<T> clazz) {
		return new RowMapper<T>(){
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				return translate(clazz,rs);
			}
		};
	}
	private static <T> T translate(Class<T> clazz, ResultSet set) {
		T obj = null;
		try {
			obj = clazz.newInstance();
			map.get(clazz);
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				String fName = f.getName();
				f.setAccessible(true);
				Matcher matcher = Pattern.compile("[A-Z]").matcher(fName);
		        while(matcher.find()){
		        	fName = fName.replace(matcher.group(), "_"+ matcher.group().toLowerCase());
		        }
				if (f.getType().equals(String.class)) {
					f.set(obj, set.getString(fName));
					continue;
				}
				if (f.getType().equals(Integer.class)) {
					f.set(obj, set.getInt(fName));
					continue;
				}
				if (f.getType().equals(Long.class)) {
					f.set(obj, set.getLong(fName));
					continue;
				}
				if (f.getType().equals(Boolean.class)) {
					f.set(obj, set.getBoolean(fName));
					continue;
				}

				if (f.getType().equals(Byte.class)) {
					f.set(obj, set.getByte(fName));
					continue;
				}
				if (f.getType().equals(Short.class)) {
					f.set(obj, set.getShort(fName));
					continue;
				}
				if (f.getType().equals(Double.class)) {
					f.set(obj, set.getDouble(fName));
					continue;
				}
				if (f.getType().equals(Float.class)) {
					f.set(obj, set.getFloat(fName));
					continue;
				}
				if (f.getType().equals(BigDecimal.class)) {
					f.set(obj, set.getBigDecimal(fName));
					continue;
				}
				if (f.getType().equals(Date.class)) {
					f.set(obj, set.getDate(fName));
					continue;
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
}