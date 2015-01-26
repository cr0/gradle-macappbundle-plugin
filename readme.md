### gradle-macappbundle

Create OS X application bundles from Java projects using gradle. A JRE can optionally be included.

#### It's not my work, though...

This is a fork of [gradle-macappbundle](https://code.google.com/p/gradle-macappbundle) by @crotwell. However, it differs in a few things:

* Instead of providing two binary stubs (for Apple Java and Oracle Java) this version uses infinitekind's extended `JavaAppLauncher` which is available at [bitbucket](https://bitbucket.org/infinitekind/appbundler/).
* **Now it's also possible to extend the classpath!** Oracle's version did not allow altering the classpath, in fact it always used the default `Java` folder within an app bundle. Using the new `JVMClassPath` property in `Info.plist` the classpath can include either single `jar`s or whole folders when the wildcard `*` is used. Of course, the contents of the `Java` folder within the bundle are also included.
* Support for Apple Java has been removed since Apple stopped the development and recommends using Oracle's version.

#### How to use

First, you need to include the `macappbundle` plugin using the following fancy gradle 2.1+ syntax. The plugin is available `jcenter`.

```
plugins {
  id "com.github.cr0.macappbundle" version "3.0.0"
}
```

Please have a look at [bintray](https://bintray.com/cr0/gradle-plugins/gradle-macappbundle-plugin) for more details.

The following example shows a working configuration including the extendable classpath feature.

```
macAppBundle {
	appName = "Foo App"
	appCategory = "public.app-category.utilities"
	icon = "gradle/icon/fooapp.icns"
	agent = true

	version = "0.1.3-alpha+SNAPSHOT.123456789"
	shortVersion = "0.1.3a (nightly)"

	mainClassName = "org.foo.app.Launcher"

	bundleJRE = false
	bundleExecutable = "FooApp"
	bundleIdentifier = "org.foo.app"
	bundleCopyright = "Copyright 2015 Foo App"

	bundleExtras.put("NSHighResolutionCapable", "true")

	javaProperties.put("file.encoding", "utf-8")
	javaXProperties.add("mx2048M")
	javaXProperties.add("startOnFirstThread")

	javaClassPath.add("/Users/\$CURRENT_USER/.config/fooapp/plugins/*")

	archiveName = "Foo App ${pluginVersion}"
}
```
There are a few other options available -- if you're interested have a look at the [source code](https://github.com/cr0/gradle-macappbundle-plugin/blob/master/src/main/groovy/com/github/cr0/gradle/macAppBundle/MacAppBundlePluginExtension.groovy).

If you use `createApp`, the plugin creates an app named `Foo App.app` in the `build/macApp` (settable using `appOutputDir`) directory.

You can also package the app as either a ZIP file (`createAppZip`) or a DMG with an optional background image (`createDmg`; applicable only on OS X).

#### Licenses

Shoutouts to crotwell and infinitekind.

* [crotwell/gradle-macappbundle](http://code.google.com/p/gradle-macappbundle) is licensed under the *Apache License 2.0*
* [infinitekind/appbundler](https://bitbucket.org/infinitekind/appbundler/) is licensed under the *GNU General Public License version 2*