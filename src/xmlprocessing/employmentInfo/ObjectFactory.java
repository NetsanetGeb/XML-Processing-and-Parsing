
package xmlprocessing.employmentInfo;

import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


  
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EmploymentInfo }
     * 
     */
    public EmploymentInfo createEmploymentInfo() {
        return new EmploymentInfo();
    }

    /**
     * Create an instance of {@link EmploymentInfo.Employment }
     * 
     */
    public EmploymentInfo.Employment createEmploymentInfoEmployment() {
        return new EmploymentInfo.Employment();
    }

    /**
     * Create an instance of {@link EmploymentInfo.Employment.Company }
     * 
     */
    public EmploymentInfo.Employment.Company createEmploymentInfoEmploymentCompany() {
        return new EmploymentInfo.Employment.Company();
    }

}
