# Fix up unaligned user accesses silently, as the legacy VB-8034 image did; the
# LCI daemon copies from its uncached DMA buffer at unaligned offsets.
do_install:append:vb8034() {
    sed -i 's|echo "3" > /proc/cpu/alignment|echo "2" > /proc/cpu/alignment|' \
        ${D}${sysconfdir}/init.d/alignment.sh
    grep -q 'echo "2" > /proc/cpu/alignment' ${D}${sysconfdir}/init.d/alignment.sh
}

# Set the hostname the way the legacy VB-8034 image did (ni-rt.ini Host_Name,
# defaulting to <DeviceDesc>-<serial#>); the read-only /etc/hostname is generic.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:vb8034 = " file://lci-hostname.sh"
do_install:append:vb8034() {
    install -m 0755 ${WORKDIR}/lci-hostname.sh ${D}${sysconfdir}/init.d/hostname.sh
}
