import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.util.stream.Collectors

//读取项目的基础配置
def paramsFile = new File("jCoder.json");
params = new JsonSlurper().parseText(paramsFile.text)

readContext()
readTags()

readWorkModuleFolder()
readWorkModulePackage()

readSdkModuleFolder()
readSdkModulePackage()

readRoute()
readEntityName()

saveParams(paramsFile)

def templates = [
        "query"     : [
                ["fileType": "qo", "template": "ctrl/QueryQO.java", 'postfix': 'QueryQO'],
                ["fileType": "vo", "template": "ctrl/ListVO.java", 'postfix': 'ListVO'],
                ["fileType": "controller", "template": "ctrl/AdminUserInfoQueryController.java"]
        ],
        "list"      : [
                ["fileType": "qo", "template": "ctrl/ListQO.java", 'postfix': 'ListQO'],
                ["fileType": "vo", "template": "ctrl/ListVO.java", 'postfix': 'ListVO'],
                ["fileType": "controller", "template": "ctrl/AdminUserInfoListController.java"]
        ],
        "get"       : [
                ["fileType": "qo", "template": "ctrl/IdQO.java", 'postfix': 'IdQO'],
                ["fileType": "vo", "template": "ctrl/ListVO.java", 'postfix': 'ListVO'],
                ["fileType": "vo", "template": "ctrl/DetailVO.java", 'postfix': 'DetailVO'],
                ["fileType": "controller", "template": "ctrl/AdminUserInfoGetController.java"]
        ],
        "save"      : [
                ["fileType": "qo", "template": "ctrl/EditQO.java", 'postfix': 'EditQO'],
                ["fileType": "controller", "template": "ctrl/AdminUserInfoSaveController.java"]
        ],
        "delete"    : [
                ["fileType": "qo", "template": "ctrl/IdQO.java", 'postfix': 'IdQO'],
                ["fileType": "controller", "template": "ctrl/AdminUserInfoDeleteController.java"]
        ],
        "set-sort"  : [
                ["fileType": "controller", "template": "ctrl/AdminUserInfoSetSortController.java"]
        ],
        "set-status": [
                ["fileType": "controller", "template": "ctrl/AdminUserInfoSetStatusController.java"]
        ],
        "noEntity"  : [
                ["fileType": "controller", "template": "ctrl/AdminNoEntityController.java"]
        ]
]

def ops
if (params.routeRaw.endsWith("crud")) {
    ops = ['query', 'list', 'get', 'save', 'delete', 'set-sort', 'set-status'];
} else {
    if (!params.entityClassName) {
        ops = ['noEntity']
    } else if (params.routeRaw.endsWith('set-sort')) {
        ops = ['set-sort']
    } else if (params.routeRaw.endsWith('set-status')) {
        ops = ['set-status']
    } else {
        String[] s = params.routeRaw.split("-");
        ops = [s[s.length - 1]]
    }
}
ops.forEach(op -> {
    def route = params.routeRaw.replaceAll("-crud", "-" + op)
    List files = templates[op]
    if (!files) files = templates["list"]

    files.forEach(file -> {
        params['route'] = route
        generateFile(route, file["fileType"], file["template"], file['postfix'])
    })
})


/**
 * 生成文件
 * @param route ：路由
 * @param fileType ：controller、qo、vo
 * @param template : 模版文件
 * @param postfix : 类名后缀，qo、vo会重复使用，这里指定专有名称
 * @return
 */
def generateFile(route, fileType, template, postfix) {
    //printf("+++ route:%s, fileType:%s, template:%s +++, postfix:%s\n", [route, fileType, template, postfix])

    String className = getCamelCase(route, true)
    String outFilePath
    switch (fileType) {
        case "qo":
            params.qoClassName = params.entityClassName.replaceAll("Entity", "").replaceAll("Tab", "") + postfix
            params.qoPackage = params.workModulePackage + ".qo"

            outFilePath = params.workBaseFolder +
                    File.separator + getPackagePath(params.qoPackage) +
                    File.separator + params.qoClassName + ".java"
            break;

        case "vo":
            params.voClassName = params.entityClassName.replaceAll("Entity", "").replaceAll("Tab", "") + postfix
            params.voPackage = params.workModulePackage + ".vo"

            outFilePath = params.workBaseFolder +
                    File.separator + getPackagePath(params.voPackage) +
                    File.separator + params.voClassName + ".java"
            break;

        case "controller":
            params.ctrlClassName = className + "Controller"
            params.ctrlPackage = params.workModulePackage + ".controller"

            outFilePath = params.workBaseFolder +
                    File.separator + getPackagePath(params.ctrlPackage) +
                    File.separator + params.ctrlClassName + ".java"
            break;

        default:
            throw new Exception("fileType undefined")
    }


    File outFile = new File(outFilePath);
    if (!outFile.getParentFile().exists()) {
        outFile.getParentFile().mkdirs();
    }

    if (outFile.exists()) {
        println "skipped : ${outFile.getAbsoluteFile()}"
    } else {
        println "File : ${outFile.getAbsoluteFile()}"
        def engine = new groovy.text.GStringTemplateEngine()
        def templateFile = new File("template/${template}")
        def result = engine.createTemplate(templateFile).make(params)

        PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"))
        output.withPrintWriter { out ->
            out.println "${result}".trim()
        }
    }
}

//读取上下文
def readContext() {
    println "context : ${params.context}"
    def input = System.console().readLine 'context : '
    if (!(input?.trim())) {
        input = params.context
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. workModuleFolder pattern does not match."
        readContext()
        return
    }
    params.context = input?.trim()
}

//读取swagger分类标签
def readTags() {
    println "tags : ${params.tags}"
    def input = System.console().readLine 'tags : '
    if (!(input?.trim())) {
        input = params.tags
    }
    def ok = input ==~ /^.{2,}$/
    if (!ok) {
        println "ERROR. workModuleFolder pattern does not match."
        readTags()
        return
    }
    params.tags = input?.trim()
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
def readVueModuleFolder() {
    if (!params.vueModuleFolder)
        println 'Please input vue module folder, example : system'
    else
        println "vueModuleFolder : ${params.vueModuleFolder}"
    def input = System.console().readLine 'vueWorkFolder : '
    if (!input) {
        input = params.vueModuleFolder
    }
    params.vueModuleFolder = input?.trim()

    def vueBaseFolder = params.vueWorkFolder +
            File.separator + "src" +
            File.separator + "views"
    if (params.vueModuleFolder)
        vueBaseFolder = vueBaseFolder +
                File.separator + params.vueModuleFolder
    params.vueBaseFolder = vueBaseFolder
}

//读取route
def readRoute() {
    if (!params.routeRaw)
        println '''/**
 * 请输入路由名称，例如:system-user-add
 * 以短横线分隔的多个小写英文单词，建议命名方式为：[端口名]-[模块名]-[功能名]-[操作]
 * 端口名通常为用户群体的分类方式，例如：admin、patient、doctor、common等
 * 模块和功能名根据实际业务进行定义，简单业务也可以合并为一个单词
 * 操作可选谓词：query、list、get、save、delete、crud，也可以自定义谓词
 */
'''
    else
        println "Route : ${params.routeRaw}"
    def input = System.console().readLine 'Route : '
    if (!(input?.trim())) {
        input = params.routeRaw
    }
    def ok = input ==~ /^[a-z]{2,}[a-z0-9]*(-[a-z]{2,}[a-z0-9]*){1,9}$/
    if (!ok) {
        println "ERROR. Route pattern does not match."
        readRoute()
        return
    }
    params.routeRaw = input?.trim()
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
    params.entityObjectName = params.entityClassName.substring(0, 1).toLowerCase() + params.entityClassName.substring(1)
    params.isMongo = params.entityClassName ==~ /.*Entity$/

    def entityPath = new File(params.sdkBaseFolder + getPackagePath(params.sdkModulePackage + ".dao.entity"))
    File entityFile = new File(entityPath, params.entityClassName + ".java");
    if (!entityFile.exists()) {
        println "ERROR. Entity ${entityFile.absolutePath} is not found."
        readEntityName()
        return
    }

    params.managerClassName = params.isMongo
            ? params.entityClassName.replaceAll("Entity", "Manager")
            : params.entityClassName.replaceAll("Tab", "Manager")
    params.managerObjectName = params.managerClassName.substring(0, 1).toLowerCase() + params.managerClassName.substring(1)

    def fields = getFields(entityFile)
    params.fields = fields

    fields.forEach(field -> {
        if (field.isKey) {
            params.idGetMethod = getMethodName(field.fieldName)
            params.idFieldName = field.fieldName
        }
    })
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
    def paramsMatcher = body =~ "(?ms)((@Id)?\\s*/\\*\\*(.*?)\\*/)([\\s\\S]*?)(private\\s+([\\s\\S]+?)\\s+(\\S+);)"
    while (paramsMatcher.find()) {
        def id = paramsMatcher.group(2)
        def mess = paramsMatcher.group(3)
        if (mess) {
            mess = mess.replaceAll("\\*", "").replaceAll("\\s", "").replaceAll("\"", "\\\\\"")
        }
        def anno = paramsMatcher.group(4)
        def type = paramsMatcher.group(6)
        def field = paramsMatcher.group(7)

        def skip = field =~ /serialVersionUID/
        def isKey = (anno =~ '@Id') || (id =~ '@Id')
        if (!skip) {
            list.add(["type": type, "fieldName": field, "columnName": field, "isKey": isKey.find(), "comment": mess])
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