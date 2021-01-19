package com.zeus_logistics.ZL.Tests;

import com.zeus_logistics.ZL.interactors.NewOrderInteractor;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Tests {

    @Mock
    NewOrderInteractor newOrderInteractor;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void validateIfNewOrderIsReady() {
        Assert.assertEquals(false, newOrderInteractor.isOrderReadyToSubmit());
    }
}
