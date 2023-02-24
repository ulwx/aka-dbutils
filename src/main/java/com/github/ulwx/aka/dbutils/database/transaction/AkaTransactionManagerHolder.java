
package com.github.ulwx.aka.dbutils.database.transaction;


public final class AkaTransactionManagerHolder {
    
    private static final ThreadLocal<AKaTransactionManager> CONTEXT = new ThreadLocal<>();
    
    /**
     * Get transaction type for current thread.
     *
     * @return transaction Manager
     */
    public static AKaTransactionManager get() {
        return CONTEXT.get();
    }
    
    /**
     * Set transaction type for current thread.
     *
     * @param transactionManager transaction Manager
     */
    public static void set(final AKaTransactionManager transactionManager) {
        CONTEXT.set(transactionManager);
    }
    
    /**
     * Clear transaction type for current thread.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
