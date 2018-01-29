
package xmlprocessing.companyInfo;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {


   
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Companies }
     * 
     */
    public Companies createCompanies() {
        return new Companies();
    }

    /**
     * Create an instance of {@link Companies.Company }
     * 
     */
    public Companies.Company createCompaniesCompany() {
        return new Companies.Company();
    }

}
