import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.stream.Collectors

//读取项目的基础配置
def paramsFile = new File("jCoder.json");
params = new JsonSlurper().parseText(paramsFile.text)

readSdkModuleFolder()
readSdkModulePackage()

readEntityName()
readRoute()

readVueWorkFolder()
readVuePath()

saveParams(paramsFile)

if (!params.route.endsWith("crud")) {
    println "ERROR. Route must end in crud."
    return
}
def routePrefix = params.route.replaceAll("-crud", "")
params['routePrefix'] = routePrefix;
params['methodPrefix'] = getCamelCase(routePrefix, false);

def templates = [
        ["templateFile": "vue/SysRoleApi.ts", "fileType": "api"],
        ["templateFile": "vue/SysRoleEdit.vue", "fileType": "dialog"],
        ["templateFile": "vue/SysRoleIndex.vue", "fileType": "index"],
]
templates.forEach(template -> {
    generateFile(template)
})

def command = "cmd /c cd ${params.vueWorkFolder} && npm run lint:eslint"
def process = command.execute()
process.waitForProcessOutput()

/**
 * 生成文件
 * @return
 */
def generateFile(template) {
    File outFilePath = new File(params.vueBaseFolder);
    File outFile;
    switch (template["fileType"]) {
        case "api":
            outFile = new File(outFilePath, "${params.entityName}Api.ts")
            break;
        case "dialog":
            outFile = new File(outFilePath, "/components/${params.entityName}Edit.vue")
            break;
        case "index":
            outFile = new File(outFilePath, "${params.entityName}Index.vue")
            break;
    }

    if (!outFile.getParentFile().exists()) {
        outFile.getParentFile().mkdirs();
    }
    if (outFile.exists()) {
        //println "skipped : ${outFile.getAbsoluteFile()}"
        println "delete : ${outFile.getAbsoluteFile()}"
        outFile.delete()
    }
    {
        println "File : ${outFile.getAbsoluteFile()}"
        def engine = new groovy.text.GStringTemplateEngine()
        def templateFile = new File("template/${template["templateFile"]}")
        def result = engine.createTemplate(templateFile).make(params)

        PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"))
        output.withPrintWriter { out ->
            out.println "${result}"
        }
    }
}

//读取工作模块目录
def readWorkModuleFolder() {
    println "workModuleFolder : ${params.workModuleFolder}"
    def input = System.console().readLine 'workModuleFolder : '
    if (!(input?.trim())) {
        input = params.workModuleFolder
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. workModuleFolder pattern does not match."
        readWorkModuleFolder()
        return
    }
    params.workModuleFolder = input?.trim()

    def workBaseFolder = System.getProperty("user.dir") +
            File.separator + params.workModuleFolder +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "java";

    params.workBaseFolder = workBaseFolder
}

//读取工作模块包名
def readWorkModulePackage() {
    println "workModulePackage : ${params.workModulePackage}"
    def input = System.console().readLine 'workModulePackage : '
    if (!(input?.trim())) {
        input = params.workModulePackage
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. sdkModuleFolder pattern does not match."
        readWorkModulePackage()
        return
    }
    params.workModulePackage = input?.trim()
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

//读取vue工作目录
def readVueWorkFolder() {
    if (!params.vueWorkFolder)
        println 'Please input vue work folder, example : d:\\vue-project'
    else
        println "vueWorkFolder : ${params.vueWorkFolder}"
    def input = System.console().readLine 'vueWorkFolder : '
    if (!(input?.trim())) {
        input = params.vueWorkFolder
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. sdkModuleFolder pattern does not match."
        readVueWorkFolder()
        return
    }
    params.vueWorkFolder = input?.trim()
}


//读取vue工作目录
def readVuePath() {
    if (!params.vuePath)
        println 'Please input vue module folder, example : /system/roleManager'
    else
        println "vuePath : ${params.vuePath}"
    def input = System.console().readLine 'vueWorkFolder : '
    if (!(input?.trim())) {
        input = params.vuePath
    }
    params.vuePath = input?.trim()

    def vueBaseFolder = params.vueWorkFolder +
            File.separator + "src" +
            File.separator + "views" +
            File.separator + params.vuePath
    params.vueBaseFolder = vueBaseFolder
    //vue组件名
    def array = params.vuePath.split("/");
    if (array) {
        params.vueName = array[array.length - 1]
    }
    //vueComponent
    params.vueComponent = params.vuePath + "/" + getCamelCase(params.entityClassName.replaceAll("Entity", "Index"))
}

//读取route
def readRoute() {
    if (!params.route)
        println '''/**
 * 请输入路由名称，例如:system-user-add
 * 以短横线分隔的多个小写英文单词，建议命名方式为：[端口名]-[模块名]-[功能名]-[操作]
 * 端口名通常为用户群体的分类方式，例如：admin、patient、doctor、common等
 * 模块和功能名根据实际业务进行定义，简单业务也可以合并为一个单词
 * 操作可选谓词：query、list、get、save、delete、crud，也可以自定义谓词
 */'''
    else
        println "Route : ${params.route}"
    def route = System.console().readLine 'Route : '
    if (!(route?.trim())) {
        route = params.route
    }
    def ok = route ==~ /^[a-z]{2,}[a-z0-9]*(-[a-z]{2,}[a-z0-9]*){1,9}$/
    if (!ok) {
        println "ERROR. Route pattern does not match."
        readRoute()
        return
    }
    params.route = route?.trim()
}

//读取Entity
def readEntityName() {
    if (!params.entityClassName)
        println 'Please input entity name, example : UserEntity'
    else
        println "Entity : ${params.entityClassName}"
    def input = System.console().readLine 'Entity : '
    if (!(input?.trim())) {
        input = params.entityClassName
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. sdkModuleFolder pattern does not match."
        readEntityName()
        return
    }

    params.entityClassName = input?.trim()
    params.entityName = params.entityClassName.replaceAll("Entity", "")

    def entityPath = new File(params.sdkBaseFolder + getPackagePath(params.sdkModulePackage + ".dao.entity"))
    File entityFile = new File(entityPath, params.entityClassName + ".java");
    if (!entityFile.exists()) {
        println "ERROR. Entity ${entityFile.absolutePath} is not found."
        readEntityName()
        return
    }

    params.managerClassName = params.entityClassName.replaceAll("Entity", "Manager")

    def fields = getFields(entityFile)
    params.fields = fields
    for (def f : fields) {
        def firstIdField = f['field']
        params.firstIdField = firstIdField
        params.firstIdGetMethod = 'get' + getCamelCase(firstIdField, true)
        break
    }
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