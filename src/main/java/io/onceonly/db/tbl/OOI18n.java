package io.onceonly.db.tbl;

import io.onceonly.annotation.I18nCfg;
import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.OId;
import io.onceonly.db.annotation.Tbl;
import io.onceonly.util.OOUtils;

@Tbl
public class OOI18n {
	@OId
    @Col(size=64,nullable = false)
	private String id;
	@Col(size=255,nullable=false)
	private String name;
	@Col(size=32,nullable=true)
	private String val;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String toString() {
		return OOUtils.toJSON(this);
	}
	
	public static String msgId(String lang,String format) {
		return "msg/"+lang+"_"+OOUtils.encodeMD5(format);		
	}
	
	public static String constId(String lang,Class<?> clazz,String fieldName) {
		I18nCfg group = clazz.getAnnotation(I18nCfg.class);
		return "const/" + group.value()+ "_"+ clazz.getSimpleName() + "_" + fieldName;
	}
}
