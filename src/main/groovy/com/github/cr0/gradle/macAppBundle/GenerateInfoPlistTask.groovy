/*
 Copyright 2015 cr0 (Copyright 2014 crotwell)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.github.cr0.gradle.macAppBundle
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class GenerateInfoPlistTask extends DefaultTask {

    static final String XML_DEF_LINE = '<?xml version="1.0" encoding="UTF-8"?>';
    static final String DOCTYPE_LINE = '<!DOCTYPE plist SYSTEM "file://localhost/System/Library/DTDs/PropertyList.dtd">'
    static final String URL_DOCTYPE_LINE = '<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">'
    static final String SHAMELESS_PROMO = '<!-- created with Gradle, http://gradle.org, and the MacAppBundle plugin, https://github.com/cr0/gradle-macappbundle, forked from http://code.google.com/p/gradle-macappbundle -->'

    @OutputFile
    File getPlistFile() {
        return project.file("${-> project.buildDir}/${-> project.macAppBundle.appOutputDir}/${-> project.macAppBundle.appName}.app/Contents/Info.plist")
    }

    @TaskAction
    def void writeInfoPlist() {
        MacAppBundlePluginExtension extension = project.macAppBundle
        extension.javaClassPath.add("\$APP_ROOT/$extension.jarSubdir/*")

        def file = getPlistFile()
        file.parentFile.mkdirs()

        def writer = new BufferedWriter(new FileWriter(file))
        writer.writeLine(XML_DEF_LINE);
        writer.writeLine(URL_DOCTYPE_LINE);
        writer.writeLine(SHAMELESS_PROMO);

        def xml = new MarkupBuilder(writer)
        xml.plist(version: "1.0") {
            dict() {
                key('CFBundleDevelopmentRegion')
                string(extension.bundleDevelopmentRegion)
                key('CFBundleExecutable')
                string(extension.bundleExecutable)
                key('CFBundleIconFile')
                string(project.file(extension.icon).name)
                key('CFBundleIdentifier')
                string(extension.mainClassName)

                key('CFBundleInfoDictionaryVersion')
                string(extension.bundleInfoDictionaryVersion)
                key('CFBundleName')
                string(extension.appName)
                key('CFBundlePackageType')
                string(extension.bundlePackageType)

                key('CFBundleVersion')
                string(project.version)
                key('CFBundleAllowMixedLocalizations')

                if (extension.bundleAllowMixedLocalizations) {
                    string('true')
                } else {
                    string('false')
                }
                key('CFBundleSignature')
                string(extension.creatorCode)

                if (extension.bundleJRE) {
                    def jreVersion = new File(extension.jreHome).getParentFile().getParentFile().getName()
                    key('JVMRuntime')
                    string(jreVersion)
                }
                key('JVMMainClassName')
                string(extension.mainClassName)
                key('JVMOptions')
                array() {
                    extension.javaProperties.each { k, v ->
                        string("-D$k=$v")
                    }
                    extension.javaXProperties.each { v ->
                        string("-X$v")
                    }
                    extension.javaExtras.each { k, v ->
                        string("$k=$v")
                    }
                }
                key('JVMClassPath')
                array() {
                    extension.javaClassPath.each { v ->
                        string("$v")
                    }
                }
                extension.bundleExtras.each { k, v ->
                    key("$k")
                    doValue(xml, v)
                }
            }
        }
        writer.close()
    }

    def doValue(xml, value) {
        if (value instanceof String) {
            xml.string("$value")
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            xml.date(sdf.format(value));
            //YYYY-MM-DD HH:MM:SS
        } else if (value instanceof Short || value instanceof Integer) {
            xml.integer(value)
        } else if (value instanceof Float || value instanceof Double) {
            xml.real(value)
        } else if (value instanceof Boolean) {
            if (value) {
                xml.true()
            } else {
                xml.false()
            }
        } else if (value instanceof Map) {
            xml.dict {
                value.each { subk, subv ->
                    key("$subk")
                    doValue(xml, subv)
                }
            }
        } else if (value instanceof List || value instanceof Object[]) {
            xml.array {
                value.each { subv ->
                    doValue(xml, subv)
                }
            }
        } else throw new InvalidUserDataException("unknown type for plist: " + value)
    }
}
