package io.onceonly.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;


public class AnnotationScanner {
	private static final Log logger = LogFactory.getLog(AnnotationScanner.class);
    private final Set<Class<?>> filter = new HashSet<>();
    
    public AnnotationScanner(Class<?> ...annotation) {
    	filter.addAll(Arrays.asList(annotation));
	}
    
	public Set<Class<?>> getFilter() {
		return filter;
	}

	private final Map<Class<?>,Set<Class<?>>> annotations = new HashMap<>();
    
    public void scanPackages(String ...packages){
    	logger.info("dls scan packages:" + packages);
		ClassScanning scanner = new ClassScanning(true);
		Set<BeanDefinition> list = new HashSet<>();
		for(String pkg:packages){
			list.addAll(scanner.findCandidateComponents(pkg));
		}
	    for(BeanDefinition o :list){
			String className = o.getBeanClassName();
				try {
					Class<?> clazz = ClassUtils.getDefaultClassLoader().loadClass(className);
					for(Annotation a:clazz.getAnnotations()){
						if(filter.contains(a.annotationType())) {
							putClass(a.annotationType(),clazz);							
						}
					}
				} catch (ClassNotFoundException e) {
					logger.warn(e.getMessage());
					e.printStackTrace();
				}
		}
    }
    
    private void putClass(Class<?> annotation,Class<?> clazz) {
		Set<Class<?>> clazzList = annotations.get(annotation);
		if(clazzList == null) {
			clazzList = new HashSet<>();
			annotations.put(annotation, clazzList);
		}
		clazzList.add(clazz);
    }
	
    public Set<Class<?>> getClasses(Class<?> annotation) {
		return annotations.get(annotation);
	}
	private static class ClassScanning extends ClassPathScanningCandidateComponentProvider {
		
		public ClassScanning(boolean useDefaultFilters) {
			super(useDefaultFilters);
		}
		
		protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
			return true;
		}
	}
}
