##Configuring Ant in Eclipse for build.xml
I've included a quick little build.xml file. In order to use this, you'll need to set two properties in eclipse.

-  Go to: Window -> Preferences -> Ant -> Runtime -> Properties (tab)

Now, you'll need to either add the following properties to the global properties:

    Name: bukkit.jar 
    Value: /path/to/bukkit.jar

    Name: minecraft.dir
    Value: /path/to/minecraft/server/root

Or, you can add the following content to a file, and import it under the "Global property files" section:

    bukkit.jar=C:/dev/Minecraft/Bukkit/Bukkit/target/bukkit-0.0.1-SNAPSHOT.jar
    minecraft.dir=C:/Minecraft
  
##Using this template in Eclipse

1.  Assuming you have eGit installed; File, Import, Git -> Project from Git
2.  Clone this repo into your development folder (remeber project source and git source must be in the same directory) (Example Destination: C:\dev\Eclipse\MyPlugin)
3.  Click next and select "Use the New Projects Wizard" and click Finish
4.  Select Java -> Java Project and click Next
5.  Project Name should match the one in step 2 (MyPlugin), and the location should match as well (C:\dev\Eclipse).
    
    If you did this correctly, you should see "The wizard will automatically configure the JRE and the project layout based on the existing source"

6.  Click finish to create your project
7.  Add the bukkit jar to your build path and rename all the "templateplugin" instances. Some spots you will need to rename are:
     -  com.minecarts.__templateplugin__.*
     -  com.minecarts.__templateplugin__.__TemplatePlugin__.java
     -  plugin.yml
 
*The build.xml file should automatically detect the plugin name*

