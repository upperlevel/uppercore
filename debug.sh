#!/bin/sh

echo "Building solution..."
gradle build
gradle buildTest

if [ ! -f tmp/spigot.jar ]
then
    echo "Downloading spigot.jar..."
    mkdir tmp
    cd tmp
    wget https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar -O spigot.jar
    echo "eula=true" > eula.txt
    cd ..
fi

echo "Copying plugins..."
mkdir tmp/plugins
cp build/libs/Uppercore.jar tmp/plugins/Uppercore.jar
cp build/libs/UppercoreTest-1.3.jar tmp/plugins/UppercoreTest.jar

cd tmp
echo "Starting..."
java -jar spigot.jar
cd ..
