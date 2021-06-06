package io.blueman.geekschool.spring.boot.core;

import lombok.Data;

@Data
public class School {
    private String name;

    private Klass klass;
    
    public void ding(){
    
        System.out.println("Class " + klass.getName() + " have " + this.klass.getStudents().size() + " students");
        
    }
    
}
