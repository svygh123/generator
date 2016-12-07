package com.isoft.generator;

import cn.org.rapid_framework.generator.GeneratorFacade;

public class App {
    public static void main(String[] args) throws Exception {
        String templatePath = "C:\\download\\eclipse\\workspace\\generator\\template";
        GeneratorFacade g = new GeneratorFacade();
        g.getGenerator().addTemplateRootDir(templatePath);
        // g.getGenerator().setOutRootDir("C:\\download\\eclipse\\workspace\\generator\\generator-output");
        g.deleteOutRootDir();
        g.generateByTable("MM_Material", "MM_Supplier"); // 可多表生成MM_InStock
// C:\download\eclipse\workspace\generator\generator-output\java_src\main\java\com\isoftoon\ld\fx\model
// C:\download\eclipse\workspace\generator\generator-output\java_src\main\java\com\isoftoon\ld\fx\service
// C:\download\eclipse\workspace\generator\generator-output\java_src\main\java\com\isoftoon\ld\fx\controller
// C:\download\eclipse\workspace\generator\generator-output\java_src\main\java\com\isoftoon\ld\fx\dialog
    }
}
