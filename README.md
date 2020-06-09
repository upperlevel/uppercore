


# What is Uppercore?

Uppercore is a set of utilities implemented above the Bukkit API that aims to provide:
* A user-friendly, fluent, interface, many times better than the one offered by the Bukkit API.
* Some already-written classes that gather logic useful for plugin development.
* An intrinsic system of configurations bound with placeholders for messages, scoreboards or anything.
* A generic interface for DB acccess, both SQL or NoSQL.

### Why have we decided to develop it?
The Bukkit API is really near to the actual Minecraft Vanilla server structure and is so far from what many Bukkit developers really search for. Developing many plugins for Bukkit, we've ended up having a set of utilities that were the same for all projects. For example: using the bare Bukkit scoreboard is not that easy, usually people writes their own wrappers. Having all of that duplicated code, led us to start thinking of a library that could gather it all and be useful also for other developers.

## Most relevant features

Here's a list of the most relevant features Uppercore currently offers.
You can dive into these APIs in ther relative wiki pages. 

##### arena-api
A set of classes to handle arena creation, editing with support for signs and Bungee mode.

##### board-api
Wraps the Bukkit scoreboard and permits easy config loading with placeholders support.

##### command-api
Wraps the Bukkit commands API providing a clean interface able to handle commands-tree. Supports both class based and functional commands and, based on a given commands-tree is able to generate a HelpCommand.

##### config-api
Permits to handle a key-value configuration originating from a Yaml file, that doesn't rely on Bukkit. It's been added an annotation system that permits to parse custom types and a tracker to fetch errors during parsing.

##### gui-api
A powerful GUI library that permits to manage fixed menu, based on Bukkit inventories. Supports links, GUI-history, configuration loading and has also been implemented the possibilty of running custom Javascript/Python scripts on item click.

##### hotbar-api
This API shares the same backend with GUIs but is applied only to hotbars. Hotbars are a set of items (a menu) that can be applied to the 8 slots that are visible within the players' inventory.

##### storage-api
A common gateway for DB access. This API permits you to write DB-agnostic code using methods as easy as `putSomeData(...)` and `readSomeData(...)`. The DB-drivers are obviously not included in Uppercore and are downloaded when the plugin is enabled. These are the storage types supported: MariaDb, MySql, MongoDb, RethinkDb, NitriteDb (for local only savings).

##### update-api
An utility made to check whether there's an updated version of the plugin on Spigot and, in case, notify the plugin users.

## Installation

Currently Uppercore isn't published on Maven central repository. Thus, you have to download this repository and publish Uppercore to Maven local, that is the Maven's repository that lies offline on your computer.

Requirements:
* git
* gradle

Clone the repository (or just download and extract the ZIP) wherever you like using:
```
git clone https://github.com/upperlevel/uppercore.git
```

With a terminal, step inside the Uppercore folder:
```
cd uppercore
```

Publish Uppercore to Maven local using:
```
gradle publishToMavenLocal
```

## Depending
If your plugin project's dependency system is based on Gradle, these are the lines that must be present within your `build.gradle` in order to use Uppercore:
```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation group: 'xyz.upperlevel.uppercore', name: 'uppercore', version: '2.0'
}

shadowJar {
    relocate 'xyz.upperlevel.uppercore', '<your-project-package-name>.uppercore'
}
```

Basically, Uppercore is fetched in the Maven local repository (so it must be already installed) and is compiled with the dependant plugin code. To avoid issues its code is relocated.

## Get started
Within your plugin's entry class, insert the following line inside the `onEnable()` method.
```java
@Override
public void onEnable() {
     Uppercore.hook(this, BSTATS_ID);
}
```
Where `BSTATS_ID` is the ID of the plugin in [bstats.org](https://bstats.org), a metrics platform.
If not supported, just set it to 0.
