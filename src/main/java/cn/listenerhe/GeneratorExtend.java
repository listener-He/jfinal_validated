package cn.listenerhe;

import com.jfinal.plugin.activerecord.generator.Generator;

import javax.sql.DataSource;

/**
 * @Auther: hehh
 * @Date: 2018/12/29 15:17
 * @Description: 代码生成器扩展
 */
public class GeneratorExtend extends Generator{


    /**
     * 构造 Generator，生成 BaseModel、Model、MappingKit 三类文件，其中 MappingKit 输出目录与包名与 Model相同
     *
     * @param dataSource           数据源
     * @param baseModelPackageName base model 包名
     * @param baseModelOutputDir   base mode 输出目录
     * @param modelPackageName     model 包名
     * @param modelOutputDir       model 输出目录
     */
    public GeneratorExtend(DataSource dataSource,
                           String baseModelPackageName,
                           String baseModelOutputDir,
                           String modelPackageName,
                           String modelOutputDir) {
        super(dataSource, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
    }
}
