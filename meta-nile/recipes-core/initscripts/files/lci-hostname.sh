#!/bin/sh
### BEGIN INIT INFO
# Provides:          hostname
# Required-Start:
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Set the LCI hostname from ni-rt.ini or the U-Boot env
### END INIT INFO

NIRTCFG=/usr/local/natinst/bin/nirtcfg

HOSTNAME=$($NIRTCFG --get section=SystemSettings,token=Host_Name 2>/dev/null)

if [ $? -eq 0 ] && [ -n "$HOSTNAME" ]; then
    hostname "$HOSTNAME"
else
    # Legacy default: "NI VB-8034" + serial# -> VB8034-<serial#>
    DEVICE=$(fw_printenv -n DeviceDesc 2>/dev/null | sed 's/-//; s/^[[:alnum:]]* //')
    SERIAL=$(fw_printenv -n serial# 2>/dev/null)
    if [ -n "$DEVICE" ] && [ -n "$SERIAL" ]; then
        hostname "$DEVICE-$SERIAL"
        $NIRTCFG --set section=SystemSettings,token=Host_Name,value="$(hostname)"
    elif [ -f /etc/hostname ]; then
        hostname -F /etc/hostname
    fi
fi
