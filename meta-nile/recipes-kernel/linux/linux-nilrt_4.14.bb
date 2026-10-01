SUMMARY = "NI Linux RT 4.14 kernel for the VB-8034 (Zynq-7000) 4.x uplift"
DESCRIPTION = "NILRT 4.14 (ni/linux nilrt/26.5/4.14) as the 4.x stepping stone off \
the legacy 3.2 VirtualBench kernel, toward a modern 6.x base. This branch already \
ships NI Zynq-7000 targets (cRIO/sbRIO), so it carries mainline Zynq support \
(MACB GEM, PL353 NAND, xilinx_uartps, devcfg) and builds under a modern toolchain \
-- unlike 3.2, which needed the gcc-13 workaround patch pile."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

LINUX_VERSION = "4.14"
PV = "${LINUX_VERSION}+git"

SRC_URI = "git://github.com/jhershbe/linux.git;protocol=https;branch=virtualbench/26.5/4.14"
# Fork of ni/linux nilrt/26.5/4.14 carrying the forward-ported VB-8034 content
# (ni-77DE.dts with USB0 pinmux and PL fabric clocks), the pl353 NAND ofpart
# fix, the ChipIdea Zynq OTG termination fix, the NILRT on-target tools opt-in,
# the g_lci USB gadget port, the uio_lci LCI FPGA driver, and the SWDT reset.
SRCREV = "7298f9b4dd983aff989aff7011bea44f4e729777"

S = "${WORKDIR}/git"

ARCH = "arm"
KBUILD_DEFCONFIG = "nati_zynq_defconfig"
KCONFIG_MODE = "alldefconfig"
# The raw, uncompressed Image the legacy lci.itb FIT loads at 0x8000 comes from
# the machine's KERNEL_IMAGETYPES:append " Image". The primary type is uImage to
# match the Xilinx machine default so packagegroup-core-boot's
# kernel-image-${KERNEL_IMAGETYPE} rdepend (kernel-image-uimage) is satisfiable.
# uImage is not used inside the FIT; VB boots the raw Image.
KERNEL_IMAGETYPE = "uImage"
# VB-8034 device tree (device code 0x77DE); overridable per machine.
KERNEL_DEVICETREE = "ni-77DE.dtb"
KERNEL_VERSION_SANITY_SKIP = "1"

# NILRT ships a plain defconfig, not yocto-kernel-cache .scc metadata, so the
# linux-yocto default KERNEL_FEATURES (e.g. cfg/fs/vfat.scc) don't resolve.
# do_kernel_configme below copies nati_zynq_defconfig over .config, so treat the
# dangling features as warnings instead of failing configuration.
KERNEL_DANGLING_FEATURES_WARN_ONLY = "1"
KERNEL_FEATURES:remove = "cfg/fs/vfat.scc"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:vb8034 = "vb8034"

require recipes-kernel/linux/linux-yocto.inc

do_kernel_configme:append:vb8034() {
        cp ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${B}/.config
        # Our update payload is an lzo-compressed squashfs mounted directly as the
        # root fs (no initramfs), so squashfs+lzo must be built in, not modules.
        printf 'CONFIG_SQUASHFS=y\nCONFIG_SQUASHFS_LZO=y\n' >> ${B}/.config
        # Keep the controller and PHY built in, but load g_lci after the MTD
        # partitions exist: its CD-ROM backing file is an mtdblock node created
        # after the early UDC probe. modprobe consumes the g_lci.* boot options.
        printf 'CONFIG_USB_GADGET=y\nCONFIG_USB_CHIPIDEA=y\nCONFIG_USB_CHIPIDEA_UDC=y\nCONFIG_USB_ULPI_BUS=y\nCONFIG_USB_CHIPIDEA_ULPI=y\nCONFIG_NOP_USB_XCEIV=y\nCONFIG_USB_LIBCOMPOSITE=y\nCONFIG_USB_F_HID_BULK=y\nCONFIG_USB_F_MASS_STORAGE=y\nCONFIG_USB_G_LCI=m\n' >> ${B}/.config
        # The LCI daemon drives the FPGA through /dev/uio0 (uio_lci).
        printf 'CONFIG_UIO=y\nCONFIG_UIO_LCI=y\n' >> ${B}/.config
        # Zynq SWDT for the daemon, nowayout as in the legacy ni_lci_defconfig.
        printf 'CONFIG_WATCHDOG=y\nCONFIG_WATCHDOG_CORE=y\nCONFIG_WATCHDOG_NOWAYOUT=y\nCONFIG_CADENCE_WATCHDOG=y\n' >> ${B}/.config
}
