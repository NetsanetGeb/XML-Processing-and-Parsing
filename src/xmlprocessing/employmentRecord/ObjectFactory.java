
package xmlprocessing.employmentRecord;

import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


 
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Employee }
     * 
     */
    public Employee createEmployee() {
        return new Employee();
    }

    /**
     * Create an instance of {@link Employee.Employment }
     * 
     */
    public Employee.Employment createEmployeeEmployment() {
        return new Employee.Employment();
    }

}
