package orz.springframwork.util;

import com.orz.springframwork.util.ClassUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class ClassUtilTest {

    @DisplayName("提取目标类方法：extractPackageClassTest")
    @Test
    public void extractPackageClassTest() {

        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.imooc");
        System.out.println(classSet);
    }
}
