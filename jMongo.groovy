import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.stream.Collectors

//读取项目的基础配置
def paramsFile = new File("jCoder.json");
params = new JsonSlurper().parseText(paramsFile.text)

readSdkModuleFolder()
readSdkModulePackage()

readCollection()

saveParams(paramsFile)

def templates = [
        ["fileType": "entity", "template": "dao/UserInfoEntity.java", 'postfix': 'EditQO'],
        ["fileType": "repository", "template": "dao/UserInfoRepository.java", 'postfix': 'EditQO'],
        ["fileType": "manager", "template": "dao/UserInfoManager.java", 'postfix': 'EditQO'],
]
println JsonOutput.toJson(params)

templates.forEach(file -> {
    generateFile(file["fileType"], file["template"], file['postfix'])
})

def generateFile(fileType, template, postfix) {
    String camelCaseName = getCamelCase(params.collection, true);
    String outFilePath
    switch (fileType) {
        case "entity":
            params.entityClassName = camelCaseName + "Entity"
            params.entityPackage = params.sdkModulePackage + ".dao.entity"

            outFilePath = params.sdkBaseFolder +
                    File.separator + getPackagePath(params.entityPackage) +
                    File.separator + camelCaseName + "Entity.java"
            break;

        case "repository":
            params.repositoryClassName = camelCaseName + "Repository"
            params.repositoryPackage = params.sdkModulePackage + ".dao.repository"

            outFilePath = params.sdkBaseFolder +
                    File.separator + getPackagePath(params.repositoryPackage) +
                    File.separator + camelCaseName + "Repository.java"
            break;

        case "manager":
            params.managerClassName = camelCaseName + "Manager"
            params.managerPackage = params.sdkModulePackage + ".manager"

            outFilePath = params.sdkBaseFolder +
                    File.separator + getPackagePath(params.managerPackage) +
                    File.separator + camelCaseName + "Manager.java"
            break;
    }

    File outFile = new File(outFilePath);

    if (!outFile.getParentFile().exists()) {
        outFile.getParentFile().mkdirs();
    }

    println "File : ${outFile.getAbsoluteFile()}"
    if (outFile.exists()) {
        println "skipped"
    } else {
        def engine = new groovy.text.GStringTemplateEngine()
        def templateFile = new File("template/${template}")
        def result = engine.createTemplate(templateFile).make(params)

        PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"))
        output.withPrintWriter { out ->
            {
                out.println "${result}".trim()
            }
        }
    }
}

//读取SDK模块目录
def readSdkModuleFolder() {
    println "sdkModuleFolder : ${params.sdkModuleFolder}"
    def input = System.console().readLine 'sdkModuleFolder : '
    if (!(input?.trim())) {
        input = params.sdkModuleFolder
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. sdkModuleFolder pattern does not match."
        readSdkModuleFolder()
        return
    }
    params.sdkModuleFolder = input?.trim()

    def sdkBaseFolder = System.getProperty("user.dir") +
            File.separator + params.sdkModuleFolder +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "java";
    params.sdkBaseFolder = sdkBaseFolder
}

//SDK模块包名
def readSdkModulePackage() {
    println "sdkModulePackage : ${params.sdkModulePackage}"
    def input = System.console().readLine 'sdkModulePackage : '
    if (!(input?.trim())) {
        input = params.sdkModulePackage
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. sdkModuleFolder pattern does not match."
        readSdkModulePackage()
        return
    }
    params.sdkModulePackage = input?.trim()
}

//读取collection
def readCollection() {
    println '/**'
    println ' * 请输入mongo数据表名称，例如:dict_region'
    println ' * 以短下划线分隔的多个小写英文单词，建议命名方式为：[模块名]_[功能名]'
    println ' */'
    def tableName = System.console().readLine 'Table Name : '
    def ok = tableName ==~ /^[a-z]{2,}[a-z0-9]*(_[a-z]{2,}[a-z0-9]*){1,9}$/
    if (!ok) {
        println "ERROR. Table name pattern does not match."
        return
    }

    //输入的表名
    params.collection = tableName?.trim()
}

def saveParams(File paramsFile) {
    PrintWriter paramsOutput = new PrintWriter(new OutputStreamWriter(new FileOutputStream(paramsFile), "utf-8"))
    paramsOutput.withPrintWriter { out ->
        out.println JsonOutput.toJson(params)
    }
}

/**
 * 计算驼峰命名
 * @params name* @params capitalize 首字母是否大写
 * @return
 */
def getCamelCase(String name, boolean capitalize = false) {
    name = name.replaceAll("[A-Z]", "_\$0");
    String[] vars = name.split("-|_");

    String s = Arrays.stream(vars)
            .filter(t -> t != null && !t.trim().equals(""))
            .map(t -> {
                String lower = t.toLowerCase();
                return lower.substring(0, 1).toUpperCase() + lower.substring(1);
            })
            .collect(Collectors.joining())
    if (capitalize || s.length() == 1)
        return s;
    else
        return s.substring(0, 1).toLowerCase() + s.substring(1);
}

/**
 * 计算下划线分割的命名
 * @params name* @return
 */
def getSnakelCase(String name) {
    name = name.replaceAll("[A-Z]", "_\$0");
    String[] vars = name.split("-|_");

    String s = Arrays.stream(vars)
            .filter(t -> t != null && !t.trim().equals(""))
            .map(t -> t.toLowerCase())
            .collect(Collectors.joining("_"))
    return s;
}

/**
 * 计算中横线分割的命名
 * @params name* @return
 */
def getKebablCase(String name) {
    name = name.replaceAll("[A-Z]", "_\$0");
    String[] vars = name.split("-|_");

    String s = Arrays.stream(vars)
            .filter(t -> t != null && !t.trim().equals(""))
            .map(t -> t.toLowerCase())
            .collect(Collectors.joining("-"))
    return s;
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