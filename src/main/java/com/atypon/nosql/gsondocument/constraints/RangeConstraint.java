package com.atypon.nosql.gsondocument.constraints;

import com.google.gson.JsonElement;

public class RangeConstraint implements Constraint {
    private final double firstNumber;

    private final double secondNumber;

    public static RangeConstraint between(double firstNumber, double secondNumber) {
        return new RangeConstraint(firstNumber, secondNumber);
    }

    public RangeConstraint(double firstNumber, double secondNumber) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        double number = jsonElement.getAsDouble();
        return number >= firstNumber && number <= secondNumber;
    }
}
