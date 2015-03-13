# Introduction #

This is a quick guide to preparing your machine and obtaining/compiling/running G15Control.

# Preparing your machine #

First of all, you will need java6 jdk installed (There is no official release of G15Control yet, so you will need to compile everything yourself).

You will also need ant to build the project, and svn to check it out.

This can be done using:
```
[user@machine:~]$ sudo apt-get install sun-java6-jdk ant subversion
```

To allow for the automatic building of plugins when comipiling you will also need ant-contrib. This can be downloaded from [here](http://downloads.sourceforge.net/ant-contrib/ant-contrib-1.0b3-bin.tar.gz?modtime=1162486708) and should be saved as `/usr/share/java/ant-contrib.jar` so that the build script can find it.

To allow G15Control to talk to the G15, you will also need G15Daemon, which can be downloaded from [here](http://sourceforge.net/project/showfiles.php?group_id=172261&package_id=199133) (As of writing, 1.9.4 is the latest and recommended version)

Once you have it downloaded, the install process is rather standard, you will need the standard compiling tools (`sudo apt-get install build-essential`) and then the process for installing will be something like:
```
[user@machine:~]$ wget "http://downloads.sourceforge.net/g15daemon/g15daemon-1.9.4.tar.gz?modtime=1199530238&big_mirror=0"
[user@machine:~]$ tar -zxvf g15daemon-1.9.4.tar.gz
[user@machine:~]$ cd g15daemon-1.9.4/
[user@machine:~/g15daemon-1.9.4]$ ./configure
[user@machine:~/g15daemon-1.9.4]$ make
[user@machine:~/g15daemon-1.9.4]$ sudo make install
```

You can then run G15Daemon by doing:
```
[user@machine:~]$ sudo modprobe uinput
[user@machine:~]$ sudo /usr/sbin/g15daemon -s
```
(These 2 commands, without the `sudo` can be placed into `/etc/rc.local` to automatically start G15Daemon when your system starts)

You should then see a clock appear on the LCD.

# Getting and installing G15Control #

The G15Control source is stored in a subversion repository on google code, so to get the source we can do:
```
[user@machine:~]$ svn checkout http://g15control.googlecode.com/svn/trunk/ g15control
```

After the source is checked out, we can compile G15Control by doing:
```
[user@machine:~]$ cd g15control
[user@machine:~/g15control]$ ant clean jar
```

and then to run it:
```
[user@machine:~]$ java -jar ~/g15control/dist/G15Control.jar
```

The first time you run it you will be greeted with:
```
[user@machine:~]$ java -jar ~/g15control/dist/G15Control.jar
Using Config: /home/user/.g15control/g15control.config
Config file not found, default created, please edit the file and change the default settings.
[user@machine:~]$ 
```

The default config however isn't very useful.
There is however, a more complete example config which you can find in ~/g15control/examples/g15control.config

# Plugins #

G15Control looks for plugins in ~/.g15control/plugins

If you installed ant-contrib the plugins will have been built and placed into ~/g15control/plugins. The easiest way to use these, is to symlink the 2 directories:
```
[user@machine:~]$ ln -s ~/g15control/plugins ~/.g15control/
```

Then if you recompile G15Control, the updated plugins will compiled aswell, alternatively you can do:
```
[user@machine:~]$ mkdir ~/.g15control/plugins
[user@machine:~]$ cp ~/g15control/plugins/*.jar  ~/.g15control/plugins/
```
In which case you will need to remember to copy the plugins when you recompile.

# Third Party Plugins #

Third party plugins (such as those from http://g15.md87.co.uk (created by Chris Smith)) are distributed as .jar files, and just need to dropped into the plugins directory, and added to the config file (eg `<plugin>smpload.jar</plugin>`).

Once this is done you can go to the main screen of G15Control (press the circle (or MR depending on how you started G15Daemon) button untill you get to the screen that says "G15Control" in the top, and "Menu" at the bottom) and press the menu key (the one underneath the "menu" text). Then you can navigate the menu using the "[<]" and "[>]" keys until you see "Reload Config", press the "OK" key - and any plugins listed in the config will be loaded if not already loaded, plugins can be unloaded using the "Unload Plugin" menu item)