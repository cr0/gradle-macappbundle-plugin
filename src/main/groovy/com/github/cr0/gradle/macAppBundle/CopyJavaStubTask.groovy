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

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

class CopyJavaStubTask extends DefaultTask {

    @TaskAction
    def void writeStub() {
        def dest = project.file("${project.buildDir}/${project.macAppBundle.appOutputDir}/${project.macAppBundle.appName}.app/Contents/MacOS/${project.macAppBundle.bundleExecutable}")
        dest.parentFile.mkdirs()

        def outStream = new BufferedOutputStream(new FileOutputStream(dest))
        def buf = new byte[1024]
        def inStream = this.getClass().getClassLoader().getResourceAsStream("com/github/cr0/macAppBundle/JavaAppLauncher")

        if (inStream == null) throw new RuntimeException("Can't find resource for JavaAppLauncher in jar")

        int numRead = inStream.read(buf)

        while (numRead > 0) {
            outStream.write(buf, 0, numRead)
            numRead = inStream.read(buf)
        }

        inStream.close()
        outStream.close()
    }
}
