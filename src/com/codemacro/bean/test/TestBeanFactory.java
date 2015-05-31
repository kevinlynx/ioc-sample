package com.codemacro.bean.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codemacro.bean.BeanFactory;

public class TestBeanFactory {

  public static void main(String[] args) throws ClassNotFoundException {
    BeanFactory bf = new BeanFactory();
    bf.build("test1", bf.loadClass("com.codemacro.bean.test.Test1"));
    assert bf.getBean("test1") != null;
    {
      Map<String, String> props = new HashMap<String, String>();
      props.put("Test1", "test1");
      bf.buildWithSetters("test2", bf.loadClass("com.codemacro.bean.test.Test2"), props);
      assert bf.getBean("test2") != null;
    }
    {
      List<String> params = new ArrayList<String>();
      params.add("test1");
      bf.buildWithConstructor("test2", Test2.class, params);
      assert bf.getBean("test2") != null;
    }
    {
      bf.buildWithInject("test2", Test2.class);
      System.out.println("test1 " + bf.getBean("test1"));
      System.out.println("inject " + ((Test2)bf.getBean("test2")).getTest1());
    }
  }
}
