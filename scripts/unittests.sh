jar=lib/jEN.jar:lib/json_simple-1.1.jar
junit=lib/junit-4.8.1.jar
testclasses='com.echonest.api.v4.tests.APITests com.echonest.api.v4.tests.ArtistTests com.echonest.api.v4.tests.SongTests com.echonest.api.v4.tests.TrackTests'


echo tc $testclasses
echo java -cp $jar:$junit org.junit.runner.JUnitCore $testclasses
java -DECHO_NEST_API_KEY=$ECHO_NEST_API_KEY -cp $jar:$junit org.junit.runner.JUnitCore $testclasses
