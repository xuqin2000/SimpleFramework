package com.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Apple {

    private int price;
    public String name;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws Exception {
        //正常调用
        Apple apple = new Apple();
        apple.setPrice(15);
        System.out.println("正常调用为" + apple.getPrice());

        //1.使用反射，获取类的 Class 对象实例
        Class cls = Class.forName("com.reflect.Apple");
/*       //获取成员变量(公有)
//        Field field1 =  cls.getField("name");
//        System.out.println(field1);
//        //获取成员变量(私有)
//        Field field2=cls.getDeclaredField("price");
//        System.out.println(field2);

       // field2.set("price",20);
       */
        //2.cls.getConstructor().newInstance()获取无参构造方法创建对象
        Object appleobj = cls.getConstructor().newInstance();
        //3.cls.getMethod获取成员方法
        Method setmethod = cls.getMethod("setPrice", int.class);
        //System.out.println(setmethod);

        // 4.调用appleobj的成员方法
        setmethod.invoke(appleobj, 20);

        Method getmethod = cls.getMethod("getPrice");
        System.out.println("反射调用为" + getmethod.invoke(appleobj));
    }
}
