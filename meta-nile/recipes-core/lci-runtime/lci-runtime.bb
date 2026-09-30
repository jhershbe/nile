SUMMARY = "VB-8034 LCI runtime startup"
LICENSE = "CLOSED"

SRC_URI = "file://lci-runtime.init"

inherit update-rc.d
INITSCRIPT_NAME = "lci-runtime"
INITSCRIPT_PARAMS = "start 27 5 . stop 90 0 1 6 ."

# libcrypto-ni (NI feed, used by vb8034Daemon) needs libatomic but its feed
# package does not declare it.
RDEPENDS:${PN} = "kernel-module-g-lci kernel-module-usb-f-hid-bulk libatomic"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/lci-runtime.init ${D}${sysconfdir}/init.d/lci-runtime
}

FILES:${PN} += "${sysconfdir}/init.d/lci-runtime"
