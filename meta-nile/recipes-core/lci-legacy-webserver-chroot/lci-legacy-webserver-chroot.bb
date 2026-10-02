SUMMARY = "LCI legacy system web server chroot startup"
LICENSE = "CLOSED"

SRC_URI = "file://lci-legacy-webserver.init"

# The chroot itself comes from the feed-only lci-legacy-webserver package
# (installed by nile-image-lci); without it the init script does nothing.
inherit update-rc.d
INITSCRIPT_NAME = "lci-legacy-webserver"
INITSCRIPT_PARAMS = "start 29 5 . stop 20 0 1 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/lci-legacy-webserver.init ${D}${sysconfdir}/init.d/lci-legacy-webserver
}
