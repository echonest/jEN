jar=lib/jEN.jar:lib/json_simple-1.1.jar
junit=lib/junit-4.8.1.jar

echo java -DECHO_NEST_API_KEY=$ECHO_NEST_API_KEY -cp $jar:$junit org.junit.runner.JUnitCore com.echonest.api.v4.tests.$1
java -DECHO_NEST_API_KEY=$ECHO_NEST_API_KEY -cp $jar:$junit org.junit.runner.JUnitCore com.echonest.api.v4.tests.$1
