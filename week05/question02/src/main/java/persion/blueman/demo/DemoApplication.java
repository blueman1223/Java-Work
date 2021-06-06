package persion.blueman.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import persion.blueman.bean.AnnotationBean;
import persion.blueman.bean.ConfigurationBean;
import persion.blueman.bean.XmlBean;

public class DemoApplication {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext xmlCtx = new ClassPathXmlApplicationContext("application-context.xml");
		XmlBean xmlBean = (XmlBean) xmlCtx.getBean("xmlBean");
		System.out.println(xmlBean);
		AnnotationConfigApplicationContext annotationCtx = new AnnotationConfigApplicationContext("persion.blueman.bean");
		AnnotationBean annotationBean = (AnnotationBean) annotationCtx.getBean("annotationBean");
		System.out.println(annotationBean);
		ConfigurationBean configurationBean = (ConfigurationBean) annotationCtx.getBean("configurationBean");
		System.out.println(configurationBean);

	}

}
