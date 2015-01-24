### gradle-macappbundle

Create OS X application bundles from Java projects using gradle. A JRE can optionally be included.

#### It's not my work, though...

This is a fork of [gradle-macappbundle](https://code.google.com/p/gradle-macappbundle) by @crotwell. However, it differs in a few things:

* Instead of providing two binary stubs (for Apple Java and Oracle Java) this version uses infinitekind's extended `JavaAppLauncher` which is available at [bitbucket](https://bitbucket.org/infinitekind/appbundler/).
* Now it's also possible to extend the classpath! Oracle's version did not allow altering the classpath, in fact it always used the default `Java` folder within an app bundle. Using the new `JVMClassPath` property in `Info.plist` the classpath can include either single `jar`s or whole folders when the wildcard `*` is used. Of course, the contents of the `Java` folder within the bundle are also included.
* Support for Apple Java has been removed since Apple stopped the development and recommends using Oracle's version.

#### Licenses

Shoutouts to crotwell and infinitekind.

* [crotwell/gradle-macappbundle](http://code.google.com/p/gradle-macappbundle) is licensed under the *Apache License 2.0*
* [infinitekind/appbundler](https://bitbucket.org/infinitekind/appbundler/) is licensed under the *GNU General Public License version 2*


#### Todo

Once all features from [infinitekind/appbundler](https://bitbucket.org/infinitekind/appbundler/) are included and when I did some code cleanup, I'll submit this plugin to a repository so that you all can use it to create awesome OS X applications.