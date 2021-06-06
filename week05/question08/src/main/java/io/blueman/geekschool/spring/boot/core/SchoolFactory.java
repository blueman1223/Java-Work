package io.blueman.geekschool.spring.boot.core;

import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Properties;

public class SchoolFactory {

    public static School createSchool(final String name) {
        Student blueman = new Student();
        blueman.setId("G20210579040147");
        blueman.setName("blueman");
        Klass klass = new Klass();
        klass.setName("Java course");
        klass.setStudents(Collections.singletonList(blueman));
        School geekSchool = new School();
        geekSchool.setKlass(klass);
        geekSchool.setName(name);
        return geekSchool;
    }
}
