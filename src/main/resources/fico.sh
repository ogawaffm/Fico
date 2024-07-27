#!/bin/bash
if ! [[ -v JAVA_HOME ]] || [ "$JAVA_HOME" == "" ]; then
    JAVA="$(which java)"
    if ! echo $JAVA | grep -q 'java'; then
        echo "ERROR: JAVA_HOME is not set and no Java found using PATH variable."
        echo "       Set JAVA_HOME to a JDK root directory [Adviced] or extend"
        echo "       the PATH variable by a reference to a JDK 'bin' directory."
        echo "       Version must be at least 11. Check using: java --version"
        echo
        exit 1
    else
        echo "WARN: JAVA_HOME is not set. Using Java found via PATH variable which is:"
        echo "      $JAVA"
        echo "      Set JAVA_HOME variable to a JDK root directory [Adviced]."
        echo "      Version must be at least 11. Check using: java --version"
        echo
    fi
    JAVA=java
else
    JAVA=$JAVA_HOME/bin/java
fi
APP_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)
if [ "$(uname)" == "Darwin" ]; then
    SAP_LIB=$APP_DIR/sap/jco/mac64
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    SAP_LIB=$APP_DIR/sap/jco/linux64
else
    echo ERROR: Cannot set library path for native code, because of unrecognized operating system.
    echo
fi
# TODO DYLD_LIBRARY_PATH ?!?!?!? for mac
# set cp to /sap/jco first, than to /lib since later has a disallowed jco jar with a version in its name
$JAVA -Djava.library.path="$SAP_LIB" -cp "$APP_DIR":"$APP_DIR/sap/jco/*":"$SAP_LIB":"$APP_DIR/lib/*" your.com.Main "$@"