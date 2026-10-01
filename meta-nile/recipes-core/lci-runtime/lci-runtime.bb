SUMMARY = "VB-8034 LCI runtime startup"
LICENSE = "CLOSED"

SRC_URI = "file://lci-runtime.init \
           file://90-lci-runtime.rules \
           file://91-lci-watchdog.rules \
           "

inherit update-rc.d
INITSCRIPT_NAME = "lci-runtime"
INITSCRIPT_PARAMS = "start 27 5 . stop 90 0 1 6 ."

# libcrypto-ni (NI feed, used by vb8034Daemon) needs libatomic but its feed
# package does not declare it. vb8034Daemon crashes without avahi-daemon.
RDEPENDS:${PN} = "kernel-module-g-lci kernel-module-usb-f-hid-bulk libatomic avahi-daemon"

do_install() {
    install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/udev/rules.d
    install -m 0755 ${WORKDIR}/lci-runtime.init ${D}${sysconfdir}/init.d/lci-runtime
    install -m 0644 ${WORKDIR}/90-lci-runtime.rules ${WORKDIR}/91-lci-watchdog.rules \
        ${D}${sysconfdir}/udev/rules.d/
}

# Separate so dev images can leave the daemon's watchdog unarmed for debugging.
PACKAGES =+ "${PN}-watchdog"
FILES:${PN}-watchdog = "${sysconfdir}/udev/rules.d/91-lci-watchdog.rules"
RDEPENDS:${PN}-watchdog = "${PN}"

FILES:${PN} += "${sysconfdir}/init.d/lci-runtime ${sysconfdir}/udev/rules.d/90-lci-runtime.rules"
