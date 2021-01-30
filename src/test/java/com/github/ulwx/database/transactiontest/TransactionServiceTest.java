package com.github.ulwx.database.transactiontest;

import com.github.ulwx.database.DbException;
import com.github.ulwx.database.MDbTransactionManager;
import com.github.ulwx.database.transactiontest.bean.testa.T1;
import com.github.ulwx.database.transactiontest.dao.Dao;
import com.github.ulwx.database.transactiontest.dao.testa.T1Dao;
import com.github.ulwx.tool.MDbUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class TransactionServiceTest {


    public static void initialize() throws Exception {

        String ret = MDbUtils.exeScript(Dao.testa, Dao.class.getPackage().getName(), "testa.sql");
        System.out.println("result=\n" + ret + "");
        ret = MDbUtils.exeScript(Dao.testb, Dao.class.getPackage().getName(), "testb.sql");
        System.out.println("result=\n" + ret + "");
    }

    @After
    public void clearContext() {

    }

    public TransactionServiceTest() {
        // TODO Auto-generated constructor stub
    }

    public void testSimpleForNoTransaction() throws Exception {
        T1Dao.updateA(1);
        T1 t1 = T1Dao.getOne();
        T1Dao.updateA(2);
        T1Dao.updateA(3);
        Assert.assertSame(t1.getA(), 1);
    }

    public void testSimpleForTransactionCommitForTesta() throws Exception {
        T1Dao.updateA(1);
        MDbTransactionManager.execute(() -> {
            T1 t1 = T1Dao.getOne();
            T1Dao.updateA(2);
            T1Dao.updateA(3);
        });
        T1 t1 = T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionCommitForTestb() throws Exception {
        com.github.ulwx.database.transactiontest.dao.testb.T1Dao.updateA(1);
        MDbTransactionManager.execute(() -> {
            com.github.ulwx.database.transactiontest.bean.testb.T1 t1 = com.github.ulwx.database.transactiontest.dao.testb.T1Dao
                    .getOne();
            com.github.ulwx.database.transactiontest.dao.testb.T1Dao.updateA(2);
            com.github.ulwx.database.transactiontest.dao.testb.T1Dao.updateA(3);
        });
        com.github.ulwx.database.transactiontest.bean.testb.T1 t1 =
                com.github.ulwx.database.transactiontest.dao.testb.T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionRollBack() throws Exception {
        T1Dao.updateA(1);
        int i = 1;
        try {
            MDbTransactionManager.execute(() -> {
                T1 t1 = T1Dao.getOne();
                T1Dao.updateA(2);
                T1Dao.updateA(3);
                throw new RuntimeException();
            });
        } catch (DbException e) {
            Assert.assertTrue(e instanceof  DbException);
        }

        T1 t1 = T1Dao.getOne();
        Assert.assertSame(t1.getA(), 1);

    }



    public void testSimpleForTransactionTwoForTesta() throws Exception {
        T1Dao.updateA(1);
        MDbTransactionManager.execute(()-> {
            testSimpleForTransactionCommitForTesta();
            testSimpleForTransactionCommitForTesta();
        });

        T1 t1 = T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionTwoForTestb() throws Exception {
        com.github.ulwx.database.transactiontest.dao.testb.T1Dao.updateA(1);
        MDbTransactionManager.execute(()-> {
            testSimpleForTransactionCommitForTestb();
            testSimpleForTransactionCommitForTestb();
        });

        com.github.ulwx.database.transactiontest.bean.testb.T1 t1 = com.github.ulwx.database.transactiontest.dao.testb.T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionTwoDif() throws Exception {
        T1Dao.updateA(1);
        com.github.ulwx.database.transactiontest.dao.testb.T1Dao.updateA(1);
        MDbTransactionManager.execute(()-> {
            testSimpleForTransactionTwoForTesta();
            testSimpleForTransactionTwoForTestb();
        });

        T1 t1 = T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);
        com.github.ulwx.database.transactiontest.bean.testb.T1 t11 = com.github.ulwx.database.transactiontest.dao.testb.T1Dao
                .getOne();
        Assert.assertSame(t11.getA(), 3);

    }


    @Test
    public void testAll() throws Exception {
        testSimpleForNoTransaction();
        testSimpleForTransactionCommitForTesta();
        testSimpleForTransactionRollBack();
        testSimpleForTransactionTwoForTesta();
         testSimpleForTransactionTwoDif();
    }


    public static void main(String[] args) throws Exception {
        //initialize();

    }

}
