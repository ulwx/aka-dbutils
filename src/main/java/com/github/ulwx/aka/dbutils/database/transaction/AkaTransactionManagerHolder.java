
package com.github.ulwx.aka.dbutils.database.transaction;


import java.util.Stack;

public final class AkaTransactionManagerHolder {
    
    private static final ThreadLocal<Stack<AKaTransactionManager>> CONTEXT =
            new ThreadLocal<Stack<AKaTransactionManager>>() {
        @Override
        protected Stack<AKaTransactionManager> initialValue() {
            return new Stack<AKaTransactionManager>();
        }
    };
    
    /**
     * Get transaction type for current thread.
     *
     * @return transaction Manager
     */
    public static AKaTransactionManager get() {
        if(CONTEXT.get().size()>0) {
            return CONTEXT.get().peek();
        }else {
            return null;
        }
    }
    
    /**
     * Set transaction type for current thread.
     *
     * @param transactionManager transaction Manager
     */
    public static void set(final AKaTransactionManager transactionManager) {
        CONTEXT.get().push(transactionManager);
    }
    
    /**
     * Clear transaction type for current thread.
     */
    public static void clear() {
        if(CONTEXT.get().size()>0) {
            CONTEXT.get().pop();
        }
    }

}
