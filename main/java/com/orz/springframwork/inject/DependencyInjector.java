package com.orz.springframwork.inject;

import com.orz.springframwork.core.BeanContainer;
import com.orz.springframwork.inject.annotation.Autowired;
import com.orz.springframwork.util.ClassUtil;
import com.orz.springframwork.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {
    /**
     * Bean容器
     */
    private BeanContainer beanContainer;

    public DependencyInjector() {
        beanContainer = BeanContainer.getInstance();
    }

    /*
    执行ioc
     */
    public void doIoc() {
        if (ValidationUtil.isEmpty(beanContainer.getClasses())) {
            log.warn("empty classset in BeanContainer");
            return;
        }
        //1.遍历Bean容器中所有的Class对象
        for (Class<?> clazz : beanContainer.getClasses()) {
            //2.遍历Class对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)) {
                continue;
            }
            for (Field field : fields) {
                //3、找出被Autowired标记的成员变量
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();
                    //4. 获取这些成员变量
                    Class<?> fileClass = field.getType();
                    //5. 获取这些成员变量的类型在容器里对应的实例
                    Object filedValue = getFiledInstance(fileClass, autowiredValue);
                    if (filedValue == null) {
                        throw new RuntimeException("unable inject relevant type,target fieldClass is:" + fileClass.getName());
                    } else {
                        //6.通过反射将对应的成员变量实例注入到成员变量所在类的实例里
                        Object targetBean = beanContainer.getBean(clazz);
                        ClassUtil.setFiled(field, targetBean, filedValue, true);
                    }


                }

            }

        }

    }

    /**
     * 根据Class在beanContainer里获取其实例或者实现类
     *
     * @param fileClass
     * @return
     */
    private Object getFiledInstance(Class<?> fileClass, String autowiredValue) {
        Object fileValue = beanContainer.getBean(fileClass);
        if (fileValue != null) {
            return fileValue;
        } else {
            Class<?> implementedClass = getImplementedClass(fileClass, autowiredValue);
            if (implementedClass != null) {
                return beanContainer.getBean(implementedClass);
            } else {
                return null;
            }
        }
    }

    /**
     * 获取接口实现类
     *
     * @param fileClass
     * @return
     */
    private Class<?> getImplementedClass(Class<?> fileClass, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesbySuper(fileClass);
        if (!ValidationUtil.isEmpty(classSet)) {
            if (ValidationUtil.isEmpty(autowiredValue)) {
                if (classSet.size() == 1) {
                    return classSet.iterator().next();
                } else {
                    //如果多于两个实现类且用户未指定其中一个实现类，则抛出异常
                    throw new RuntimeException("multiple implemented classes for" + fileClass.getName() + "please set @Autowired`s value to pick one");
                }
            } else {
                for (Class<?> clazz : classSet) {
                    if (autowiredValue.equals(clazz.getSimpleName())) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }

}
