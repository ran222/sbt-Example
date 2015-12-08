name := "finagle-example"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.7"
libraryDependencies += "log4j" % "log4j" % "1.2.16"
libraryDependencies += "com.twitter" %% "finagle-http" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-thrift" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-exp" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-kestrel" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-memcached" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-mysql" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-redis" % "6.30.0"
libraryDependencies += "com.twitter" %% "finagle-serversets" % "6.30.0"