/*
 * $RCSfile: CartServiceTest.java,v $
 * $Revision: 1.1 $
 *
 * Comments:
 *
 * (C) Copyright ParaSoft Corporation 2011.  All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 * $Author: jfulmer $          $Locker:  $
 * $Date: 2012/10/30 00:27:32 $
 * $Log: CartServiceTest.java,v $
 * Revision 1.1  2012/10/30 00:27:32  jfulmer
 * @task 45549 - PR 102758 - Added bookstore version 2 to parabank bookstore tutorial.
 *
 * Revision 1.1  2011/11/09 23:27:33  dchung
 * @task 38201 - Restore WS-Security Web Services, refactor Bookstore Web service, store Bookstore Web service on Parabank2
 *
 */
package com.parasoft.bookstore2;

import junit.framework.*;

public class CartServiceTest extends TestCase{
    public void testAddItemToCart() {
        DisplayOrder no = null;
        //add cart with negative quantity
        try {
            no = new CartService().addItemToCart(0,0,-1);
        } catch (Exception e) {
            assertEquals(e.getMessage(),"Cannot have an order with negative quantity.");
        }
        assertNull(no);
        try {
            //add order to cart with id 0
            no = new CartService().addItemToCart(0, 1, 1);
            assertEquals(no.getCartId(), 1);
            Book book = no.getItem().getBook();
            assertEquals(book.getProductInfo().getId(), 1);
            assertEquals(book.getProductInfo().getStockQuantity(), 20);
            assertEquals(book.getPublisher(), "Prentice Hall");
            //add order to existing cart
            no = new CartService().addItemToCart(1, 1, 1);
            assertEquals(no.getCartId(), 1);
            Order order = no.getItem();
            assertEquals(order.getQuantity(), 2);
            //add order to non-existent cart 
            no = new CartService().addItemToCart(1000, 1, 1);
        } catch (Exception e) {
            assertEquals("An order with Cart Id 1000 does not exist!", e.getMessage());
        }
    }
    
    public void testUpdateItemInCart() {
        DisplayOrder no = null;
        //update order with negative quantity
        try {
            no = new CartService().updateItemInCart(1, 1, -1);
        } catch (Exception e) {
            assertEquals("Cannot update an order with negative quantity.", e.getMessage());
        }
        assertNull(no);
        //update order with higher quantity than what's in stock
        try {
            no = new CartService().updateItemInCart(1, 1, 1000);
        } catch (Exception e) {
            assertEquals("Did not update order with cartId 1," + 
                    " 1000 is greater than the quantity in stock: 20",
                    e.getMessage());
        }
        assertNull(no);
    }
}
