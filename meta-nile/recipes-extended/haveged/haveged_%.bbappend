FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# meta-oe's haveged recipe installs only the binary -- no init script or service.
# Under sysvinit we must start it ourselves, and EARLY (rcS, before sshd's rc5
# S09) so the kernel CRNG is seeded before anything calls getrandom(). Linux 4.x
# getrandom() blocks until CRNG init and the Zynq-7000 PS has no hardware RNG.
SRC_URI:append = " file://haveged.init"

inherit update-rc.d
INITSCRIPT_NAME = "haveged"
# Start in rcS but AFTER S03mountall.sh + S37populate-volatile.sh, since haveged
# needs /run (tmpfs) writable for its pidfile and command socket. Still well
# before sshd (rc5 S09), so the CRNG is seeded before anything needs entropy.
INITSCRIPT_PARAMS = "start 38 S ."

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/haveged.init ${D}${sysconfdir}/init.d/haveged
}

FILES:${PN} += "${sysconfdir}/init.d/haveged"
