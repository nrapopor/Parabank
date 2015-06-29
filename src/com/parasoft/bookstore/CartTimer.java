package com.parasoft.bookstore;

import java.util.*;

public class CartTimer extends TimerTask {
    public void run() {
        new CartService().removeExpiredOrdersAndBooks();
    }
}
