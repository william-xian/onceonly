package io.onceonly.beans;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import io.onceonly.annotation.I18nCfg;
import io.onceonly.annotation.I18nCfgBrief;
import io.onceonly.annotation.I18nMsg;
import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.Page;
import io.onceonly.db.tbl.OOI18n;
import io.onceonly.exception.Failed;
import io.onceonly.util.AnnotationScanner;
import io.onceonly.util.OOReflectUtil;
import io.onceonly.util.OOUtils;

// TODO @Component
@Order(1)
public class StartupRunner implements CommandLineRunner {
	private static final Log logger = LogFactory.getLog(StartupRunner.class);
    @Autowired
    private I18nRepository i18nRepository;

    @Value("${cn.dls.packages}")
    private String packages;
    
    private final static AnnotationScanner annotations = new AnnotationScanner(OOI18n.class,I18nMsg.class,I18nCfg.class);
 
    private void loadI18nToCache(){
    	Cnd<OOI18n> cnd = new Cnd<>();
    	cnd.setPage(1);
    	cnd.setPageSize(Integer.MAX_VALUE);
        Page<OOI18n> i18ns = i18nRepository.find(cnd);
        Iterator<OOI18n> iter = i18ns.getData().iterator();
        while(iter.hasNext()) {
        	OOI18n i = iter.next();
        	i18nRepository.findByIdStartingWith(i.getId());
        }
    }
    private void annlysisI18nMsg(){
    	Set<Class<?>> classes = annotations.getClasses(I18nMsg.class);
    	if(classes == null) return;
    	List<OOI18n> i18ns = new ArrayList<>();
    	for(Class<?>clazz:classes){
    		I18nMsg group = clazz.getAnnotation(I18nMsg.class);
    		for(Field field:clazz.getFields()){
    			field.setAccessible(true);
    			try {
					String name = field.get(null).toString();
					String id ="msg/"+group.value()+"_"+OOUtils.encodeMD5(name);
					OOI18n i18n = i18nRepository.get(name);
					if(i18n == null) {
						i18n = new OOI18n();	
						i18n.setId(id);
						i18n.setId("msg/"+group.value()+"_"+OOUtils.encodeMD5(name));
						i18n.setName(name);
						i18ns.add(i18n);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Failed.throwError(e.getMessage());
				}
    		}
    	}
		i18nRepository.insert(i18ns);
    }

    private void annlysisConst(){
    	Set<Class<?>> classes = annotations.getClasses(I18nCfg.class);
    	if(classes == null) return;
    	List<OOI18n> i18ns = new ArrayList<>();
    	for(Class<?>clazz:classes){
    		I18nCfg group = clazz.getAnnotation(I18nCfg.class);
    		for(Field field:clazz.getFields()){
    			field.setAccessible(true);
    			I18nCfgBrief cons = field.getAnnotation(I18nCfgBrief.class);
    			try {
					String fieldname = field.getName();
					String val = field.get(null).toString();
					String id = "const/" + group.value()+ "_"+ clazz.getSimpleName() + "_" + fieldname;
					String name = cons.value();
					OOI18n i18n = i18nRepository.get(id);
					if(i18n == null) {
						i18n = new OOI18n();
						i18n.setId(id);
						i18n.setName(name);
						i18n.setVal(val);
			        	logger.debug("add: " + i18n);
			        	i18ns.add(i18n);
					}else {
						/** The val depend on database */
						if(!val.equals(i18n.getVal())){
							i18n.setVal(val);
							field.set(null, OOReflectUtil.strToBaseType(field.getType(), val));
				        	logger.debug("reload: " + i18n);
						}
						if(!i18n.getName().equals(name) ){
							i18n.setName(name);
							i18nRepository.insert(i18n);
				        	logger.debug("update: " + i18n);
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Failed.throwError(e.getMessage());
				}
    		}
    	}
		i18nRepository.insert(i18ns);
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("dls framework runner " + OOUtils.toJSON(args));
        if(packages != null && !packages.trim().equals("")){
        	annotations.scanPackages(packages.split(","));
        }
        annlysisI18nMsg();
        annlysisConst();
        loadI18nToCache();
    }
}
