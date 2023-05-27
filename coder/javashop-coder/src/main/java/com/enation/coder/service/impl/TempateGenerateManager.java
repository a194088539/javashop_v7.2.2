package com.enation.coder.service.impl;

import java.util.Date;
import java.util.List;

import com.enation.coder.model.po.Module;
import com.enation.coder.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.enation.coder.model.enums.FiledDataType;
import com.enation.coder.model.enums.PowerType;
import com.enation.coder.model.po.DataField;
import com.enation.coder.model.po.DataModel;
import com.enation.coder.model.po.Project;
import com.enation.coder.model.vo.CoderParam;
import com.enation.coder.util.Constants;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 模板生成manager
 *
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月12日 下午1:59:26
 */
@Service
public class TempateGenerateManager implements ITempateGenerateManager {


    @Autowired
    private IProjectManager projectManager;

    @Autowired
    private IDataModelManager dataModelManager;

    @Autowired
    private IDataFieldManager dataFieldManager;

    @Autowired
    private IModuleManager moduleManager;

    @Override
    public Model generateModel(CoderParam param, Model model) {

        //查询模板
        DataModel dataModel = this.dataModelManager.get(param.getModelId());

        //查询项目
        Project project = projectManager.get(dataModel.getProject_id());

        model.addAttribute("time", DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));

        model.addAttribute("packageStr", project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".model");
        model.addAttribute("username", param.getUsername());
        model.addAttribute("version", dataModel.getVersion());
        model.addAttribute("since", dataModel.getVersion());
        model.addAttribute("model", dataModel);
        String s = dataModel.getEnglish_name();
        String className = (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        model.addAttribute("className", className);

        // 生成属性，getter,setter方法
        List<DataField> fieldList = this.dataFieldManager.getFieldByModel(param.getModelId());

        StringBuilder sb = new StringBuilder();
        StringBuilder sbMethods = new StringBuilder();
        StringBuilder importStr = new StringBuilder();
        StringBuilder equalsMethodStr = new StringBuilder();
        StringBuilder hashMethodStr = new StringBuilder();
        StringBuilder toStringMethodStr = new StringBuilder();
        // 重写equals方法
        equalsMethodStr.append("	@Override\r\n")
                .append("    public boolean equals(Object o) {\r\n")
                .append("        if (this == o) {return true;}\r\n")
                .append("        if (o == null || getClass() != o.getClass()) {return false;}\r\n")
                .append("        " + className + " that = (" + className + ") o;\r\n");
        //重写hashMethodStr
        hashMethodStr.append("    @Override\r\n")
                .append("    public int hashCode() {\r\n")
                .append("        int result = 0;\r\n");
        //重写toString方法
        toStringMethodStr.append("    @Override\r\n")
                .append("    public String toString() {\r\n")
                .append("        return \"" + className + "{\" +\r\n");
        int i = 0;

        for (DataField field : fieldList) {

            String propColumnName = field.getEnglish_name();//对应数据库的字段名
            String propName = lowerUpperCaseColumn(field.getEnglish_name());//属性名，转成小写驼峰格式
            String propType = FiledDataType.valueOf(field.getData_type()).getType();

            // 注释、类型、名称
            sb.append("    /**")
                    .append(field.getChina_name())
                    .append("*/\r\n");

            if (field.getIs_primary() == 1) {//是主键
                sb.append("    @Id(name = \"" + propColumnName + "\")\r\n");
                sbMethods.append("    @PrimaryKeyField\r\n");
                sb.append("    @ApiModelProperty(hidden=true)\r\n");
            } else {
                sb.append("    @Column(name = \"" + propColumnName + "\")\r\n");

                String validateItem = field.getValidate_items();//校验
                String isEmpty = "false";
                if (!StringUtil.isEmpty(validateItem)) {
                    switch (validateItem) {
                        case "notempty"://必填
                            sb.append("    @NotEmpty(message=\"" + field.getChina_name() + "不能为空\")\r\n");
                            isEmpty = "true";
                            importStr.append("import javax.validation.constraints.NotEmpty;\r\n");
                            break;
                        case "number"://数字
                            sb.append("    @Min(message=\"必须为数字\", value = 0)\r\n");
                            importStr.append("import javax.validation.constraints.Min;\r\n");
                            break;
                        case "email"://邮件
                            sb.append("    @Email(message=\"格式不正确\")\r\n");
                            importStr.append("import javax.validation.constraints.Email;\r\n");
                            break;

                        default:
                            break;
                    }
                }

                sb.append("    @ApiModelProperty(name=\"" + propColumnName + "\",value=\"" + field.getChina_name() + "\",required=" + isEmpty + ")\r\n");
            }


            sb.append("    private ")
                    .append(propType)
                    .append(" ")
                    .append(propName)
                    .append(";\r\n");

            sbMethods.append("    public ")
                    .append(propType)
                    .append(" get")
                    .append(propName.substring(0, 1).toUpperCase())
                    .append(propName.substring(1))
                    .append("() {\r\n")
                    .append("        return ")
                    .append(propName)
                    .append(";\r\n")
                    .append("    }\r\n")
                    .append("    public void set")
                    .append(propName.substring(0, 1).toUpperCase())
                    .append(propName.substring(1))
                    .append("(")
                    .append(propType)
                    .append(" ")
                    .append(propName)
                    .append(") {\r\n")
                    .append("        this.")
                    .append(propName)
                    .append(" = ")
                    .append(propName)
                    .append(";\r\n    }\r\n")
                    .append("\r\n");

            equalsMethodStr.append("        if (" + propName + " != null ? !" + propName + ".equals(that." + propName + ") : that." + propName + " != null) {return false;}\r\n");
            hashMethodStr.append("        result = 31 * result + (" + propName + " != null ? " + propName + ".hashCode() : 0);\r\n");
            if (i == 0) {
                if ("String".equals(propType)) {
                    toStringMethodStr.append("                \"billId='\" + " + propName + "' +\r\n");
                } else {
                    toStringMethodStr.append("                \"" + propName + "=\" + " + propName + " +\r\n");
                }
            } else {
                if ("String".equals(propType)) {
                    toStringMethodStr.append("                \", " + propName + "='\" + " + propName + " + '\\'' +\r\n");
                } else {
                    toStringMethodStr.append("                \", " + propName + "=\" + " + propName + " +\r\n");
                }
            }


            i++;
        }

        equalsMethodStr.append("        return true;\r\n    }\r\n");
        hashMethodStr.append("        return result;\r\n    }\r\n");
        toStringMethodStr.append("                '}';\r\n    }\r\n");

        model.addAttribute("serialVersionNum", StringUtil.generate16LongNum());
        model.addAttribute("propertiesStr", sb.toString());
        model.addAttribute("methodStr", sbMethods.toString());
        model.addAttribute("importStr", importStr.toString());
        model.addAttribute("equalsMethodStr", equalsMethodStr.toString());
        model.addAttribute("hashMethodStr", hashMethodStr.toString());
        model.addAttribute("toStringMethodStr", toStringMethodStr.toString());
        return null;
    }

    @Override
    public Model generateController(CoderParam param, Model model) {

        //查询模板
        DataModel dataModel = this.dataModelManager.get(param.getModelId());

        //查询项目
        Project project = projectManager.get(dataModel.getProject_id());

        model.addAttribute("time", DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        model.addAttribute("packageStr", project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.manager + ".api");
        model.addAttribute("username", param.getUsername());
        model.addAttribute("version", dataModel.getVersion());
        model.addAttribute("since", dataModel.getVersion());
        model.addAttribute("model", dataModel);
        String s = dataModel.getEnglish_name();
        String className = (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        model.addAttribute("className", className);

        StringBuffer importStr = new StringBuffer();
        importStr.append("import " + project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".model." + className + ";\r\n");
        importStr.append("import " + project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".service." + className + "Manager;");
        model.addAttribute("importStr", importStr);

        model.addAttribute("apiPath", "/" + param.getPackageName() + "/" + s.toLowerCase() + "s");

        return null;
    }

    @Override
    public Model generateService(CoderParam param, Model model) {

        //查询模板
        DataModel dataModel = this.dataModelManager.get(param.getModelId());

        //查询项目
        Project project = projectManager.get(dataModel.getProject_id());

        model.addAttribute("time", DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        model.addAttribute("packageStr", project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".service.impl");
        model.addAttribute("username", param.getUsername());
        model.addAttribute("version", dataModel.getVersion());
        model.addAttribute("since", dataModel.getVersion());
        model.addAttribute("model", dataModel);
        String s = dataModel.getEnglish_name();
        String className = (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        model.addAttribute("className", className);

        StringBuffer importStr = new StringBuffer();
        importStr.append("import " + project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".model." + className + ";\r\n");
        importStr.append("import " + project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".service." + className + "Manager;");
        model.addAttribute("importStr", importStr);

        return null;
    }

    @Override
    public void generateInterfaceService(CoderParam param, Model model) {
        //查询模板
        DataModel dataModel = this.dataModelManager.get(param.getModelId());

        //查询项目
        Project project = projectManager.get(dataModel.getProject_id());

        model.addAttribute("time", DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        model.addAttribute("packageStr", project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".service");
        model.addAttribute("username", param.getUsername());
        model.addAttribute("model", dataModel);
        model.addAttribute("version", dataModel.getVersion());
        model.addAttribute("since", dataModel.getVersion());

        String s = dataModel.getEnglish_name();
        String className = (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        model.addAttribute("className", className);

        model.addAttribute("importStr", "import " + project.getPackage_name() + Constants.CHARACTER_POINT + PowerType.core + Constants.CHARACTER_POINT
                + param.getPackageName() + ".model." + className + ";");

    }

    /**
     * 将下划线转成大写字母
     *
     * @param columnName
     * @return
     */
    private String lowerUpperCaseColumn(String columnName) {

        StringBuffer propName = new StringBuffer("");

        for (int i = 0; i < columnName.length(); ) {
            if ("_".equals(columnName.charAt(i) + "")) {
                propName.append(Character.toUpperCase(columnName.charAt(i + 1)));
                i += 2;
            } else {
                propName.append(columnName.charAt(i));
                i += 1;
            }
        }
        return propName.toString();

    }

    @Override
    public void generatePdm(int project_id, Integer versionId, Model model) {

        Project project = projectManager.get(project_id);
        //项目名称
        model.addAttribute("project_name", project.getProject_name());
        //项目中的模块
        List<Module> moduleList = moduleManager.list(project_id,versionId);
        String pysicalDiagrams = "";
        int moduleId = 4;
        int tableKey = 1;
        int fieldKey = 1;
        int primaryKey = 1;
        int pKey = 1;
        String tables = "";
        for (Module module : moduleList) {
            String symbols = "<c:Symbols>";

            moduleId++;
            //查询模块下的模型
            List<DataModel> models = dataModelManager.listModelsByModule(module.getModule_id());
            //pdm中的表
            String modelTables = "";

            for (DataModel dataModel : models) {

                String tableSymbol = "<o:TableSymbol Id=\"ts"+tableKey+"\">\n" +
                        "            <a:CreationDate>1450250932</a:CreationDate>\n" +
                        "            <a:ModificationDate>1454399725</a:ModificationDate>\n" +
                        "            <a:IconMode>-1</a:IconMode>\n" +
                        "            <a:Rect>((-16407,16030), (-1628,22472))</a:Rect>\n" +
                        "            <a:LineColor>16512</a:LineColor>\n" +
                        "            <a:FillColor>166354</a:FillColor>\n" +
                        "            <a:ShadowColor>12632256</a:ShadowColor>\n" +
                        "            <a:BrushStyle>6</a:BrushStyle>\n" +
                        "            <a:GradientFillMode>65</a:GradientFillMode>\n" +
                        "            <a:GradientEndColor>16777215</a:GradientEndColor>\n" +
                        "            <c:Object>\n" +
                        "                <o:Table Ref=\"t"+tableKey+"\"/>\n" +
                        "            </c:Object>\n" +
                        "        </o:TableSymbol>";
                symbols+=tableSymbol;


                //查询表中的字段
                String columns = "";
                List<DataField> dataFields = dataFieldManager.listByModel(dataModel.getModel_id());

                for (DataField dataField : dataFields) {

                    String key = "";
                    if(dataField.getIs_primary() == 1){
                        key = "<a:Identity>1</a:Identity>\n" +
                                "<a:Column.Mandatory>1</a:Column.Mandatory>";
                        primaryKey = fieldKey;

                    }

                    String columm = "<o:Column Id=\"c"+fieldKey+"\">\n" +
                            "           <a:ObjectID>15327360-C96A-4AA9-90F0-AD16F15C37D8</a:ObjectID>\n" +
                            "           <a:Name>"+dataField.getChina_name()+"</a:Name>\n" +
                            "           <a:Code>"+dataField.getEnglish_name()+"</a:Code>\n" +
                            "           <a:CreationDate>1450250932</a:CreationDate>\n" +
                            "           <a:Creator>Administrator</a:Creator>\n" +
                            "           <a:ModificationDate>1450251314</a:ModificationDate>\n" +
                            "           <a:Modifier>Administrator</a:Modifier>\n" +
                            "           <a:DataType>"+dataField.getData_type().toLowerCase()+"("+dataField.getData_size()+")</a:DataType>\n" +
                            "           <a:Length>"+dataField.getData_size()+"</a:Length>\n"
                                        +key+
                            "      </o:Column>";
                    columns += columm;
                    fieldKey++;
                }

                String table = "<o:Table Id=\"t"+tableKey+"\">\n" +
                        "   <a:ObjectID>50E7C039-6B23-49B2-91C4-84D03A90EC2E</a:ObjectID>\n" +
                        "   <a:Name>" + dataModel.getModel_name() + "(" + dataModel.getTable_name() + ")</a:Name>\n" +
                        "   <a:Code>" + dataModel.getTable_name() + "</a:Code>\n" +
                        "   <a:CreationDate>1450250932</a:CreationDate>\n" +
                        "   <a:Creator>Administrator</a:Creator>\n" +
                        "   <a:ModificationDate>1454380868</a:ModificationDate>\n" +
                        "   <a:Modifier>Administrator</a:Modifier>\n" +
                        "   <a:TotalSavingCurrency/>\n" +
                        "   <c:Columns>\n" +
                            columns +
                        "   </c:Columns>\n" +
                        "   <c:Keys>\n" +
                        "       <o:Key Id=\"ck"+pKey+"\">\n" +
                        "           <a:ObjectID>82FDB823-521A-444C-A1A0-7273711B46E7</a:ObjectID>\n" +
                        "           <a:Name>Key_1</a:Name>\n" +
                        "           <a:Code>Key_1</a:Code>\n" +
                        "           <a:CreationDate>1450250932</a:CreationDate>\n" +
                        "           <a:Creator>Administrator</a:Creator>\n" +
                        "           <a:ModificationDate>1450251314</a:ModificationDate>\n" +
                        "           <a:Modifier>Administrator</a:Modifier>\n" +
                        "           <c:Key.Columns>\n" +
                        "               <o:Column Ref=\"c"+primaryKey+"\"/>\n" +
                        "           </c:Key.Columns>\n" +
                        "       </o:Key>\n" +
                        "   </c:Keys>\n" +
                        "   <c:PrimaryKey>\n" +
                        "       <o:Key Ref=\"ck"+pKey+"\"/>\n" +
                        "   </c:PrimaryKey>\n" +
                        "</o:Table>";

                modelTables += table;
                tableKey++;
                pKey++;
            }


            tables += modelTables;

            String pysicalDiagram = "<o:PhysicalDiagram Id=\"p" + moduleId + "\">\n" +
                    "    <a:ObjectID>A123929D-CEE9-47FE-B00F-ACA171653AA7</a:ObjectID>\n" +
                    "    <a:Name>" + module.getModule_name() + "</a:Name>\n" +
                    "    <a:Code>" + module.getModule_name() + "</a:Code>\n" +
                    "    <a:CreationDate>1450232720</a:CreationDate>\n" +
                    "    <a:Creator>Administrator</a:Creator>\n" +
                    "    <a:ModificationDate>1462262418</a:ModificationDate>\n" +
                    "    <a:Modifier>Administrator</a:Modifier>\n" +
                    "    <a:PaperSize>(8268, 11693)</a:PaperSize>\n" +
                    "    <a:PageMargins>((124,139), (170,139))</a:PageMargins>\n" +
                    "    <a:PageOrientation>-6716</a:PageOrientation>\n" +
                    "    <a:PaperSource>-6716</a:PaperSource>\n" +
                    symbols+
                    "    </c:Symbols>" +
                    "</o:PhysicalDiagram>";

            pysicalDiagrams += pysicalDiagram;


        }

        model.addAttribute("pysicalDiagrams", pysicalDiagrams);
        model.addAttribute("tables", tables);


    }
}
