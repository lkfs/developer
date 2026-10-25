import groovy.io.FileType

def path = new File(".")

files = []
path.eachFileRecurse(FileType.FILES) { file ->
    if (file.name.endsWith('pom.xml')) {
        files << file
    }
}

start();

//{{
def start() {
    def currentVersion = read();
    println "/** Current version : ${currentVersion} **/"
    newVersion = System.console().readLine 'New Version : '
    def ok = newVersion ==~ /^\d+(\.\d+){2}$/
    if (!ok) {
        println "ERROR. Version number is error."
        return
    }
    update();
}

def read() {
    def version = "";
    files.each { file ->
        {
            //逐行读取，并替换
            def venusGroup = false
            file.eachLine { line ->
                {
                    //判断是否venusoft组
                    def matcherGroup = line =~ "<groupId>(.*)</groupId>"
                    if (matcherGroup.find()) {
                        groupName = matcherGroup.group(1)
                        if (groupName == 'com.venusoft')
                            venusGroup = true;
                        else
                            venusGroup = false;
                    }

                    //替换版本
                    def matcherVersion = line =~ "<version>([\\d\\.]*)</version>"
                    if (matcherVersion.find() && venusGroup) {
                        version = matcherVersion.group(1)
                        return true;
                    }
                }
            }

        }
    }
    return version;
}

def update() {

    files.each { file ->
        {
            println "The pom file : ${file.getAbsolutePath()}"
            //逐行读取，并替换
            def lines = new ArrayList();
            def venusGroup = false
            file.eachLine { line ->
                {
                    //判断是否venusoft组
                    def matcherGroup = line =~ "<groupId>(.*)</groupId>"
                    if (matcherGroup.find()) {
                        groupName = matcherGroup.group(1)
                        if (groupName == 'com.venusoft')
                            venusGroup = true;
                        else
                            venusGroup = false;
                    }

                    //替换版本
                    def matcherVersion = line =~ "<version>([\\d\\.]*)</version>"
                    if (matcherVersion.find() && venusGroup) {
                        version = matcherVersion.group(1)
                        line = line.replaceAll(version, newVersion);
                        println "version : ${version} => ${newVersion}"
                    }

                    //替换 <developer.version>3.0.1</developer.version>
                    def matcherDeveloperVersion = line =~ "<developer.version>([\\d\\.]*)</developer.version>"
                    if (matcherDeveloperVersion.find()) {
                        version = matcherDeveloperVersion.group(1)
                        line = line.replaceAll(version, newVersion);
                        println "matcherDeveloperVersion : ${version} => ${newVersion}"
                    }
                    lines.add(line);
                }
            }

            //重新写入
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))
            output.withPrintWriter { out ->
                {
                    lines.forEach(line -> out.println(line))
                }
            }
        }
    }
}