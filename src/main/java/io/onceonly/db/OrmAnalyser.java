package io.onceonly.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsShipping;
import cn.mx.app.entity.UserChief;
import cn.mx.app.entity.UserFriend;
import cn.mx.app.entity.UserProfile;
import cn.mx.app.entity.Wallet;
import cn.mx.app.view.JoinDemo;
import io.onceonly.db.annotation.Join;
import io.onceonly.db.annotation.VColumn;
import io.onceonly.db.annotation.VTable;
import io.onceonly.util.OOAssert;
import io.onceonly.util.Tuple2;

public class OrmAnalyser {

	private static final Map<Class<?>,TableMeta> vtable = new HashMap<>();
	
	public static void main(String[] args) {
		/** 第1步. 解析所有的 类  */
		analyseEntityColumns(Goods.class,GoodsDesc.class,GoodsOrder.class,GoodsShipping.class,UserChief.class,UserFriend.class,UserProfile.class,Wallet.class);
		
		/** 第2步. 解析虚表 */
		analyzeVTable(JoinDemo.class);
		TableMeta tm = vtable.get(JoinDemo.class);
		
		Set<String> params = new HashSet<>();
		params.add("buyerName");
		params.add("saled");
		params.add("buyerBalance");
		SqlTask sql = tm.generateSqlByParam(params);
		System.out.println(sql);
	}
	
	private static void analyseEntityColumns(Class<?> ...entities) {
		for(Class<?> entity:entities) {
			TableMeta tm = vtable.get(entity);
			if(tm == null){
				tm = new TableMeta();
				tm.setEntity(entity);
				vtable.put(entity, tm);
			}
			Set<String> columns = new HashSet<>();
			for(Field field:entity.getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if(column != null) {
					columns.add(field.getName());
				}
			}
			tm.setColumns(columns);
		}
	}
	
	private static void analyzeVTable(Class<?> ...entities) {
		for(Class<?> entity:entities) {
			TableMeta tm = vtable.get(entity);
			if(tm == null){
				tm = new TableMeta();
				tm.setEntity(entity);
				vtable.put(entity, tm);
			}
			VTable vt = JoinDemo.class.getAnnotation(VTable.class);
			tm.setVt(vt);;
			Map<String,Class<?>> aliasToEntity = analyseAliasMapping(vt);
			tm.setAliasToEntity(aliasToEntity);
			Map<String,String> columnToOriginal = analyseColumns(entity,aliasToEntity);
			tm.setColumnToOriginal(columnToOriginal);
			Map<String,Tuple2<String,String>> depends = analyseDepend(vt);
			tm.setDepends(depends);
		}
	}
	
	
	public static Map<String,String> analyseColumns(Class<?> clazz,Map<String,Class<?>> aliasToEntity) {
		Map<String,String> map = new HashMap<>();
		for(Field field:clazz.getDeclaredFields()) {
			VColumn vc = field.getAnnotation(VColumn.class);
			if(vc != null) {
				String name = null;
				if(vc.value()!= null && !vc.value().equals("")) {
					name = vc.value();
				}else if(!vc.name().equals("")) {
					name = vc.name();
				}
				if(name != null) {
					map.put(field.getName(), name);
				}else {
					String uniqueAlias = null;
					for(String alias:aliasToEntity.keySet()) {
						Class<?> entity = aliasToEntity.get(alias);
						if(vtable.get(entity).getColumns().contains(field.getName())) {
							if(uniqueAlias != null) {
								OOAssert.fatal("%s和%s 都包含字段:%s\n", uniqueAlias,alias,field.getName());
							}else {
								uniqueAlias = alias;	
							}
						}
					}
					if(uniqueAlias != null) {
						map.put(field.getName(), uniqueAlias+"." + field.getName());	
					}else {
						OOAssert.fatal("找不到 %s\n",field.getName());
					}
				}
			}
		}
		return map;
	}
	
	public static Map<String,Class<?>>	analyseAliasMapping(VTable vt) {
		Map<String,Class<?>> aliasToEntity = new HashMap<>();
		if(!vt.alias().equals("")) {
			aliasToEntity.put(vt.alias(), vt.mainTable());
		}else {
			aliasToEntity.put(vt.mainTable().getSimpleName(), vt.mainTable());
		}
		for(Join join:vt.joins()) {
			if(join.left() != void.class) {
				if(!join.alias().equals(""))
				{
					aliasToEntity.put(join.alias(), join.left());
				}else {
					aliasToEntity.put(join.left().getSimpleName(), join.left());
				}
			}else if(join.right() != void.class) {
				if(!join.alias().equals(""))
				{
					aliasToEntity.put(join.alias(), join.right());
				}else {
					aliasToEntity.put(join.right().getSimpleName(), join.right());
				}
			}
			if(!join.tAlias().equals("") && join.target() != void.class) {
				aliasToEntity.put(join.tAlias(), join.target());
			}else if(join.target() != void.class){
				aliasToEntity.put(join.target().getSimpleName(), join.target());
			}
		}
		return aliasToEntity;
	}
	
	public static Map<String,Tuple2<String,String>> analyseDepend(VTable vt) {
		Map<String,Tuple2<String,String>> map = new HashMap<>();
		for(Join join:vt.joins()) {
			String tAlias = vt.alias();
			if(!join.tAlias().equals("")) {
				tAlias = join.tAlias();
			}else if(join.target() != void.class) {
				tAlias = join.target().getSimpleName();
			}else {
				tAlias = vt.alias();
			}
			String fAlias = null;
			if(!join.alias().equals("")) {
				fAlias = join.alias();
			}else {
				if(join.left() != void.class) {
					fAlias = join.left().getSimpleName();
				}else if(join.right() != void.class) {
					fAlias = join.right().getSimpleName();
				}	
			}
			if(fAlias != null) {
				Tuple2<String,String> otherDepend = map.get(fAlias);
				if(otherDepend != null && !otherDepend.equals(tAlias)) {
					OOAssert.fatal("%s 依赖关系两个不同的表：%s, %s",fAlias, otherDepend, tAlias);
				}else {
					map.put(fAlias,new Tuple2<String,String>(tAlias,join.cnd()));	
				}
			}else {
				OOAssert.fatal("没有别名引用类，也没有实体类，错误的配置");
			}
		}
		return map;
	}

}
