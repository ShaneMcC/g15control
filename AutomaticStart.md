# Introduction #

This will show you how to automatically start and stop G15Control and G15Daemon when the G15 is plugged and unplugged.

This guide is mainly aimed at ubuntu, but other distros should be somewhat similar.

# udev #

This requires udev (which I believe most linux distros use now?), so the first thing to do is to create a file in /etc/udev/rules.d called 20-g15.rules with the following content:
```
SYSFS{../name}=="G15 Keyboard G15 Keyboard", RUN+="/usr/local/bin/g15daemon-hotplug"
```

now restart udev:
```
sudo /etc/init.d/udev restart
```

# Helper Scripts #

The script above references the script /usr/local/bin/g15daemon-hotplug, this needs to be created to suit your system, mine looks like this:
```
#!/bin/bash
# start/stop g15daemon and g15control when plugged/unplugged
case $ACTION in
	"add")
		# G15 being plugged, start g15daemon
		# (the -s switch makes MR the G15Daemon change screen button to make L1 the
		# change screen button for G15Control)
		modprobe uinput
		/usr/sbin/g15daemon -s
		# Now start G15Control
		su shane -c "cd /home/shane/projects/G15Control/trunk/ ; nohup /usr/bin/ant run >/dev/null 2>&1 &"
		;;
	"remove")
		# G15 being unplugged, kill g15daemon
		killall -9 g15daemon
		# And also G15Control
		ps aux | grep G15Control | grep -v grep | awk '{print $2}' | xargs kill -9
		;;
	*)
		exit 0
		;;
esac
```