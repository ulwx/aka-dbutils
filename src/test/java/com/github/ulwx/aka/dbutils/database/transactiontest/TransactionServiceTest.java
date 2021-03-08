package com.github.ulwx.aka.dbutils.database.transactiontest;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.MDbTransactionManager;
import com.github.ulwx.aka.dbutils.database.transactiontest.bean.testb.T1;
import com.github.ulwx.aka.dbutils.database.transactiontest.dao.Dao;
import com.github.ulwx.aka.dbutils.database.transactiontest.dao.testb.T1Dao;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class TransactionServiceTest {


    public static void initialize() throws Exception {

        String ret = MDbUtils.exeScript(Dao.testa, Dao.class.getPackage().getName(), "testa.sql",false);
        System.out.println("result=\n" + ret + "");
        ret = MDbUtils.exeScript(Dao.testb, Dao.class.getPackage().getName(), "testb.sql",false);
           System.out.println("result=\n" + ret + "");
    }

    @After
    public void clearContext() {

    }

    public TransactionServiceTest() {
        // TODO Auto-generated constructor stub
    }

    public void testSimpleForNoTransaction() throws Exception {
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(1);
        com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(2);
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(3);
        Assert.assertSame(t1.getA(), 1);
    }

    public void testSimpleForTransactionCommitForTesta() throws Exception {
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(1);
        MDbTransactionManager.execute(() -> {
            com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
            com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(2);
            com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(3);
        });
        com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionCommitForTestb() throws Exception {
        T1Dao.updateA(1);
        MDbTransactionManager.execute(() -> {
            T1 t1 = T1Dao
                    .getOne();
            T1Dao.updateA(2);
            T1Dao.updateA(3);
        });
        T1 t1 =
                T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionRollBack() throws Exception {
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(1);
        int i = 1;
        try {
            MDbTransactionManager.execute(() -> {
                com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
                com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(2);
                com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(3);
                throw new RuntimeException();
            });
        } catch (DbException e) {
            Assert.assertTrue(e instanceof  DbException);
        }

        com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
        Assert.assertSame(t1.getA(), 1);

    }



    public void testSimpleForTransactionTwoForTesta() throws Exception {
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(1);
        MDbTransactionManager.execute(()-> {
            testSimpleForTransactionCommitForTesta();
            testSimpleForTransactionCommitForTesta();
        });

        com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionTwoForTestb() throws Exception {
        T1Dao.updateA(1);
        MDbTransactionManager.execute(()-> {
            testSimpleForTransactionCommitForTestb();
            testSimpleForTransactionCommitForTestb();
        });

        T1 t1 = T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);

    }

    public void testSimpleForTransactionTwoDif() throws Exception {
        com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.updateA(1);
        T1Dao.updateA(1);
        MDbTransactionManager.execute(()-> {
            testSimpleForTransactionTwoForTesta();
            testSimpleForTransactionTwoForTestb();
        });

        com.github.ulwx.aka.dbutils.database.transactiontest.bean.testa.T1 t1 = com.github.ulwx.aka.dbutils.database.transactiontest.dao.testa.T1Dao.getOne();
        Assert.assertSame(t1.getA(), 3);
        T1 t11 = T1Dao
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
        initialize();

    }

}
