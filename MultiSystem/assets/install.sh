#!/system/bin/sh
#
# MultiSystem for Android
#
# (C) 2014-2015 hsbadr @ xda-developers
#
# If you'd like to support my work,
# please donate via PayPal or Google to:
# Hamada S. Badr <hamada.s.badr@gmail.com> 
#

MultiSystemMNT=/MultiSystem
MultiSystemBLK=/dev/block/mmcblk1p2
MultiSystemLOG=/data/log/MultiSystem.log
MultiSystemFiles=/data/data/com.hsbadr.MultiSystem/files/.MultiSystem
HijackBIN=e2fsck

BBX=/system/xbin/busybox

PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin
LD_LIBRARY_PATH=.:/sbin:/system/vendor/lib:/system/lib

# remount root read/write
mount -o remount,rw rootfs

# remount system read/write
mount -o remount,rw /system

# header
logheader ()
{
	$BBX echo -e "#" >> $MultiSystemLOG
	$BBX echo -e "# MultiSystem for Android" >> $MultiSystemLOG
	$BBX echo -e "#" >> $MultiSystemLOG
	$BBX echo -e "# (C) 2014-2015 hsbadr @ xda-developers" >> $MultiSystemLOG
	$BBX echo -e "#" >> $MultiSystemLOG
	$BBX echo -e "# If you'd like to support my work," >> $MultiSystemLOG
	$BBX echo -e "# please donate via PayPal or Google to:" >> $MultiSystemLOG
	$BBX echo -e "# Hamada S. Badr <hamada.s.badr@gmail.com>" >> $MultiSystemLOG
	$BBX echo -e "#" >> $MultiSystemLOG

	return 0
}

# execution log
execlog ()
{
	$BBX mkdir -p $($BBX dirname $MultiSystemLOG)
	if [ ! -f $MultiSystemLOG ]; then
		$BBX touch $MultiSystemLOG
		logheader
	fi

	$BBX echo -e "[$($BBX date +"%d-%m-%Y %H:%M:%S")] > $*" >> $MultiSystemLOG
	$* >> $MultiSystemLOG 2>&1
	$BBX echo -e "[$($BBX date +"%d-%m-%Y %H:%M:%S")] Exit $?" >> $MultiSystemLOG

	return $?
}

# echo log
echolog ()
{
	$BBX mkdir -p $($BBX dirname $MultiSystemLOG)
	if [ ! -f $MultiSystemLOG ]; then
		$BBX touch $MultiSystemLOG
		logheader
	fi

	$BBX echo -e "[$($BBX date +"%d-%m-%Y %H:%M:%S")] $*" >> $MultiSystemLOG

	return 0
}

# install BusyBox
if [ ! -x $BBX ] && [ -f $MultiSystemFiles/bin/busybox ]; then
	echolog "Installing BusyBox..."
	execlog cp $MultiSystemFiles/bin/busybox /system/xbin/busybox
	execlog chmod 0755 /system/xbin/busybox
	execlog chown 0:2000 /system/xbin/busybox
	execlog /system/xbin/busybox --install -s /system/xbin
fi

# update hostname
execlog $BBX hostname MultiSystem

# create /bin symlinks
if [ ! -e /bin ]; then
	execlog $BBX ln -s /system/bin /bin
fi

# create /etc symlinks
if [ ! -e /etc ]; then
	execlog $BBX ln -s /system/etc /etc
fi

# create /lib symlinks
if [ ! -e /lib ]; then
	execlog $BBX ln -s /system/lib /lib
fi

# symlink /etc/mtab to /proc/self/mounts
if [ ! -e /system/etc/mtab ]; then
	execlog $BBX ln -s /proc/self/mounts /system/etc/mtab
fi

# mount MultiSystem partition
if [ -e $MultiSystemBLK ]; then
	# create MultiSystem directory
	execlog $BBX mkdir -p $MultiSystemMNT

	# mount MultiSystem partition read/write
	checkMultiSystemMNT=$(cat /proc/mounts | grep " $MultiSystemMNT ")
	if [ -z "$checkMultiSystemMNT" ]; then
		execlog mount -t ext4 -o rw $MultiSystemBLK $MultiSystemMNT
	fi
else
	echolog "MultiSystem partition not found!"
	exit 1
fi

# extract MultiSystem binaries
if [ -f $MultiSystemFiles/bin/MultiSystem.tgz ]; then
	execlog $BBX tar -zxvf $MultiSystemFiles/bin/MultiSystem.tgz -C $MultiSystemMNT
else
	echolog "MultiSystem binaries not found!"
	exit 1
fi


# create MultiSystem symlink
if [ -f $MultiSystemMNT/bin/MultiSystem ]; then
	execlog $BBX rm -f /system/xbin/MultiSystem
	execlog $BBX ln -s $MultiSystemMNT/bin/MultiSystem /system/xbin/MultiSystem
fi

# create bash symlink
if [ -f $MultiSystemMNT/bin/bash ]; then
	execlog $BBX rm -f /system/xbin/bash
	execlog $BBX ln -s $MultiSystemMNT/bin/bash /system/xbin/bash
fi

# create rsync symlink
if [ -f $MultiSystemMNT/bin/rsync ]; then
	execlog $BBX rm -f /system/xbin/rsync
	execlog $BBX ln -s $MultiSystemMNT/bin/rsync /system/xbin/rsync
fi

# install MultiSystem hijack
if [ -f $MultiSystemMNT/bin/$HijackBIN ]; then
	if [ ! -f /system/bin/$HijackBIN.bin ]; then
		execlog $BBX cp /system/bin/$HijackBIN /system/bin/$HijackBIN.bin
		execlog $BBX chmod 0755 /system/bin/$HijackBIN.bin
		execlog $BBX chown 0:2000 /system/bin/$HijackBIN.bin
	fi
	execlog $BBX cp $MultiSystemMNT/bin/$HijackBIN /system/bin/$HijackBIN
	execlog $BBX chmod 0755 /system/bin/$HijackBIN
	execlog $BBX chown 0:2000 /system/bin/$HijackBIN
else
	echolog "MultiSystem hijack not found!"
	exit 1
fi

execlog $BBX sync

# remount system read only
execlog mount -o remount,ro /system

# remount root read only
execlog mount -o remount,ro rootfs
