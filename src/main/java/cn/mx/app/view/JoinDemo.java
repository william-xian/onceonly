package cn.mx.app.view;

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
import io.onceonly.db.annotation.Join;
import io.onceonly.db.annotation.VColumn;
import io.onceonly.db.annotation.VTable;

/**
 * @author Administrator
 *
 */
@VTable(
	mainTable = UserChief.class,alias ="buyer",
	joins = {
		@Join(left=Wallet.class,alias="uw"),
		@Join(left=UserProfile.class,alias="up"),
		@Join(left=GoodsOrder.class,alias="go",cnd="go.userId = buyer.id and go.del=false"),
		@Join(left=GoodsShipping.class,alias="gs",tAlias="go",cnd="gs.goodsOrderId=go.id and gs.del = false"),
		@Join(left=UserChief.class,alias="receiver",tAlias="gs",cnd="gs.receiverId=receiver.id"),
		@Join(left=Goods.class,alias="g",tAlias="go",cnd = "go.goodsId=g.id and g.del=false g.genre = 1"),
		@Join(left=GoodsDesc.class,alias="gd",tAlias="g")
	}
)
public class JoinDemo {
	@VColumn("buyer.name")
	private String buyerName;
	@VColumn("g.name")
	private String goodsName;
	@VColumn("gd.saled")
	private Integer saled;
	@VColumn("receiver.name")
	private String receiverName;
	@VColumn("wc.balance")
	private int buyerBalance;
	
	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Integer getSaled() {
		return saled;
	}

	public void setSaled(Integer saled) {
		this.saled = saled;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public int getBuyerBalance() {
		return buyerBalance;
	}

	public void setBuyerBalance(int buyerBalance) {
		this.buyerBalance = buyerBalance;
	}

	private static final Map<Class<?>,Set<String>> entityToColumns = new HashMap<>();
	
	private static void analyseEntityColumns(Class<?> ...entities) {
		for(Class<?> entity:entities) {
			Set<String> columns = new HashSet<>();
			entityToColumns.put(entity, columns);
			for(Field field:entity.getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if(column != null) {
					columns.add(field.getName());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		
		analyseEntityColumns(Goods.class,GoodsDesc.class,GoodsOrder.class,GoodsShipping.class,UserChief.class,UserFriend.class,UserProfile.class,Wallet.class);
		
		Set<String> params = new HashSet<>();
		params.add("buyerName");
		params.add("saled");
		params.add("buyerBalance");
		
		String sql = generateSqlByParam(JoinDemo.class,params);
		System.out.println(sql);
	}
	
	private static String generateSqlByParam(Class<?> clazz,Set<String> params) {
		VTable vt = JoinDemo.class.getAnnotation(VTable.class);
		Map<String,Class<?>> aliasToEntity = analyseAliasMapping(vt);
		Map<String,String> columnToOriginal = analyseColumns(JoinDemo.class,aliasToEntity,entityToColumns);
		Map<String,String> depends = analyseDepend(vt);
		Set<String> classes = generateDenpendTableByParams(columnToOriginal,depends,params);
		classes.add(vt.alias());
		
		/** TODO 这些数据不再sql中 需要手动补全 */
		Set<String> set = new HashSet<>(aliasToEntity.keySet());
		set.removeAll(classes);
		
		String joinSQL = generateJoinSQL(vt,classes);
		StringBuffer sb = new StringBuffer();
		if(columnToOriginal.isEmpty()) {
			System.err.println("没有字段的表");
		}
		sb.append("select ");
		boolean hasBefore = false;
		for(String column:columnToOriginal.keySet()) {
			String original = columnToOriginal.get(column);
			if(classes.contains(original.split("\\.")[0])) {
				if(hasBefore) {
					sb.append(", " + original + " " + column);
				}else {
					sb.append(original + " " + column);	
				}
				hasBefore = true;
			}
		}
		sb.append("\n" + joinSQL);
		return sb.toString();
	}
	
	/**
	 * 
	 * @param aliasToEntity
	 * @param columnToOriginal
	 * @param denpends
	 * @param params
	 * @return
	 */
	private static Set<String> generateDenpendTableByParams(Map<String,String> columnToOriginal,Map<String,String> depends,Set<String> params) {
		Set<String> set = new HashSet<>();		
		if(params == null || params.isEmpty()) {
			return set;
		}
		for(String column:params) {
			String orignal = columnToOriginal.get(column);
			if(orignal.matches(".*\\(.*\\).*")) {
				String oc = orignal.replaceAll(".*\\((.*)\\).*", "$1");
				if(oc.matches("[a-zA-Z0-9_]{1,}\\.[a-zA-Z0-9_]{1,}")) {
					set.add(oc.split("\\.")[0]);
				}
			}else if(orignal.matches("[a-zA-Z0-9_]{1,}\\.[a-zA-Z0-9_]{1,}")) {
				set.add(orignal.split("\\.")[0]);
			}
			
		}
		Set<String> result = new HashSet<>();
		for(String alias:set) {
			result.add(alias);
			String depend = depends.get(alias);
			while(depend != null) { 
				result.add(depend);
				depend = depends.get(depend);
			}
		}
		return result;
	}
	
	public static Map<String,String> analyseColumns(Class<?> clazz,Map<String,Class<?>> aliasToEntity,Map<Class<?>,Set<String>> entityToColumns) {
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
						if(entityToColumns.get(entity).contains(field.getName())) {
							if(uniqueAlias != null) {
								System.err.printf("%s和%s 都包含字段:%s\n", uniqueAlias,alias,field.getName());
							}else {
								uniqueAlias = alias;	
							}
						}
					}
					if(uniqueAlias != null) {
						map.put(field.getName(), uniqueAlias+"." + field.getName());	
					}else {
						System.err.printf("找不到 %s\n",field.getName());
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
	
	public static Map<String,String> analyseDepend(VTable vt) {
		Map<String,String> map = new HashMap<>();
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
				String otherDepend = map.get(fAlias);
				if(otherDepend != null && !otherDepend.equals(tAlias)) {
					System.err.println(fAlias+"依赖关系两个不同的表：" + otherDepend + "," + tAlias);
				}else {
					map.put(fAlias,tAlias);	
				}
			}else {
				System.err.println("没有别名引用类，也没有实体类，错误的配置");
			}
		}
		return map;
	}
	
	public static String generateJoinSQL(VTable vt, Set<String> classes) {
		StringBuffer sb  = new StringBuffer();
		sb.append(String.format("from %s %s \n", vt.mainTable().getSimpleName(),vt.alias()));
		for(Join join:vt.joins()) {
			String name = "";
			if(!join.alias().equals("")) {
				name = join.alias();
			} else {
				if(join.left() != void.class) {
					name = join.left().getSimpleName();
				}else if(join.right() != void.class) {
					name = join.right().getSimpleName();
				}else {
					System.err.println("没有别名引用类，也没有实体类，错误的配置");
					continue;
				}
			}
			if(classes != null && !classes.contains(name)) {
				continue;
			}
			
			String sql = null;
			String originTbl = join.alias();
			String targetTbl = join.tAlias();
			if(join.tAlias().equals("")) {
				if(join.target() != void.class) {
					targetTbl = join.target().getSimpleName();
				}else {
					if(!vt.alias().equals("")) {
						targetTbl = vt.alias();
					}else {
						targetTbl = vt.mainTable().getSimpleName();
					}
				}
			}
			if(join.left() != void.class) {
				if(join.alias().equals("")) {
					originTbl = join.left().getSimpleName();
				}
				if(!join.cnd().equals("")) {
					sql = String.format("left join %s %s on %s", 
							join.left().getSimpleName(), join.alias(),join.cnd());
				}else {
					sql = String.format("left join %s %s on %s.id = %s.id and %s.del=false", 
							join.left().getSimpleName(), join.alias(),originTbl,targetTbl,originTbl);
				}
			}else if(join.right() != void.class) {
				if(join.alias().equals("")) {
					originTbl = join.right().getSimpleName();
				}
				if(!join.cnd().equals("")) {
					sql = String.format("right join %s %s on %s", 
							join.right().getSimpleName(), join.alias(),join.cnd());
				}else {
					sql = String.format("right join %s %s on %s.id = %s.id and %s.del=false", 
							join.right().getSimpleName(), join.alias(),originTbl,targetTbl,originTbl);
				}
			}else {
				System.err.println("没有别名引用类，也没有实体类，错误的配置");
			}
			sb.append(sql+"\n");
		}
		return sb.toString();
	}
}
