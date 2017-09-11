package io.onceonly.beans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.onceonly.annotation.Const;
import io.onceonly.annotation.I18nConst;
import io.onceonly.annotation.I18nMsg;
import io.onceonly.db.annotation.VColumn;
import io.onceonly.db.annotation.VTable;
import io.onceonly.exception.Failed;
import io.onceonly.util.AnnotationScanner;
import io.onceonly.util.DlsUtils;

@Component
@Order(1)
public class StartupRunner implements CommandLineRunner {
	private static final Log logger = LogFactory.getLog(StartupRunner.class);
    @Autowired
    private I18nRepository i18nRepository;

    @Value("${cn.dls.packages}")
    private String packages;
    
    private final static AnnotationScanner annotations = new AnnotationScanner(I18n.class,I18nMsg.class,I18nConst.class,VTable.class);
 
    private void loadI18nToCache(){
        Iterable<I18n> i18ns = i18nRepository.findAll();
        Iterator<I18n> iter = i18ns.iterator();
        while(iter.hasNext()) {
        	I18n i = iter.next();
        	i18nRepository.findByIdStartingWith(i.getId());
        }
    }
    private void annlysisI18nMsg(){
    	Set<Class<?>> classes = annotations.getClasses(I18nMsg.class);
    	if(classes == null) return;
    	for(Class<?>clazz:classes){
    		I18nMsg group = clazz.getAnnotation(I18nMsg.class);
    		for(Field field:clazz.getFields()){
    			field.setAccessible(true);
    			try {
					String name = field.get(null).toString();
					String id ="msg/"+group.value()+"_"+DlsUtils.encodeMD5(name);
					I18n i18n = i18nRepository.findOne(name);
					if(i18n == null) {
						i18n = new I18n();	
						i18n.setId(id);
						i18n.setId("msg/"+group.value()+"_"+DlsUtils.encodeMD5(name));
						i18n.setName(name);
						i18nRepository.save(i18n);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Failed.throwError(e.getMessage());
				}
    		}
    	}
    }

    private void annlysisConst(){
    	Set<Class<?>> classes = annotations.getClasses(I18nConst.class);
    	if(classes == null) return;
    	for(Class<?>clazz:classes){
    		I18nConst group = clazz.getAnnotation(I18nConst.class);
    		for(Field field:clazz.getFields()){
    			field.setAccessible(true);
    			Const cons = field.getAnnotation(Const.class);
    			try {
					String fieldname = field.getName();
					String val = field.get(null).toString();
					String id = "const/" + group.value()+ "_"+ clazz.getSimpleName() + "_" + fieldname;
					String name = cons.name();
					I18n i18n = i18nRepository.findOne(id);
					if(i18n == null) {
						i18n = new I18n();
						i18n.setId(id);
						i18n.setName(name);
						i18n.setVal(val);
						i18nRepository.save(i18n);
			        	logger.debug("add: " + i18n);
					}else {
						/** The val depend on database */
						if(!val.equals(i18n.getVal())){
							i18n.setVal(val);
							field.set(null, DlsUtils.strToBaseType(field.getType(), val));
				        	logger.debug("reload: " + i18n);
						}
						if(!i18n.getName().equals(name) ){
							i18n.setName(name);
							i18nRepository.save(i18n);
				        	logger.debug("update: " + i18n);
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Failed.throwError(e.getMessage());
				}
    		}
    	}
    }
    
    /**
     * TODO 解析注解 VTable
     * 1. 生成自动连接的表模板 
     * 2. 优化并动态生成连接信息，（分页，数据量，是否是筛选条件，参数警告信息：是否建立索引，等）
     */
	private void annlysisVTable(){
    	Set<Class<?>> classes = annotations.getClasses(VTable.class);
    	if(classes == null) return;
    	for(Class<?>clazz:classes){
        	Map<String,Field> fieldsMapping = new HashMap<>();
        	for(Field field:clazz.getFields()){
    			VColumn column = field.getAnnotation(VColumn.class);
    			if(column != null) {
        			field.setAccessible(true);
    				if(column.value() != ""){
    					fieldsMapping.put(column.value(), field);
    				}
    			}
    		}
        	//VTABLES.put(clazz, fieldsMapping);
        	logger.debug(clazz.getName()+":" + fieldsMapping.keySet());
    	}
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("dls framework runner " + DlsUtils.toJSON(args));
        if(packages != null && !packages.trim().equals("")){
        	annotations.scanPackages(packages.split(","));
        }
        annlysisI18nMsg();
        annlysisConst();
        annlysisVTable();
        loadI18nToCache();
    }
}
