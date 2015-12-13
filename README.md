# reagent-native

Reagent on android via ReactNative (WIP)

## Usage
Get it imported in Eclipse
* import as an android project, NOT as a gradle project.
* change the src path under property to Reagent/src/main/java
* manually edit .classpath to include facebook react's jar files

How to run:

* Plug in your device or run emulator
* lein cljsbuild auto dev
* react-native run-android
* react-native start

Configure remote host for development (shake your phone).

Or use `adb reverse tcp:8081 tcp:8081` (available only for Android 5.0.0+).

## License

Copyright © 2015 Max Gonzih <gonzih at gmail dot com>

Distributed under the Eclipse Public License either version 1.0 or any later version.
