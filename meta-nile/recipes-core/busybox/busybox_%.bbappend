FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# VB-8034's read-only rootfs has no /bin/hostname; enable the busybox applet
# so the hostname init script can run.
SRC_URI:append:vb8034 = " file://hostname.cfg"
