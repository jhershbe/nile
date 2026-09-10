SUMMARY = "LCI target writable-volume mounts and U-Boot env configuration"
DESCRIPTION = "sysvinit scripts that mount the persistent config and cal UBI \
volumes and the fw_(printenv|setenv) configuration for the VB-8034."
LICENSE = "CLOSED"

SRC_URI = "\
    file://mountutils \
    file://mountconfig \
    file://mountcal \
    file://populateconfig \
    file://fw_env.config \
"

S = "${WORKDIR}"

# ubiattach/ubimkvol/etc. come from mtd-utils; fw_(printenv|setenv) from libubootenv.
RDEPENDS:${PN} = "mtd-utils libubootenv-bin"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/mountutils      ${D}${sysconfdir}/init.d/mountutils
    install -m 0755 ${S}/mountconfig     ${D}${sysconfdir}/init.d/mountconfig
    install -m 0755 ${S}/mountcal        ${D}${sysconfdir}/init.d/mountcal
    install -m 0755 ${S}/populateconfig  ${D}${sysconfdir}/init.d/populateconfig

    # Static rcS.d links: mount the writable volumes during sysinit, before
    # run-postinsts (S99) configures nirtcfg into /etc/natinst/share.
    install -d ${D}${sysconfdir}/rcS.d
    ln -sf ../init.d/mountconfig    ${D}${sysconfdir}/rcS.d/S36mountconfig
    ln -sf ../init.d/mountcal       ${D}${sysconfdir}/rcS.d/S36mountcal
    ln -sf ../init.d/populateconfig ${D}${sysconfdir}/rcS.d/S38populateconfig

    install -m 0644 ${S}/fw_env.config ${D}${sysconfdir}/fw_env.config
}

FILES:${PN} += "${sysconfdir}/fw_env.config"

COMPATIBLE_MACHINE = "vb8034"
