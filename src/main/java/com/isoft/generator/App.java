package com.isoft.generator;

import java.io.File;

import cn.org.rapid_framework.generator.GeneratorFacade;

public class App {
    public static void main(String[] args) throws Exception {
        String templatePath = "C:\\download\\eclipse\\workspace\\generator\\template";
        GeneratorFacade g = new GeneratorFacade();
        g.getGenerator().addTemplateRootDir(templatePath);
        // g.getGenerator().setOutRootDir("C:\\download\\eclipse\\workspace\\generator\\generator-output");
        g.deleteOutRootDir();
        //g.generateByTable("MM_Stock", "MM_InStock", "MM_OutStock", "MM_Material", "MM_Supplier"); // 可多表生成MM_InStock
        g.generateByTable("PP_TaskList"); // 可多表生成MM_InStock
        openDirectory("C:\\download\\eclipse\\workspace\\generator\\generator-output\\java_src\\main\\java\\com\\isoftoon\\ld\\fx");
    }

    // 打开生成的目录[model，service，controller，dialog]
    public static void openDirectory(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            return;
        }
        Runtime runtime = null;
        try {
            runtime = Runtime.getRuntime();
            //if (!SystemUtil.isWindows) {
                // System.out.println("is linux");
            //    runtime.exec("nautilus " + folder);
            //} else {
                runtime.exec("cmd /c start explorer " + folder);
            //}
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != runtime) {
                runtime.runFinalization();
            }
        }
    }
}
