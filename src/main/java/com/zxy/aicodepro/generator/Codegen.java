package com.zxy.aicodepro.generator;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Codegen {

    private static final String[] TABLE_NAMES = {"user"};

    public static void main(String[] args) {
        try (HikariDataSource dataSource = createDataSource()) {
            Generator generator = new Generator(dataSource, createGlobalConfig());
            generator.generate();
        }
    }

    private static HikariDataSource createDataSource() {
        Dict config = YamlUtil.loadByPath("application.yml");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(getDataSourceProperty(config, "url"));
        dataSource.setUsername(getDataSourceProperty(config, "username"));
        dataSource.setPassword(getDataSourceProperty(config, "password"));
        return dataSource;
    }

    private static String getDataSourceProperty(Dict config, String property) {
        String path = "spring.datasource." + property;
        Object value = config.getByPath(path);
        if (value == null) {
            throw new IllegalStateException("Missing datasource config: " + path);
        }
        return String.valueOf(value);
    }

    private static GlobalConfig createGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();

        globalConfig.getPackageConfig()
                .setBasePackage("com.zxy.aicodepro.genresult");

        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                .setLogicDeleteColumn("isDelete");

        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        globalConfig.enableMapper();
        globalConfig.enableMapperXml();
        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        globalConfig.enableController();

        globalConfig.getJavadocConfig()
                .setAuthor("zxy")
                .setSince("");
        return globalConfig;
    }
}
