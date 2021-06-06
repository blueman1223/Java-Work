package io.blueman.geekschool.spring.boot.core;

import lombok.Data;

import java.util.List;

@Data
public class Klass { 
    private String name;
    private List<Student> students;
    
    public void dong(){
        System.out.println(this.getStudents());
    }
    
}
