package com.openclassrooms.tourguide.helper;

public class InternalTestHelper {

	// Private constructor to hide the implicit public one
	private InternalTestHelper() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	// Set this default up to 100,000 for testing
	private static int internalUserNumber = 100_000;
	
	public static void setInternalUserNumber(final int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}
	
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
