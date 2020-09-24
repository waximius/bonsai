#!/bin/bash

#if type -p java; then
#	_java=java
#elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
#	_java="$JAVA_HOME/bin/java"
#else
#	echo "Uh Oh!  Please install Java before running Bonsai."
#fi
#
#if [[ "$_java" ]]; then
#	"$_java" JVMARGS -jar Bonsai.jar
#fi

curdir=`pwd`
export JAVA_HOME=${curdir}/jre/bin
_java="$JAVA_HOME/java"
"$_java" JVMARGS -jar Bonsai.jar
