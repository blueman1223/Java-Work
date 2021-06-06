package persion.blueman.bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component("annotationBean")
@Data
@ToString
public class AnnotationBean {

    private String desc;
}
