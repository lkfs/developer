import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.stream.Collectors

typeMapping = [
        (~/(?i)tinyint|smallint|int/): "Integer",
        (~/(?i)bigint/)              : "Long",
        (~/(?i)float|double/)        : "Double",
        (~/(?i)decimal/)             : "BigDecimal",
        (~/(?i)datetime|timestamp/)  : "LocalDateTime",
        (~/(?i)date/)                : "LocalDate",
        (~/(?i)time/)                : "LocalTime",
        (~/(?i)/)                    : "String"
]

def templates = [
        ["fileType": "entity", "template": "dao/mysql/TaxCurrencyTab.java"],
        ["fileType": "repository", "template": "dao/mysql/TaxCurrencyRepository.java"],
        ["fileType": "manager", "template": "dao/mysql/TaxCurrencyManager.java"],
]

baseDir = "D:\\ws_java\\developer-4.0" // 项目根路径
if (!baseDir.endsWith(File.separator))
    baseDir = baseDir + File.separator

//读取项目的基础配置
def paramsFile = new File(new File(baseDir), "jCoder.json")
params = new JsonSlurper().parseText(paramsFile.text)
params.sdkBaseFolder = baseDir + params.sdkModuleFolder +
        File.separator + "src" +
        File.separator + "main" +
        File.separator + "java";

SELECTION.filter { it instanceof DasTable }.each {
    params.table = it.getName()
    params.entityClassName = getCamelCase(params.table, true);
    params.fields = calcFields(it)

    //log(JsonOutput.toJson(params))

    for (file in templates) {
        generateFile(file["fileType"], file["template"])
    }
}
saveParams(paramsFile)

def generateFile(fileType, template) {
    String outFilePath
    switch (fileType) {
        case "entity":
            outFilePath = params.sdkBaseFolder +
                    File.separator + getPackagePath(params.sdkModulePackage + ".dao.entity") +
                    File.separator + params.entityClassName + "Tab.java"
            break;

        case "repository":
            outFilePath = params.sdkBaseFolder +
                    File.separator + getPackagePath(params.sdkModulePackage + ".dao.repository") +
                    File.separator + params.entityClassName + "Repository.java"
            break;

        case "manager":
            outFilePath = params.sdkBaseFolder +
                    File.separator + getPackagePath(params.sdkModulePackage + ".manager") +
                    File.separator + params.entityClassName + "Manager.java"
            break;
    }

    File outFile = new File(outFilePath);

    if (!outFile.getParentFile().exists()) {
        outFile.getParentFile().mkdirs();
    }

    if (!outFile.exists()) {
        def engine = new groovy.text.GStringTemplateEngine()
        def templateFile = new File(baseDir, "template/${template}")
        def result = engine.createTemplate(templateFile).make(params)

        PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"))
        output.withPrintWriter { out -> out.println "${result}".trim() }
    }
}

def saveParams(File paramsFile) {
    PrintWriter paramsOutput = new PrintWriter(new OutputStreamWriter(new FileOutputStream(paramsFile), "utf-8"))
    paramsOutput.withPrintWriter { out ->
        out.println JsonOutput.toJson(params)
    }
}

def log(message) {
    def logName = "d:\\groovy.log";
    FileWriter writer = new FileWriter(logName, true);
    writer.write(message);
    writer.write("\r\n")
    writer.close()
}

def calcFields(table) {
    def idFound = 0;
    def fields = DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def fieldName = getCamelCase(col.getName(), false);
        fields += [[fieldName : fieldName,
                    columnName: col.getName(),
                    type      : typeStr,
                    isKey     : (fieldName.endsWith("Id") || fieldName.endsWith("id")) && idFound++ == 0,
                    comment   : col.getComment()]]
    }

    //判断是否包含通用字段
    commonFields = ["note", "sort", "version", "createdBy", "createdAt", "updatedBy", "updatedAt", "deletedFlag"];
    def commonFieldCount = 0;
    fields.each() {
        if (commonFields.contains(it.fieldName))
            commonFieldCount++;
    }
    if (commonFieldCount >= 8) {
        fields.removeAll { commonFields.contains(it.fieldName) }
        params.isCommon = true
    } else {
        params.isCommon = false
    }

    return fields
}

/**
 * 计算驼峰命名
 * @params name* @params capitalize 首字母是否大写
 * @return
 */
def getCamelCase(String name, boolean capitalize = false) {
    name = name.replaceAll("[A-Z]", "_\$0");
    String[] vars = name.split("-|_");

    StringBuilder sb = new StringBuilder();
    vars.each {
        if (it != null && !it.trim().equals("")) {
            String lower = it.toLowerCase();
            if (sb.length() > 0) {
                sb.append(lower.substring(0, 1).toUpperCase() + lower.substring(1));
            } else {
                if (capitalize) {
                    sb.append(lower.substring(0, 1).toUpperCase() + lower.substring(1));
                } else {
                    sb.append(lower)
                }
            }
        }
    }
    return sb.toString();
}

/**
 * 计算下划线分割的命名
 * @params name* @return
 */
def getSnakelCase(String name) {
    name = name.replaceAll("[A-Z]", "_\$0");
    String[] vars = name.split("-|_");

    StringBuilder sb = new StringBuilder();
    vars.each {
        if (it != null && !it.trim().equals("")) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(it);
        }
    }
    return sb.toString();
}

/**
 * 计算中横线分割的命名
 * @params name* @return
 */
def getKebablCase(String name) {
    name = name.replaceAll("[A-Z]", "_\$0");
    String[] vars = name.split("-|_");

    StringBuilder sb = new StringBuilder();
    vars.each {
        if (it != null && !it.trim().equals("")) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(it);
        }
    }
    return sb.toString();
}

/**
 * 包名的路径
 * @params packageName* @return
 */
def getPackagePath(packageName) {
    String[] vars = packageName.split("\\.")
    File.separator + Arrays.stream(vars).collect(Collectors.joining(File.separator));
}

/**
 * 提取Entity中的field
 * @params sourceFile* @return
 */
def getFields(File sourceFile) {
    def bodyMatcher = sourceFile.text =~ "(?ms)public\\s+class\\s+\\S+.*?\\{(.*)\\}"
    if (!bodyMatcher.find()) {
        println "find entity body error"
        return null;
    }
    def body = bodyMatcher.group(1)

    //剔除内部类
    def subClassMatcher = body =~ "(?ms)public\\s+static\\s+class\\s+\\S+.*?\\{(.*?)\\}"
    if (subClassMatcher.find()) {
        println "subClassMatcher.find()"
        body = subClassMatcher.replaceAll("")
    }

    def list = new ArrayList();
    def paramsMatcher = body =~ "(?ms)(/\\*\\*(.*?)\\*/)[\\s\\S]*?(private\\s+(\\S+)\\s+(\\S+);)"
    while (paramsMatcher.find()) {
        def mess = paramsMatcher.group(2)
        if (mess) {
            mess = mess.replaceAll("\\*", "").replaceAll("\\s", "")
        }
        def type = paramsMatcher.group(4)
        def field = paramsMatcher.group(5)

        def skip = field =~ /serialVersionUID/
        if (!skip) {
            list.add(["type": type, "field": field, "mess": mess])
        }
    }
    return list
}

def getMethodName(String field) {
    if (field && field.length() > 1)
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1)
    else
        return field;
}

def getRandom() {
    Math.abs(new Random().nextLong())
}