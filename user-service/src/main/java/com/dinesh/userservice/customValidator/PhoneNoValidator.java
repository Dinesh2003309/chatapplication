package com.dinesh.userservice.customValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNoValidator implements ConstraintValidator<PhoneNo, String> {
    /**
     * Validates a phone number based on the country.
     *
     * @param phoneNumber The phone number to be validated.
     * @param constraintValidatorContext The context object for the validation.
     * @return Returns true if the phone number is valid for the specified country, otherwise false.
     */
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return validatePhoneNumberForCountry(phoneNumber);
    }

    /**
     * Validates a phone number based on the country.
     * Checks if the phone number matches the specified patterns for the US and India.
     *
     * @param phoneNumber The phone number to be validated.
     * @return Returns true if the phone number is valid for the specified country, otherwise false.
     */
    private boolean validatePhoneNumberForCountry(String phoneNumber) {
        if (phoneNumber == null) {
            return true;
        }
        String usPattern = "^(?:(\\+1)|())([2-9]\\d{2}[2-9]\\d{6})$";
        String indiaPattern = "^(\\+?91[\\s.-]?)?[6789]\\d{9}$";
        return phoneNumber.matches(usPattern) || phoneNumber.matches(indiaPattern);
    }
}
