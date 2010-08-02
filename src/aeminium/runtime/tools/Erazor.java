package aeminium.runtime.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Erazor {
	protected static final Object NULL = null;
	protected static final Map<Class, List<Field>> fieldCache = new ConcurrentHashMap<Class, List<Field>>();
	
	public static void eraseCapturedReferences(Object obj) {
		Class klazz = obj.getClass();

		List<Field> fields = fieldCache.get(klazz);
		if ( fields == null ) {
			fields = new ArrayList<Field>();
			for ( Field f : klazz.getDeclaredFields() ) {
				if ( f.isSynthetic() &&  !f.getType().isPrimitive() ) {
					f.setAccessible(true);
					fields.add(f);
				}
			}
			fieldCache.put(klazz, fields);
		}

		for( Field f : fields ) {
			try {
				f.set(obj, NULL);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
