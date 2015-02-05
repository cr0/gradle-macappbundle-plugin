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

import groovy.transform.EqualsAndHashCode
import org.gradle.api.Project

@EqualsAndHashCode
class MacAppBundlePluginExtension implements Serializable {

    /** The output directory for building the app, relative to the build directory. */
    String appOutputDir = "macApp"

    /** Creator code, issued by Apple. Four question marks is the default if no code has been issued. */
    String creatorCode = '????'

    /** Icon for this application, probably needs to be a '.icns' file (default Apple GenericApp.icns). */
    String icon = 'GenericApp.icns'

    /** The JVM version needed. Can append a + to set a minimum. */
    String jvmVersion

    /** The name of the application, without the .app extension. (default $project.name). */
    String appName

    /** The name of the volume (default project.name-project.version). */
    String volumeName

    /** The base name of the dmg/zip file, without the extension (default project.name-project.version). */
    String archiveName

    /** The output directory for building the dmg, relative to the build directory. */
    String archiveOutputDir

    /** The background image for the DMG. */
    String backgroundImage

    /** The initial class to start the application, must contain a public static void main method. */
    String mainClassName

    /** Additional properties for the JVM automatically prefixed with -D. */
    Map javaProperties = [:]

    /** Additional, Oracle-specific properties for the JVM automatically prefixed with -X. */
    List javaXProperties = []

    /** List of folders to search for additional jars including wildcard support. */
    List javaClassPath = []

    /** List of arguments to pass to the application. */
    List arguments = []

    /**  Directory to put the project jars (default 'Java'). */
    String jarSubdir = 'Java'

    /** The name of the executable run by the bundle (default 'JavaAppLauncher'). */
    String bundleExecutable = 'JavaAppLauncher'

    /** BundleAllowMixedLocalizations (default true). */
    boolean bundleAllowMixedLocalizations = true

    /** FQDN which identifies the app within the OS. */
    String bundleIdentifier

    /** The development region (default 'English'). */
    String bundleDevelopmentRegion = 'English'

    /** Additional keys to add the Plist.info */
    Map bundleExtras = [:]

    /** A valid category which is used in the Mac App Store for categorization. */
    String appCategory

    /** Version of the app. */
    String version

    /** Version of the app which is shown to the user. */
    String shortVersion

    /** Agent apps don't add an icon to the dock while running. */
    boolean agent = false

    /** Copyright notice to place in the Plist.info file (default 'Copyright $YEAR $projectName'). */
    String bundleCopyright

    /** BundlePackageType (default 'APPL') */
    String bundlePackageType = 'APPL'

    /** BundleInfoDictionaryVersion (default 6.0) */
    String bundleInfoDictionaryVersion = '6.0'

    /** Add a JRE to the bundle (default false) */
    boolean bundleJRE = false;

    /** Directory from which to copy the JRE. Generally this will be the same as
     $JAVA_HOME or the result of /usr/libexec/java_home. Note that to be compatible
     with the appbundler utility from Oracle, this is usually the Contents/Home
     subdirectory of the JDK install.

     If bundleJRE is true, but jreHome is null, it will be set to the output of
     /usr/libexec/java_home, which should be correct in most cases.

     For example:
     /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home
     */
    String jreHome

    String certIdentity = null
    String codeSignCmd = "codesign"
    String keyChain = null
    String setFileCmd = "/usr/bin/SetFile"

    // http://asmaloney.com/2013/07/howto/packaging-a-mac-os-x-application-using-a-dmg/
    String backgroundScript = """
    tell application "Finder"
        tell disk "\${VOL_NAME}"
           open
           set current view of container window to icon view
           set toolbar visible of container window to false
           set statusbar visible of container window to false
           set the bounds of container window to {400, 100, 920, 440}
           set viewOptions to the icon view options of container window
           set arrangement of viewOptions to not arranged
           set icon size of viewOptions to 72
           set background picture of viewOptions to file ".background:\${DMG_BACKGROUND_IMG}"
           set position of item "\${APP_NAME}.app" of container window to {160, 205}
           set position of item "Applications" of container window to {360, 205}
           close
           open
           update without registering applications
           delay 2
        end tell
     end tell
"""

    /** Enable debug mode will tell the java launcher to be chatty */
    boolean debug = false;

    /** configures default values that depend on values set in the build file like version, and so must be
     * done late in the run order, after the build script is evaluated but before any task in the plugin is run
     * @param project
     */
    void configureDefaults(Project project) {
        if (appName == null) appName = "${-> project.name}"
        if (volumeName == null) volumeName = "${-> project.name}-${-> project.version}"
        if (archiveName == null) archiveName = "${-> project.name}-${-> project.version}"
        if (archiveOutputDir == null) archiveOutputDir = "${-> project.distsDirName}"
        if (version == null) version = "${-> project.version}"
        if (shortVersion == null) shortVersion = "${-> project.version}"
        if (bundleCopyright == null) bundleCopyright = "Copyright ${-> Calendar.instance.get(Calendar.YEAR)} ${-> project.name}"
        if (jvmVersion == null) jvmVersion = project.targetCompatibility.toString() + "+"
        if (bundleJRE && jreHome == null) jreHome = findJREHome()
    }

    public String getJREDirName() {
        return new File(jreHome).getParentFile().getParentFile().getName()
    }

    private String findJREHome() {
        File jhFile = new File("/usr/libexec/java_home");

        if (!jhFile.exists()) throw new RuntimeException("bundleJRE not set and unable to find ${-> jhFile.absolutePath}, is oracle java installed?")

        def proc = jhFile.absolutePath.execute()
        proc.waitFor()
        def retCode = proc.exitValue();

        if (retCode) throw new RuntimeException("bundleJRE not set and return code of ${-> jhFile.absolutePath} is nonzero: $retCode")

        return proc.in.text.trim();
    }

}
