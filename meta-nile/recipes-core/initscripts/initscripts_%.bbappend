# Fix up unaligned user accesses silently, as the legacy VB-8034 image did; the
# LCI daemon copies from its uncached DMA buffer at unaligned offsets.
do_install:append:vb8034() {
    sed -i 's|echo "3" > /proc/cpu/alignment|echo "2" > /proc/cpu/alignment|' \
        ${D}${sysconfdir}/init.d/alignment.sh
    grep -q 'echo "2" > /proc/cpu/alignment' ${D}${sysconfdir}/init.d/alignment.sh
}
