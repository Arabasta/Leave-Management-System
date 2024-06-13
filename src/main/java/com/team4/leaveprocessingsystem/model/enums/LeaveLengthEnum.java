package com.team4.leaveprocessingsystem.model.enums;

public enum LeaveLengthEnum {
    ONE_DAY_LEAVE(1),
    TWO_DAY_LEAVE(2),
    FIVE_DAY_LEAVE(5),
    FOURTEEN_DAY_LEAVE(14),
    THIRTY_DAY_LEAVE(30);

    private final int value;

    LeaveLengthEnum(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
/*
* Well, you can't quite do it that way.
* PAGE.SIGN_CREATE will never return 1; it will return PAGE.SIGN_CREATE.
* That's the point of enumerated types.

However, if you're willing to add a few keystrokes, you can add fields to your enums, like this:

    public enum PAGE{
        SIGN_CREATE(0),
        SIGN_CREATE_BONUS(1),
        HOME_SCREEN(2),
        REGISTER_SCREEN(3);

        private final int value;

        PAGE(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }
And then you call PAGE.SIGN_CREATE.getValue() to get 0.
*
* */
