package builders;

import builders.deb.DebBuilder;
import builders.rpm.RpmBuilder;


public enum BuildType {
    RPM("rpm", RpmBuilder.class),
    DEB("deb", DebBuilder.class);

    final String name;
    final Class<? extends PkgBuilder> impl;
    BuildType(String name , Class<? extends PkgBuilder> impl){
        this.name = name;
        this.impl = impl;
    }

    public Class<? extends PkgBuilder> getImpl() {
        return impl;
    }

    public String getName() {
        return name;
    }
}
