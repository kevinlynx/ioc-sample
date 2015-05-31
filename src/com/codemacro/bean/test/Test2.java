package com.codemacro.bean.test;

import com.codemacro.bean.Inject;

public class Test2 {
  @Inject
  public Test1 test1;
  
  public Test2() {
    System.out.println("Test2 ctor");
  }

  public Test2(Test1 test1) {
    System.out.println("Test2 ctor with Test1");
    this.test1 = test1;
  }

  public void setTest1(Test1 t) {
    test1 = t;
    System.out.println("Test2.setTest1");
  }
  
  public Test1 getTest1() {
    return test1;
  }
}
