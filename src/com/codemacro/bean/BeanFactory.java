package com.codemacro.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClassSetMethods {
  private Map<String, Method> methods = new HashMap<String, Method>();

  public ClassSetMethods(Class<?> c) {
    Method[] methods = c.getMethods();
    for (Method m : methods) {
      String mname = m.getName();
      Class<?>[] ptypes = m.getParameterTypes();
      if (mname.startsWith("set") && ptypes.length == 1 && m.getReturnType() == Void.TYPE) {
        String name = mname.substring("set".length());
        this.methods.put(name, m);
      }
    }
  }
  
  public Method get(String name) {
    return methods.get(name);
  }
}

public class BeanFactory {

  private Map<String, Object> beans = new HashMap<String, Object>();
  
  public Class<?> loadClass(String fullName) throws ClassNotFoundException {
    return Class.forName(fullName);
  }
  
  public Object getBean(String name) {
    return beans.get(name);
  }

  public Object build(String name, Class<?> c) {
    try {
      Object obj = c.newInstance();
      beans.put(name, obj);
      return obj;
    } catch (Exception e) {
      throw new RuntimeException("build bean failed", e);
    }
  }

  public Object buildWithSetters(String name, Class<?> c, Map<String, String> props) {
    try {
      ClassSetMethods setMethods = new ClassSetMethods(c);
      Object obj = c.newInstance();
      for (Map.Entry<String, String> entrys : props.entrySet()) {
        String pname = entrys.getKey();
        String beanName = entrys.getValue();
        Method m = setMethods.get(pname);
        Object val = getBean(beanName);
        m.invoke(obj, val);
      }
      beans.put(name, obj);
      return obj;
    } catch (Exception e) {
      throw new RuntimeException("build bean failed", e);
    }
  }
  
  public Object buildWithConstructor(String name, Class<?> c, List<String> beanNames) {
    try {
      Constructor<?>[] ctors = c.getConstructors();
      assert ctors.length == 1;
      Constructor<?> cc = ctors[0];
      Class<?>[] ptypes = cc.getParameterTypes();
      assert ptypes.length == beans.size();
      Object[] args = new Object[ptypes.length];
      for (int i = 0; i < beanNames.size(); ++i) {
        args[i] = getBean(beanNames.get(i));
      }
      Object obj = cc.newInstance(args);
      beans.put(name, obj);
      return obj;
    } catch (Exception e) {
      throw new RuntimeException("build bean failed", e);
    }
  }
  
  public Object buildWithInject(String name, Class<?> c) {
    try {
      Object obj = c.newInstance();
      Field[] fields = c.getDeclaredFields();
      for (Field f :fields) {
        Inject inject = f.getAnnotation(Inject.class);
        if (inject != null) {
          Object bean = getBeanByType(f.getType());
          f.set(obj, bean);
        } else {
          throw new RuntimeException("not found bean " + f.getName());
        }
      }
      beans.put(name, obj);
      return obj;
    } catch (Exception e) {
      throw new RuntimeException("build bean failed", e);
    }
  }
  
  private Object getBeanByType(Class<?> t) {
    for (Object obj : beans.values()) {
      if (obj.getClass() == t) {
        return obj;
      }
    }
    return null;
  }
}
