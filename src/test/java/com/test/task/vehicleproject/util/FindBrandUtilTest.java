package com.test.task.vehicleproject.util;

import com.test.task.vehicleproject.brands.VehicleBrand;
import com.test.task.vehicleproject.brands.impl.Ford;
import com.test.task.vehicleproject.brands.impl.Tesla;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindBrandUtilTest {

    @Test
    public void testFindBrandWhenBrandExists() {
        VehicleBrand brand1 = new Ford();
        VehicleBrand brand2 = new Tesla();
        List<VehicleBrand> mockBrandList = Arrays.asList(brand1, brand2);

        FindBrandUtil findBrandUtil = new FindBrandUtil(mockBrandList);

        String result = findBrandUtil.findBrand("FORD");

        assertEquals("FORD", result);
    }

    @Test
    public void testFindBrandWhenBrandDoesNotExist() {
        VehicleBrand brand1 = new Ford();
        VehicleBrand brand2 = new Tesla();
        List<VehicleBrand> mockBrandList = Arrays.asList(brand1, brand2);

        FindBrandUtil findBrandUtil = new FindBrandUtil(mockBrandList);

        String result = findBrandUtil.findBrand("TOYOTA");

        assertEquals("OTHER", result);
    }
}