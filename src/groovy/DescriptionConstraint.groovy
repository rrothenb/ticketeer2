import org.codehaus.groovy.grails.validation.AbstractConstraint

import org.springframework.validation.Errors

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rick
 */
class DescriptionConstraint extends AbstractConstraint {
    private String description;
	
    public void setParameter(Object constraintParameter) {
        if(!(constraintParameter instanceof String))
        throw new IllegalArgumentException("Parameter for constraint " + getName() + " of property ["
               +constraintPropertyName+"] of class ["
               +constraintOwningClass+"] must be a String value");

        description = (String)constraintParameter
        super.setParameter(constraintParameter);
    }

    protected void processValidate(Object target, Object propertyValue, Errors errors) {
    }

    boolean supports(Class type) {
        return true
    }

    String getName() {
        return "futureOnly"
    }

    String toString() {
        return description
    }
}

