package com.github.xuchen93.core.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.JdbcType;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.xuchen93.core.model.db.SimplePGResultSet;
import lombok.SneakyThrows;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.ResultMap;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author xuchen.wang
 * @date 2024/2/26
 */
public class DbUtil {

    private DbUtil() {
    }

    /**
     * 控制台输出model对象的mybatis resultMap配置
     * 约定：
     * 驼峰映射
     * 只包含简单字段和List两种字段类型
     */
    public static void soutModelXml(Class cl) {
        List<String> list = generateModelXml(cl);
        for (String s : list) {
            System.out.println(s);
        }
    }

    /**
     * 控制台输出mybatis addList的insert
     */
    public static void soutAddListEntity(Class cl) {
        Set<String> set = new HashSet<>();
        set.add("serialVersionUID");
        Field[] fields = ReflectUtil.getFields(cl);
        System.out.println("\t<insert id =\"addList\" parameterType=\"java.util.List\" >");
        System.out.println("\t\tinsert into XXX");
        System.out.print("\t\t(");
        StringBuilder sb = new StringBuilder(100);
        for (Field field : fields) {
            if (set.contains(field.getName())) {
                continue;
            }
            sb.append(StrUtil.toSymbolCase(field.getName(), '_') + ",");
        }
        System.out.print(sb.deleteCharAt(sb.length() - 1));
        System.out.println(")");
        System.out.println("\t\tvalues");
        System.out.println("\t\t<foreach collection =\"list\" item=\"item\" index= \"index\" separator =\",\">");
        System.out.print("\t\t(");
        sb = new StringBuilder(300);
        for (Field field : fields) {
            if (set.contains(field.getName())) {
                continue;
            }
            if ("version".equals(field.getName())) {
                sb.append("0,");
            } else {
                sb.append(String.format("#{item.%s},", field.getName()));
            }
        }
        System.out.print(sb.deleteCharAt(sb.length() - 1));
        System.out.println(")");
        System.out.println("\t\t</foreach>");
        System.out.println("\t</insert>");
    }


    @SneakyThrows
    private static List<String> generateModelXml(Class cl) {
        List<String> result = new ArrayList<>(100);
        Field[] fields = ReflectUtil.getFields(cl);
        List<Class> fieldClassList = new ArrayList<>(10);
        List<String> tempList = new ArrayList<>(10);
        result.add(StrUtil.format("\t<resultMap id=\"{}\" type=\"{}\">", StrUtil.lowerFirst(cl.getSimpleName()), cl.getCanonicalName()));
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            if (Collection.class.isAssignableFrom(field.getType())) {
                String className = StrUtil.subBetween(field.getGenericType().toString(), "<", ">");
                if (className == null) {//该list没有泛型
                    tempList.add(StrUtil.format("\t\t<collection  property=\"{}\" resultType=\"{}\"/>", field.getName(), "xxx.xxx"));
                    continue;
                }
                List<String> list = StrUtil.split(className, '.');
                fieldClassList.add(Class.forName(className));
                tempList.add(StrUtil.format("\t\t<collection  property=\"{}\" resultMap=\"{}\"/>", field.getName(), StrUtil.lowerFirst(list.get(list.size() - 1))));
            } else {
                result.add(StrUtil.format("\t\t<result column=\"{}\" property=\"{}\"/>", StrUtil.toUnderlineCase(field.getName()), field.getName()));
            }
        }
        result.addAll(tempList);
        result.add("\t</resultMap>");
        result.add("");
        for (Class aClass : fieldClassList) {
            result.addAll(generateModelXml(aClass));
        }
        return result;
    }

    /**
     * 将navicat导出的json数据直接转换为的list
     */
    public static <T> List<T> handleSimpleNavicatJson(String jsonFilePath, Class<T> c) {
        return readNavicatJson(jsonFilePath).toList(c);
    }

    /**
     * 将navicat导出的json数据根据mybatis-plus的entity转换为list
     */
    public static <T> List<T> handleMybatisPlusComplexNavicatJson(String jsonFilePath, Class<T> c) {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), null), c);
        ResultMap resultMap = tableInfo.getConfiguration().getResultMap("mybatis-plus_" + c.getSimpleName());
        return handleComplexNavicatJson(jsonFilePath, resultMap, c);
    }

    /**
     * 将navicat导出的json数据根据xml文件转换为list
     */
    public static <T> List<T> handleMybatisXmlComplexNavicatJson(String jsonFilePath, String mapperXmlPath, Class<T> c) {
        MybatisConfiguration configuration = new MybatisConfiguration();
        XMLMapperBuilder builder = new XMLMapperBuilder(FileUtil.getInputStream(mapperXmlPath), configuration, StrUtil.format("file [{}]", mapperXmlPath), new HashMap<>());
        builder.parse();
        ResultMap resultMap = configuration.getResultMap("BaseResultMap");
        return handleComplexNavicatJson(jsonFilePath, resultMap, c);
    }

    /**
     * 将数据库导出的json转成含有typeHandle对象
     */
    public static <T> List<T> handleComplexNavicatJson(String jsonFilePath, ResultMap resultMap, Class<T> c) {
        JSONArray jsonArray = readNavicatJson(jsonFilePath);
        List<T> list = new ArrayList<>(jsonArray.size());
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            T bean = jsonObject.toBean(c);
            handleTypeHandleField(bean, resultMap, new SimplePGResultSet(jsonObject));
            list.add(bean);
        }
        return list;
    }

    public static void handleTypeHandleField(Object obj, ResultMap resultMap, ResultSet resultSet) {
        resultMap.getResultMappings().forEach(mapping -> {
            if (mapping.getJdbcType().TYPE_CODE == JdbcType.OTHER.typeCode) {
                try {
                    String field = StrUtil.toCamelCase(mapping.getColumn());
                    Object value = mapping.getTypeHandler().getResult(resultSet, field);
                    ReflectUtil.setFieldValue(obj, field, value);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public static JSONArray readNavicatJson(String jsonFilePath) {
        String string = FileUtil.readUtf8String(jsonFilePath);
        JSONObject jsonObject = JSONUtil.parseObj(string);
        return jsonObject.getJSONArray("RECORDS");
    }

}
