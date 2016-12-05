package com.isoft.generator;

import cn.org.rapid_framework.generator.GeneratorFacade;

public class App {
    public static void main(String[] args) throws Exception {
        String templatePath = "C:\\download\\eclipse\\workspace\\generator\\template";
        GeneratorFacade g = new GeneratorFacade();
        g.getGenerator().addTemplateRootDir(templatePath);
        // g.getGenerator().setOutRootDir("C:\\download\\eclipse\\workspace\\generator\\generator-output");
        g.deleteOutRootDir();
        g.generateByTable("MM_InStock"); // 可多表生成
// C:\download\eclipse\workspace\generator\generator-output\java_src\main\java\com\isoftoon\ld\fx\model
// C:\download\eclipse\workspace\generator\generator-output\java_src\main\java\com\isoftoon\ld\fx\service
    }
}
