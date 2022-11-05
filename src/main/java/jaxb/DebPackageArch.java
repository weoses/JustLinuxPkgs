package jaxb;

/**
 * dpkg-architecture -L listing
 */
public enum DebPackageArch {
    all,
    uclibc_linux_armel,
    uclibc_linux_i386,
    uclibc_linux_ia64,
    uclibc_linux_alpha,
    uclibc_linux_amd64,
    uclibc_linux_arc,
    uclibc_linux_armeb,
    uclibc_linux_arm,
    uclibc_linux_arm64,
    uclibc_linux_avr32,
    uclibc_linux_hppa,
    uclibc_linux_m32r,
    uclibc_linux_m68k,
    uclibc_linux_mips,
    uclibc_linux_mipsel,
    uclibc_linux_mipsr6,
    uclibc_linux_mipsr6el,
    uclibc_linux_mips64,
    uclibc_linux_mips64el,
    uclibc_linux_mips64r6,
    uclibc_linux_mips64r6el,
    uclibc_linux_nios2,
    uclibc_linux_or1k,
    uclibc_linux_powerpc,
    uclibc_linux_powerpcel,
    uclibc_linux_ppc64,
    uclibc_linux_ppc64el,
    uclibc_linux_riscv64,
    uclibc_linux_s390,
    uclibc_linux_s390x,
    uclibc_linux_sh3,
    uclibc_linux_sh3eb,
    uclibc_linux_sh4,
    uclibc_linux_sh4eb,
    uclibc_linux_sparc,
    uclibc_linux_sparc64,
    uclibc_linux_tilegx,
    musl_linux_armhf,
    musl_linux_i386,
    musl_linux_ia64,
    musl_linux_alpha,
    musl_linux_amd64,
    musl_linux_arc,
    musl_linux_armeb,
    musl_linux_arm,
    musl_linux_arm64,
    musl_linux_avr32,
    musl_linux_hppa,
    musl_linux_m32r,
    musl_linux_m68k,
    musl_linux_mips,
    musl_linux_mipsel,
    musl_linux_mipsr6,
    musl_linux_mipsr6el,
    musl_linux_mips64,
    musl_linux_mips64el,
    musl_linux_mips64r6,
    musl_linux_mips64r6el,
    musl_linux_nios2,
    musl_linux_or1k,
    musl_linux_powerpc,
    musl_linux_powerpcel,
    musl_linux_ppc64,
    musl_linux_ppc64el,
    musl_linux_riscv64,
    musl_linux_s390,
    musl_linux_s390x,
    musl_linux_sh3,
    musl_linux_sh3eb,
    musl_linux_sh4,
    musl_linux_sh4eb,
    musl_linux_sparc,
    musl_linux_sparc64,
    musl_linux_tilegx,
    armhf,
    armel,
    mipsn32,
    mipsn32el,
    mipsn32r6,
    mipsn32r6el,
    mips64,
    mips64el,
    mips64r6,
    mips64r6el,
    powerpcspe,
    x32,
    arm64ilp32,
    i386,
    ia64,
    alpha,
    amd64,
    arc,
    armeb,
    arm,
    arm64,
    avr32,
    hppa,
    m32r,
    m68k,
    mips,
    mipsel,
    mipsr6,
    mipsr6el,
    nios2,
    or1k,
    powerpc,
    powerpcel,
    ppc64,
    ppc64el,
    riscv64,
    s390,
    s390x,
    sh3,
    sh3eb,
    sh4,
    sh4eb,
    sparc,
    sparc64,
    tilegx,
    kfreebsd_armhf,
    kfreebsd_i386,
    kfreebsd_ia64,
    kfreebsd_alpha,
    kfreebsd_amd64,
    kfreebsd_arc,
    kfreebsd_armeb,
    kfreebsd_arm,
    kfreebsd_arm64,
    kfreebsd_avr32,
    kfreebsd_hppa,
    kfreebsd_m32r,
    kfreebsd_m68k,
    kfreebsd_mips,
    kfreebsd_mipsel,
    kfreebsd_mipsr6,
    kfreebsd_mipsr6el,
    kfreebsd_mips64,
    kfreebsd_mips64el,
    kfreebsd_mips64r6,
    kfreebsd_mips64r6el,
    kfreebsd_nios2,
    kfreebsd_or1k,
    kfreebsd_powerpc,
    kfreebsd_powerpcel,
    kfreebsd_ppc64,
    kfreebsd_ppc64el,
    kfreebsd_riscv64,
    kfreebsd_s390,
    kfreebsd_s390x,
    kfreebsd_sh3,
    kfreebsd_sh3eb,
    kfreebsd_sh4,
    kfreebsd_sh4eb,
    kfreebsd_sparc,
    kfreebsd_sparc64,
    kfreebsd_tilegx,
    knetbsd_i386,
    knetbsd_ia64,
    knetbsd_alpha,
    knetbsd_amd64,
    knetbsd_arc,
    knetbsd_armeb,
    knetbsd_arm,
    knetbsd_arm64,
    knetbsd_avr32,
    knetbsd_hppa,
    knetbsd_m32r,
    knetbsd_m68k,
    knetbsd_mips,
    knetbsd_mipsel,
    knetbsd_mipsr6,
    knetbsd_mipsr6el,
    knetbsd_mips64,
    knetbsd_mips64el,
    knetbsd_mips64r6,
    knetbsd_mips64r6el,
    knetbsd_nios2,
    knetbsd_or1k,
    knetbsd_powerpc,
    knetbsd_powerpcel,
    knetbsd_ppc64,
    knetbsd_ppc64el,
    knetbsd_riscv64,
    knetbsd_s390,
    knetbsd_s390x,
    knetbsd_sh3,
    knetbsd_sh3eb,
    knetbsd_sh4,
    knetbsd_sh4eb,
    knetbsd_sparc,
    knetbsd_sparc64,
    knetbsd_tilegx,
    kopensolaris_i386,
    kopensolaris_ia64,
    kopensolaris_alpha,
    kopensolaris_amd64,
    kopensolaris_arc,
    kopensolaris_armeb,
    kopensolaris_arm,
    kopensolaris_arm64,
    kopensolaris_avr32,
    kopensolaris_hppa,
    kopensolaris_m32r,
    kopensolaris_m68k,
    kopensolaris_mips,
    kopensolaris_mipsel,
    kopensolaris_mipsr6,
    kopensolaris_mipsr6el,
    kopensolaris_mips64,
    kopensolaris_mips64el,
    kopensolaris_mips64r6,
    kopensolaris_mips64r6el,
    kopensolaris_nios2,
    kopensolaris_or1k,
    kopensolaris_powerpc,
    kopensolaris_powerpcel,
    kopensolaris_ppc64,
    kopensolaris_ppc64el,
    kopensolaris_riscv64,
    kopensolaris_s390,
    kopensolaris_s390x,
    kopensolaris_sh3,
    kopensolaris_sh3eb,
    kopensolaris_sh4,
    kopensolaris_sh4eb,
    kopensolaris_sparc,
    kopensolaris_sparc64,
    kopensolaris_tilegx,
    hurd_i386,
    hurd_ia64,
    hurd_alpha,
    hurd_amd64,
    hurd_arc,
    hurd_armeb,
    hurd_arm,
    hurd_arm64,
    hurd_avr32,
    hurd_hppa,
    hurd_m32r,
    hurd_m68k,
    hurd_mips,
    hurd_mipsel,
    hurd_mipsr6,
    hurd_mipsr6el,
    hurd_mips64,
    hurd_mips64el,
    hurd_mips64r6,
    hurd_mips64r6el,
    hurd_nios2,
    hurd_or1k,
    hurd_powerpc,
    hurd_powerpcel,
    hurd_ppc64,
    hurd_ppc64el,
    hurd_riscv64,
    hurd_s390,
    hurd_s390x,
    hurd_sh3,
    hurd_sh3eb,
    hurd_sh4,
    hurd_sh4eb,
    hurd_sparc,
    hurd_sparc64,
    hurd_tilegx,
    darwin_i386,
    darwin_ia64,
    darwin_alpha,
    darwin_amd64,
    darwin_arc,
    darwin_armeb,
    darwin_arm,
    darwin_arm64,
    darwin_avr32,
    darwin_hppa,
    darwin_m32r,
    darwin_m68k,
    darwin_mips,
    darwin_mipsel,
    darwin_mipsr6,
    darwin_mipsr6el,
    darwin_mips64,
    darwin_mips64el,
    darwin_mips64r6,
    darwin_mips64r6el,
    darwin_nios2,
    darwin_or1k,
    darwin_powerpc,
    darwin_powerpcel,
    darwin_ppc64,
    darwin_ppc64el,
    darwin_riscv64,
    darwin_s390,
    darwin_s390x,
    darwin_sh3,
    darwin_sh3eb,
    darwin_sh4,
    darwin_sh4eb,
    darwin_sparc,
    darwin_sparc64,
    darwin_tilegx,
    dragonflybsd_i386,
    dragonflybsd_ia64,
    dragonflybsd_alpha,
    dragonflybsd_amd64,
    dragonflybsd_arc,
    dragonflybsd_armeb,
    dragonflybsd_arm,
    dragonflybsd_arm64,
    dragonflybsd_avr32,
    dragonflybsd_hppa,
    dragonflybsd_m32r,
    dragonflybsd_m68k,
    dragonflybsd_mips,
    dragonflybsd_mipsel,
    dragonflybsd_mipsr6,
    dragonflybsd_mipsr6el,
    dragonflybsd_mips64,
    dragonflybsd_mips64el,
    dragonflybsd_mips64r6,
    dragonflybsd_mips64r6el,
    dragonflybsd_nios2,
    dragonflybsd_or1k,
    dragonflybsd_powerpc,
    dragonflybsd_powerpcel,
    dragonflybsd_ppc64,
    dragonflybsd_ppc64el,
    dragonflybsd_riscv64,
    dragonflybsd_s390,
    dragonflybsd_s390x,
    dragonflybsd_sh3,
    dragonflybsd_sh3eb,
    dragonflybsd_sh4,
    dragonflybsd_sh4eb,
    dragonflybsd_sparc,
    dragonflybsd_sparc64,
    dragonflybsd_tilegx,
    freebsd_i386,
    freebsd_ia64,
    freebsd_alpha,
    freebsd_amd64,
    freebsd_arc,
    freebsd_armeb,
    freebsd_arm,
    freebsd_arm64,
    freebsd_avr32,
    freebsd_hppa,
    freebsd_m32r,
    freebsd_m68k,
    freebsd_mips,
    freebsd_mipsel,
    freebsd_mipsr6,
    freebsd_mipsr6el,
    freebsd_mips64,
    freebsd_mips64el,
    freebsd_mips64r6,
    freebsd_mips64r6el,
    freebsd_nios2,
    freebsd_or1k,
    freebsd_powerpc,
    freebsd_powerpcel,
    freebsd_ppc64,
    freebsd_ppc64el,
    freebsd_riscv64,
    freebsd_s390,
    freebsd_s390x,
    freebsd_sh3,
    freebsd_sh3eb,
    freebsd_sh4,
    freebsd_sh4eb,
    freebsd_sparc,
    freebsd_sparc64,
    freebsd_tilegx,
    netbsd_i386,
    netbsd_ia64,
    netbsd_alpha,
    netbsd_amd64,
    netbsd_arc,
    netbsd_armeb,
    netbsd_arm,
    netbsd_arm64,
    netbsd_avr32,
    netbsd_hppa,
    netbsd_m32r,
    netbsd_m68k,
    netbsd_mips,
    netbsd_mipsel,
    netbsd_mipsr6,
    netbsd_mipsr6el,
    netbsd_mips64,
    netbsd_mips64el,
    netbsd_mips64r6,
    netbsd_mips64r6el,
    netbsd_nios2,
    netbsd_or1k,
    netbsd_powerpc,
    netbsd_powerpcel,
    netbsd_ppc64,
    netbsd_ppc64el,
    netbsd_riscv64,
    netbsd_s390,
    netbsd_s390x,
    netbsd_sh3,
    netbsd_sh3eb,
    netbsd_sh4,
    netbsd_sh4eb,
    netbsd_sparc,
    netbsd_sparc64,
    netbsd_tilegx,
    openbsd_i386,
    openbsd_ia64,
    openbsd_alpha,
    openbsd_amd64,
    openbsd_arc,
    openbsd_armeb,
    openbsd_arm,
    openbsd_arm64,
    openbsd_avr32,
    openbsd_hppa,
    openbsd_m32r,
    openbsd_m68k,
    openbsd_mips,
    openbsd_mipsel,
    openbsd_mipsr6,
    openbsd_mipsr6el,
    openbsd_mips64,
    openbsd_mips64el,
    openbsd_mips64r6,
    openbsd_mips64r6el,
    openbsd_nios2,
    openbsd_or1k,
    openbsd_powerpc,
    openbsd_powerpcel,
    openbsd_ppc64,
    openbsd_ppc64el,
    openbsd_riscv64,
    openbsd_s390,
    openbsd_s390x,
    openbsd_sh3,
    openbsd_sh3eb,
    openbsd_sh4,
    openbsd_sh4eb,
    openbsd_sparc,
    openbsd_sparc64,
    openbsd_tilegx,
    aix_i386,
    aix_ia64,
    aix_alpha,
    aix_amd64,
    aix_arc,
    aix_armeb,
    aix_arm,
    aix_arm64,
    aix_avr32,
    aix_hppa,
    aix_m32r,
    aix_m68k,
    aix_mips,
    aix_mipsel,
    aix_mipsr6,
    aix_mipsr6el,
    aix_mips64,
    aix_mips64el,
    aix_mips64r6,
    aix_mips64r6el,
    aix_nios2,
    aix_or1k,
    aix_powerpc,
    aix_powerpcel,
    aix_ppc64,
    aix_ppc64el,
    aix_riscv64,
    aix_s390,
    aix_s390x,
    aix_sh3,
    aix_sh3eb,
    aix_sh4,
    aix_sh4eb,
    aix_sparc,
    aix_sparc64,
    aix_tilegx,
    solaris_i386,
    solaris_ia64,
    solaris_alpha,
    solaris_amd64,
    solaris_arc,
    solaris_armeb,
    solaris_arm,
    solaris_arm64,
    solaris_avr32,
    solaris_hppa,
    solaris_m32r,
    solaris_m68k,
    solaris_mips,
    solaris_mipsel,
    solaris_mipsr6,
    solaris_mipsr6el,
    solaris_mips64,
    solaris_mips64el,
    solaris_mips64r6,
    solaris_mips64r6el,
    solaris_nios2,
    solaris_or1k,
    solaris_powerpc,
    solaris_powerpcel,
    solaris_ppc64,
    solaris_ppc64el,
    solaris_riscv64,
    solaris_s390,
    solaris_s390x,
    solaris_sh3,
    solaris_sh3eb,
    solaris_sh4,
    solaris_sh4eb,
    solaris_sparc,
    solaris_sparc64,
    solaris_tilegx,
    uclinux_armel,
    uclinux_i386,
    uclinux_ia64,
    uclinux_alpha,
    uclinux_amd64,
    uclinux_arc,
    uclinux_armeb,
    uclinux_arm,
    uclinux_arm64,
    uclinux_avr32,
    uclinux_hppa,
    uclinux_m32r,
    uclinux_m68k,
    uclinux_mips,
    uclinux_mipsel,
    uclinux_mipsr6,
    uclinux_mipsr6el,
    uclinux_mips64,
    uclinux_mips64el,
    uclinux_mips64r6,
    uclinux_mips64r6el,
    uclinux_nios2,
    uclinux_or1k,
    uclinux_powerpc,
    uclinux_powerpcel,
    uclinux_ppc64,
    uclinux_ppc64el,
    uclinux_riscv64,
    uclinux_s390,
    uclinux_s390x,
    uclinux_sh3,
    uclinux_sh3eb,
    uclinux_sh4,
    uclinux_sh4eb,
    uclinux_sparc,
    uclinux_sparc64,
    uclinux_tilegx,
    mint_m68k;

}
