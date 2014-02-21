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
class SecondaryConstraint extends AbstractConstraint {
    private boolean secondary;
	
    public void setParameter(Object constraintParameter) {
        if(!(constraintParameter instanceof Boolean))
        throw new IllegalArgumentException("Parameter for constraint " + getName() + " of property ["
               +constraintPropertyName+"] of class ["
               +constraintOwningClass+"] must be a boolean value");

        this.secondary = ((Boolean)constraintParameter).booleanValue();
        super.setParameter(constraintParameter);
    }

    protected void processValidate(Object target, Object propertyValue, Errors errors) {
    }

    boolean supports(Class type) {
        return true;
    }

    String getName() {
        return "secondary";
    }
}

