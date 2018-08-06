import subprocess
import os
import urllib.request as urllib
import shutil


def do():
    # Build Uppercore
    print("Building Uppercore")
    code = subprocess.call(["gradle", "build"], shell=True)
    if code:
        exit(1)

    # Build UppercoreTest
    print("Building UppercoreTest")
    code = subprocess.call(["gradle", "buildTest"], shell=True)
    if code:
        exit(1)

    # Create tmp directory
    if not os.path.isdir("tmp"):
        os.mkdir("tmp")

    # Download Spigot
    if not os.path.isfile("tmp/spigot.jar"):
        print("Spigot not present. Preparing 'tmp' directory.")

        # Download spigot.jar
        opener = urllib.URLopener()
        opener.retrieve("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar", "tmp/spigot.jar")
        print("Spigot 1.12 downloaded.")

    # Re-write eula.txt file
    if os.path.isfile("tmp/eula.txt"):
        print("EULA was present. Removing it to be sure.")
        os.remove("tmp/eula.txt")

    eula = open("tmp/eula.txt", "wt")
    eula.write("eula=true")
    eula.close()
    print("Re-wrote EULA file.")

    # Re-create plugins folder
    shutil.rmtree("tmp/plugins")
    os.mkdir("tmp/plugins")
    print("Plugins folder re-created.")

    # Move built JARs to plugins folder
    shutil.copyfile("build/libs/Uppercore.jar", "tmp/plugins/Uppercore.jar")
    shutil.copyfile("build/libs/UppercoreTest-1.3.jar", "tmp/plugins/UppercoreTest.jar")  # TODO do not use version
    print("Uppercore copied.")

    # Start Spigot JAR
    print("Starting Spigot...")

    os.chdir("tmp")
    subprocess.Popen(["java", "-jar", "spigot.jar"], shell=True).communicate()  # TODO Ctrl+C works only with Enter key

    exit(0)


try:
    do()
except KeyboardInterrupt:
    print("Exiting...")
