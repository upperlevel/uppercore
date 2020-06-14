


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

## Docs

Don't hesitate to learn how to use Uppercore, [go to the wiki](https://github.com/upperlevel/uppercore/wiki)!
